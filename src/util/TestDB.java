package util;

public class TestDB {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("KẾT NỐI SQL SERVER THÀNH CÔNG");
        } else {
            System.out.println("KẾT NỐI SQL SERVER THẤT BẠI");
        }
    }
}
