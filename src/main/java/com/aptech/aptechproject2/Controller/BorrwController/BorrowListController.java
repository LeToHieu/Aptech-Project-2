package com.aptech.aptechproject2.Controller.BorrwController;

import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.UserDAO;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Timestamp;

import java.io.IOException;
import java.util.List;

public class BorrowListController {

    @FXML private TableView<Borrow> borrowTable;
    @FXML private TableColumn<Borrow, Integer> idCol;
    @FXML private TableColumn<Borrow, String> userCol, bookCol, borrowDayCol, expireDayCol, returnDayCol, statusCol;
    @FXML private ComboBox<User> userCombo;
    @FXML private ComboBox<Book> bookCombo;
    @FXML private Button borrowBtn, returnBtn, editBtn, deleteBtn;

    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowDayCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getBorrowDay().toString()));
        expireDayCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getExpireDay().toString()));
        returnDayCol.setCellValueFactory(cell -> {
            Timestamp rt = cell.getValue().getReturnDateTime();
            return new javafx.beans.property.SimpleStringProperty(rt != null ? rt.toString() : "-");
        });
        statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getStatusName()));

        loadBorrows();
        loadUsersAndBooks();
    }

    private void loadBorrows() {
        borrowDAO.updateOverdueStatus(); // Cập nhật quá hạn
        List<Borrow> borrows = borrowDAO.getAllBorrows();
        borrowTable.setItems(FXCollections.observableArrayList(borrows));
    }

    private void loadUsersAndBooks() {
        userCombo.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
        bookCombo.setItems(FXCollections.observableArrayList(bookDAO.getAllBooks()));
        userCombo.setPromptText("Chọn người dùng");
        bookCombo.setPromptText("Chọn sách");
    }

    @FXML
    private void onBorrow() {
        User user = userCombo.getValue();
        Book book = bookCombo.getValue();
        if (user == null || book == null) {
            alert("Vui lòng chọn người dùng và sách!");
            return;
        }
        if (confirm("Xác nhận mượn sách cho " + user.getUsername() + "?")) {
            if (borrowDAO.create(user.getId(), book.getId())) {
                success("Mượn sách thành công! Hạn trả: 14 ngày");
                loadBorrows();
                userCombo.setValue(null);
                bookCombo.setValue(null);
            } else {
                alert("Mượn thất bại!");
            }
        }
    }

    @FXML
    private void onReturn() {
        Borrow selected = borrowTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getStatus() != 0) {
            alert("Vui lòng chọn một phiếu đang mượn!");
            return;
        }
        if (confirm("Xác nhận trả sách: " + selected.getBookTitle() + "?")) {
            if (borrowDAO.returnBook(selected.getId())) {
                success("Trả sách thành công!");
                loadBorrows();
            } else {
                alert("Trả sách thất bại!");
            }
        }
    }

    @FXML
    private void onRefresh() { loadBorrows(); }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneManager.loadScene("/com/aptech/aptechproject2/fxml/login.fxml", borrowTable.getScene());
    }

    private void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg).show(); }
    private void success(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    @FXML
    private void onEdit() {
        Borrow selected = borrowTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Vui lòng chọn phiếu mượn!");
            return;
        }
        if (selected.getStatus() != 0) {
            alert("Chỉ có thể sửa phiếu đang mượn!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aptech/aptechproject2/fxml/AdminPage/BorrowFXML/borrow_edit.fxml"));
            Scene scene = new Scene(loader.load());
            EditBorrowController controller = loader.getController();
            controller.setBorrow(selected);
            Stage stage = new Stage();
            stage.setTitle("Sửa Phiếu Mượn");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            loadBorrows();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Borrow selected = borrowTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Vui lòng chọn phiếu mượn!");
            return;
        }

        if (confirm("Xóa phiếu mượn này? Không thể khôi phục!")) {
            if (borrowDAO.delete(selected.getId())) {  // DÙNG DAO
                success("Xóa thành công!");
                loadBorrows();
            } else {
                alert("Xóa thất bại! Có thể phiếu đã bị xóa hoặc lỗi DB.");
            }
        }
    }
}