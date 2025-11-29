// AllBooksController.java (Optimized: Added pagination with pageSize=10, filters for authors/categories similar to SelectAuthors/SelectCategories with removable chips. Search uses searchBooks method. Data loaded dynamically. Average rating and available books calculated. Integrated with user_dashboard if needed via navigation.)
package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

public class AllBooksController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol, totalBookCol, borrowBookCol;
    @FXML private TableColumn<Book, String> titleCol, descriptionCol, authorsCol, categoriesCol;
    @FXML private TableColumn<Book, Double> averageRatingCol;
    @FXML private TableColumn<Book, Integer> availableCol; // New column for available books
    @FXML private TextField searchField;
    @FXML private ListView<HBox> authorsFilterList, categoriesFilterList;
    @FXML private Button prevPageBtn, nextPageBtn;
    @FXML private Label pageLabel;

    private final BookDAO bookDAO = new BookDAO();
    private ObservableList<Book> displayedBooks = FXCollections.observableArrayList();
    private ObservableList<Author> filterAuthors = FXCollections.observableArrayList();
    private ObservableList<Category> filterCategories = FXCollections.observableArrayList();

    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupFilterListViews();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadBooks(1)); // Reload on search change
        loadBooks(currentPage);
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        totalBookCol.setCellValueFactory(new PropertyValueFactory<>("totalBook"));
        borrowBookCol.setCellValueFactory(new PropertyValueFactory<>("borrowBook"));
        availableCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalBook() - cellData.getValue().getBorrowBook()).asObject());
        authorsCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAuthors().stream().map(Author::getName).collect(Collectors.joining(", "))
        ));
        categoriesCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategories().stream().map(Category::getName).collect(Collectors.joining(", "))
        ));
        averageRatingCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAverageRating()).asObject());
        bookTable.setItems(displayedBooks);
    }

    private void setupFilterListViews() {
        authorsFilterList.setItems(FXCollections.observableArrayList());
        categoriesFilterList.setItems(FXCollections.observableArrayList());
    }

    private void loadBooks(int page) {
        String keyword = searchField.getText().trim();
        List<Author> authors = new ArrayList<>(filterAuthors);
        List<Category> categories = new ArrayList<>(filterCategories);

        // Use existing searchBooks for filtering, but add pagination logic
        List<Book> allFilteredBooks = bookDAO.searchBooks(keyword, authors, categories);
        int totalCount = allFilteredBooks.size();
        totalPages = (int) Math.ceil((double) totalCount / pageSize);
        currentPage = Math.max(1, Math.min(page, totalPages));

        int offset = (currentPage - 1) * pageSize;
        List<Book> paginatedBooks = allFilteredBooks.subList(
                Math.min(offset, totalCount),
                Math.min(offset + pageSize, totalCount)
        );

        // Calculate average rating if not set (assuming you have a method or query for it)
        for (Book book : paginatedBooks) {
            if (book.getAverageRating() == 0) {
                book.setAverageRating(calculateAverageRating(book.getId())); // Implement if needed
            }
        }

        displayedBooks.setAll(paginatedBooks);
        updatePaginationControls();
    }

    private double calculateAverageRating(int bookId) {
        // Placeholder: Implement query to get AVG(rating) from review table

        return bookDAO.calculateAverageRating(bookId);
    }

    private void updatePaginationControls() {
        pageLabel.setText("Trang " + currentPage + " / " + totalPages);
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 1) {
            loadBooks(currentPage - 1);
        }
    }

    @FXML
    private void onNextPage() {
        if (currentPage < totalPages) {
            loadBooks(currentPage + 1);
        }
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
            if (newAuthors != null && !newAuthors.isEmpty()) {
                filterAuthors.addAll(newAuthors);
                updateAuthorsFilterListView();
                loadBooks(1);
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
            if (newCategories != null && !newCategories.isEmpty()) {
                filterCategories.addAll(newCategories);
                updateCategoriesFilterListView();
                loadBooks(1);
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
                loadBooks(currentPage);
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
                loadBooks(currentPage);
            });
            hbox.getChildren().addAll(label, removeBtn);
            items.add(hbox);
        }
        categoriesFilterList.setItems(items);
    }

    // Navigation back to dashboard if needed
    @FXML
    private void onBackToDashboard() {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/user_dashboard.fxml", bookTable.getScene());
    }
}