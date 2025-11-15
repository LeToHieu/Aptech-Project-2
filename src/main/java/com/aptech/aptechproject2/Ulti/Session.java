package com.aptech.aptechproject2.Ulti;

import com.aptech.aptechproject2.Model.User;

public class Session {
    // Biến tĩnh lưu user hiện tại
    private static User currentUser;

    // Thiết lập user hiện tại
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Lấy user hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }

    // Xóa session (đăng xuất)
    public static void clear() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        if (currentUser.getRole() != 0){
            return true;
        }else{
            return false;
        }
    }

}
