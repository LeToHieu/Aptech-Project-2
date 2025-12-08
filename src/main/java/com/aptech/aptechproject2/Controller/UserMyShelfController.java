// UserMyShelfController.java - New controller for user_my_shelf.fxml
package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMyShelfController {

    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private GridPane booksGrid;
    @FXML private Pagination pagination;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private BookDAO bookDAO = new BookDAO();
    private ObservableList<Borrow> allBorrows = FXCollections.observableArrayList();
    private List<Borrow> filteredBorrows = new ArrayList<>();
    private final int PAGE_SIZE = 20;
    private final int COLUMNS = 5;

    @FXML
    private void initialize() {
        loadBorrows();
        setupPagination();
        btnSearch.setOnAction(this::onSearch);
    }

    private void loadBorrows() {
        long userId = Session.getCurrentUser().getId();
        allBorrows.setAll(borrowDAO.getBorrowsByUser(userId).stream()
                .filter(b -> b.getStatus() == 1)  // Only status 1: đang mượn
                .collect(Collectors.toList()));
        filteredBorrows = new ArrayList<>(allBorrows);
        updateGrid(0);
    }

    private void onSearch(ActionEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            filteredBorrows = new ArrayList<>(allBorrows);
        } else {
            filteredBorrows = allBorrows.stream()
                    .filter(b -> b.getBookTitle().toLowerCase().contains(keyword) ||
                            (b.getBookDescription() != null && b.getBookDescription().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }
        setupPagination();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredBorrows.size() / PAGE_SIZE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, old, newIdx) -> updateGrid(newIdx.intValue()));
        updateGrid(0);
    }

    private void updateGrid(int pageIndex) {
        booksGrid.getChildren().clear();
        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredBorrows.size());

        for (int i = start; i < end; i++) {
            Borrow borrow = filteredBorrows.get(i);
            Book book = bookDAO.getBookById((int) borrow.getBookId());  // Load full book if needed
            VBox card = createBookCard(book, borrow);
            int row = (i - start) / COLUMNS;
            int col = (i - start) % COLUMNS;
            booksGrid.add(card, col, row);
        }
    }

    private VBox createBookCard(Book book, Borrow borrow) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);
        card.setPrefHeight(350);

        ImageView imageView = ImageUtil.createRoundedImageView(book.getImage(), 160, 240, 10);

        Text title = new Text(book.getTitle());
        title.setWrappingWidth(170);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        Text borrowDate = new Text("Mượn: " + borrow.getBorrowDayFormatted());
        borrowDate.setStyle("-fx-font-size: 12px; -fx-fill: #636e72;");

        Text expireDate = new Text("Hạn: " + borrow.getExpireDayFormatted());
        expireDate.setStyle("-fx-font-size: 12px; -fx-fill: #636e72;");

        HBox ratingBox = createRatingBox(book.getAverageRating());

        card.getChildren().addAll(imageView, title, borrowDate, expireDate, ratingBox);

        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.08); scaleIn.setToY(1.08);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        card.setOnMouseClicked(e -> openBorrowedBookDetail(borrow));

        return card;
    }

    private HBox createRatingBox(double rating) {
        // Similar to UserAllBooksController
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

    private void openBorrowedBookDetail(Borrow borrow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/borrowed_book_detail.fxml"));
            Parent root = loader.load();
            BorrowedBookDetailController controller = loader.getController();
            controller.setBorrow(borrow);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết sách đang mượn - " + borrow.getBookTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(booksGrid.getScene().getWindow());
            stage.showAndWait();

            // Refresh after close (in case returned)
            loadBorrows();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi mở chi tiết: " + ex.getMessage());
            error.showAndWait();
        }
    }
}