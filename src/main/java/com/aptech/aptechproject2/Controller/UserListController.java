package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.stream.Collectors;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idCol;
    @FXML private TableColumn<User, String> usernameCol, emailCol, phoneCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, String> dateCol;
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
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("updateTime"));

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
                            u.getPhoneNumber().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }

    @FXML private void onSearch() { filterUsers(searchField.getText()); }
    @FXML private void onAdd() { /* Mở dialog thêm user */ }
    @FXML private void onEdit() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Mở dialog sửa user
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