package dao;

import model.GiangVien;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDB {

    /* ================= LOAD GIẢNG VIÊN ================= */

    public List<GiangVien> getAllActive() {
        return getByStatus("Đang công tác");
    }

    public List<GiangVien> getAllDeleted() {
        return getByStatus("Nghỉ việc");
    }

    private List<GiangVien> getByStatus(String status) {
        List<GiangVien> list = new ArrayList<>();
        String sql = """
            SELECT MaGV, HoTen, Email, MaKhoa, TenDangNhap, TrangThai
            FROM GiangVien
            WHERE TrangThai = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= INSERT / UPDATE ================= */

    public boolean insert(String maGV, String hoTen, String email,
                          String maKhoa, String tenDangNhap) {

        String sql = """
            INSERT INTO GiangVien
            (MaGV, HoTen, Email, MaKhoa, TenDangNhap, TrangThai)
            VALUES (?, ?, ?, ?, ?, N'Đang công tác')
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ps.setString(2, hoTen);
            ps.setString(3, email);
            ps.setString(4, maKhoa);
            ps.setString(5, tenDangNhap);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(String maGV, String hoTen, String email, String maKhoa) {
        String sql = """
            UPDATE GiangVien
            SET HoTen = ?, Email = ?, MaKhoa = ?
            WHERE MaGV = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hoTen);
            ps.setString(2, email);
            ps.setString(3, maKhoa);
            ps.setString(4, maGV);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= SOFT DELETE / RESTORE ================= */

    public boolean softDelete(String maGV) {
        return updateTrangThai(maGV, "Nghỉ việc");
    }

    public boolean restore(String maGV) {
        return updateTrangThai(maGV, "Đang công tác");
    }

    private boolean updateTrangThai(String maGV, String tt) {
        String sql = "UPDATE GiangVien SET TrangThai = ? WHERE MaGV = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, tt);
            ps.setString(2, maGV);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= KHOA ================= */

    public List<String> getAllMaKhoa() {
        List<String> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT MaKhoa FROM Khoa");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(rs.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= LOGIN – GIẢNG VIÊN ================= */

    public String[] getGiangVienByTenDangNhap(String tenDN) {
        String sql = """
            SELECT MaGV, HoTen, Email, MaKhoa
            FROM GiangVien
            WHERE TenDangNhap = ?
              AND TrangThai = N'Đang công tác'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, tenDN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ================= THỜI KHÓA BIỂU ================= */

    public List<Object[]> getTKBGiangVien(String maGV) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT mh.TenMon, tkb.MaLop, tkb.NgayHoc,
                   tkb.Thu, tkb.TietBatDau, tkb.SoTiet, tkb.Phong
            FROM ThoiKhoaBieu tkb
            JOIN MonHoc mh ON tkb.MaMon = mh.MaMon
            WHERE tkb.MaGV = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getString(7)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= NHẬP ĐIỂM ================= */

    public List<String> getMonGiangDay(String maGV) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT MaMon FROM PhanCong WHERE MaGV = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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
public boolean existsMaGV(String maGV) {
    String sql = "SELECT COUNT(*) FROM GiangVien WHERE MaGV = ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, maGV);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}


public GiangVien getByMaGV(String maGV) {
    String sql = "SELECT * FROM GiangVien WHERE MaGV = ?";
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, maGV);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new GiangVien(
                rs.getString("MaGV"), rs.getString("HoTen"),
                rs.getString("Email"), rs.getString("MaKhoa"),
                rs.getString("TenDangNhap"), rs.getString("TrangThai")
            );
        }
    } catch (Exception e) { e.printStackTrace(); }
    return null;
}


public boolean update(String maGV, String hoTen, String email, String maKhoa, String tenDangNhap) {
    String sql = """
        UPDATE GiangVien 
        SET HoTen = ?, Email = ?, MaKhoa = ?, TenDangNhap = ? 
        WHERE MaGV = ?
    """;
    try (Connection c = DBConnection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, hoTen);
        ps.setString(2, email);
        ps.setString(3, maKhoa);
        ps.setString(4, tenDangNhap);
        ps.setString(5, maGV);
        return ps.executeUpdate() > 0;
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}
}
