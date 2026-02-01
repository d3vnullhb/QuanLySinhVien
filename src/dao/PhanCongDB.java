package dao;

import model.PhanCong;
import util.DBConnection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanCongDB {

    /* ================= GET ACTIVE ================= */
    public List<PhanCong> getAllActive() {
        List<PhanCong> list = new ArrayList<>();

        String sql = """
            SELECT pc.MaPC,
                   gv.MaGV, gv.HoTen AS TenGV,
                   mh.MaMon, mh.TenMon,
                   l.MaLop, l.TenLop,
                   pc.HocKy, pc.NamHoc
            FROM PhanCong pc
            JOIN GiangVien gv ON pc.MaGV = gv.MaGV
            JOIN MonHoc mh ON pc.MaMon = mh.MaMon
            JOIN Lop l ON pc.MaLop = l.MaLop
            WHERE pc.TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhanCong pc = new PhanCong();
                pc.setMaPC(rs.getInt("MaPC"));
                pc.setMaGV(rs.getString("MaGV"));
                pc.setTenGV(rs.getString("TenGV"));
                pc.setMaMon(rs.getString("MaMon"));
                pc.setTenMon(rs.getString("TenMon"));
                pc.setMaLop(rs.getString("MaLop"));
                pc.setTenLop(rs.getString("TenLop"));
                pc.setHocKy(rs.getInt("HocKy"));
                pc.setNamHoc(rs.getString("NamHoc"));
                list.add(pc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= GET DELETED ================= */
    public List<PhanCong> getDeleted() {
        List<PhanCong> list = new ArrayList<>();

        String sql = """
            SELECT pc.MaPC,
                   gv.HoTen AS TenGV,
                   mh.TenMon,
                   l.TenLop,
                   pc.HocKy,
                   pc.NamHoc
            FROM PhanCong pc
            JOIN GiangVien gv ON pc.MaGV = gv.MaGV
            JOIN MonHoc mh ON pc.MaMon = mh.MaMon
            JOIN Lop l ON pc.MaLop = l.MaLop
            WHERE pc.TrangThai = 0
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhanCong pc = new PhanCong();
                pc.setMaPC(rs.getInt("MaPC"));
                pc.setTenGV(rs.getString("TenGV"));
                pc.setTenMon(rs.getString("TenMon"));
                pc.setTenLop(rs.getString("TenLop"));
                pc.setHocKy(rs.getInt("HocKy"));
                pc.setNamHoc(rs.getString("NamHoc"));
                list.add(pc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= CHECK TRÙNG ================= */
    private boolean existsPhanCong(
            String maGV, String maMon, String maLop, int hocKy, String namHoc) {

        String sql = """
            SELECT COUNT(*)
            FROM PhanCong
            WHERE MaGV = ?
              AND MaMon = ?
              AND MaLop = ?
              AND HocKy = ?
              AND NamHoc = ?
              AND TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ps.setString(2, maMon);
            ps.setString(3, maLop);
            ps.setInt(4, hocKy);
            ps.setString(5, namHoc);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsPhanCongExcept(
            int maPC, String maGV, String maMon, String maLop, int hocKy, String namHoc) {

        String sql = """
            SELECT COUNT(*)
            FROM PhanCong
            WHERE MaGV = ?
              AND MaMon = ?
              AND MaLop = ?
              AND HocKy = ?
              AND NamHoc = ?
              AND TrangThai = 1
              AND MaPC <> ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ps.setString(2, maMon);
            ps.setString(3, maLop);
            ps.setInt(4, hocKy);
            ps.setString(5, namHoc);
            ps.setInt(6, maPC);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= INSERT ================= */
    public boolean insert(String maGV, String maMon, String maLop, int hocKy, String namHoc) {

        if (existsPhanCong(maGV, maMon, maLop, hocKy, namHoc)) {
            JOptionPane.showMessageDialog(null,
                    "Phân công này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = """
            INSERT INTO PhanCong (MaGV, MaMon, MaLop, HocKy, NamHoc, TrangThai)
            VALUES (?, ?, ?, ?, ?, 1)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ps.setString(2, maMon);
            ps.setString(3, maLop);
            ps.setInt(4, hocKy);
            ps.setString(5, namHoc);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Không thể thêm phân công!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    /* ================= UPDATE ================= */
    public boolean update(int maPC, String maGV, String maMon, String maLop, int hocKy, String namHoc) {

        if (existsPhanCongExcept(maPC, maGV, maMon, maLop, hocKy, namHoc)) {
            JOptionPane.showMessageDialog(null,
                    "Phân công này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = """
            UPDATE PhanCong
            SET MaGV = ?,
                MaMon = ?,
                MaLop = ?,
                HocKy = ?,
                NamHoc = ?
            WHERE MaPC = ? AND TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maGV);
            ps.setString(2, maMon);
            ps.setString(3, maLop);
            ps.setInt(4, hocKy);
            ps.setString(5, namHoc);
            ps.setInt(6, maPC);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Không thể cập nhật phân công!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    public boolean Delete(int maPC) {
        String sql = "UPDATE PhanCong SET TrangThai = 0 WHERE MaPC = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maPC);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= RESTORE ================= */
    public boolean restore(int maPC) {
        String sql = "UPDATE PhanCong SET TrangThai = 1 WHERE MaPC = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maPC);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
