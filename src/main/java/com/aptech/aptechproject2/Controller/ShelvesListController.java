package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ShelfDAO;
import com.aptech.aptechproject2.Model.Shelf;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ShelvesListController {

    @FXML private TableView<Shelf> shelfTable;
    @FXML private TableColumn<Shelf, String> nameCol, userCol, descCol, countCol, timeCol;
    @FXML private Button addBtn, editBtn, deleteBtn, viewBtn;

    private final ShelfDAO shelfDAO = new ShelfDAO();
    private final boolean isAdmin = Session.isAdmin();

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        countCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cell.getValue().getBookCount()) + " sách"));
        timeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCreateTime().toString()));

        loadShelves();

        // Chỉ Admin thấy cột user
        userCol.setVisible(isAdmin);
    }

    private void loadShelves() {
        long userId = Session.getCurrentUser().getId();
        var shelves = isAdmin ? shelfDAO.getAllShelves() : shelfDAO.getShelvesByUser(userId);
        shelfTable.setItems(FXCollections.observableArrayList(shelves));
    }

    @FXML private void onAdd() { openDialog("/com/aptech/aptechproject2/fxml/shelf_add.fxml", "Tạo Kệ Mới"); }
    @FXML private void onEdit() {
        Shelf selected = shelfTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Chọn kệ!"); return; }
        if (!isAdmin && selected.getUserId() != Session.getCurrentUser().getId()) {
            alert("Bạn chỉ sửa được kệ của mình!"); return;
        }
        openDialog("/com/aptech/aptechproject2/fxml/shelf_edit.fxml", "Sửa Kệ", selected);
    }

    @FXML private void onDelete() {
        Shelf selected = shelfTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Chọn kệ!"); return; }
        if (!isAdmin && selected.getUserId() != Session.getCurrentUser().getId()) {
            alert("Bạn chỉ xóa được kệ của mình!"); return;
        }
        if (confirm("Xóa kệ \"" + selected.getName() + "\"?")) {
            if (shelfDAO.delete(selected.getId(), selected.getUserId())) {
                success("Xóa thành công!");
                loadShelves();
            } else {
                alert("Xóa thất bại!");
            }
        }
    }

    @FXML private void onView() {
        Shelf selected = shelfTable.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Chọn kệ!"); return; }
        openDialog("/com/aptech/aptechproject2/fxml/shelf_detail.fxml", selected.getName() + " - Sách", selected);
    }

    private void openDialog(String fxml, String title) { openDialog(fxml, title, null); }
    private void openDialog(String fxml, String title, Shelf shelf) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);

            if (shelf != null) {
                if (fxml.contains("edit")) {
                    EditShelfController ctrl = loader.getController();
                    ctrl.setShelf(shelf);
                } else if (fxml.contains("detail")) {
                    ShelfDetailController ctrl = loader.getController();
                    ctrl.setShelf(shelf);
                }else if (fxml.contains("edit")) {
                    EditShelfController ctrl = loader.getController();
                    ctrl.setShelf(shelf);
                } else if (fxml.contains("detail")) {
                    ShelfDetailController ctrl = loader.getController();
                    ctrl.setShelf(shelf);
                }
            }

            stage.showAndWait();
            loadShelves();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg).show(); }
    private void success(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().filter(ButtonType.OK::equals).isPresent();
    }
}