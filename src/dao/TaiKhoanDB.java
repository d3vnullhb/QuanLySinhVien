package dao;

import model.TaiKhoan;
import util.DBConnection;
import java.sql.*;

public class TaiKhoanDB {

    public TaiKhoan login(String user, String pass) {
        String sql = "SELECT TenDangNhap, VaiTro FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThai = N'Hoạt động'";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            var rs = ps.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString(1));
                tk.setVaiTro(rs.getString(2));
                return tk;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean exists(String u) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenDangNhap=?";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            return ps.executeQuery().next();
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean insert(String u, String p, String role) {
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Salt, VaiTro, TrangThai, NgayTao) VALUES (?, ?, 's1', ?, N'Hoạt động', GETDATE())";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setString(2, p);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateUsername(String oldUser, String newUser) {
        String sql = "UPDATE TaiKhoan SET TenDangNhap=? WHERE TenDangNhap=?";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, newUser);
            ps.setString(2, oldUser);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean softDelete(String u) {
        if (u == null) return false;
        String sql = "UPDATE TaiKhoan SET TrangThai=N'Bị khóa' WHERE TenDangNhap=?";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean restore(String u) {
        if (u == null) return false;
        String sql = "UPDATE TaiKhoan SET TrangThai=N'Hoạt động' WHERE TenDangNhap=?";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean resetPassword(String u, String p) {
        String sql = "UPDATE TaiKhoan SET MatKhau=? WHERE TenDangNhap=?";
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, u);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}