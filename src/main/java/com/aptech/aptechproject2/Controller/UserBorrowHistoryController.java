package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.Model.Borrow;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserBorrowHistoryController {

    @FXML private TableView<Borrow> borrowTable;

    @FXML private TableColumn<Borrow, Long> colId;
    @FXML private TableColumn<Borrow, String> colBook;
    @FXML private TableColumn<Borrow, String> colBorrowDate;
    @FXML private TableColumn<Borrow, String> colReturnDate;
    @FXML private TableColumn<Borrow, Borrow> actionColumn;

    @FXML private Label emptyLabel;

    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final ObservableList<Borrow> borrowList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        borrowTable.setItems(borrowList);

        // Set cellValueFactory cho từng cột
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDayFormatted"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("expireDayFormatted"));  // Hạn trả là expireDay

        // CellFactory cho cột Action
        actionColumn.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(param.getValue()));

        actionColumn.setCellFactory(col -> new TableCell<Borrow, Borrow>() {

            private final Button cancelButton = new Button("Hủy yêu cầu");

            {
                cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                        + "-fx-background-radius: 15; -fx-padding: 5 15;");
                cancelButton.setOnAction(e -> {
                    Borrow borrow = getTableView().getItems().get(getIndex());
                    if (borrow != null && borrow.getStatus() == 0) {
                        cancelBorrow(borrow);
                    }
                });
            }

            @Override
            protected void updateItem(Borrow borrow, boolean empty) {
                super.updateItem(borrow, empty);

                if (empty || borrow == null) {
                    setGraphic(null);
                    return;
                }

                // Chỉ hiển thị nút cho đơn Pending
                if (borrow.getStatus() == 0) {
                    setGraphic(cancelButton);
                } else {
                    setGraphic(null);
                }
            }
        });

        loadBorrowHistory();
    }

    private void loadBorrowHistory() {
        User user = Session.getCurrentUser();
        if (user == null) {
            emptyLabel.setText("Vui lòng đăng nhập để xem lịch sử mượn.");
            emptyLabel.setVisible(true);
            return;
        }

        List<Borrow> list = borrowDAO.getBorrowsByUser(user.getId());
        borrowList.setAll(list);
        emptyLabel.setVisible(list.isEmpty());
    }

    private void cancelBorrow(Borrow borrow) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hủy yêu cầu mượn sách");
        confirm.setHeaderText("Bạn có chắc muốn hủy yêu cầu mượn sách này?");
        confirm.setContentText("Sách: " + borrow.getBookTitle());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (borrowDAO.cancelPendingBorrow(borrow.getId())) {
                new Alert(Alert.AlertType.INFORMATION, "Đã hủy yêu cầu thành công!").showAndWait();
                loadBorrowHistory();
            } else {
                new Alert(Alert.AlertType.ERROR, "Không thể hủy. Vui lòng thử lại.").show();
            }
        }
    }
}