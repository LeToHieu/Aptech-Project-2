// BookDAO.java (Updated with the 3 new methods: getTotalBooksCount, getBooksPaginated, searchBooksByTitlePaginated)
// Note: Changed getTotalBooksCount return type to int for accuracy (count should be integer). If double is needed for some reason, adjust accordingly.
package com.aptech.aptechproject2.DAO;

import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookDAO {

//    public List<Book> getAllBooks() {
//        List<Book> books = new ArrayList<>();
//        String sql = "SELECT * FROM book ORDER BY CreateTime DESC";
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Book book = extractBook(rs);
//                book.setAuthors(getAuthorsForBook(book.getId()));
//                book.setCategories(getCategoriesForBook(book.getId()));
//                books.add(book);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return books;
//    }

    public List<Book> getLatestBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY CreateTime DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /*public List<Book> searchBooksByTitle(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE Title LIKE ? ORDER BY CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }*/

    public List<Book> searchBooks(String keyword, List<Author> authors, List<Category> categories) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT b.* FROM book b ");
        if (!authors.isEmpty()) {
            sql.append("JOIN bookauthor ba ON b.Id = ba.BookId ");
        }
        if (!categories.isEmpty()) {
            sql.append("JOIN bookcategory bc ON b.Id = bc.BookId ");
        }
        sql.append("WHERE 1=1 ");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (b.Title LIKE ? OR b.Description LIKE ?) ");
        }
        if (!authors.isEmpty()) {
            sql.append("AND ba.AuthorId IN (");
            for (int i = 0; i < authors.size(); i++) {
                sql.append("?");
                if (i < authors.size() - 1) sql.append(",");
            }
            sql.append(") ");
        }
        if (!categories.isEmpty()) {
            sql.append("AND bc.CategoryId IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append("?");
                if (i < categories.size() - 1) sql.append(",");
            }
            sql.append(") ");
        }
        sql.append("ORDER BY b.CreateTime DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            for (Author author : authors) {
                ps.setInt(paramIndex++, author.getId());
            }
            for (Category category : categories) {
                ps.setInt(paramIndex++, category.getId());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean create(Book book) {
        String sql = "INSERT INTO book (Title, Description, TotalBook, BorrowBook, Image, Url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setInt(3, book.getTotalBook());
            ps.setInt(4, book.getBorrowBook());
            ps.setString(5, book.getImage());
            ps.setString(6, book.getUrl());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    book.setId(id);
                    updateBookAuthors(book);
                    updateBookCategories(book);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Book book) {
        String sql = "UPDATE book SET Title = ?, Description = ?, TotalBook = ?, BorrowBook = ?, Image = ?, Url = ? WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setInt(3, book.getTotalBook());
            ps.setInt(4, book.getBorrowBook());
            ps.setString(5, book.getImage());
            ps.setString(6, book.getUrl());
            ps.setInt(7, book.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                updateBookAuthors(book);
                updateBookCategories(book);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM book WHERE Id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public List<Author> getAuthorsForBook(int bookId) {
        List<Author> authors = new ArrayList<>();  // ← Khởi tạo ngay từ đầu
        String sql = "SELECT a.* FROM author a " +
                "JOIN bookauthor ba ON a.Id = ba.AuthorId " +
                "WHERE ba.BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(new Author(
                            rs.getInt("Id"),
                            rs.getString("Name"),
                            rs.getString("Description"),
                            rs.getString("Image")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors; // ← Luôn trả về List rỗng nếu không có, không bao giờ null
    }

    public List<Category> getCategoriesForBook(int bookId) {
        List<Category> categories = new ArrayList<>();  // ← Khởi tạo ngay từ đầu
        String sql = "SELECT c.* FROM Category c " +
                "JOIN bookcategory bc ON c.Id = bc.CategoryId " +
                "WHERE bc.BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(
                            rs.getInt("Id"),
                            rs.getString("Name"),
                            rs.getString("Description")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories; // ← Luôn trả về List rỗng nếu không có
    }

    private void updateBookAuthors(Book book) throws SQLException {
        String deleteSql = "DELETE FROM bookauthor WHERE BookId = ?";
        String insertSql = "INSERT INTO bookauthor (BookId, AuthorId) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, book.getId());
                deletePs.executeUpdate();
            }
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                for (Author author : book.getAuthors()) {
                    insertPs.setInt(1, book.getId());
                    insertPs.setInt(2, author.getId());
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }
            conn.commit();
        }
    }

    private void updateBookCategories(Book book) throws SQLException {
        String deleteSql = "DELETE FROM bookcategory WHERE BookId = ?";
        String insertSql = "INSERT INTO bookcategory (BookId, CategoryId) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, book.getId());
                deletePs.executeUpdate();
            }
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                for (Category category : book.getCategories()) {
                    insertPs.setInt(1, book.getId());
                    insertPs.setInt(2, category.getId());
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }
            conn.commit();
        }
    }

    public int getSearchBooksCount(String keyword) {
        String sql = "SELECT COUNT(*) FROM book WHERE Title LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Book> getBooksByCategory(int categoryId, int limit, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM book b JOIN bookcategory bc ON b.Id = bc.BookId " +
                "WHERE bc.CategoryId = ? ORDER BY b.Id ASC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public int getBooksCountByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM book b JOIN bookcategory bc ON b.Id = bc.BookId WHERE bc.CategoryId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Book> getBooksByAuthor(int authorId, int limit, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM book b JOIN bookauthor ba ON b.Id = ba.BookId " +
                "WHERE ba.AuthorId = ? ORDER BY b.Id ASC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public int getBooksCountByAuthor(int authorId) {
        String sql = "SELECT COUNT(*) FROM book b JOIN bookauthor ba ON b.Id = ba.BookId WHERE ba.AuthorId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Book> getBooksByBorrowCount(int limit, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COUNT(br.Id) AS borrow_count " +
                "FROM book b LEFT JOIN borrow br ON b.Id = br.BookId " +
                "GROUP BY b.Id ORDER BY borrow_count DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(extractBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getBooksByAverageRating(int limit, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, AVG(r.Rating) AS avg_rating " +
                "FROM book b LEFT JOIN review r ON b.Id = r.BookId " +
                "GROUP BY b.Id ORDER BY avg_rating DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAverageRating(rs.getDouble("avg_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // New methods added as per request
    public int getTotalBooksCount() {
        String sql = "SELECT COUNT(*) FROM book";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Book> getBooksPaginated(int pageSize, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY CreateTime DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooksByTitlePaginated(String keyword, int pageSize, int offset) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE Title LIKE ? ORDER BY CreateTime DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public double calculateAverageRating(int bookId) {
        // Placeholder: Implement query to get AVG(rating) from review table
        String sql = "SELECT AVG(Rating) FROM review WHERE BookId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = ((java.sql.Connection) conn).prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


    Book extractBook(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getInt("Id"),
                rs.getString("Title"),
                rs.getString("Description"),
                rs.getInt("TotalBook"),
                rs.getInt("BorrowBook"),
                rs.getString("Image"),
                rs.getString("Url"),
                rs.getTimestamp("CreateTime"),
                rs.getTimestamp("UpdateTime")
        );

        // Cố gắng lấy averageRating
        try {
            // Kiểm tra cột có tồn tại không trước khi getDouble
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase("averageRating")) {
                    book.setAverageRating(rs.getDouble("averageRating"));
                    break;
                }
            }
        } catch (SQLException e) {
            // Bỏ qua lỗi nếu cột không tồn tại
        }
        return book;
    }

    /**
     * Lấy danh sách sách được đánh giá cao nhất (Top Rated).
     * Sắp xếp theo điểm trung bình rating giảm dần.
     */
    public List<Book> getTopRatedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = """
        SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating
        FROM book b
        LEFT JOIN review r ON b.Id = r.BookId
        GROUP BY b.Id
        ORDER BY averageRating DESC, b.Title ASC
        LIMIT ?
        """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBook(rs);
                    book.setAverageRating(rs.getDouble("averageRating"));

                    // DÒNG NÀY BẮT BUỘC PHẢI CÓ!!!
                    book.setAuthors(getAuthorsForBook(book.getId()));
                    book.setCategories(getCategoriesForBook(book.getId()));

                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getMostBorrowedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = """
        SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating
        FROM book b
        LEFT JOIN review r ON b.Id = r.BookId
        GROUP BY b.Id
        ORDER BY b.BorrowBook DESC, b.Title ASC
        LIMIT ?
        """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBook(rs);
                    book.setAverageRating(rs.getDouble("averageRating"));

                    // DÒNG NÀY BẮT BUỘC PHẢI CÓ!!!
                    book.setAuthors(getAuthorsForBook(book.getId()));
                    book.setCategories(getCategoriesForBook(book.getId()));

                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Tìm kiếm sách theo tiêu đề, có tính rating.
     */
    public List<Book> searchBooksByTitle(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating " +
                "FROM book b LEFT JOIN review r ON b.Id = r.BookId " +
                "WHERE b.Title LIKE ? " +
                "GROUP BY b.Id " +
                "ORDER BY b.Title ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // BookDAO.java - Add these methods or update existing

    public List<Book> getBooksByAuthor(int authorId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating " +
                "FROM book b " +
                "JOIN bookauthor ba ON b.Id = ba.BookId " +
                "LEFT JOIN review r ON b.Id = r.BookId " +
                "WHERE ba.AuthorId = ? " +
                "GROUP BY b.Id " +
                "ORDER BY b.CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBook(rs);
                    book.setAuthors(getAuthorsForBook(book.getId()));
                    book.setCategories(getCategoriesForBook(book.getId()));
                    book.setAverageRating(rs.getDouble("averageRating"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getRandomBooksByAuthor(int authorId, int limit) {
        List<Book> allBooks = getBooksByAuthor(authorId);
        if (allBooks.size() > limit) {
            Collections.shuffle(allBooks);
            return allBooks.subList(0, limit);
        }
        return allBooks;
    }

    public List<Book> getBooksByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating " +
                "FROM book b " +
                "JOIN bookcategory bc ON b.Id = bc.BookId " +
                "LEFT JOIN review r ON b.Id = r.BookId " +
                "WHERE bc.CategoryId = ? " +
                "GROUP BY b.Id " +
                "ORDER BY b.CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBook(rs);
                    book.setAuthors(getAuthorsForBook(book.getId()));
                    book.setCategories(getCategoriesForBook(book.getId()));
                    book.setAverageRating(rs.getDouble("averageRating"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getRandomBooksByCategory(int categoryId, int limit) {
        List<Book> allBooks = getBooksByCategory(categoryId);
        if (allBooks.size() > limit) {
            Collections.shuffle(allBooks);
            return allBooks.subList(0, limit);
        }
        return allBooks;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating " +
                "FROM book b " +
                "LEFT JOIN review r ON b.Id = r.BookId " +
                "GROUP BY b.Id " +
                "ORDER BY b.CreateTime DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = extractBook(rs);
                book.setAuthors(getAuthorsForBook(book.getId()));
                book.setCategories(getCategoriesForBook(book.getId()));
                book.setAverageRating(rs.getDouble("averageRating"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int id) {
        String sql = "SELECT b.*, COALESCE(AVG(r.Rating), 0) AS averageRating " +
                "FROM book b " +
                "LEFT JOIN review r ON b.Id = r.BookId " +
                "WHERE b.Id = ? " +
                "GROUP BY b.Id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book book = extractBook(rs);
                    book.setAuthors(getAuthorsForBook(book.getId()));
                    book.setCategories(getCategoriesForBook(book.getId()));
                    book.setAverageRating(rs.getDouble("averageRating"));
                    return book;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === DUYỆT MƯỢN SÁCH (Pending → Đang mượn) ===
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

    // === TỪ CHỐI MƯỢN SÁCH (Pending → Bị từ chối) ===
    public boolean rejectBorrow(long borrowId) {
        String sql = "UPDATE borrow SET Status = 2, ReturnDateTime = NOW() WHERE Id = ? AND Status = 0";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, borrowId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === TRẢ SÁCH (Đang mượn hoặc Quá hạn → Đã trả) ===
    public boolean returnBook(long borrowId, long librarianId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Kiểm tra trạng thái hợp lệ: chỉ trả khi là 1 (đang mượn) hoặc 3 (quá hạn)
            String checkSql = "SELECT Status, BookId FROM borrow WHERE Id = ? FOR UPDATE";
            int currentStatus = -1;
            long bookId = 0;

            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, borrowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentStatus = rs.getInt("Status");
                        bookId = rs.getLong("BookId");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (currentStatus != 1 && currentStatus != 3) {
                conn.rollback();
                return false; // Không phải trạng thái được phép trả
            }

            // Cập nhật trạng thái trả sách
            String updateBorrow = "UPDATE borrow SET Status = 4, ReturnDateTime = NOW(), LibrarianId = ? WHERE Id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBorrow)) {
                ps.setLong(1, librarianId);
                ps.setLong(2, borrowId);
                ps.executeUpdate();
            }

            // Tăng số sách có sẵn (nếu dùng bảng book_inventory thì sửa ở đây)
            // Nếu bạn dùng cột BorrowBook trong bảng book:
            String updateBook = "UPDATE book SET BorrowBook = BorrowBook - 1 WHERE Id = ? AND BorrowBook > 0";
            try (PreparedStatement ps = conn.prepareStatement(updateBook)) {
                ps.setLong(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ignored) {}
        }
    }

    // === TỰ ĐỘNG CẬP NHẬT QUÁ HẠN (nên gọi định kỳ, ví dụ mỗi ngày 1 lần) ===
    public void updateOverdueStatus() {
        String sql = "UPDATE borrow SET Status = 3 WHERE Status = 1 AND ExpireDay < NOW()";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === KIỂM TRA NGƯỜI DÙNG CÓ THỂ MƯỢN SÁCH NÀY KHÔNG ===
    public boolean canUserBorrowBook(long userId, long bookId) {
        String sql = """
        SELECT COUNT(*) FROM borrow 
        WHERE UserId = ? AND BookId = ? AND Status IN (0,1,3)
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}