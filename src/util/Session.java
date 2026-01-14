package util;

import model.TaiKhoan;

public class Session {
    public static TaiKhoan currentUser;
    
    public static void logout() {
        currentUser = null;
    }
}
