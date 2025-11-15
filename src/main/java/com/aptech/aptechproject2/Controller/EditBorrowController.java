package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.Model.Borrow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;

public class EditBorrowController {

    @FXML private TextField userField, bookField, expireField;
    @FXML private Label errorLabel;

    private Borrow borrow;

    public void setBorrow(Borrow b) {
        this.borrow = b;
        userField.setText(b.getUserName());
        bookField.setText(b.getBookTitle());
        expireField.setText(b.getExpireDay().toLocalDateTime().toLocalDate().toString());
    }

    @FXML
    private void onUpdate() {
        String dateStr = expireField.getText().trim();
        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            errorLabel.setText("Định dạng ngày: YYYY-MM-DD");
            return;
        }

        String sql = "UPDATE Borrow SET ExpireDay = ? WHERE Id = ?";
        try (Connection c = com.aptech.aptechproject2.Ulti.DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dateStr + " 23:59:59");
            ps.setLong(2, borrow.getId());
            if (ps.executeUpdate() > 0) {
                close();
            } else {
                errorLabel.setText("Cập nhật thất bại!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML private void onCancel() { close(); }
    private void close() { ((Stage) userField.getScene().getWindow()).close(); }
}