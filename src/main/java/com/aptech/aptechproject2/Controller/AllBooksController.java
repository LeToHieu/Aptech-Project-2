package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Ulti.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

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
            DropShadow ds = new DropShadow();
            ds.setRadius(8);
            ds.setColor(Color.gray(0.3));

            ScaleTransition stEnter = new ScaleTransition(Duration.millis(150), box);
            stEnter.setToX(1.05);
            stEnter.setToY(1.05);
            ScaleTransition stExit = new ScaleTransition(Duration.millis(150), box);
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