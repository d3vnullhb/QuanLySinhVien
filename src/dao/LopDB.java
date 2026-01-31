package dao;

import model.Lop;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LopDB {

    /* ================= LỚP ĐANG HOẠT ĐỘNG ================= */
    public List<Lop> getAllActive() {
        List<Lop> list = new ArrayList<>();
        String sql = """
            SELECT MaLop, TenLop, MaKhoa
            FROM Lop
            WHERE TrangThai = 1
            ORDER BY MaLop
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lop lop = new Lop();
                lop.setMaLop(rs.getString("MaLop"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setMaKhoa(rs.getString("MaKhoa"));
                list.add(lop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= LỚP ĐÃ XÓA ================= */
    public List<Lop> getAllDeleted() {
        List<Lop> list = new ArrayList<>();
        String sql = """
            SELECT MaLop, TenLop, MaKhoa
            FROM Lop
            WHERE TrangThai = 0
            ORDER BY MaLop
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lop lop = new Lop();
                lop.setMaLop(rs.getString("MaLop"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setMaKhoa(rs.getString("MaKhoa"));
                list.add(lop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= THÊM ================= */
    public boolean insert(Lop lop) {
        String checkSql = "SELECT TrangThai FROM Lop WHERE MaLop = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setString(1, lop.getMaLop());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                if (rs.getInt("TrangThai") == 0) {
                    return restore(lop.getMaLop()); // tự khôi phục
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sql = """
            INSERT INTO Lop (MaLop, TenLop, MaKhoa, TrangThai)
            VALUES (?, ?, ?, 1)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getTenLop());
            ps.setString(3, lop.getMaKhoa());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= CẬP NHẬT ================= */
    public boolean update(Lop lop) {
        String sql = """
            UPDATE Lop
            SET TenLop = ?, MaKhoa = ?
            WHERE MaLop = ? AND TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lop.getTenLop());
            ps.setString(2, lop.getMaKhoa());
            ps.setString(3, lop.getMaLop());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= XÓA MỀM ================= */
    public boolean softDelete(String maLop) {
        String sql = "UPDATE Lop SET TrangThai = 0 WHERE MaLop = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= KHÔI PHỤC ================= */
    public boolean restore(String maLop) {
        String sql = "UPDATE Lop SET TrangThai = 1 WHERE MaLop = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
