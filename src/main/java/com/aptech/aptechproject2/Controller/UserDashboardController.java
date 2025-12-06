package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class UserDashboardController {

    @FXML private StackPane contentArea;
    @FXML private HBox navigation;  // Giờ là HBox (khớp với FXML)

    // Các thành phần navigation giờ inject trực tiếp (vì gộp)
    @FXML private Hyperlink navHome, navAllBooks, navCategories, navAuthors, navMyShelf, navBorrowHistory, navProfile;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {
        // Gán onAction trực tiếp ở đây (không cần NavigationController riêng nữa)
        navHome.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_home_page.fxml"));
        navAllBooks.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_all_books.fxml"));
        navCategories.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_categories.fxml"));
        navAuthors.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_authors.fxml"));
        navMyShelf.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_my_shelf.fxml"));
        navBorrowHistory.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_borrow_history.fxml"));
        navProfile.setOnAction(e -> loadPage("/com/aptech/aptechproject2/fxml/user_profile.fxml"));

        btnLogout.setOnAction(e -> {
            Session.clear();
            SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", btnLogout.getScene());
        });

        // Load trang chủ mặc định
        loadPage("/com/aptech/aptechproject2/fxml/user_home_page.fxml");
    }

    private void loadPage(String fxmlPath) {
        if (contentArea != null) {
            SceneManager.loadInto(contentArea, fxmlPath);
        }
    }
}