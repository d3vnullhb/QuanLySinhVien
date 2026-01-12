package dao;

import model.GiangVien;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDB {

    public List<GiangVien> getAll() {
        List<GiangVien> list = new ArrayList<>();

        String sql = """
            SELECT MaGV, HoTen, Email, MaKhoa, TenDangNhap, TrangThai
            FROM GiangVien
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new GiangVien(
                    rs.getString("MaGV"),
                    rs.getString("HoTen"),
                    rs.getString("Email"),
                    rs.getString("MaKhoa"),
                    rs.getString("TenDangNhap"),
                    rs.getString("TrangThai")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAll GiangVien:");
            e.printStackTrace();
        }

        return list;
    }

    
    public boolean insert(String maGV, String hoTen, String email,
                          String maKhoa, String tenDangNhap) {

        String sql = """
            INSERT INTO GiangVien (MaGV, HoTen, Email, MaKhoa, TenDangNhap, TrangThai)
            VALUES (?, ?, ?, ?, ?, N'Đang công tác')
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maGV.trim());
            ps.setString(2, hoTen.trim());
            ps.setString(3, email.trim());
            ps.setString(4, maKhoa.trim());
            ps.setString(5, tenDangNhap == null || tenDangNhap.isBlank()
                    ? null
                    : tenDangNhap.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi insert GiangVien:");
            e.printStackTrace(); // hiện rõ FK, PK, NOT NULL
        }

        return false;
    }

   
    public boolean update(String maGV, String hoTen, String email,
                          String maKhoa, String tenDangNhap) {

        String sql = """
            UPDATE GiangVien
            SET HoTen = ?, Email = ?, MaKhoa = ?, TenDangNhap = ?
            WHERE MaGV = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hoTen.trim());
            ps.setString(2, email.trim());
            ps.setString(3, maKhoa.trim());
            ps.setString(4, tenDangNhap == null || tenDangNhap.isBlank()
                    ? null
                    : tenDangNhap.trim());
            ps.setString(5, maGV.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi update GiangVien:");
            e.printStackTrace();
        }

        return false;
    }

    
    public boolean softDelete(String maGV) {

        String sql = """
            UPDATE GiangVien
            SET TrangThai = N'Nghỉ'
            WHERE MaGV = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maGV.trim());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi softDelete GiangVien:");
            e.printStackTrace();
        }

        return false;
    }

   
    public List<String> getAllMaKhoa() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT MaKhoa FROM Khoa";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaKhoa"));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAllMaKhoa:");
            e.printStackTrace();
        }

        return list;
    }
}
