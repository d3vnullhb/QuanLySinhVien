package dao;

import util.DBConnection;
import util.Session;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class StudentDB {

    /* ================= 1. LẤY THÔNG TIN SINH VIÊN ================= */
    public String[] getThongTinSinhVien() {
        String[] info = new String[3]; // MaSV, MaLop, HoTen

        String sql = "SELECT MaSV, MaLop, HoTen FROM SinhVien WHERE TenDangNhap = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Session.currentUser.getTenDangNhap());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                info[0] = rs.getString("MaSV");
                info[1] = rs.getString("MaLop");
                info[2] = rs.getString("HoTen");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    /* ================= 2. THỜI KHÓA BIỂU ================= */
    public Vector<Vector<String>> getTKB(String maLop) {
        Vector<Vector<String>> data = new Vector<>();

        String sql = """
            SELECT m.TenMon, g.HoTen, t.NgayHoc, t.Thu, 
                   t.TietBatDau, t.SoTiet, t.Phong
            FROM ThoiKhoaBieu t
            JOIN MonHoc m ON t.MaMon = m.MaMon
            JOIN GiangVien g ON t.MaGV = g.MaGV
            WHERE t.MaLop = ?
            ORDER BY t.NgayHoc, t.TietBatDau
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLop);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                Vector<String> row = new Vector<>();

                row.add(rs.getString("TenMon"));
                row.add(rs.getString("HoTen"));

                Date ngayHoc = rs.getDate("NgayHoc");
                row.add(ngayHoc != null ? df.format(ngayHoc) : "");

                row.add("Thứ " + rs.getInt("Thu"));
                row.add(String.valueOf(rs.getInt("TietBatDau")));
                row.add(rs.getString("Phong"));

                data.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /* ================= 3. ĐIỂM SINH VIÊN ================= */
    public Vector<Vector<String>> getDiem(String maSV) {
        Vector<Vector<String>> data = new Vector<>();

        String sql = """
            SELECT m.TenMon, m.SoTinChi,
                   d.DiemChuyenCan, d.DiemGiuaKy, d.DiemCuoiKy,
                   d.DiemTong, d.DiemChu, d.KetQua
            FROM Diem d
            JOIN MonHoc m ON d.MaMon = m.MaMon
            WHERE d.MaSV = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vector<String> row = new Vector<>();

                row.add(rs.getString("TenMon"));
                row.add(rs.getString("SoTinChi"));
                row.add(String.valueOf(rs.getObject("DiemChuyenCan")));
                row.add(String.valueOf(rs.getObject("DiemGiuaKy")));
                row.add(String.valueOf(rs.getObject("DiemCuoiKy")));
                row.add(String.valueOf(rs.getObject("DiemTong")));
                row.add(rs.getString("DiemChu"));
                row.add(rs.getString("KetQua"));

                data.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /* ================= 4. PROFILE SINH VIÊN ================= */
    public String[] getStudentProfile(String maSV) {
        String[] info = new String[8];
        // MaSV, HoTen, NgaySinh, GioiTinh, SDT, DiaChi, TenLop, TrangThai

        String sql = """
            SELECT sv.MaSV, sv.HoTen, sv.NgaySinh, sv.GioiTinh,
                   sv.SoDienThoai, sv.DiaChi, l.TenLop, sv.TrangThai
            FROM SinhVien sv
            JOIN Lop l ON sv.MaLop = l.MaLop
            WHERE sv.MaSV = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                info[0] = rs.getString("MaSV");
                info[1] = rs.getString("HoTen");

                Date d = rs.getDate("NgaySinh");
                info[2] = d != null ? d.toString() : "Chưa cập nhật";

                info[3] = rs.getString("GioiTinh");
                info[4] = rs.getString("SoDienThoai");
                info[5] = rs.getString("DiaChi");
                info[6] = rs.getString("TenLop");
                info[7] = rs.getString("TrangThai");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }
}
