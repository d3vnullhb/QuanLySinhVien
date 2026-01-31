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
            e.printStackTrace();
        }
        return null;
    }

    /* ================= CHECK ================= */

    public boolean exists(String tenDangNhap) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap.trim());
            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= INSERT ================= */

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
            e.printStackTrace();
        }
        return false;
    }

    /* ================= SOFT DELETE ================= */

    public boolean softDelete(String tenDangNhap) {

        String sql = """
            UPDATE TaiKhoan
            SET TrangThai = N'Ngừng hoạt động'
            WHERE TenDangNhap = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap.trim());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= RESTORE ================= */

    public boolean restore(String tenDangNhap) {

        String sql = """
            UPDATE TaiKhoan
            SET TrangThai = N'Hoạt động'
            WHERE TenDangNhap = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap.trim());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= RESET PASSWORD ================= */

    public boolean resetPassword(String tenDangNhap, String matKhauMoi) {

        String sql = """
            UPDATE TaiKhoan
            SET MatKhau = ?
            WHERE TenDangNhap = ?
              AND TrangThai = N'Hoạt động'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, matKhauMoi.trim());
            ps.setString(2, tenDangNhap.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= LIST ================= */

    public List<String> getAllTenDangNhapGV() {
        return getByRole("GIANGVIEN");
    }

    public List<String> getAllTenDangNhapSV() {
        return getByRole("SINHVIEN");
    }

    private List<String> getByRole(String role) {

        List<String> list = new ArrayList<>();

        String sql = """
            SELECT TenDangNhap
            FROM TaiKhoan
            WHERE VaiTro = ?
              AND TrangThai = N'Hoạt động'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("TenDangNhap"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
