package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookListController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleCol, authorCol, genreCol;
    @FXML private TableColumn<Book, Integer> yearCol;

    private final BookDAO bookDAO = new BookDAO();

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("publishedYear"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genres"));

        loadBooks();
    }

    @FXML
    private void loadBooks() {
        bookTable.setItems(FXCollections.observableArrayList(bookDAO.getAll()));
    }

    @FXML
    private void onLogout() {
        SceneManager.setCurrentUser(null);
        SceneManager.loadScene("/fxml/login.fxml", bookTable.getScene());
    }
}