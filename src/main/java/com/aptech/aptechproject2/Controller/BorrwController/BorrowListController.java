package com.aptech.aptechproject2.Controller.BorrwController;

import com.aptech.aptechproject2.DAO.BookDAO;
import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Ulti.SceneManager;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.util.List;

public class BorrowListController {

    @FXML private TableView<Borrow> borrowTable;
    @FXML private TableColumn<Borrow, Long> idCol;
    @FXML private TableColumn<Borrow, String> userCol, bookCol, statusCol, borrowDayCol, expireDayCol, returnDayCol;

    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;
    @FXML private Button returnBtn;

    private final BookDAO bookDAO = new BookDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadBorrows();

        // Tự động ẩn/hiện nút theo dòng được chọn
        borrowTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                approveBtn.setVisible(false);
                rejectBtn.setVisible(false);
                returnBtn.setVisible(false);
                return;
            }

            int status = newVal.getStatus();
            approveBtn.setVisible(status == 0);        // Chỉ hiện khi đang chờ duyệt
            rejectBtn.setVisible(status == 0);         // Chỉ hiện khi đang chờ duyệt
            returnBtn.setVisible(status == 1 || status == 3); // Chỉ hiện khi đang mượn hoặc quá hạn
        });
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));

        borrowDayCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBorrowDayFormatted()));
        expireDayCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExpireDayFormatted()));
        returnDayCol.setCellValueFactory(cellData -> {
            Timestamp rt = cellData.getValue().getReturnDateTime();
            return new SimpleStringProperty(rt != null
                    ? rt.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "");
        });
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatusName()));
    }

    // DUYỆT MƯỢN
    @FXML
    private void onApprove() {
        Borrow selected = getSelectedBorrow();
        if (selected == null || selected.getStatus() != 0) return;

        if (SceneManager.confirm("Duyệt phiếu mượn sách:\n" + selected.getBookTitle() + "\nCho: " + selected.getUserName())) {
            long librarianId = Session.getCurrentUser().getId();
            if (borrowDAO.approveBorrow(selected.getId(), librarianId, 14)) {
                SceneManager.success("Đã duyệt mượn sách thành công!");
                loadBorrows();
            } else {
                SceneManager.alert("Duyệt thất bại! Có thể sách đã hết hoặc lỗi hệ thống.");
            }
        }
    }

    // TỪ CHỐI
    @FXML
    private void onReject() {
        Borrow selected = getSelectedBorrow();
        if (selected == null || selected.getStatus() != 0) return;

        if (SceneManager.confirm("Từ chối phiếu mượn này?")) {
            if (borrowDAO.rejectBorrow(selected.getId())) {
                SceneManager.success("Đã từ chối phiếu mượn!");
                loadBorrows();
            } else {
                SceneManager.alert("Từ chối thất bại!");
            }
        }
    }

    // TRẢ SÁCH
    @FXML
    private void onReturn() {
        Borrow selected = getSelectedBorrow();
        if (selected == null || (selected.getStatus() != 1 && selected.getStatus() != 3)) return;

        if (SceneManager.confirm("Xác nhận nhận lại sách:\n" + selected.getBookTitle() + "\nTừ: " + selected.getUserName())) {
            long librarianId = Session.getCurrentUser().getId();
            if (borrowDAO.returnBook(selected.getId(), librarianId)) {
                SceneManager.success("Nhận trả sách thành công!");
                loadBorrows();
            } else {
                SceneManager.alert("Trả sách thất bại!");
            }
        }
    }

    @FXML
    private void onRefresh() {
        loadBorrows();
    }

    // Helper
    private Borrow getSelectedBorrow() {
        Borrow selected = borrowTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneManager.alert("Vui lòng chọn một phiếu mượn!");
        }
        return selected;
    }

    private void loadBorrows() {
        List<Borrow> borrows = borrowDAO.getAllBorrows();
        borrowTable.setItems(FXCollections.observableArrayList(borrows));
    }
}