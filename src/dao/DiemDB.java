package dao;

import model.Diem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DiemDB {

    /* ================= LẤY DS ĐIỂM THEO LỚP – MÔN ================= */
    public List<Diem> getByLopMon(
            String maLop, String maMon, int hocKy, String namHoc) {

        List<Diem> list = new ArrayList<>();

        String sql = """
            SELECT d.MaSV, sv.HoTen,
                   d.DiemChuyenCan, d.DiemGiuaKy, d.DiemCuoiKy,
                   d.DiemTong, d.DiemChu, d.KetQua
            FROM Diem d
            JOIN SinhVien sv ON d.MaSV = sv.MaSV
            WHERE sv.MaLop = ?
              AND d.MaMon = ?
              AND d.HocKy = ?
              AND d.NamHoc = ?
              AND d.LanThi = 1
            ORDER BY sv.MaSV
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLop);
            ps.setString(2, maMon);
            ps.setInt(3, hocKy);
            ps.setString(4, namHoc);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Diem d = new Diem();
                d.setMaSV(rs.getString("MaSV"));
                d.setHoTen(rs.getString("HoTen"));
                d.setDiemChuyenCan(rs.getDouble("DiemChuyenCan"));
                d.setDiemGiuaKy(rs.getDouble("DiemGiuaKy"));
                d.setDiemCuoiKy(rs.getDouble("DiemCuoiKy"));
                d.setDiemTong(rs.getDouble("DiemTong"));
                d.setDiemChu(rs.getString("DiemChu"));
                d.setKetQua(rs.getString("KetQua"));
                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= CẬP NHẬT ĐIỂM ================= */
    public boolean updateDiem(Diem d) {

        String sql = """
            UPDATE Diem
            SET DiemChuyenCan = ?,
                DiemGiuaKy = ?,
                DiemCuoiKy = ?
            WHERE MaSV = ? AND MaMon = ?
              AND HocKy = ? AND NamHoc = ? AND LanThi = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, d.getDiemChuyenCan());
            ps.setDouble(2, d.getDiemGiuaKy());
            ps.setDouble(3, d.getDiemCuoiKy());
            ps.setString(4, d.getMaSV());
            ps.setString(5, d.getMaMon());
            ps.setInt(6, d.getHocKy());
            ps.setString(7, d.getNamHoc());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ================= LẤY DS NĂM HỌC ================= */
    public List<String> getAllNamHoc() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT NamHoc FROM Diem ORDER BY NamHoc DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= LẤY DS LỚP ================= */
    public List<String> getAllLop() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT MaLop FROM Lop WHERE TrangThai = 1 ORDER BY MaLop";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaLop"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= LẤY DS MÔN ================= */
    public List<String> getAllMon() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT MaMon, TenMon FROM MonHoc WHERE TrangThai = 1 ORDER BY TenMon";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaMon") + " - " + rs.getString("TenMon"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
