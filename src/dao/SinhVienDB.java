package dao;

import model.SinhVien;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDB {

    public List<SinhVien> getAllActive() {
        List<SinhVien> list = new ArrayList<>();

        String sql = """
            SELECT MaSV, HoTen, NgaySinh, GioiTinh,
                   DiaChi, SoDienThoai, MaLop, TenDangNhap
            FROM SinhVien
            WHERE TrangThai = N'Đang học'
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsMaSV(String maSV) {
        String sql = "SELECT 1 FROM SinhVien WHERE MaSV = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSV);
            return ps.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(SinhVien sv) {

        if (existsMaSV(sv.getMaSV())) return false;

        String sql = """
            INSERT INTO SinhVien
            (MaSV, HoTen, NgaySinh, GioiTinh, DiaChi,
             SoDienThoai, MaLop, TenDangNhap, TrangThai)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Đang học')
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sv.getMaSV());
            ps.setString(2, sv.getHoTen());
            ps.setDate(3, new java.sql.Date(sv.getNgaySinh().getTime()));
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getDiaChi());
            ps.setString(6, sv.getSoDienThoai());
            ps.setString(7, sv.getMaLop());
            ps.setString(8, sv.getTenDangNhap());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   public boolean update(SinhVien sv) {

    String sql = """
        UPDATE SinhVien
        SET HoTen = ?,
            NgaySinh = ?,
            GioiTinh = ?,
            DiaChi = ?,
            SoDienThoai = ?,
            MaLop = ?,
            TenDangNhap = CASE
                WHEN TenDangNhap IS NULL THEN ?
                ELSE TenDangNhap
            END
        WHERE MaSV = ?
    """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, sv.getHoTen());
        ps.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
        ps.setString(3, sv.getGioiTinh());
        ps.setString(4, sv.getDiaChi());
        ps.setString(5, sv.getSoDienThoai());
        ps.setString(6, sv.getMaLop());
        ps.setString(7, sv.getTenDangNhap()); 
        ps.setString(8, sv.getMaSV());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}


    public boolean delete(String maSV) {
        String sql = "DELETE FROM SinhVien WHERE MaSV = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private SinhVien map(ResultSet rs) throws SQLException {
        SinhVien sv = new SinhVien();
        sv.setMaSV(rs.getString("MaSV"));
        sv.setHoTen(rs.getString("HoTen"));
        sv.setNgaySinh(rs.getDate("NgaySinh"));
        sv.setGioiTinh(rs.getString("GioiTinh"));
        sv.setDiaChi(rs.getString("DiaChi"));
        sv.setSoDienThoai(rs.getString("SoDienThoai"));
        sv.setMaLop(rs.getString("MaLop"));
        sv.setTenDangNhap(rs.getString("TenDangNhap"));
        return sv;
    }
}
