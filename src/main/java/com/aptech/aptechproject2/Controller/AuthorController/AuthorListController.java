package com.aptech.aptechproject2.Controller.AuthorController;

import com.aptech.aptechproject2.DAO.AuthorDAO;
import com.aptech.aptechproject2.Model.Author;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class AuthorListController {

    @FXML private TableView<Author> authorTable;
    @FXML private TableColumn<Author, Integer> idCol;
    @FXML private TableColumn<Author, String> nameCol, descriptionCol;
    @FXML private TableColumn<Author, String> imageCol;  // Sửa để hiển thị preview
    @FXML private TextField searchField;
    @FXML private Button addBtn, editBtn, deleteBtn;

    private final AuthorDAO authorDAO = new AuthorDAO();
    private ObservableList<Author> allAuthors = FXCollections.observableArrayList();
    private ObservableList<Author> filteredAuthors = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Sửa imageCol để hiển thị preview ImageView
        imageCol.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    ImageUtil.loadImageToView(imagePath, imageView);
                    }
                    setGraphic(imageView);
                    setText(null);
                }

        });
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));

        loadAuthors();
        searchField.textProperty().addListener((obs, old, newVal) -> filterAuthors(newVal));
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadAuthors() {
        allAuthors.setAll(authorDAO.getAllAuthors());
        filteredAuthors.setAll(allAuthors);
        authorTable.setItems(filteredAuthors);
    }

    private void filterAuthors(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            filteredAuthors.setAll(allAuthors);
        } else {
            filteredAuthors.setAll(allAuthors.stream()
                    .filter(a -> a.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                            (a.getDescription() != null && a.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                    .collect(Collectors.toList()));
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/Author/author_add.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Thêm Tác Giả");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadAuthors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        Author selected = authorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một tác giả để sửa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa tác giả này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/Author/author_edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditAuthorController controller = loader.getController();
            controller.setAuthor(selected);
            Stage stage = new Stage();
            stage.setTitle("Sửa Tác Giả");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadAuthors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Author selected = authorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một tác giả để xóa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setContentText("Bạn có chắc chắn muốn xóa tác giả này?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (authorDAO.delete(selected.getId())) {
                loadAuthors();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Lỗi");
                error.setContentText("Xóa tác giả thất bại!");
                error.showAndWait();
            }
        }
    }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", authorTable.getScene());
    }
}