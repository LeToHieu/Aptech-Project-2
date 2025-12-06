package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

import java.util.ArrayList;
import java.util.List;

public class UserHomeController {

    @FXML private TextField searchField;
    @FXML private Button btnViewAllBooks, btnSearch;  // Thêm fx:id cho buttons
    @FXML private HBox topRatedCarouselContainer, mostBorrowedCarouselContainer;
    @FXML private Label topRatedPageLabel, mostBorrowedPageLabel;

    private BookDAO bookDAO = new BookDAO();
    private List<Book> topRatedBooks = new ArrayList<>();
    private List<Book> mostBorrowedBooks = new ArrayList<>();
    private int topRatedCurrentPage = 1;
    private int mostBorrowedCurrentPage = 1;
    private int topRatedTotalPages = 1;
    private int mostBorrowedTotalPages = 1;
    private final int CAROUSEL_PAGE_SIZE = 5;

    @FXML
    private void initialize() {
        Platform.runLater(this::loadDashboardData);
    }

    private void loadDashboardData() {
        loadTopRatedBooks();
        loadMostBorrowedBooks();
    }

    private void loadTopRatedBooks() {
        try {
            topRatedBooks = bookDAO.getTopRatedBooks(20);
            topRatedTotalPages = (int) Math.ceil((double) topRatedBooks.size() / CAROUSEL_PAGE_SIZE);
            updateTopRatedCarouselView();
        } catch (Exception e) {
            showError("Lỗi load sách đánh giá cao: " + e.getMessage());
        }
    }

    private void updateTopRatedCarouselView() {
        topRatedCarouselContainer.getChildren().clear();
        int startIndex = (topRatedCurrentPage - 1) * CAROUSEL_PAGE_SIZE;
        int endIndex = Math.min(startIndex + CAROUSEL_PAGE_SIZE, topRatedBooks.size());

        for (int i = startIndex; i < endIndex; i++) {
            topRatedCarouselContainer.getChildren().add(createBookCard(topRatedBooks.get(i)));
        }

        topRatedPageLabel.setText(topRatedCurrentPage + " / " + topRatedTotalPages);
    }

    private void loadMostBorrowedBooks() {
        try {
            mostBorrowedBooks = bookDAO.getMostBorrowedBooks(20);
            mostBorrowedTotalPages = (int) Math.ceil((double) mostBorrowedBooks.size() / CAROUSEL_PAGE_SIZE);
            updateMostBorrowedCarouselView();
        } catch (Exception e) {
            showError("Lỗi load sách mượn nhiều: " + e.getMessage());
        }
    }

    private void updateMostBorrowedCarouselView() {
        mostBorrowedCarouselContainer.getChildren().clear();
        int startIndex = (mostBorrowedCurrentPage - 1) * CAROUSEL_PAGE_SIZE;
        int endIndex = Math.min(startIndex + CAROUSEL_PAGE_SIZE, mostBorrowedBooks.size());

        for (int i = startIndex; i < endIndex; i++) {
            mostBorrowedCarouselContainer.getChildren().add(createBookCard(mostBorrowedBooks.get(i)));
        }

        mostBorrowedPageLabel.setText(mostBorrowedCurrentPage + " / " + mostBorrowedTotalPages);
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, #00000022, 15, 0, 0, 8);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);
        ImageUtil.loadImageToView(book.getImage(), imageView);

        Rectangle clip = new Rectangle(150, 220);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        Text title = new Text(book.getTitle());
        title.setWrappingWidth(170);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        HBox ratingBox = createRatingBox(book.getAverageRating());

        card.getChildren().addAll(imageView, title, ratingBox);

        // Hover effect
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

    private HBox createRatingBox(double rating) {
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER);

        int fullStars = (int) Math.round(rating);
        for (int i = 0; i < fullStars; i++) {
            Text star = new Text("★");
            star.setStyle("-fx-fill: #f39c12; -fx-font-size: 15;");
            ratingBox.getChildren().add(star);
        }

        int emptyStars = 5 - fullStars;
        for (int i = 0; i < emptyStars; i++) {
            Text emptyStar = new Text("☆");
            emptyStar.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 15;");
            ratingBox.getChildren().add(emptyStar);
        }

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

    private void openBookDetail(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/book_detail.fxml"));
            Parent root = loader.load();
            BookDetailController controller = loader.getController();
            controller.setBook(book);  // <--

            Stage stage = new Stage();
            stage.setTitle("Chi tiết sách - " + book.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.initOwner(searchField.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception ex) {
            showError("Lỗi mở chi tiết sách: " + ex.getMessage());
        }
    }



    @FXML
    private void prevTopRated(ActionEvent event) {
        if (topRatedCurrentPage > 1) {
            topRatedCurrentPage--;
        } else {
            topRatedCurrentPage = topRatedTotalPages;
        }
        updateTopRatedCarouselView();
    }

    @FXML
    private void nextTopRated(ActionEvent event) {
        if (topRatedCurrentPage < topRatedTotalPages) {
            topRatedCurrentPage++;
        } else {
            topRatedCurrentPage = 1;
        }
        updateTopRatedCarouselView();
    }

    @FXML
    private void prevMostBorrowed(ActionEvent event) {
        if (mostBorrowedCurrentPage > 1) {
            mostBorrowedCurrentPage--;
        } else {
            mostBorrowedCurrentPage = mostBorrowedTotalPages;
        }
        updateMostBorrowedCarouselView();
    }

    @FXML
    private void nextMostBorrowed(ActionEvent event) {
        if (mostBorrowedCurrentPage < mostBorrowedTotalPages) {
            mostBorrowedCurrentPage++;
        } else {
            mostBorrowedCurrentPage = 1;
        }
        updateMostBorrowedCarouselView();
    }

    @FXML
    private void onSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadDashboardData();
        } else {
            // Thay bằng logic tìm kiếm thật (load page tìm kiếm)
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tìm kiếm cho: " + keyword + ". (Implement search here)");
            alert.show();
        }
    }

    @FXML
    private void onViewAllBooks(ActionEvent event) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/user_all_books.fxml", btnViewAllBooks.getScene());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}