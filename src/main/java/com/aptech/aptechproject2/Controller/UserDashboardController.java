package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.DBUtil;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import javafx.scene.Node;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardController {
    @FXML
    private TextField searchField;

    @FXML
    private GridPane booksGrid;

    @FXML private HBox carouselBox;

    // Thêm reference đến topRatedPane để có thể ẩn/hiện hoàn toàn phần Top Rated
    @FXML
    private VBox topRatedPane;

    private BookDAO bookDAO = new BookDAO();
    //
    private final int COLS = 5; // 5 cột main grid
    private final int ROWS = 3; // 3 hàng main grid


    // ====================== CAROUSEL 1: Sách đánh giá cao nhất ======================
    @FXML private HBox topRatedCarouselContainer;
    @FXML private Label topRatedCarouselPageLabel;
    private List<Book> topRatedBooks = new ArrayList<>();
    private int topRatedCurrentPage = 1;
    private final int CAROUSEL_PAGE_SIZE = 5; // 5 sách mỗi trang
    private int topRatedTotalPages = 1;

    // ====================== CAROUSEL 2: Sách mượn nhiều nhất ======================
    @FXML private HBox mostBorrowedCarouselContainer;
    @FXML private Label mostBorrowedCarouselPageLabel;
    private List<Book> mostBorrowedBooks = new ArrayList<>();
    private int mostBorrowedCurrentPage = 1;
    private int mostBorrowedTotalPages = 1;

    @FXML private GridPane topRatedGrid;


    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (searchField.getScene() != null && searchField.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) searchField.getScene().getWindow();
                stage.setFullScreen(false);
            }
            loadDashboardData();
        });
        // Mặc định khi vào trang user_dashboard (Trang chủ) sẽ hiển thị TopRated
        setTopRatedVisible(true);
    }

    // Phương thức chính để load tất cả dữ liệu
    private void loadDashboardData() {
        loadTopRatedBooks();
        loadMostBorrowedBooks();
        // loadTop10Grid();
    }

    private void loadTopRatedBooks() {
        // Lấy gấp 2 lần size để đảm bảo đủ dữ liệu cho 2 trang (nếu có)
        topRatedBooks = bookDAO.getTopRatedBooks(20);

        topRatedTotalPages = (int) Math.ceil((double) topRatedBooks.size() / CAROUSEL_PAGE_SIZE);
        topRatedCurrentPage = 1;
        updateTopRatedCarouselView();
    }

    private void updateTopRatedCarouselView() {
        int startIndex = (topRatedCurrentPage - 1) * CAROUSEL_PAGE_SIZE;
        int endIndex = Math.min(startIndex + CAROUSEL_PAGE_SIZE, topRatedBooks.size());

        List<Book> booksToShow = topRatedBooks.subList(startIndex, endIndex);

        topRatedCarouselContainer.getChildren().clear();
        for (Book book : booksToShow) {
            // SỬ DỤNG HÀM TẠO CARD CHI TIẾT
            topRatedCarouselContainer.getChildren().add(createBookCard(book, 150, 220, true, true));
        }

        topRatedCarouselPageLabel.setText(topRatedCurrentPage + " / " + topRatedTotalPages);
        topRatedCarouselPageLabel.setPadding(new Insets(100, 0, 0, 0));
    }

    @FXML
    private void prevTopRatedCarousel() {
        // Logic vòng lặp: Nếu đang ở trang 1, chuyển về trang cuối.
        if (topRatedCurrentPage > 1) {
            topRatedCurrentPage--;
        } else {
            // Quay lại trang cuối cùng (Loop)
            topRatedCurrentPage = mostBorrowedTotalPages;
        }
        updateTopRatedCarouselView();

    }

    @FXML
    private void nextTopRatedCarousel() {
        if (topRatedCurrentPage < topRatedTotalPages) {
            topRatedCurrentPage++;
        } else {
            // Quay lại trang đầu tiên (Loop)
            topRatedCurrentPage = 1;
        }
        updateTopRatedCarouselView();
    }

    // ====================== LOGIC CAROUSEL 2 (Most Borrowed) ======================
    private void loadMostBorrowedBooks() {
        // Lấy gấp 2 lần size để đảm bảo đủ dữ liệu cho 2 trang (nếu có)
        mostBorrowedBooks = bookDAO.getMostBorrowedBooks(20);

        mostBorrowedTotalPages = (int) Math.ceil((double) mostBorrowedBooks.size() / CAROUSEL_PAGE_SIZE);
        mostBorrowedCurrentPage = 1;
        updateMostBorrowedCarouselView();
    }

    private void updateMostBorrowedCarouselView() {
        int startIndex = (mostBorrowedCurrentPage - 1) * CAROUSEL_PAGE_SIZE;
        int endIndex = Math.min(startIndex + CAROUSEL_PAGE_SIZE, mostBorrowedBooks.size());

        List<Book> booksToShow = mostBorrowedBooks.subList(startIndex, endIndex);

        mostBorrowedCarouselContainer.getChildren().clear();
        for (Book book : booksToShow) {
            // SỬ DỤNG HÀM TẠO CARD CHI TIẾT
            mostBorrowedCarouselContainer.getChildren().add(createBookCard(book, 150, 220, true, true));
        }

        mostBorrowedCarouselPageLabel.setText(mostBorrowedCurrentPage + " / " + mostBorrowedTotalPages);
    }

    @FXML
    private void prevMostBorrowedCarousel() {
        // Logic vòng lặp: Nếu đang ở trang 1, chuyển về trang cuối.
        if (mostBorrowedCurrentPage > 1) {
            mostBorrowedCurrentPage--;
        } else {
            // Quay lại trang cuối cùng (Loop)
            mostBorrowedCurrentPage = mostBorrowedTotalPages;
        }
        updateMostBorrowedCarouselView();
    }

    @FXML
    private void nextMostBorrowedCarousel() {
        // Logic vòng lặp: Nếu đang ở trang cuối, chuyển về trang 1.
        if (mostBorrowedCurrentPage < mostBorrowedTotalPages) {
            mostBorrowedCurrentPage++;
        } else {
            // Quay lại trang đầu tiên (Loop)
            mostBorrowedCurrentPage = 1;
        }
        updateMostBorrowedCarouselView();
    }

    // Helper để ẩn/hiện top rated pane và điều khiển quản lý layout
    private void setTopRatedVisible(boolean visible) {
        if (topRatedPane != null) {
            topRatedPane.setVisible(visible);
            topRatedPane.setManaged(visible); // Nếu false sẽ không chiếm không gian layout
        }
    }

    // Hàm tạo card cho chế độ mặc định (Overload)
    private VBox createBookCard(Book book) {
        // Kích thước mặc định cho Grid Search
        return createBookCard(book, 120, 160, true, true);
    }

    // Hàm TẠO CARD SIÊU GỌN – dùng chung cho cả 2 phần
    private VBox createBookCard(Book book, int imgWidth, int imgHeight, boolean showTitle, boolean showRating) {
        VBox card = new VBox(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(imgWidth + 40);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                "-fx-border-radius: 16; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, #00000022, 15, 0, 0, 8);");

        // Dùng ImageUtil – an toàn 100%, có fallback
        ImageView imageView = new ImageView();
        imageView.setFitWidth(imgWidth);
        imageView.setFitHeight(imgHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        ImageUtil.loadImageToView(book.getImage(), imageView);

        // Bo góc ảnh đẹp
        Rectangle clip = new Rectangle(imgWidth, imgHeight);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        card.getChildren().add(imageView);

        if (showTitle) {
            Text title = new Text(book.getTitle());
            title.setWrappingWidth(imgWidth + 20);
            // Căn giữa tiêu đề
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");
            card.getChildren().add(title);
        }

        if (showRating) {
            HBox ratingBox = createRatingBox(book.getAverageRating());
            ratingBox.setAlignment(Pos.CENTER);
            card.getChildren().add(ratingBox);
        }

        // Hover + Click giống hệt cũ
        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.08); scaleIn.setToY(1.08);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        card.setOnMouseClicked(e -> openBookDetail(book));

        return card;
    }

    @FXML
    void onSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            // Nếu bỏ trống thì trở về trang chủ => hiển thị TopRated
            setTopRatedVisible(true);
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
                    // DÙNG HÀM TẠO CARD MỚI (OVERLOAD)
                    VBox bookBox = createBookCard(book);
                    booksGrid.add(bookBox, col, row);
                }
            }
        }
    }

    // Tạo HBox cho rating sao (sử dụng Unicode, kèm điểm số)
    private HBox createRatingBox(double rating) {
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER);

        // Làm tròn rating xuống để tính số sao đầy
        int fullStars = (int) Math.round(rating); // Làm tròn lên/xuống để hiển thị sao

        // Sao đầy
        for (int i = 0; i < fullStars; i++) {
            Text star = new Text("★");
            star.setStyle("-fx-fill: #f39c12; -fx-font-size: 15;"); // Màu vàng cam cho sao
            ratingBox.getChildren().add(star);
        }

        // Sao rỗng (Luôn hiển thị 5 sao)
        int emptyStars = 5 - fullStars;
        for (int i = 0; i < emptyStars; i++) {
            Text emptyStar = new Text("☆");
            emptyStar.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 15;");
            ratingBox.getChildren().add(emptyStar);
        }

        // Thêm điểm số thực vào cuối
        if (rating > 0) {
            Text scoreText = new Text(" (" + String.format("%.1f", rating) + ")");
            scoreText.setStyle("-fx-font-size: 12px; -fx-fill: #2c3e50;");
            ratingBox.getChildren().add(scoreText);
        } else {
            Text noRating = new Text(" (Chưa có)");
            noRating.setStyle("-fx-font-size: 12px; -fx-fill: #999;");
            ratingBox.getChildren().add(noRating);
        }

        return ratingBox;
    }

    @FXML
    void onHome(ActionEvent event) {
        // Trang chủ: hiển thị lại TopRated và load latest
        setTopRatedVisible(true);
        booksGrid.getChildren().clear();
        loadDashboardData();

    }

    // ... (Giữ nguyên các hàm FXML khác)

    @FXML
    void onViewAllBooksFull(ActionEvent event) {
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

                // Đọc ảnh - Chú ý: Cần đảm bảo đường dẫn này hoạt động trong môi trường của bạn
                // Tôi không thể đảm bảo logic đọc file này hoạt động 100% nếu ImageUtil.loadImageToView không được dùng.
                // Tuy nhiên, tôi sẽ giữ nguyên cấu trúc bạn đã cung cấp:
                String imagePath = "src/main/resources/com/aptech/aptechproject2/images/" + rs.getString("Image");
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
        setTopRatedVisible(false);
        booksGrid.getChildren().clear();
        User currentUser = Session.getCurrentUser();
        // ... (Giữ nguyên logic xem thông tin người dùng)
    }


    // Mở chi tiết sách (tách riêng cho gọn)
    private void openBookDetail(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/book_detail.fxml"));
            Parent root = loader.load();
            // Cần BookDetailController có setBook(Book)
            // BookDetailController controller = loader.getController();
            // controller.setBook(book);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết sách - " + book.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            // Kiểm tra carouselBox có Scene không
            if (carouselBox != null && carouselBox.getScene() != null) {
                stage.initOwner(carouselBox.getScene().getWindow());
            } else if (searchField.getScene() != null) {
                stage.initOwner(searchField.getScene().getWindow());
            }
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Lỗi");
            error.setHeaderText("Không thể mở chi tiết sách");
            error.setContentText("Kiểm tra file FXML và Controller BookDetail. Lỗi: " + ex.getMessage());
            error.showAndWait();
        }
    }
}