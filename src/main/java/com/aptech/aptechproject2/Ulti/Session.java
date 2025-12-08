package com.aptech.aptechproject2.Ulti;

import com.aptech.aptechproject2.Model.User;

public class Session {

    // TOÀN BỘ LÀ STATIC – GỌI TRỰC TIẾP, KHÔNG CẦN getInstance()
    private static User currentUser;

    // Đăng nhập
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Lấy user hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }

    // Kiểm tra đăng nhập
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Kiểm tra quyền
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == 1;
    }

    public static boolean isLibrarian() {
        return currentUser != null && currentUser.getRole() == 2;
    }

    public static boolean isUser() {
        return currentUser != null && currentUser.getRole() == 0;
    }

    public static boolean isLibrarianOrAdmin() {
        return currentUser != null && currentUser.getRole() != 0;
    }

    // Đăng xuất
    public static void clear() {
        currentUser = null;
    }
}