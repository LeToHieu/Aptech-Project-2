package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.util.StringConverter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserAllBooksController {

    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private ComboBox<Author> authorCombo;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private HBox selectedAuthorsBox, selectedCategoriesBox;
    @FXML private GridPane booksGrid;
    @FXML private Pagination pagination;

    private BookDAO bookDAO = new BookDAO();
    private AuthorDAO authorDAO = new AuthorDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();
    private List<Book> filteredBooks = new ArrayList<>();
    private List<Author> selectedAuthors = new ArrayList<>();
    private List<Category> selectedCategories = new ArrayList<>();
    private final int PAGE_SIZE = 20;
    private final int COLUMNS = 5;

    @FXML
    private void initialize() {
        allBooks.setAll(bookDAO.getAllBooks());
        filteredBooks = new ArrayList<>(allBooks);

        // Setup combos with name converter
        authorCombo.setConverter(new StringConverter<Author>() {
            @Override
            public String toString(Author author) {
                return author != null ? author.getName() : "";
            }
            @Override
            public Author fromString(String string) {
                return null;
            }
        });
        authorCombo.setItems(FXCollections.observableArrayList(authorDAO.getAllAuthors()));
        authorCombo.setOnAction(e -> addAuthorFilter(authorCombo.getValue()));

        categoryCombo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }
            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        categoryCombo.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
        categoryCombo.setOnAction(e -> addCategoryFilter(categoryCombo.getValue()));

        pagination.setPageCount(getPageCount());
        pagination.currentPageIndexProperty().addListener((obs, old, newIndex) -> loadPage(newIndex.intValue()));
        loadPage(0);
    }

    private void addAuthorFilter(Author author) {
        if (author != null && !selectedAuthors.contains(author)) {
            selectedAuthors.add(author);
            addChipToBox(selectedAuthorsBox, author.getName(), () -> removeAuthorFilter(author));
            applyFilters();
        }
        authorCombo.setValue(null);
    }

    private void removeAuthorFilter(Author author) {
        selectedAuthors.remove(author);
        refreshSelectedBoxes();
        applyFilters();
    }

    private void addCategoryFilter(Category category) {
        if (category != null && !selectedCategories.contains(category)) {
            selectedCategories.add(category);
            addChipToBox(selectedCategoriesBox, category.getName(), () -> removeCategoryFilter(category));
            applyFilters();
        }
        categoryCombo.setValue(null);
    }

    private void removeCategoryFilter(Category category) {
        selectedCategories.remove(category);
        refreshSelectedBoxes();
        applyFilters();
    }

    private void addChipToBox(HBox box, String text, Runnable onClose) {
        HBox chip = new HBox(5);
        chip.setAlignment(Pos.CENTER);
        chip.setStyle("-fx-background-color: #326fc8; -fx-background-radius: 20; -fx-padding: 5 10;");
        Label label = new Label(text);
        Button close = new Button("x");
        close.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
        close.setOnAction(e -> onClose.run());
        chip.getChildren().addAll(label, close);
        box.getChildren().add(chip);
    }

    private void refreshSelectedBoxes() {
        selectedAuthorsBox.getChildren().clear();
        selectedCategoriesBox.getChildren().clear();
        for (Author a : selectedAuthors) {
            addChipToBox(selectedAuthorsBox, a.getName(), () -> removeAuthorFilter(a));
        }
        for (Category c : selectedCategories) {
            addChipToBox(selectedCategoriesBox, c.getName(), () -> removeCategoryFilter(c));
        }
    }

    private void applyFilters() {
        String keyword = searchField.getText().trim().toLowerCase();
        List<Integer> authorIds = selectedAuthors.stream().map(Author::getId).collect(Collectors.toList());
        List<Integer> categoryIds = selectedCategories.stream().map(Category::getId).collect(Collectors.toList());

        filteredBooks = allBooks.stream()
                .filter(book -> keyword.isEmpty() || book.getTitle().toLowerCase().contains(keyword))
                .filter(book -> authorIds.isEmpty() || book.getAuthors().stream().anyMatch(a -> authorIds.contains(a.getId())))
                .filter(book -> categoryIds.isEmpty() || book.getCategories().stream().anyMatch(c -> categoryIds.contains(c.getId())))
                .collect(Collectors.toList());

        pagination.setPageCount(getPageCount());
        loadPage(0);
    }

    @FXML
    private void onSearch(ActionEvent event) {
        applyFilters();
    }

    private int getPageCount() {
        return (int) Math.ceil((double) filteredBooks.size() / PAGE_SIZE);
    }

    private void loadPage(int pageIndex) {
        booksGrid.getChildren().clear();
        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredBooks.size());

        int row = 0, col = 0;
        for (int i = start; i < end; i++) {
            VBox card = createBookCard(filteredBooks.get(i));
            booksGrid.add(card, col, row);
            col++;
            if (col == COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, #00000022, 15, 0, 0, 8);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);
        ImageUtil.loadImageToView(book.getImage(), imageView);

        Rectangle clip = new Rectangle(150, 220);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        Text title = new Text(book.getTitle());
        title.setWrappingWidth(170);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center;");

        Text authors = new Text("Authors: " + book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")));
        authors.setWrappingWidth(170);
        authors.setStyle("-fx-font-size: 12px; -fx-fill: #636e72;");

        Text categories = new Text("Categories: " + book.getCategories().stream().map(Category::getName).collect(Collectors.joining(", ")));
        categories.setWrappingWidth(170);
        categories.setStyle("-fx-font-size: 12px; -fx-fill: #636e72;");

        HBox ratingBox = createRatingBox(book.getAverageRating());

        card.getChildren().addAll(imageView, title, authors, categories, ratingBox);

        DropShadow shadow = new DropShadow(20, Color.rgb(0,0,0,0.25));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.08); scaleIn.setToY(1.08);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1); scaleOut.setToY(1);

        card.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.play(); card.setEffect(shadow); card.setCursor(Cursor.HAND); });
        card.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.play(); card.setEffect(null); });
        card.setOnMouseClicked(e -> openBookDetail(book));

        return card;
    }

    private HBox createRatingBox(double rating) {
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER);

        int fullStars = (int) Math.round(rating);
        for (int i = 0; i < fullStars; i++) {
            Text star = new Text("★");
            star.setStyle("-fx-fill: #f39c12; -fx-font-size: 15;");
            ratingBox.getChildren().add(star);
        }

        int emptyStars = 5 - fullStars;
        for (int i = 0; i < emptyStars; i++) {
            Text emptyStar = new Text("☆");
            emptyStar.setStyle("-fx-fill: #bdc3c7; -fx-font-size: 15;");
            ratingBox.getChildren().add(emptyStar);
        }

        if (rating > 0) {
            Text scoreText = new Text(" (" + String.format("%.1f", rating) + ")");
            scoreText.setStyle("-fx-font-size: 12px; -fx-fill: #2c3e50;");
            ratingBox.getChildren().add(scoreText);
        } else {
            Text noRating = new Text(" (Chưa có)");
            noRating.setStyle("-fx-font-size: 12px; -fx-fill: #999;");
            ratingBox.getChildren().add(noRating);
        }

        return ratingBox;
    }

    private void openBookDetail(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/book_detail.fxml"));
            Parent root = loader.load();
            BookDetailController controller = loader.getController();
            controller.setBook(book);  // <-- truyền book vào đây

            Stage stage = new Stage();
            stage.setTitle("Chi tiết sách - " + book.getTitle());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(booksGrid.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi mở chi tiết sách: " + ex.getMessage());
            error.showAndWait();
        }
    }
}