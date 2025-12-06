package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.effect.DropShadow;
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
import javafx.animation.ScaleTransition;

import java.util.ArrayList;
import java.util.List;

public class UserAuthorsController {

    @FXML private GridPane authorsGrid;
    @FXML private Pagination pagination;

    private AuthorDAO authorDAO = new AuthorDAO();
    private List<Author> allAuthors = new ArrayList<>();
    private final int PAGE_SIZE = 8;  // Authors per page
    private final int COLUMNS = 4;  // Cards per row

    @FXML
    private void initialize() {
        allAuthors = authorDAO.getAllAuthors();
        pagination.setPageCount(getPageCount());
        pagination.currentPageIndexProperty().addListener((obs, old, newIndex) -> loadPage(newIndex.intValue()));
        loadPage(0);
    }

    private int getPageCount() {
        return (int) Math.ceil((double) allAuthors.size() / PAGE_SIZE);
    }

    private void loadPage(int pageIndex) {
        authorsGrid.getChildren().clear();
        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allAuthors.size());

        int row = 0, col = 0;
        for (int i = start; i < end; i++) {
            VBox card = createAuthorCard(allAuthors.get(i));
            authorsGrid.add(card, col, row);
            col++;
            if (col == COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createAuthorCard(Author author) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, #00000022, 15, 0, 0, 8);");

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        ImageUtil.loadImageToView(author.getImage(), imageView);
        Rectangle clip = new Rectangle(80, 80);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        VBox info = new VBox(5);
        Label name = new Label(author.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        Text desc = new Text(author.getDescription());
        desc.setWrappingWidth(150);
        desc.setStyle("-fx-font-size: 14px; -fx-fill: #636e72;");

        info.getChildren().addAll(name, desc);
        content.getChildren().addAll(imageView, info);

        card.getChildren().add(content);

        // Hover effect
        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05); scaleIn.setToY(1.05);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        card.setOnMouseClicked(e -> openAuthorDetail(author));

        return card;
    }

    private void openAuthorDetail(Author author) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/author_detail.fxml"));
            Parent root = loader.load();
            AuthorDetailController controller = loader.getController();
            controller.setAuthor(author);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết Tác Giả - " + author.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(authorsGrid.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi mở chi tiết tác giả: " + ex.getMessage());
            error.showAndWait();
        }
    }
}