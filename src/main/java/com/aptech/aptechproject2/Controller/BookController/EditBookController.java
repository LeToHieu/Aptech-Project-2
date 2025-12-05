// EditBookController.java (Updated with TotalBook and BorrowBook handling)
package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditBookController {

    @FXML private TextField titleField, urlField, totalBookField;
    @FXML private TextArea descriptionField;
    @FXML private ImageView imagePreview;
    @FXML private ListView<HBox> selectedAuthorsList, selectedCategoriesList;
    @FXML private Label errorLabel;

    private Book bookToEdit;
    private final BookDAO bookDAO = new BookDAO();
    private String imagePath;
    private ObservableList<Author> selectedAuthors = FXCollections.observableArrayList();
    private ObservableList<Category> selectedCategories = FXCollections.observableArrayList();

    public void setBook(Book book) {
        this.bookToEdit = book;
        titleField.setText(book.getTitle());
        descriptionField.setText(book.getDescription());
        urlField.setText(book.getUrl());
        totalBookField.setText(String.valueOf(book.getTotalBook()));
        imagePath = book.getImage();

        ImageUtil.loadImageToView(imagePath, imagePreview);


        selectedAuthors.setAll(book.getAuthors());
        selectedCategories.setAll(book.getCategories());
        updateAuthorsListView();
        updateCategoriesListView();
    }

    @FXML
    private void initialize() {
        setupListViews();
    }

    private void setupListViews() {
        selectedAuthorsList.setItems(FXCollections.observableArrayList());
        selectedCategoriesList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void onChooseImage() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        String newImagePath = ImageUtil.selectAndSaveImage(stage, imagePreview);
        if (newImagePath != null) {
            imagePath = newImagePath;
        }
    }

    @FXML
    private void onAddAuthors() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_authors.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SelectAuthorsController controller = loader.getController();
            List<Author> newAuthors = controller.getSelectedAuthors();
            if (newAuthors != null) {
                selectedAuthors.addAll(newAuthors);
                updateAuthorsListView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_categories.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SelectCategoriesController controller = loader.getController();
            List<Category> newCategories = controller.getSelectedCategories();
            if (newCategories != null) {
                selectedCategories.addAll(newCategories);
                updateCategoriesListView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAuthorsListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Author author : selectedAuthors) {
            HBox hbox = new HBox(10);
            Label label = new Label(author.getName());
            Button removeBtn = new Button("x");
            removeBtn.setOnAction(e -> {
                selectedAuthors.remove(author);
                updateAuthorsListView();
            });
            hbox.getChildren().addAll(label, removeBtn);
            items.add(hbox);
        }
        selectedAuthorsList.setItems(items);
    }

    private void updateCategoriesListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Category category : selectedCategories) {
            HBox hbox = new HBox(10);
            Label label = new Label(category.getName());
            Button removeBtn = new Button("x");
            removeBtn.setOnAction(e -> {
                selectedCategories.remove(category);
                updateCategoriesListView();
            });
            hbox.getChildren().addAll(label, removeBtn);
            items.add(hbox);
        }
        selectedCategoriesList.setItems(items);
    }

    @FXML
    private void onUpdateBook() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String totalBookStr = totalBookField.getText().trim();
//        String borrowBookStr = borrowBookField.getText().trim();
//        String url = urlField.getText().trim();
        String url = urlField.getText() == null || urlField.getText().trim().isEmpty()
                ? null
                : urlField.getText().trim();

        if (title.isEmpty()) {
            errorLabel.setText("Vui lòng điền ít nhất tựa đề!");
            return;
        }
        int totalBook;
        int borrowBook;
        try {
            totalBook = Integer.parseInt(totalBookStr);
//            borrowBook = Integer.parseInt(borrowBookStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("Tổng sách và sách mượn phải là số nguyên!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa sách này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }
        String originalImagePath = bookToEdit.getImage();
        if (imagePath != null && !imagePath.equals(originalImagePath)) {
            ImageUtil.deleteImage(originalImagePath);
        }

        bookToEdit.setTitle(title);
        bookToEdit.setDescription(description);
        bookToEdit.setTotalBook(totalBook);
//        bookToEdit.setBorrowBook(borrowBook);
//tôi muốn xóa hình ảnh đã tồn trưóc đó nếu mà hình ảnh không tồn tại thì bỏ qua lỗi
        bookToEdit.setImage(imagePath);
        bookToEdit.setUrl(url);
        bookToEdit.setAuthors(new ArrayList<>(selectedAuthors));
        bookToEdit.setCategories(new ArrayList<>(selectedCategories));

        if (bookDAO.update(bookToEdit)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Cập nhật sách thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}