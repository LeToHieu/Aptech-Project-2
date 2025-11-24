package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.Model.Author;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class SelectAuthorsController {

    @FXML private TableView<Author> authorsTable;
    private List<Author> selectedAuthors; // To return selected

    private final AuthorDAO authorDAO = new AuthorDAO();

    @FXML
    private void initialize() {
        TableColumn<Author, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Author, String> nameCol = new TableColumn<>("Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorsTable.getColumns().setAll(idCol, nameCol);

        authorsTable.setItems(FXCollections.observableArrayList(authorDAO.getAllAuthors()));
        authorsTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    @FXML
    private void onSelect() {
        selectedAuthors = authorsTable.getSelectionModel().getSelectedItems();
        onCancel(); // Close dialog
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) authorsTable.getScene().getWindow();
        stage.close();
    }

    public List<Author> getSelectedAuthors() {
        return selectedAuthors;
    }
}