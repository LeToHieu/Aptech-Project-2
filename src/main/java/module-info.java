module com.aptech.aptechproject2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    requires javafx.graphics;
//    requires com.aptech.aptechproject2;
//    requires com.aptech.aptechproject2;


    opens com.aptech.aptechproject2 to javafx.fxml, javafx.base;
    opens com.aptech.aptechproject2.Controller to javafx.fxml, javafx.base;
    opens com.aptech.aptechproject2.Model to javafx.fxml, javafx.base; // nếu dùng FXML binding
//    opens com.aptech.aptechproject2.Model.Book to javafx.fxml;
    exports com.aptech.aptechproject2;
    opens com.aptech.aptechproject2.Controller.AdminController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.AuthorController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.BookController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.CategoryController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.UserController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.ReviewController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.ShelfController to javafx.base, javafx.fxml;
    opens com.aptech.aptechproject2.Controller.BorrwController to javafx.base, javafx.fxml;
}