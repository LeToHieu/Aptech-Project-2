package com.aptech.aptechproject2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BookApp - Login");
        stage.setScene(scene);

        // ✅ Không cho resize dưới kích thước này
        stage.setMinWidth(900);
        stage.setMinHeight(550);

        stage.setMaximized(true); // FULL MÀN
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
