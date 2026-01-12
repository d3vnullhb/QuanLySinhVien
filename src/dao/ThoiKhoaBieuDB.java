package dao;

import model.ThoiKhoaBieu;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThoiKhoaBieuDB {

    public List<ThoiKhoaBieu> getAllActive() {
        List<ThoiKhoaBieu> list = new ArrayList<>();
        String sql = """
            SELECT tkb.MaTKB, tkb.MaLop, mh.TenMon, gv.HoTen,
                   tkb.Thu, tkb.TietBatDau, tkb.SoTiet,
                   tkb.Phong, tkb.HocKy, tkb.NamHoc
            FROM ThoiKhoaBieu tkb
            JOIN MonHoc mh ON tkb.MaMon = mh.MaMon
            JOIN GiangVien gv ON tkb.MaGV = gv.MaGV
            WHERE tkb.TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThoiKhoaBieu t = new ThoiKhoaBieu();
                t.setMaTKB(rs.getInt("MaTKB"));
                t.setMaLop(rs.getString("MaLop"));
                t.setTenMon(rs.getString("TenMon"));
                t.setTenGV(rs.getString("HoTen"));
                t.setThu(rs.getInt("Thu"));
                t.setTietBatDau(rs.getInt("TietBatDau"));
                t.setSoTiet(rs.getInt("SoTiet"));
                t.setPhong(rs.getString("Phong"));
                t.setHocKy(rs.getInt("HocKy"));
                t.setNamHoc(rs.getString("NamHoc"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(String maLop, String maMon, String maGV,
                          int thu, int tietBD, int soTiet,
                          String phong, int hocKy, String namHoc) {

        String sql = """
            INSERT INTO ThoiKhoaBieu
            (MaLop, MaMon, MaGV, Thu, TietBatDau, SoTiet, Phong, HocKy, NamHoc)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maLop);
            ps.setString(2, maMon);
            ps.setString(3, maGV);
            ps.setInt(4, thu);
            ps.setInt(5, tietBD);
            ps.setInt(6, soTiet);
            ps.setString(7, phong);
            ps.setInt(8, hocKy);
            ps.setString(9, namHoc);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean softDelete(int maTKB) {
        String sql = "UPDATE ThoiKhoaBieu SET TrangThai = 0 WHERE MaTKB = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maTKB);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
