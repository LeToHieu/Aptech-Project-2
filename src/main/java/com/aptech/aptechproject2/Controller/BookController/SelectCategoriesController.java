package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class SelectCategoriesController {

    @FXML private TableView<Category> categoriesTable;
    private List<Category> selectedCategories; // To return selected

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    private void initialize() {
        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Category, String> nameCol = new TableColumn<>("Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoriesTable.getColumns().setAll(idCol, nameCol);

        categoriesTable.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
        categoriesTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    @FXML
    private void onSelect() {
        selectedCategories = categoriesTable.getSelectionModel().getSelectedItems();
        onCancel(); // Close dialog
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) categoriesTable.getScene().getWindow();
        stage.close();
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }
}