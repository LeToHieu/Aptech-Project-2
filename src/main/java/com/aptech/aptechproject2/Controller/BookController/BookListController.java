package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookListController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol;
    @FXML private TableColumn<Book, String> titleCol, descriptionCol, imageCol, urlCol, createTimeCol, updateTimeCol, authorsCol, categoriesCol;
    @FXML private TextField searchField;
    @FXML private Button addBtn, editBtn, deleteBtn;
    @FXML private ListView<HBox> authorsFilterList, categoriesFilterList;

    private final BookDAO bookDAO = new BookDAO();
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();
    private ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
    private ObservableList<Author> filterAuthors = FXCollections.observableArrayList();
    private ObservableList<Category> filterCategories = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        createTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreateTime() != null ? cellData.getValue().getCreateTime().toString() : ""));
        updateTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdateTime() != null ? cellData.getValue().getUpdateTime().toString() : ""));
        authorsCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAuthors().stream().map(Author::getName).collect(Collectors.joining(", "))
        ));
        categoriesCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategories().stream().map(Category::getName).collect(Collectors.joining(", "))
        ));

        loadBooks();
        searchField.textProperty().addListener((obs, old, newVal) -> filterBooks(newVal));
        setupFilterListViews();
    }

    private void setupFilterListViews() {
        authorsFilterList.setItems(FXCollections.observableArrayList());
        categoriesFilterList.setItems(FXCollections.observableArrayList());
    }

    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        allBooks.setAll(books);
        filteredBooks.setAll(allBooks);
        bookTable.setItems(filteredBooks);
    }

    private void filterBooks(String keyword) {
        List<Author> authors = new ArrayList<>(filterAuthors);
        List<Category> categories = new ArrayList<>(filterCategories);
        List<Book> results = bookDAO.searchBooks(keyword, authors, categories);
        filteredBooks.setAll(results);
    }

    @FXML
    private void onSelectAuthorsFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_authors.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SelectAuthorsController controller = loader.getController();
            List<Author> newAuthors = controller.getSelectedAuthors();
            if (newAuthors != null) {
                filterAuthors.addAll(newAuthors);
                updateAuthorsFilterListView();
                filterBooks(searchField.getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSelectCategoriesFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_categories.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SelectCategoriesController controller = loader.getController();
            List<Category> newCategories = controller.getSelectedCategories();
            if (newCategories != null) {
                filterCategories.addAll(newCategories);
                updateCategoriesFilterListView();
                filterBooks(searchField.getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAuthorsFilterListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Author author : filterAuthors) {
            HBox hbox = new HBox(10);
            Label label = new Label(author.getName());
            Button removeBtn = new Button("x");
            removeBtn.setOnAction(e -> {
                filterAuthors.remove(author);
                updateAuthorsFilterListView();
                filterBooks(searchField.getText());
            });
            hbox.getChildren().addAll(label, removeBtn);
            items.add(hbox);
        }
        authorsFilterList.setItems(items);
    }

    private void updateCategoriesFilterListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Category category : filterCategories) {
            HBox hbox = new HBox(10);
            Label label = new Label(category.getName());
            Button removeBtn = new Button("x");
            removeBtn.setOnAction(e -> {
                filterCategories.remove(category);
                updateCategoriesFilterListView();
                filterBooks(searchField.getText());
            });
            hbox.getChildren().addAll(label, removeBtn);
            items.add(hbox);
        }
        categoriesFilterList.setItems(items);
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/book_add.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Thêm Sách");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadBooks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một sách để sửa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa sách này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/book_edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditBookController controller = loader.getController();
            controller.setBook(selected);
            Stage stage = new Stage();
            stage.setTitle("Sửa Sách");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadBooks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một sách để xóa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setContentText("Bạn có chắc chắn muốn xóa sách này?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (bookDAO.delete(selected.getId())) {
                loadBooks();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Lỗi");
                error.setContentText("Xóa sách thất bại!");
                error.showAndWait();
            }
        }
    }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", bookTable.getScene());
    }
}