package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryListController {

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idCol;
    @FXML private TableColumn<Category, String> nameCol, descriptionCol;
    @FXML private TextField searchField;
    @FXML private Button addBtn, editBtn, deleteBtn;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Category> allCategories = FXCollections.observableArrayList();
    private ObservableList<Category> filteredCategories = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadCategories();
        searchField.textProperty().addListener((obs, old, newVal) -> filterCategories(newVal));
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        allCategories.setAll(categories);
        filteredCategories.setAll(allCategories);
        categoryTable.setItems(filteredCategories);
    }

    private void filterCategories(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            filteredCategories.setAll(allCategories);
        } else {
            filteredCategories.setAll(allCategories.stream()
                    .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            (c.getDescription() != null && c.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                    .collect(Collectors.toList()));
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/category_add.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Thêm Thể Loại");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một thể loại để sửa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa thể loại này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/category_edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditCategoryController controller = loader.getController();
            controller.setCategory(selected);
            Stage stage = new Stage();
            stage.setTitle("Sửa Thể Loại");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một thể loại để xóa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setContentText("Bạn có chắc chắn muốn xóa thể loại này?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (categoryDAO.delete(selected.getId())) {
                loadCategories();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Lỗi");
                error.setContentText("Xóa thể loại thất bại!");
                error.showAndWait();
            }
        }
    }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", categoryTable.getScene());
    }
}