package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;

import java.util.List;

public class CategoryDetailController {

    @FXML private Label nameLabel, descLabel, pageLabel;
    @FXML private HBox booksCarousel;

    private BookDAO bookDAO = new BookDAO();
    private List<Book> books;
    private int currentPage = 1;
    private final int PAGE_SIZE = 5;  // Books per carousel page
    private int totalPages = 1;

    public void setCategory(Category category) {
        nameLabel.setText(category.getName());
        descLabel.setText(category.getDescription());

        books = bookDAO.getRandomBooksByCategory(category.getId(), 20);
        totalPages = (int) Math.ceil((double) books.size() / PAGE_SIZE);
        updateCarousel();
    }

    private void updateCarousel() {
        booksCarousel.getChildren().clear();
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, books.size());

        for (int i = start; i < end; i++) {
            booksCarousel.getChildren().add(createBookCard(books.get(i)));
        }

        pageLabel.setText(currentPage + " / " + totalPages);
    }

    @FXML
    private void prevBook(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
        } else {
            currentPage = totalPages;
        }
        updateCarousel();
    }

    @FXML
    private void nextBook(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
        } else {
            currentPage = 1;
        }
        updateCarousel();
    }

    // Copy createBookCard and createRatingBox from UserHomeController
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

        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.08); scaleIn.setToY(1.08);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        // Add click to open book detail if needed
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
            controller.setBook(book);  // <-- truyền book vào đây

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
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}