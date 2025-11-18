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

    public List<Borrow> getAllBorrows() {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.Id, b.BorrowDay, b.ExpireDay, b.ReturnDateTime, b.UserId, b.BookId, b.Status, u.UserName, bk.Title " +
                "FROM borrow b LEFT JOIN `user` u ON b.UserId = u.Id LEFT JOIN book bk ON b.BookId = bk.Id ORDER BY b.BorrowDay DESC";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Borrow br = new Borrow();
                br.setId(rs.getLong("Id"));
                br.setBorrowDay(rs.getTimestamp("BorrowDay"));
                br.setExpireDay(rs.getTimestamp("ExpireDay"));
                br.setReturnDateTime(rs.getTimestamp("ReturnDateTime"));
                br.setUserId(rs.getLong("UserId"));
                br.setBookId(rs.getLong("BookId"));
                br.setStatus(rs.getInt("Status"));
                try { br.setUserName(rs.getString("UserName")); } catch (Exception ignored) {}
                try { br.setBookTitle(rs.getString("Title")); } catch (Exception ignored) {}
                list.add(br);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
}
