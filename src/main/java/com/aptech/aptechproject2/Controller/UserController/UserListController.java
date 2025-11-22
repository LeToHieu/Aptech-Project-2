package com.aptech.aptechproject2.Controller.UserController;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idCol;
    @FXML private TableColumn<User, String> usernameCol, emailCol, phoneCol, roleCol, dateCol;
    @FXML private TextField searchField;
    @FXML private Button searchBtn, addBtn, editBtn, deleteBtn;

    private final UserDAO userDAO = new UserDAO();
    private ObservableList<User> allUsers = FXCollections.observableArrayList();
    private ObservableList<User> filteredUsers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createTime")); // Đã sửa từ updateTime

        loadUsers();
        searchField.textProperty().addListener((obs, old, newVal) -> filterUsers(newVal));
    }

    private void loadUsers() {
        allUsers.setAll(userDAO.getAllUsers());
        filteredUsers.setAll(allUsers);
        userTable.setItems(filteredUsers);
    }

    private void filterUsers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            filteredUsers.setAll(allUsers);
        } else {
            filteredUsers.setAll(allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                            u.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                            (u.getPhoneNumber() != null && u.getPhoneNumber().toLowerCase().contains(keyword.toLowerCase())))
                    .collect(Collectors.toList()));
        }
    }

    @FXML private void onSearch() { filterUsers(searchField.getText()); }

    @FXML private void onAdd() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn thêm người dùng mới?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/UserFXML/user_add.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Thêm Người Dùng");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadUsers(); // Load lại sau khi đóng dialog
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onEdit() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setContentText("Vui lòng chọn một người dùng để sửa!");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setContentText("Bạn có chắc chắn muốn sửa thông tin người dùng này?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/UserFXML/user_edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditUserController controller = loader.getController();
            controller.setUser(selected);
            Stage stage = new Stage();
            stage.setTitle("Sửa Thông Tin Người Dùng");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadUsers(); // Load lại sau khi đóng dialog
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete()) {
            if (userDAO.delete(selected.getId())) {
                loadUsers();
            }
        }
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc chắn muốn xóa user này?");
        return alert.showAndWait().get() == ButtonType.OK;
    }
}