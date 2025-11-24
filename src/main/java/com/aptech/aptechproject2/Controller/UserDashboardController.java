package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.DBUtil;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UserDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private GridPane booksGrid, topRatedGrid;

    // Thêm reference đến topRatedPane để có thể ẩn/hiện hoàn toàn phần Top Rated
    @FXML
    private VBox topRatedPane;

    private BookDAO bookDAO = new BookDAO();

    private final int COLS = 5; // 5 cột main grid
    private final int ROWS = 3; // 3 hàng main grid

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setFullScreen(true);
        });

        // Mặc định khi vào trang user_dashboard (Trang chủ) sẽ hiển thị TopRated
        setTopRatedVisible(true);
        loadLatestBooks();
        loadTopRatedBooks();
    }

    // Helper để ẩn/hiện top rated pane và điều khiển quản lý layout
    private void setTopRatedVisible(boolean visible) {
        if (topRatedPane != null) {
            topRatedPane.setVisible(visible);
            topRatedPane.setManaged(visible); // Nếu false sẽ không chiếm không gian layout
        }
    }

    // Load 15 sách mới nhất cho main grid
    private void loadLatestBooks() {
        booksGrid.getChildren().clear();
        List<Book> books = bookDAO.getLatestBooks(ROWS * COLS);
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (index >= books.size()) break;
                Book book = books.get(index++);
                VBox bookBox = createBookBox(book, 120, 160); // Size lớn cho main
                booksGrid.add(bookBox, col, row);
            }
        }
    }

    // Tạo VBox cho sách (param size cho image)
    private VBox createBookBox(Book book, int imgWidth, int imgHeight) {
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        box.setSpacing(5);

        try {
            String path = "/com/aptech/aptechproject2/images/" + book.getImage();
            ImageView imageView = new ImageView(new Image(new FileInputStream(path)));
            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);
            box.getChildren().add(imageView);

            // make clickable to open book detail modal
            box.setOnMouseClicked(evt -> {
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/book_detail.fxml"));
                    javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                    BookDetailController controller = loader.getController();
                    controller.setBook(book);
                    javafx.stage.Stage stage = new javafx.stage.Stage();
                    stage.setTitle("Chi tiết sách");
                    stage.setScene(scene);
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // hover effect
            javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
            ds.setRadius(8);
            ds.setColor(javafx.scene.paint.Color.gray(0.3));

            javafx.animation.ScaleTransition stEnter = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), box);
            stEnter.setToX(1.05);
            stEnter.setToY(1.05);
            javafx.animation.ScaleTransition stExit = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), box);
            stExit.setToX(1.0);
            stExit.setToY(1.0);

            box.setOnMouseEntered(evt -> {
                stExit.stop();
                stEnter.playFromStart();
                box.setEffect(ds);
                box.setCursor(javafx.scene.Cursor.HAND);
                box.toFront();
            });
            box.setOnMouseExited(evt -> {
                stEnter.stop();
                stExit.playFromStart();
                box.setEffect(null);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Text titleText = new Text(book.getTitle());
        titleText.setWrappingWidth(imgWidth);
        box.getChildren().add(titleText);

        return box;
    }

    @FXML
    void onSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            // Nếu bỏ trống thì trở về trang chủ => hiển thị TopRated
            setTopRatedVisible(true);
            loadLatestBooks();
        } else {
            // Tìm kiếm: ẩn TopRated vì không phải trang chủ
            setTopRatedVisible(false);
            booksGrid.getChildren().clear();
            List<Book> books = bookDAO.searchBooksByTitle(keyword);
            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (index >= books.size()) break;
                    Book book = books.get(index++);
                    VBox bookBox = createBookBox(book, 120, 160);
                    booksGrid.add(bookBox, col, row);
                }
            }
        }
    }

    private void loadTopRatedBooks() {
        if (topRatedGrid == null) return;
        topRatedGrid.getChildren().clear();
        List<Book> topBooks = bookDAO.getTopRatedBooks(10);
        int index = 0;
        int topRows = 2; // 2 hàng
        int topCols = 5; // 5 cột (5x2=10)
        for (int row = 0; row < topRows; row++) {
            for (int col = 0; col < topCols; col++) {
                if (index >= topBooks.size()) break;
                Book book = topBooks.get(index++);
                VBox bookBox = createBookBox(book, 115, 115); // Size nhỏ
                HBox ratingBox = createRatingBox(book.getAverageRating()); // Thêm rating sao
                bookBox.getChildren().add(ratingBox);
                topRatedGrid.add(bookBox, col, row);
            }
        }
    }

    // Tạo HBox cho rating sao (sử dụng Unicode, không cần image)
    private HBox createRatingBox(double rating) {
        HBox ratingBox = new HBox(2);
        int fullStars = (int) rating;
        boolean hasHalf = rating - fullStars >= 0.5;

        for (int i = 0; i < fullStars; i++) {
            Text star = new Text("★"); // Sao đầy (Unicode)
            star.setStyle("-fx-fill: black; -fx-font-size: 15;");
            ratingBox.getChildren().add(star);
        }

        if (hasHalf) {
            Text halfStar = new Text("½★"); // Sao nửa (Unicode hoặc kết hợp)
            halfStar.setStyle("-fx-fill: black; -fx-font-size: 15;");
            ratingBox.getChildren().add(halfStar);
        }

        // Điền sao rỗng đến 5 sao nếu muốn (tùy chọn)
        int emptyStars = 5 - fullStars - (hasHalf ? 1 : 0);
        for (int i = 0; i < emptyStars; i++) {
            Text emptyStar = new Text("☆"); // Sao rỗng (Unicode)
            emptyStar.setStyle("-fx-fill: black; -fx-font-size: 15;");
            ratingBox.getChildren().add(emptyStar);
        }

        return ratingBox;
    }

    @FXML
    void onHome(ActionEvent event) {
        // Trang chủ: hiển thị lại TopRated và load latest
        setTopRatedVisible(true);
        loadLatestBooks(); // Refresh trang chủ (load latest)
        loadTopRatedBooks();
    }

    @FXML
    void onViewAllBooksFull(ActionEvent event) {
        // Khi chuyển sang view khác (all_books), ẩn TopRated ở dashboard
        setTopRatedVisible(false);
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/all_books.fxml", searchField.getScene());
    }

    @FXML
    void onLogout(ActionEvent event) {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", searchField.getScene());
    }

    @FXML
    void onViewCategories(ActionEvent event) {
        // Khi xem categories, ẩn TopRated
        setTopRatedVisible(false);
        booksGrid.getChildren().clear();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name, description FROM category");
            ResultSet rs = ps.executeQuery();

            int row = 0;
            while (rs.next()) {
                VBox card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 8;");
                Text name = new Text("Tên thể loại: " + rs.getString("name"));
                Text desc = new Text("Mô tả: " + rs.getString("description"));
                card.getChildren().addAll(name, desc);
                booksGrid.add(card, 0, row++);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onViewAuthors(ActionEvent event) {
        // Khi xem authors, ẩn TopRated
        setTopRatedVisible(false);
        booksGrid.getChildren().clear();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT Name, Description, Image FROM author");
            ResultSet rs = ps.executeQuery();

            int row = 0;
            while (rs.next()) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #f1f1f1; -fx-border-color: #ccc; -fx-border-radius: 8;");

                // Đọc ảnh
                String imagePath = "src/main/resources/com/aptech/aptechproject2/" + rs.getString("Image");
                ImageView imgView = new ImageView(new Image(new FileInputStream(imagePath)));
                imgView.setFitWidth(100);
                imgView.setFitHeight(100);

                VBox infoBox = new VBox(5);
                Text name = new Text("Tên tác giả: " + rs.getString("Name"));
                Text desc = new Text("Mô tả: " + rs.getString("Description"));
                desc.setWrappingWidth(400);
                infoBox.getChildren().addAll(name, desc);

                card.getChildren().addAll(imgView, infoBox);
                booksGrid.add(card, 0, row++);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onViewUserInfo(ActionEvent event) {
        // Khi xem thông tin người dùng, ẩn TopRated
        setTopRatedVisible(false);
        booksGrid.getChildren().clear();
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Bạn chưa đăng nhập. Vui lòng đăng nhập lại!");
            alert.showAndWait();
            return;
        }

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #f1f1f1; -fx-border-color: #ccc; -fx-border-radius: 8;");

        TextField usernameField = new TextField(currentUser.getUsername());
        usernameField.setPromptText("Tên người dùng");

        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Email");

        TextField phoneField = new TextField(currentUser.getPhoneNumber());
        phoneField.setPromptText("Số điện thoại");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu (để trống nếu không đổi)");

        Button updateBtn = new Button("Cập Nhật");
        updateBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = passwordField.getText().trim();

                String sql = "UPDATE user SET UserName=?, Email=?, PhoneNumber=?, Password=? WHERE Id=?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, phone);

                    String hashed = null;
                    if (!password.isEmpty()) {
                        hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                        ps.setString(4, hashed);
                    } else {
                        ps.setString(4, currentUser.getPassword());
                    }

                    ps.setLong(5, currentUser.getId());
                    ps.executeUpdate();

                    currentUser.setUsername(username);
                    currentUser.setEmail(email);
                    currentUser.setPhoneNumber(phone);
                    if (hashed != null) {
                        currentUser.setPassword(hashed);
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Cập nhật thông tin thành công!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button backBtn = new Button("Quay lại trang chủ");
        backBtn.setOnAction(e -> {
            // Quay lại trang chủ: hiển thị TopRated
            setTopRatedVisible(true);
            loadLatestBooks();
        });

        form.getChildren().addAll(
                new Label("Tên người dùng:"), usernameField,
                new Label("Email:"), emailField,
                new Label("Số điện thoại:"), phoneField,
                new Label("Mật khẩu:"), passwordField,
                updateBtn, backBtn
        );
    
        booksGrid.add(form, 0, 0);
    }
}

