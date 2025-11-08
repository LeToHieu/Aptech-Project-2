package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> usernameCol, emailCol, fullNameCol;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        loadUsers();
    }

    public void loadUsers() {
        userTable.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
    }
}