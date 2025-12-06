package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Category;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

import java.util.List;

public class UserCategoriesController {

    @FXML private GridPane categoriesGrid;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private final int COLUMNS = 4;  // Categories per row

    @FXML
    private void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        int row = 0, col = 0;
        for (Category category : categories) {
            VBox card = createCategoryCard(category);
            categoriesGrid.add(card, col, row);
            col++;
            if (col == COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCategoryCard(Category category) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, #00000022, 15, 0, 0, 8);");

        Label name = new Label(category.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        Text desc = new Text(category.getDescription());
        desc.setWrappingWidth(220);
        desc.setStyle("-fx-font-size: 14px; -fx-fill: #636e72;");

        card.getChildren().addAll(name, desc);

        // Hover effect
        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.05); scaleIn.setToY(1.05);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        card.setOnMouseClicked(e -> openCategoryDetail(category));

        return card;
    }

    private void openCategoryDetail(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/category_detail.fxml"));
            Parent root = loader.load();
            CategoryDetailController controller = loader.getController();
            controller.setCategory(category);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết Thể Loại - " + category.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(categoriesGrid.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi mở chi tiết thể loại: " + ex.getMessage());
            error.showAndWait();
        }
    }
}