package com.aptech.aptechproject2.Controller.BorrwController;

import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditBorrowController {

    @FXML private Label bookTitleLabel;
    @FXML private Label userNameLabel;
    @FXML private Label borrowDayLabel;
    @FXML private TextField daysTextField; // Số ngày cho mượn
    @FXML private Button approveButton;
    @FXML private Button rejectButton;

    private Borrow borrow;
    private BorrowDAO borrowDAO;
    private long librarianId;

    // Phương thức được gọi từ BorrowListController
    public void setBorrow(Borrow borrow) {
        this.borrow = borrow;
        updateUI();
    }

    public void setBorrowDAO(BorrowDAO borrowDAO) {
        this.borrowDAO = borrowDAO;
    }

    public void setLibrarianId(long librarianId) {
        this.librarianId = librarianId;
    }

    private void updateUI() {
        if (borrow != null) {
            bookTitleLabel.setText("Sách: " + borrow.getBookTitle());
            userNameLabel.setText("Người mượn: " + borrow.getUserName());
            borrowDayLabel.setText("Ngày yêu cầu: " + borrow.getBorrowDayFormatted());
        }
    }

    @FXML
    private void onApprove() {
        String daysText = daysTextField.getText();
        int days = 14; // mặc định 14 ngày nếu không nhập

        if (!daysText.isEmpty()) {
            try {
                days = Integer.parseInt(daysText);
                if (days < 1 || days > 60) {
                    SceneManager.alert("Số ngày mượn phải từ 1 đến 60!");
                    return;
                }
            } catch (NumberFormatException e) {
                SceneManager.alert("Vui lòng nhập số ngày hợp lệ!");
                return;
            }
        }

        if (SceneManager.confirm("Duyệt phiếu mượn này?\nSố ngày mượn: " + days + " ngày")) {
            boolean success = borrowDAO.approveBorrow(borrow.getId(), librarianId, days);
            if (success) {
                SceneManager.success("Duyệt mượn sách thành công!");
                closeWindow();
            } else {
                SceneManager.alert("Duyệt thất bại! Có thể sách đã được mượn.");
            }
        }
    }

    @FXML
    private void onReject() {
        if (SceneManager.confirm("Từ chối phiếu mượn này?")) {
            if (borrowDAO.rejectBorrow(borrow.getId())) {
                SceneManager.success("Đã từ chối phiếu mượn!");
                closeWindow();
            } else {
                SceneManager.alert("Từ chối thất bại!");
            }
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rejectButton.getScene().getWindow();
        stage.close();
    }
}