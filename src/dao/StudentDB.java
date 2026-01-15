package dao;

import java.sql.*;
import java.util.Vector;
import util.Session; 

public class StudentDB {
    
    // Kết nối CSDL 
    private Connection getConnection() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true";
            String user = "sa"; 
            String pass = "123456"; 
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 1. Lấy thông tin Sinh viên (MaSV, MaLop) từ TenDangNhap đang login
    public String[] getThongTinSinhVien() {
        String[] info = new String[3]; // 0: MaSV, 1: MaLop, 2: HoTen
        try (Connection conn = getConnection()) {
            // Join từ bảng SinhVien qua TaiKhoan
            String sql = "SELECT MaSV, MaLop, HoTen FROM SinhVien WHERE TenDangNhap = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
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

    // 2. Lấy Thời khóa biểu (Dựa theo MaLop của SV đó)
    public Vector<Vector<String>> getTKB(String maLop) {
        Vector<Vector<String>> data = new Vector<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT m.TenMon, g.HoTen, t.Thu, t.TietBatDau, t.SoTiet, t.Phong " +
                         "FROM ThoiKhoaBieu t " +
                         "JOIN MonHoc m ON t.MaMon = m.MaMon " +
                         "JOIN GiangVien g ON t.MaGV = g.MaGV " +
                         "WHERE t.MaLop = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maLop);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("TenMon"));
                row.add(rs.getString("HoTen"));
                row.add("Thứ " + rs.getInt("Thu"));
                row.add(rs.getInt("TietBatDau") + ""); // Tiết
                row.add(rs.getString("Phong"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    // 3. Lấy Điểm (Dựa theo MaSV)
    public Vector<Vector<String>> getDiem(String maSV) {
        Vector<Vector<String>> data = new Vector<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT m.TenMon, m.SoTinChi, d.DiemChuyenCan, d.DiemGiuaKy, d.DiemCuoiKy, d.DiemTong, d.DiemChu, d.KetQua " +
                         "FROM Diem d " +
                         "JOIN MonHoc m ON d.MaMon = m.MaMon " +
                         "WHERE d.MaSV = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("TenMon"));
                row.add(rs.getString("SoTinChi"));
                row.add(String.valueOf(rs.getObject("DiemChuyenCan"))); // getObject để xử lý null nếu có
                row.add(String.valueOf(rs.getObject("DiemGiuaKy")));
                row.add(String.valueOf(rs.getObject("DiemCuoiKy")));
                row.add(String.valueOf(rs.getObject("DiemTong")));
                row.add(rs.getString("DiemChu"));
                row.add(rs.getString("KetQua"));
                data.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
    // 4. Lấy chi tiết thông tin sinh viên (Để hiển thị trang Profile)
    public String[] getStudentProfile(String maSV) {
        String[] info = new String[8];
        // 0: MaSV, 1: HoTen, 2: NgaySinh, 3: GioiTinh, 4: SDT, 5: DiaChi, 6: TenLop, 7: TrangThai
        try (Connection conn = getConnection()) {
            // Join bảng Lop để lấy TenLop cho đẹp thay vì MaLop
            String sql = "SELECT sv.MaSV, sv.HoTen, sv.NgaySinh, sv.GioiTinh, sv.SoDienThoai, sv.DiaChi, l.TenLop, sv.TrangThai " +
                         "FROM SinhVien sv " +
                         "JOIN Lop l ON sv.MaLop = l.MaLop " +
                         "WHERE sv.MaSV = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info[0] = rs.getString("MaSV");
                info[1] = rs.getString("HoTen");
                info[2] = rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toString() : "Chưa cập nhật";
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