package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.DBUtil;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
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
    private GridPane booksGrid;

    private BookDAO bookDAO = new BookDAO();

    private final int COLS = 4; // 4 cột
    private final int ROWS = 3; // 3 hàng

    @FXML
    public void initialize() {
        loadLatestBooks();
    }

    // Load 12 sách mới nhất
    private void loadLatestBooks() {
        booksGrid.getChildren().clear();
        List<Book> books = bookDAO.getLatestBooks(ROWS * COLS);
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (index >= books.size()) break;
                Book book = books.get(index++);
                VBox bookBox = createBookBox(book);
                booksGrid.add(bookBox, col, row);
            }
        }
    }

    // Tạo VBox cho 1 cuốn sách
    private VBox createBookBox(Book book) {
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        box.setSpacing(5);

        try {
            String path = "/com/aptech/aptechproject2/images/" + book.getImage();
            ImageView imageView = new ImageView(new Image(new FileInputStream(path)));
            imageView.setFitWidth(120);
            imageView.setFitHeight(160);
            box.getChildren().add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Text titleText = new Text(book.getTitle());
        titleText.setWrappingWidth(120);
        box.getChildren().add(titleText);

        return box;
    }

    @FXML
    void onSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadLatestBooks();
        } else {
            booksGrid.getChildren().clear();
            List<Book> books = bookDAO.searchBooksByTitle(keyword);
            int index = 0;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (index >= books.size()) break;
                    Book book = books.get(index++);
                    VBox bookBox = createBookBox(book);
                    booksGrid.add(bookBox, col, row);
                }
            }
        }
    }

    @FXML
    void onLogout(ActionEvent event) {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", searchField.getScene());
    }
    @FXML
    void onViewAllBooks(ActionEvent event) {
        booksGrid.getChildren().clear();
        List<Book> books = bookDAO.getAllBooks();
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (index >= books.size()) break;
                Book book = books.get(index++);
                VBox bookBox = createBookBox(book);
                booksGrid.add(bookBox, col, row);
            }
        }
    }

    @FXML
    void onViewCategories(ActionEvent event) {
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
        booksGrid.getChildren().clear();
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            // Xử lý nếu chưa login (phòng thủ)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Bạn chưa đăng nhập. Vui lòng đăng nhập lại!");
            alert.showAndWait();
            // TODO: Chuyển về màn hình login (sử dụng SceneManager nếu có)
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

                // Cập nhật vào DB
                String sql = "UPDATE user SET UserName=?, Email=?, PhoneNumber=?, Password=? WHERE Id=?";
                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, phone);

                    String hashed = null;
                    if (!password.isEmpty()) {
                        // Nếu đổi mật khẩu, hash trước
                        hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                        ps.setString(4, hashed);
                    } else {
                        ps.setString(4, currentUser.getPassword()); // Giữ password cũ (đã hashed)
                    }

                    ps.setLong(5, currentUser.getId());
                    ps.executeUpdate();

                    // Cập nhật session
                    currentUser.setUsername(username);
                    currentUser.setEmail(email);
                    currentUser.setPhoneNumber(phone);
                    if (hashed != null) {
                        currentUser.setPassword(hashed); // Sửa lỗi: dùng hashed thay vì ps.toString()
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
        backBtn.setOnAction(e -> loadLatestBooks());

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
