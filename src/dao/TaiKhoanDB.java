package dao;

import model.TaiKhoan;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDB {

    /* ================= LOGIN ================= */
    public TaiKhoan login(String user, String pass) {

        String sql = """
            SELECT TenDangNhap, VaiTro
            FROM TaiKhoan
            WHERE TenDangNhap = ?
              AND MatKhau = ?
              AND TrangThai = N'Hoạt động'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setVaiTro(rs.getString("VaiTro"));
                return tk;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi login TaiKhoan:");
            e.printStackTrace();
        }
        return null;
    }

   
    public boolean insert(String tenDangNhap, String matKhau, String vaiTro) {

    String sql = """
        INSERT INTO TaiKhoan
        (TenDangNhap, MatKhau, Salt, VaiTro, TrangThai, NgayTao)
        VALUES (?, ?, ?, ?, N'Hoạt động', GETDATE())
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, tenDangNhap.trim()); 
        ps.setString(2, matKhau.trim());     
        ps.setString(3, "s1");               
        ps.setString(4, vaiTro);              

        return ps.executeUpdate() > 0;

     } catch (SQLException e) {
            System.err.println("Lỗi insert TaiKhoan:");
            e.printStackTrace();
     }
        return false;
    }


    /* ================= DS TÀI KHOẢN GIẢNG VIÊN ================= */
    public List<String> getAllTenDangNhapGV() {

        List<String> list = new ArrayList<>();

        String sql = """
            SELECT TenDangNhap
            FROM TaiKhoan
            WHERE VaiTro = 'GIANGVIEN'
              AND TrangThai = N'Hoạt động'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("TenDangNhap"));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAllTenDangNhapGV:");
            e.printStackTrace();
        }
        return list;
    }
}
