package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.Review;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ReviewListController {

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, Long> idCol;
    @FXML private TableColumn<Review, String> userCol, bookCol, ratingCol, commentCol, timeCol;
    @FXML private TextField searchField;
    @FXML private Button addBtn, editBtn, deleteBtn;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        ratingCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRatingStars()));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        timeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCreatedAt().toString()));

        loadReviews();
        searchField.textProperty().addListener((obs, old, newVal) -> filterReviews(newVal));
    }

    private void loadReviews() {
        reviewTable.setItems(FXCollections.observableArrayList(reviewDAO.getAllReviews()));
    }

    private void filterReviews(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            loadReviews();
            return;
        }
        var filtered = reviewDAO.getAllReviews().stream()
                .filter(r -> r.getUserName().toLowerCase().contains(keyword.toLowerCase()) ||
                        r.getBookTitle().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
        reviewTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML private void onAdd() { openDialog("/com/aptech/aptechproject2/fxml/review_add.fxml", "Thêm Đánh Giá"); }
    @FXML
    private void onEdit() {
        Review selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Vui lòng chọn một đánh giá để sửa!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/review_edit.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Sửa Đánh Giá");
            stage.initModality(Modality.APPLICATION_MODAL);

            EditReviewController controller = loader.getController();
            controller.setReview(selected);

            stage.showAndWait();
            loadReviews();
        } catch (IOException e) {
            e.printStackTrace();
            alert("Lỗi mở form sửa!");
        }
    }

    @FXML private void onDelete() {
        Review selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Chọn đánh giá!"); return; }
        if (confirm("Xóa đánh giá này?")) {
            if (reviewDAO.delete(selected.getId())) {
                success("Xóa thành công!");
                loadReviews();
            } else {
                alert("Xóa thất bại!");
            }
        }
    }

    @FXML private void onLogout() {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", reviewTable.getScene());
    }

    private void openDialog(String fxml, String title) { openDialog(fxml, title, null); }
    private void openDialog(String fxml, String title, Review review) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);

            if (review != null) {
                if (fxml.contains("edit")) {
                    EditReviewController ctrl = loader.getController();
                    ctrl.setReview(review);
                }
            }

            stage.showAndWait();
            loadReviews();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg).show(); }
    private void success(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().filter(ButtonType.OK::equals).isPresent();
    }
}