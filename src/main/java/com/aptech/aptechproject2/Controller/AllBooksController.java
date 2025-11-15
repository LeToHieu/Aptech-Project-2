package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.util.List;

public class AllBooksController {

    @FXML
    private GridPane allBooksGrid;

    private BookDAO bookDAO = new BookDAO();

    private final int COLS = 10; // 5 cột
    private int rows = 0; // Tính động theo số sách

    @FXML
    public void initialize() {
        loadAllBooks();
    }

    private void loadAllBooks() {
        allBooksGrid.getChildren().clear();
        List<Book> books = bookDAO.getAllBooks();
        rows = (int) Math.ceil((double) books.size() / COLS);
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLS; col++) {
                if (index >= books.size()) break;
                Book book = books.get(index++);
                VBox bookBox = createBookBox(book);
                allBooksGrid.add(bookBox, col, row);
            }
        }
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