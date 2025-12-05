package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.DAO.CategoryDAO;
import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class BookDetailController {

    @FXML private ImageView coverImage;
    @FXML private Label titleLabel;
    @FXML private Label idLabel;
    @FXML private Label authorLabel;
    @FXML private Label categoryLabel;
    @FXML private Label ratingLabel;
    @FXML private Hyperlink urlLink;
    @FXML private TextArea descriptionArea;
    @FXML private Button borrowBtn, addToShelvesBtn;

    private Book book;

    private final AuthorDAO authorDAO = new AuthorDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    public void setBook(Book book) {
        this.book = book;
        if (book == null) return;

        titleLabel.setText(book.getTitle());
        idLabel.setText(String.valueOf(book.getId()));
        descriptionArea.setText(book.getDescription() == null ? "" : book.getDescription());

        // load authors from DB
        List<Author> authors = authorDAO.getAuthorsByBookId(book.getId());
        if (authors != null && !authors.isEmpty()) {
            String joined = authors.stream().map(Author::getName).collect(Collectors.joining(", "));
            authorLabel.setText(joined);
            book.setAuthors(authors);
        } else {
            authorLabel.setText("-");
        }

        // load categories
        List<Category> categories = categoryDAO.getCategoriesByBookId(book.getId());
        if (categories != null && !categories.isEmpty()) {
            String joined = categories.stream().map(Category::getName).collect(Collectors.joining(", "));
            categoryLabel.setText(joined);
            book.setCategories(categories);
        } else {
            categoryLabel.setText("-");
        }

        // load average rating
        double avg = reviewDAO.getAverageRatingForBook(book.getId());
        book.setAverageRating(avg);
        ratingLabel.setText(String.format("%.2f", avg));

        // url
        if (book.getUrl() != null && !book.getUrl().isEmpty()) {
            urlLink.setText(book.getUrl());
            urlLink.setOnAction(e -> {
                try {
                    String url = book.getUrl();
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", url).start();
                    } else { // linux
                        new ProcessBuilder("xdg-open", url).start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            urlLink.setText("-");
        }

        try {
            String path = "src/main/resources/com/aptech/aptechproject2" + book.getImage();
            coverImage.setImage(new Image(new FileInputStream(path)));
        } catch (Exception e) {
            // ignore image load errors
        }
    }

    @FXML
    private void onBorrow() {
        User current = Session.getCurrentUser();
        if (current == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Chưa đăng nhập");
            a.setHeaderText(null);
            a.setContentText("Vui lòng đăng nhập để mượn sách.");
            a.showAndWait();
            return;
        }

        borrowBtn.setDisable(true);

        Task<Long> task = new Task<>() {
            @Override
            protected Long call() {
                // days default 14
                return borrowDAO.createBorrow((int)current.getId(), book.getId(), 14);
            }
        };

        task.setOnSucceeded(evt -> {
            long res = task.getValue();
            borrowBtn.setDisable(false);
            if (res > 0) {
                Alert s = new Alert(Alert.AlertType.INFORMATION);
                s.setTitle("Thành công");
                s.setHeaderText(null);
                s.setContentText("Mượn sách thành công!");
                s.showAndWait();
            } else if (res == -1) {
                Alert w = new Alert(Alert.AlertType.WARNING);
                w.setTitle("Đã mượn");
                w.setHeaderText(null);
                w.setContentText("Bạn đã đang mượn cuốn sách này.");
                w.showAndWait();
            } else if (res == -2) {
                Alert e = new Alert(Alert.AlertType.ERROR);
                e.setTitle("Hết sách");
                e.setHeaderText(null);
                e.setContentText("Không còn bản sách sẵn có để mượn.");
                e.showAndWait();
            } else {
                Alert e = new Alert(Alert.AlertType.ERROR);
                e.setTitle("Lỗi");
                e.setHeaderText(null);
                e.setContentText("Mượn sách thất bại (mã: " + res + "). Vui lòng thử lại.");
                e.showAndWait();
            }
        });

        task.setOnFailed(evt -> {
            borrowBtn.setDisable(false);
            Alert e = new Alert(Alert.AlertType.ERROR);
            e.setTitle("Lỗi");
            e.setHeaderText(null);
            e.setContentText("Có lỗi khi thực hiện yêu cầu. Vui lòng thử lại sau.");
            e.showAndWait();
        });

        new Thread(task).start();
    }

    @FXML
    private void onAddToShelves() {
        System.out.println("Add to shelves clicked for book id=" + (book == null ? "-" : book.getId()));
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
