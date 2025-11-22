package com.aptech.aptechproject2.Controller.AuthorController;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;

public class EditAuthorController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ImageView imagePreview;
    @FXML private Label errorLabel;

    private Author authorToEdit;
    private final AuthorDAO authorDAO = new AuthorDAO();

    // Đường dẫn ảnh (khởi tạo từ author, overwrite nếu chọn mới)
    private String imagePath;

    public void setAuthor(Author author) {
        this.authorToEdit = author;
        nameField.setText(author.getName());
        descriptionField.setText(author.getDescription());
        imagePath = author.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            InputStream is = getClass().getResourceAsStream(imagePath);

            if (is != null) {
                // Load ảnh thật
                Image image = new Image(is);
                System.out.println("imagePath is: " + imagePath);

                imagePreview.setImage(image);
            } else {
                // File không tồn tại → dùng ảnh mặc định
                Image fallback = new Image(getClass().getResourceAsStream("/images/no_image.jpg"));
                imagePreview.setImage(fallback);
            }
        } else {
            // Không có đường dẫn → hiển thị ảnh mặc định
            Image fallback = new Image(getClass().getResourceAsStream("/images/no_image.jpg"));
            imagePreview.setImage(fallback);
        }
    }

    @FXML
    private void onUpdateAuthor() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Vui lòng điền tên tác giả!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa tác giả này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        authorToEdit.setName(name);
        authorToEdit.setDescription(description);
        authorToEdit.setImage(imagePath);  // imagePath có thể đã thay đổi hoặc giữ nguyên

        if (authorDAO.update(authorToEdit)) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setContentText("Cập nhật tác giả thành công!");
            success.showAndWait();
            onCancel();
        } else {
            errorLabel.setText("Cập nhật thất bại!");
        }
    }

    @FXML
    private void onChooseImage() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        String newPath = ImageUtil.selectAndSaveImage(stage, imagePreview);
        if (newPath != null) {
            this.imagePath = newPath;  // Overwrite đường dẫn cũ
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}