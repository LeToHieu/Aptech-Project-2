package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    /**
     * Create a borrow record in a safe transaction, decrement inventory if present.
     * Returns generated borrow id on success, or negative code on failure.
     */
    public long createBorrow(int userId, int bookId, int days) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1) check duplicate active borrow
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Id FROM borrow WHERE UserId = ? AND BookId = ? AND Status = 0 FOR UPDATE")) {
                ps.setInt(1, userId);
                ps.setInt(2, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        return -1; // already active
                    }
                }
            }

            // 2) if inventory exists, lock and decrement
            try (PreparedStatement psInv = conn.prepareStatement(
                    "SELECT available_copies FROM book_inventory WHERE BookId = ? FOR UPDATE")) {
                psInv.setInt(1, bookId);
                try (ResultSet rs = psInv.executeQuery()) {
                    if (rs.next()) {
                        int avail = rs.getInt("available_copies");
                        if (avail <= 0) {
                            conn.rollback();
                            return -2; // no copies
                        }
                        try (PreparedStatement upd = conn.prepareStatement(
                                "UPDATE book_inventory SET available_copies = available_copies - 1 WHERE BookId = ?")) {
                            upd.setInt(1, bookId);
                            upd.executeUpdate();
                        }
                    }
                }
            }

            // 3) insert borrow (app computes expire day)
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO borrow (BorrowDay, ExpireDay, ReturnDateTime, UserId, BookId, Status) VALUES (NOW(), DATE_ADD(NOW(), INTERVAL ? DAY), NULL, ?, ?, 0)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ins.setInt(1, days);
                ins.setInt(2, userId);
                ins.setInt(3, bookId);
                int affected = ins.executeUpdate();
                if (affected == 0) {
                    conn.rollback();
                    return -3; // failed insert
                }
                try (ResultSet gk = ins.getGeneratedKeys()) {
                    if (gk.next()) {
                        long borrowId = gk.getLong(1);
                        // insert audit if table exists
                        try (PreparedStatement audit = conn.prepareStatement(
                                "INSERT INTO borrow_audit (BorrowId, UserId, BookId, Action, Note) VALUES (?, ?, ?, 'borrow', NULL)")) {
                            audit.setLong(1, borrowId);
                            audit.setInt(2, userId);
                            audit.setInt(3, bookId);
                            try { audit.executeUpdate(); } catch (SQLException ignore) {}
                        }
                        conn.commit();
                        return borrowId;
                    } else {
                        conn.rollback();
                        return -4;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return -5;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    // ---------------------------------------------------------------------------------
    // Compatibility layer for UI controllers (methods expected by BorrowListController)
    // ---------------------------------------------------------------------------------

    public void updateOverdueStatus() {
        String sql = "UPDATE borrow SET Status = 2 WHERE Status = 0 AND ExpireDay IS NOT NULL AND ExpireDay < NOW()";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
            // optionally write audit rows for changed rows (omitted for simplicity)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean create(long userId, long bookId) {
        long id = createBorrow((int)userId, (int)bookId, 14);
        return id > 0;
    }

    public boolean returnBook(long borrowId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // get record to know bookId and status
            long bookId = -1;
            int status = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT BookId, Status FROM borrow WHERE Id = ? FOR UPDATE")) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getLong("BookId");
                        status = rs.getInt("Status");
                    } else {
                        conn.rollback();
                        return false; // not found
                    }
                }
            }

            if (status != 0) {
                conn.rollback();
                return false; // only active can be returned
            }

            // update borrow: set return time and status
            try (PreparedStatement ups = conn.prepareStatement("UPDATE borrow SET ReturnDateTime = NOW(), Status = 1 WHERE Id = ?")) {
                ups.setLong(1, borrowId);
                ups.executeUpdate();
            }

            // increment inventory if exists
            try (PreparedStatement psInv = conn.prepareStatement("SELECT available_copies FROM book_inventory WHERE BookId = ? FOR UPDATE")) {
                psInv.setLong(1, bookId);
                try (ResultSet rs = psInv.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement upd = conn.prepareStatement("UPDATE book_inventory SET available_copies = available_copies + 1 WHERE BookId = ?")) {
                            upd.setLong(1, bookId);
                            upd.executeUpdate();
                        }
                    }
                }
            }

            // insert audit
            try (PreparedStatement audit = conn.prepareStatement(
                    "INSERT INTO borrow_audit (BorrowId, UserId, BookId, Action, Note) VALUES (?, ?, ?, 'return', NULL)")) {
                audit.setLong(1, borrowId);
                // userId not strictly required here; try to read from borrow table
                // read user id
                try (PreparedStatement psu = conn.prepareStatement("SELECT UserId FROM borrow WHERE Id = ?")) {
                    psu.setLong(1, borrowId);
                    try (ResultSet rs = psu.executeQuery()) {
                        if (rs.next()) audit.setLong(2, rs.getLong("UserId")); else audit.setNull(2, Types.BIGINT);
                    }
                }
                audit.setLong(3, bookId);
                try { audit.executeUpdate(); } catch (SQLException ignore) {}
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    public boolean delete(long id) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // If it's active, try to increment inventory
            try (PreparedStatement ps = conn.prepareStatement("SELECT BookId, Status FROM borrow WHERE Id = ? FOR UPDATE")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long bookId = rs.getLong("BookId");
                        int status = rs.getInt("Status");
                        if (status == 0) {
                            // increment inventory if exists
                            try (PreparedStatement psInv = conn.prepareStatement("SELECT available_copies FROM book_inventory WHERE BookId = ? FOR UPDATE")) {
                                psInv.setLong(1, bookId);
                                try (ResultSet r2 = psInv.executeQuery()) {
                                    if (r2.next()) {
                                        try (PreparedStatement upd = conn.prepareStatement("UPDATE book_inventory SET available_copies = available_copies + 1 WHERE BookId = ?")) {
                                            upd.setLong(1, bookId);
                                            upd.executeUpdate();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        conn.rollback();
                        return false; // not found
                    }
                }
            }

            try (PreparedStatement del = conn.prepareStatement("DELETE FROM borrow WHERE Id = ?")) {
                del.setLong(1, id);
                del.executeUpdate();
            }

            // audit
            try (PreparedStatement audit = conn.prepareStatement(
                    "INSERT INTO borrow_audit (BorrowId, UserId, BookId, Action, Note) VALUES (?, NULL, NULL, 'delete', NULL)")) {
                audit.setLong(1, id);
                try { audit.executeUpdate(); } catch (SQLException ignore) {}
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    // Thêm vào BorrowDAO.java
    public String getBorrowStatus(long userId, long bookId) {
        String sql = """
        SELECT status FROM borrow 
        WHERE UserId = ? AND BookId = ? 
        AND status IN (0, 1)  -- 0: pending, 1: đang mượn
        ORDER BY BorrowDay DESC LIMIT 1
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("status") == 0 ? "PENDING" : "BORROWED";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "AVAILABLE";
    }

    // 0 = Pending (đang chờ duyệt), 1 = Đang mượn, 2 = Bị từ chối, 3 = Quá hạn, 4 = Đã trả

    /**
     * Người dùng gửi yêu cầu mượn sách
     * → Tạo bản ghi borrow (status = 0)
     * → Giảm ngay 1 cuốn trong kho (giữ chỗ)
     */
    public boolean createBorrowAndReserve(int userId, int bookId) {
        String updateBook = "UPDATE book SET BorrowBook = BorrowBook + 1 WHERE Id = ? AND TotalBook > BorrowBook";
        String insertBorrow = "INSERT INTO borrow (UserId, BookId, Status) VALUES (?, ?, 0)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Giữ chỗ trong kho
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setInt(1, bookId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false; // Hết sách hoặc không tồn tại
                }
            }

            // 2. Tạo yêu cầu mượn
            try (PreparedStatement ps = conn.prepareStatement(insertBorrow)) {
                ps.setInt(1, userId);
                ps.setInt(2, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // Thêm vào BorrowDAO.java
    public boolean cancelPendingBorrow(long borrowId) {
        String getBookId = "SELECT BookId FROM borrow WHERE Id = ? AND Status = 0";
        String updateBook = "UPDATE book SET BorrowBook = BorrowBook - 1 WHERE Id = ?";
        String deleteBorrow = "DELETE FROM borrow WHERE Id = ? AND Status = 0";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int bookId = -1;
            try (PreparedStatement ps = conn.prepareStatement(getBookId)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) bookId = rs.getInt("BookId");
                }
            }

            if (bookId == -1) {
                conn.rollback();
                return false;
            }

            // Trả lại 1 cuốn vào kho
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            // Xóa yêu cầu mượn
            try (PreparedStatement ps = conn.prepareStatement(deleteBorrow)) {
                ps.setLong(1, borrowId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // Lấy danh sách mượn theo user
    public List<Borrow> getBorrowsByUser(long userId) {
        List<Borrow> list = new ArrayList<>();
        String sql = """
            SELECT b.*, u.UserName, bk.Title, bk.Description AS BookDescription
            FROM borrow b
            JOIN user u ON b.UserId = u.Id
            JOIN book bk ON b.BookId = bk.Id
            WHERE b.UserId = ?
            ORDER BY b.BorrowDay DESC
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Borrow borrow = extractBorrow(rs);
                    list.add(borrow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // New method: Return a borrowed book
    public boolean returnBorrow(long borrowId) {
        String getBookId = "SELECT BookId FROM borrow WHERE Id = ? AND (Status = 1 OR Status = 3)";
        String updateBorrow = "UPDATE borrow SET Status = 4, ReturnDateTime = NOW() WHERE Id = ? AND (Status = 1 OR Status = 3)";
        String updateBook = "UPDATE book SET BorrowBook = BorrowBook - 1 WHERE Id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int bookId = -1;
            try (PreparedStatement ps = conn.prepareStatement(getBookId)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) bookId = rs.getInt("BookId");
                }
            }

            if (bookId == -1) {
                conn.rollback();
                return false;
            }

            // Update borrow status and return date
            try (PreparedStatement ps = conn.prepareStatement(updateBorrow)) {
                ps.setLong(1, borrowId);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Decrease borrowed count in book
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    private Borrow extractBorrow(ResultSet rs) throws SQLException {
        Borrow b = new Borrow();
        b.setId(rs.getLong("Id"));
        b.setBorrowDay(rs.getTimestamp("BorrowDay"));
        b.setExpireDay(rs.getTimestamp("ExpireDay"));
        b.setReturnDateTime(rs.getTimestamp("ReturnDateTime"));
        b.setUserId(rs.getLong("UserId"));
        b.setBookId(rs.getLong("BookId"));
        b.setStatus(rs.getInt("Status"));
        b.setUserName(rs.getString("UserName"));
        b.setBookTitle(rs.getString("Title"));
        b.setBookDescription(rs.getString("BookDescription"));  // New field
        return b;
    }

    public boolean approveBorrow(long borrowId, long librarianId, int days) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Kiểm tra trạng thái phải là 0 (Pending)
            String checkSql = "SELECT Status FROM borrow WHERE Id = ? FOR UPDATE";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Cập nhật mượn thành công: Status = 1, đặt ngày mượn + hạn trả
            String sql = "UPDATE borrow SET Status = 1, BorrowDay = NOW(), ExpireDay = DATE_ADD(NOW(), INTERVAL ? DAY) WHERE Id = ? AND Status = 0";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, days);
                ps.setLong(2, borrowId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Từ chối yêu cầu mượn (Status 0 -> 2) và hoàn lại BorrowBook.
     * PHƯƠNG THỨC NÀY CHỈ NÊN ĐƯỢC GỌI KHI Status = 0.
     */
    public boolean rejectBorrow(long borrowId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            long bookId = 0;

            // 1. Lấy BookId và kiểm tra Status (Nên là 0)
            String selectSql = "SELECT BookId, Status FROM borrow WHERE Id = ? FOR UPDATE";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getLong("BookId");
                        if (rs.getInt("Status") != 0) {
                            conn.rollback();
                            return false; // Chỉ từ chối khi Pending (0)
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Cập nhật bản ghi mượn: Status = 2 (Bị từ chối)
            String updateBorrow = "UPDATE borrow SET Status = 2, ReturnDateTime = NOW() WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBorrow)) {
                ps.setLong(1, borrowId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Giảm BorrowBook (hoàn lại sách trong kho)
            String updateBook = "UPDATE book SET BorrowBook = BorrowBook - 1 WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setLong(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Trả sách (Status 1/3 -> 4) và hoàn lại BorrowBook.
     */
    public boolean returnBook(long borrowId, long userId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            long bookId = 0;

            // 1. Lấy BookId và kiểm tra trạng thái hợp lệ (1: Đang mượn hoặc 3: Quá hạn)
            String selectSql = "SELECT BookId, Status FROM borrow WHERE Id = ? FOR UPDATE";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getLong("BookId");
                        int currentStatus = rs.getInt("Status");
                        // Chỉ cho phép trả sách khi đang mượn (1) hoặc quá hạn (3)
                        if (currentStatus != 1 && currentStatus != 3) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Cập nhật bản ghi mượn: Status = 4 (Đã trả), đặt ReturnDateTime
            String updateBorrow = "UPDATE borrow SET ReturnDateTime = NOW(), Status = 4 WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBorrow)) {
                ps.setLong(1, borrowId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Giảm BorrowBook (hoàn lại sách trong kho)
            String updateBook = "UPDATE book SET BorrowBook = BorrowBook - 1 WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setLong(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Borrow> getAllBorrows() {
        List<Borrow> list = new ArrayList<>();
        String sql = """
        SELECT b.*, u.UserName, bk.Title as bookTitle 
        FROM borrow b 
        JOIN user u ON b.UserId = u.Id 
        JOIN book bk ON b.BookId = bk.Id 
        ORDER BY b.BorrowDay DESC
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Borrow b = new Borrow();
                b.setId(rs.getLong("Id"));
                b.setBorrowDay(rs.getTimestamp("BorrowDay"));
                b.setExpireDay(rs.getTimestamp("ExpireDay"));
                b.setReturnDateTime(rs.getTimestamp("ReturnDateTime"));
                b.setUserId(rs.getLong("UserId"));
                b.setBookId(rs.getLong("BookId"));
                b.setStatus(rs.getInt("Status"));
                b.setUserName(rs.getString("UserName"));
                b.setBookTitle(rs.getString("bookTitle"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}