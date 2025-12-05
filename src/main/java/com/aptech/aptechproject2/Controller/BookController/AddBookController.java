// AddBookController.java (Updated with TotalBook and BorrowBook handling)
package com.aptech.aptechproject2.Controller.BookController;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Category;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddBookController {

    @FXML private TextField titleField, urlField, totalBookField, borrowBookField;
    @FXML private TextArea descriptionField;
    @FXML private ImageView imagePreview;
    @FXML private ListView<HBox> selectedAuthorsList;
    @FXML private ListView<HBox> selectedCategoriesList;
    @FXML private Label errorLabel;

    private final BookDAO bookDAO = new BookDAO();

    // Đường dẫn ảnh đã chọn (lưu vào DB)
    private String imagePath;

    // Danh sách tác giả & thể loại đã chọn
    private final ObservableList<Author> selectedAuthors = FXCollections.observableArrayList();
    private final ObservableList<Category> selectedCategories = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Khởi tạo ListView để hiển thị danh sách có nút xóa
        selectedAuthorsList.setItems(FXCollections.observableArrayList());
        selectedCategoriesList.setItems(FXCollections.observableArrayList());
    }

    // ========== CHỌN ẢNH ==========
    @FXML
    private void onChooseImage() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        String newPath = ImageUtil.selectAndSaveImage(stage, imagePreview);
        if (newPath != null) {
            this.imagePath = newPath; // Lưu đường dẫn tương đối để ghi vào DB
        }
    }

    // ========== THÊM TÁC GIẢ ==========
    @FXML
    private void onAddAuthors() {
        openAuthorSelectionDialog();
    }

    private void openAuthorSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_authors.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Chọn Tác Giả");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(titleField.getScene().getWindow());
            dialog.showAndWait();

            SelectAuthorsController controller = loader.getController();
            List<Author> chosen = controller.getSelectedAuthors();

            if (chosen != null && !chosen.isEmpty()) {
                selectedAuthors.addAll(chosen);
                updateAuthorsListView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== THÊM THỂ LOẠI ==========
    @FXML
    private void onAddCategories() {
        openCategorySelectionDialog();
    }

    private void openCategorySelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/aptech/aptechproject2/fxml/AdminPage/BookFXML/select_categories.fxml"));
            Stage dialog = new Stage();
            dialog.setScene(new Scene(loader.load()));
            dialog.setTitle("Chọn Thể Loại");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(titleField.getScene().getWindow());
            dialog.showAndWait();

            SelectCategoriesController controller = loader.getController();
            List<Category> chosen = controller.getSelectedCategories();

            if (chosen != null && !chosen.isEmpty()) {
                selectedCategories.addAll(chosen);
                updateCategoriesListView();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== CẬP NHẬT LISTVIEW TÁC GIẢ ==========
    private void updateAuthorsListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Author author : selectedAuthors) {
            HBox hbox = new HBox(10);
            Label nameLabel = new Label(author.getName());
            Button removeBtn = new Button("x");
            removeBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            removeBtn.setOnAction(e -> {
                selectedAuthors.remove(author);
                updateAuthorsListView();
            });
            hbox.getChildren().addAll(nameLabel, removeBtn);
            items.add(hbox);
        }
        selectedAuthorsList.setItems(items);
    }

    // ========== CẬP NHẬT LISTVIEW THỂ LOẠI ==========
    private void updateCategoriesListView() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Category category : selectedCategories) {
            HBox hbox = new HBox(10);
            Label nameLabel = new Label(category.getName());
            Button removeBtn = new Button("x");
            removeBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            removeBtn.setOnAction(e -> {
                selectedCategories.remove(category);
                updateCategoriesListView();
            });
            hbox.getChildren().addAll(nameLabel, removeBtn);
            items.add(hbox);
        }
        selectedCategoriesList.setItems(items);
    }

    // ========== NÚT THÊM SÁCH ==========
    @FXML
    private void onAddBook() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String totalBookStr = totalBookField.getText().trim();
//        String borrowBookStr = borrowBookField.getText().trim();
        String url = urlField.getText().trim();

        if (title.isEmpty()) {
            errorLabel.setText("Vui lòng nhập ít nhất tựa đề sách!");
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

        // Xác nhận trước khi thêm
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn thêm sách này?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        Book newBook = new Book();
        newBook.setTitle(title);
        newBook.setDescription(description);
        newBook.setTotalBook(totalBook);
//        newBook.setBorrowBook(borrowBook);
        newBook.setImage(imagePath);           // có thể null → DB chấp nhận NULL
        newBook.setUrl(url);
        newBook.setAuthors(new ArrayList<>(selectedAuthors));
        newBook.setCategories(new ArrayList<>(selectedCategories));

        if (bookDAO.create(newBook)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setHeaderText(null);
            success.setContentText("Thêm sách thành công!");
            success.showAndWait();
            onCancel(); // Đóng cửa sổ
        } else {
            errorLabel.setText("Thêm sách thất bại! Vui lòng thử lại.");
        }
    }

    // ========== NÚT HỦY ==========
    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}