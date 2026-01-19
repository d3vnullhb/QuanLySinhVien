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

    
        public boolean Delete(String maGV) {

         String sql = "DELETE FROM GiangVien WHERE MaGV = ?";

         try (Connection c = DBConnection.getConnection();
              PreparedStatement ps = c.prepareStatement(sql)) {

             ps.setString(1, maGV.trim());
             return ps.executeUpdate() > 0;

         } catch (SQLException e) {
             System.err.println("Lỗi Delete GiangVien:");
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
    // Lấy Gv theo tk dnhap
    public String[] getGiangVienByTenDangNhap(String tenDN) {
    String[] info = new String[4];

    String sql = """
        SELECT MaGV, HoTen, Email, MaKhoa
        FROM GiangVien
        WHERE TenDangNhap = ?
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, tenDN);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            info[0] = rs.getString("MaGV");
            info[1] = rs.getString("HoTen");
            info[2] = rs.getString("Email");
            info[3] = rs.getString("MaKhoa");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return info;
}
   // TKB giảng viên (có NGÀY)
public List<Object[]> getTKBGiangVien(String maGV) {
    List<Object[]> list = new ArrayList<>();

    String sql = """
        SELECT mh.TenMon,
               tkb.MaLop,
               tkb.NgayHoc,
               tkb.Thu,
               tkb.TietBatDau,
               tkb.SoTiet,
               tkb.Phong
        FROM ThoiKhoaBieu tkb
        JOIN MonHoc mh ON tkb.MaMon = mh.MaMon
        WHERE tkb.MaGV = ? AND tkb.TrangThai = 1
        ORDER BY tkb.NgayHoc, tkb.TietBatDau
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, maGV);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Object[]{
                rs.getString("TenMon"),
                rs.getString("MaLop"),
                rs.getDate("NgayHoc"),
                rs.getInt("Thu"),
                rs.getInt("TietBatDau"),
                rs.getInt("SoTiet"),
                rs.getString("Phong")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

// lấy ds môn gv 
public List<String> getMonGiangDay(String maGV) {
    List<String> list = new ArrayList<>();

    String sql = """
        SELECT DISTINCT MaMon
        FROM PhanCong
        WHERE MaGV = ?
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, maGV);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(rs.getString(1));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
// load sv
public List<Object[]> getSinhVienNhapDiem(String maGV, String maMon) {
    List<Object[]> list = new ArrayList<>();

    String sql = """
        SELECT sv.MaSV, sv.HoTen, sv.MaLop,
               d.DiemChuyenCan, d.DiemGiuaKy, d.DiemCuoiKy,
               d.DiemTong, d.DiemChu, d.KetQua
        FROM Diem d
        JOIN SinhVien sv ON d.MaSV = sv.MaSV
        WHERE d.MaGV = ? AND d.MaMon = ?
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, maGV);
        ps.setString(2, maMon);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Object[]{
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getFloat(4),
                rs.getFloat(5),
                rs.getFloat(6),
                rs.getFloat(7),
                rs.getString(8),
                rs.getString(9)
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
// update điểm
public boolean updateDiem(String maSV, String maMon,
                          float cc, float gk, float ck) {

    String sql = """
        UPDATE Diem
        SET DiemChuyenCan = ?, DiemGiuaKy = ?, DiemCuoiKy = ?
        WHERE MaSV = ? AND MaMon = ?
    """;

    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setFloat(1, cc);
        ps.setFloat(2, gk);
        ps.setFloat(3, ck);
        ps.setString(4, maSV);
        ps.setString(5, maMon);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    public boolean updateTaiKhoanGV(String maGV, String username) {

        String sql = """
            UPDATE GiangVien
            SET TenDangNhap = ?
            WHERE MaGV = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, maGV);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi updateTaiKhoanGV:");
            e.printStackTrace();
        }

        return false;
    }

}