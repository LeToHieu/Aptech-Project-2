package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.ShelfDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Shelf;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ShelfDetailController {

    @FXML private Label shelfNameLabel;
    @FXML private Label bookCountLabel;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleCol, authorCol, isbnCol;
    @FXML private Button removeBtn;

    private Shelf currentShelf;
    private final ShelfDAO shelfDAO = new ShelfDAO();

    public void setShelf(Shelf shelf) {
        this.currentShelf = shelf;
        shelfNameLabel.setText(shelf.getName());
        bookCountLabel.setText(shelf.getBookCount() + " sách");
        loadBooks();
    }

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty("lmao"));

//        authorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAuthorName()));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            removeBtn.setDisable(newVal == null);
        });
    }

    private void loadBooks() {
        bookTable.setItems(FXCollections.observableArrayList(currentShelf.getBooks()));
    }

    @FXML
    private void onRemove() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (confirm("Xóa sách \"" + selected.getTitle() + "\" khỏi kệ?")) {
            if (shelfDAO.removeBookFromShelf(currentShelf.getId(), selected.getId())) {
                success("Đã xóa khỏi kệ!");
                currentShelf.getBooks().remove(selected);
                loadBooks();
                bookCountLabel.setText(currentShelf.getBookCount() + " sách");
            } else {
                alert("Xóa thất bại!");
            }
        }
    }

    @FXML private void onClose() { bookTable.getScene().getWindow().hide(); }

    private void alert(String msg) { new Alert(Alert.AlertType.WARNING, msg).show(); }
    private void success(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).show(); }
    private boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().filter(ButtonType.OK::equals).isPresent();
    }
}