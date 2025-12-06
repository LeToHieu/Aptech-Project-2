package com.aptech.aptechproject2.Controller;

import com.aptech.aptechproject2.DAO.BorrowDAO;
import com.aptech.aptechproject2.DAO.ReviewDAO;
import com.aptech.aptechproject2.Model.Book;
import com.aptech.aptechproject2.Model.Review;
import com.aptech.aptechproject2.Model.User;
import com.aptech.aptechproject2.Ulti.ImageUtil;
import com.aptech.aptechproject2.Ulti.Session;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookDetailController {

    @FXML private Label titleLabel, descriptionLabel, authorsLabel, categoriesLabel;
    @FXML private Label availabilityLabel, statusIcon;
    @FXML private HBox ratingBox;
    @FXML private ImageView bookImageView;
    @FXML private VBox reviewsContainer;
    @FXML private Button borrowButton;

    private Book book;
    private BorrowDAO borrowDAO = new BorrowDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();

    public void setBook(Book book) {
        this.book = book;
        loadBookInfo();
        loadReviews();
        updateBorrowButtonState();
    }

    private void loadBookInfo() {
        titleLabel.setText(book.getTitle());
        descriptionLabel.setText(book.getDescription() != null && !book.getDescription().isEmpty() ? book.getDescription() : "Chưa có mô tả.");

        authorsLabel.setText(book.getAuthors().isEmpty() ? "Không rõ" :
                book.getAuthors().stream().map(a -> a.getName()).collect(java.util.stream.Collectors.joining(", ")));

        categoriesLabel.setText(book.getCategories().isEmpty() ? "Không rõ" :
                book.getCategories().stream().map(c -> c.getName()).collect(java.util.stream.Collectors.joining(", ")));

        int available = book.getTotalBook() - book.getBorrowBook();
        availabilityLabel.setText(available + " cuốn còn lại");
        if (available > 0) {
            statusIcon.setText("Còn hàng");
            statusIcon.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20;");
        } else {
            statusIcon.setText("Hết sách");
            statusIcon.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 20;");
        }

        // Rating
        ratingBox.getChildren().clear();
        double rating = book.getAverageRating();
        int fullStars = (int) Math.round(rating);
        for (int i = 0; i < 5; i++) {
            Text star = new Text(i < fullStars ? "★" : "☆");
            star.setStyle(i < fullStars ? "-fx-fill: #f39c12; -fx-font-size: 28px;" : "-fx-fill: #bdc3c7; -fx-font-size: 28px;");
            ratingBox.getChildren().add(star);
        }
        if (rating > 0) {
            Label score = new Label(String.format(" %.1f", rating));
            score.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            ratingBox.getChildren().add(score);
        }

        ImageUtil.loadImageToView(book.getImage(), bookImageView);
    }

    private void loadReviews() {
        reviewsContainer.getChildren().clear();
        List<Review> reviews = reviewDAO.getReviewsByBookId(book.getId());

        if (reviews.isEmpty()) {
            Label lbl = new Label("Chưa có đánh giá nào.");
            lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6;");
            reviewsContainer.getChildren().add(lbl);
            return;
        }

        for (Review r : reviews) {
            VBox box = new VBox(8);
            box.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, #00000011, 10, 0, 0, 3);");

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);

            Label user = new Label(r.getUserName());
            user.setStyle("-fx-font-weight: bold;");

            HBox stars = new HBox(3);
            for (int i = 0; i < 5; i++) {
                Text s = new Text(i < r.getRating() ? "★" : "☆");
                s.setStyle(i < r.getRating() ? "-fx-fill: #f39c12;" : "-fx-fill: #bdc3c7;");
                stars.getChildren().add(s);
            }

            Label date = new Label(r.getCreatedAt() != null ?
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(r.getCreatedAt().toLocalDateTime()) : "");
            date.setStyle("-fx-text-fill: #95a5a6;");

            header.getChildren().addAll(user, stars, new Region() {{setPrefWidth(10);}}, date);
            HBox.setHgrow(new Region(), javafx.scene.layout.Priority.ALWAYS);

            Label comment = new Label(r.getComment());
            comment.setWrapText(true);

            box.getChildren().addAll(header, comment);
            reviewsContainer.getChildren().add(box);
        }
    }

    // Cập nhật trạng thái nút mượn
    private void updateBorrowButtonState() {
        User user = Session.getCurrentUser();
        if (user == null) {
            borrowButton.setText("Đăng nhập để mượn");
            borrowButton.setDisable(true);
            return;
        }

        Task<String> task = new Task<>() {
            @Override protected String call() {
                // Kiểm tra: có đơn nào đang pending hoặc đang mượn không?
                return borrowDAO.getBorrowStatus(user.getId(), book.getId());
            }
        };

        task.setOnSucceeded(e -> {
            String status = task.getValue();
            int available = book.getTotalBook() - book.getBorrowBook();

            if (available <= 0) {
                borrowButton.setText("Hết sách");
                borrowButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 12; -fx-background-radius: 50; -fx-font-weight: bold;");
                borrowButton.setDisable(true);
            } else if ("PENDING".equals(status)) {
                borrowButton.setText("Đã gửi yêu cầu mượn");
                borrowButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 12; -fx-background-radius: 50; -fx-font-weight: bold;");
                borrowButton.setDisable(true);
            } else if ("BORROWED".equals(status)) {
                borrowButton.setText("Đang mượn sách này");
                borrowButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 12; -fx-background-radius: 50; -fx-font-weight: bold;");
                borrowButton.setDisable(true);
            } else {
                borrowButton.setText("Mượn sách");
                borrowButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 12; -fx-background-radius: 50; -fx-font-weight: bold;");
                borrowButton.setDisable(false);
            }
        });

        new Thread(task).start();
    }

    @FXML
    private void onBorrowBook() {
        User user = Session.getCurrentUser();
        if (user == null) return;

        int available = book.getTotalBook() - book.getBorrowBook();
        if (available <= 0) {
            showAlert("Sách đã hết, không thể mượn!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận mượn sách");
        confirm.setHeaderText("Mượn: " + book.getTitle());
        confirm.setContentText("Bạn sẽ được mượn trong 14 ngày.\nXác nhận gửi yêu cầu?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        borrowButton.setText("Đang xử lý...");
        borrowButton.setDisable(true);

        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() {
                return borrowDAO.createBorrowAndReserve((int)user.getId(), book.getId());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                book.setBorrowBook(book.getBorrowBook() + 1); // Cập nhật UI ngay
                showAlert("Gửi yêu cầu mượn thành công!\nVui lòng chờ quản trị viên duyệt.");
                updateBorrowButtonState();
            } else {
                showAlert("Không thể gửi yêu cầu. Vui lòng thử lại.");
                borrowButton.setDisable(false);
            }
        });

        new Thread(task).start();
    }

    @FXML
    private void onPreviewPdf() {
        if (book.getUrl() == null || book.getUrl().trim().isEmpty()) {
            showAlert("Không có link xem trước cho sách này!");
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(book.getUrl()));
        } catch (Exception e) {
            showAlert("Không thể mở link: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.showAndWait();
    }
}