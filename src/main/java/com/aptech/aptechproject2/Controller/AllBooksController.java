package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.List;

public class AllBooksController {

    @FXML private GridPane allBooksGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCombo;
    @FXML private ComboBox<Object> subFilterCombo; // Sửa từ <?> thành <Object>
    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageLabel;

    private BookDAO bookDAO = new BookDAO();
    private AuthorDAO authorDAO = new AuthorDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    private final int PAGE_SIZE = 15;
    private final int COLS = 5;
    private final int ROWS = 3;
    private int currentPage = 1;
    private int totalPages = 1;
    private String currentFilter = null;
    private Object subFilterValue = null; // Author ID or Category ID

    @FXML
    public void initialize() {
        filterCombo.setItems(FXCollections.observableArrayList(
                "Không lọc", "Lọc theo thể loại", "Lọc theo tác giả", "Lọc theo số lượt mượn", "Lọc theo đánh giá"
        ));
        filterCombo.setValue("Không lọc");
        filterCombo.valueProperty().addListener((obs, old, newVal) -> onFilterChanged(newVal));

        subFilterCombo.setVisible(false);

        loadBooks();
        updatePaginationControls();

        // Set stage size vừa đủ (ước tính dựa trên grid: 150px/book * 5 cols + gaps, 200px/row * 3 rows + header)
        Platform.runLater(() -> {
            Stage stage = (Stage) allBooksGrid.getScene().getWindow();
            stage.setWidth(800);
            stage.setHeight(700);
            stage.centerOnScreen();
        });
    }

    private void loadBooks() {
        allBooksGrid.getChildren().clear();
        List<Book> books;
        int offset = (currentPage - 1) * PAGE_SIZE;

        if ("Lọc theo thể loại".equals(currentFilter) && subFilterValue instanceof Integer catId) {
            books = bookDAO.getBooksByCategory(catId, PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getBooksCountByCategory(catId) / (double) PAGE_SIZE);
        } else if ("Lọc theo tác giả".equals(currentFilter) && subFilterValue instanceof Integer authId) {
            books = bookDAO.getBooksByAuthor(authId, PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getBooksCountByAuthor(authId) / (double) PAGE_SIZE);
        } else if ("Lọc theo số lượt mượn".equals(currentFilter)) {
            books = bookDAO.getBooksByBorrowCount(PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getTotalBooksCount() / (double) PAGE_SIZE);
        } else if ("Lọc theo đánh giá".equals(currentFilter)) {
            books = bookDAO.getBooksByAverageRating(PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getTotalBooksCount() / (double) PAGE_SIZE);
        } else if (searchField.getText().trim().isEmpty()) {
            books = bookDAO.getBooksPaginated(PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getTotalBooksCount() / (double) PAGE_SIZE);
        } else {
            books = bookDAO.searchBooksByTitlePaginated(searchField.getText().trim(), PAGE_SIZE, offset);
            totalPages = (int) Math.ceil(bookDAO.getSearchBooksCount(searchField.getText().trim()) / (double) PAGE_SIZE);
        }

        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (index >= books.size()) break;
                Book book = books.get(index++);
                VBox bookBox = createBookBox(book);
                allBooksGrid.add(bookBox, col, row);
            }
        }
        updatePaginationControls();
    }

    private void onFilterChanged(String filter) {
        currentFilter = filter;
        subFilterCombo.setVisible(false);
        subFilterValue = null;

        if ("Lọc theo thể loại".equals(filter)) {
            List<Category> categories = categoryDAO.getAllCategories();
            subFilterCombo.setItems(FXCollections.observableArrayList(categories));
            subFilterCombo.setCellFactory(lv -> new ListCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else if (item instanceof Category cat) {
                        setText(cat.getName());
                    }
                }
            });
            subFilterCombo.setVisible(true);
            subFilterCombo.valueProperty().addListener((obs, old, newVal) -> {
                if (newVal instanceof Category cat) {
                    subFilterValue = cat.getId();
                    currentPage = 1;
                    loadBooks();
                }
            });
        } else if ("Lọc theo tác giả".equals(filter)) {
            List<Author> authors = authorDAO.getAllAuthors();
            subFilterCombo.setItems(FXCollections.observableArrayList(authors));
            subFilterCombo.setCellFactory(lv -> new ListCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else if (item instanceof Author auth) {
                        setText(auth.getName());
                    }
                }
            });
            subFilterCombo.setVisible(true);
            subFilterCombo.valueProperty().addListener((obs, old, newVal) -> {
                if (newVal instanceof Author auth) {
                    subFilterValue = auth.getId();
                    currentPage = 1;
                    loadBooks();
                }
            });
        } else {
            currentPage = 1;
            loadBooks();
        }
    }

    @FXML
    private void onSearch() {
        currentPage = 1;
        loadBooks();
    }

    @FXML
    private void onPrev() {
        if (currentPage > 1) {
            currentPage--;
            loadBooks();
        }
    }

    @FXML
    private void onNext() {
        if (currentPage < totalPages) {
            currentPage++;
            loadBooks();
        }
    }

    private void updatePaginationControls() {
        pageLabel.setText("Trang " + currentPage + "/" + totalPages);
        prevBtn.setDisable(currentPage == 1);
        nextBtn.setDisable(currentPage == totalPages);
    }

    private VBox createBookBox(Book book) {
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        box.setSpacing(5);

        try {
            String path = "src/main/resources/com/aptech/aptechproject2/" + book.getImage();
            ImageView imageView = new ImageView(new Image(new FileInputStream(path)));
            imageView.setFitWidth(120);
            imageView.setFitHeight(160);
            box.getChildren().add(imageView);

            // make the box clickable to show book detail
            box.setOnMouseClicked(evt -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/book_detail.fxml"));
                    Scene scene = new Scene(loader.load());
                    BookDetailController controller = loader.getController();
                    controller.setBook(book);
                    Stage stage = new Stage();
                    stage.setTitle("Chi tiết sách");
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // hover effect: scale + drop shadow
            javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
            ds.setRadius(8);
            ds.setColor(javafx.scene.paint.Color.gray(0.3));

            javafx.animation.ScaleTransition stEnter = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), box);
            stEnter.setToX(1.05);
            stEnter.setToY(1.05);
            javafx.animation.ScaleTransition stExit = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), box);
            stExit.setToX(1.0);
            stExit.setToY(1.0);

            box.setOnMouseEntered(evt -> {
                stExit.stop();
                stEnter.playFromStart();
                box.setEffect(ds);
                box.setCursor(Cursor.HAND);
                box.toFront();
            });
            box.setOnMouseExited(evt -> {
                stEnter.stop();
                stExit.playFromStart();
                box.setEffect(null);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Text titleText = new Text(book.getTitle());
        titleText.setWrappingWidth(120);
        box.getChildren().add(titleText);

        return box;
    }

    @FXML
    void onBack(ActionEvent event) {
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/user_dashboard.fxml", allBooksGrid.getScene());
    }
}