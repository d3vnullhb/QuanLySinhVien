package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDB {

    /* ================= LẤY DANH SÁCH NĂM HỌC ================= */
    public List<String> getAllNamHoc() {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT NamHoc
            FROM Diem
            ORDER BY NamHoc DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("NamHoc"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= THỐNG KÊ THEO LỚP ================= */
    public List<Object[]> thongKeTheoLop(int hocKy, String namHoc) {

        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT sv.MaLop,
                   COUNT(DISTINCT sv.MaSV) AS TongSV,
                   SUM(CASE WHEN d.KetQua = N'Đạt' THEN 1 ELSE 0 END) AS Dau,
                   SUM(CASE WHEN d.KetQua = N'Học lại' THEN 1 ELSE 0 END) AS Rot,
                   CAST(
                       SUM(CASE WHEN d.KetQua = N'Đạt' THEN 1 ELSE 0 END) * 100.0
                       / COUNT(DISTINCT sv.MaSV)
                   AS FLOAT) AS TyLeDau
            FROM Diem d
            JOIN SinhVien sv ON d.MaSV = sv.MaSV
            WHERE d.HocKy = ? AND d.NamHoc = ?
            GROUP BY sv.MaLop
            ORDER BY sv.MaLop
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, hocKy);
            ps.setString(2, namHoc);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("MaLop"),
                        rs.getInt("TongSV"),
                        rs.getInt("Dau"),
                        rs.getInt("Rot"),
                        Math.round(rs.getDouble("TyLeDau") * 100.0) / 100.0
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= THỐNG KÊ THEO MÔN ================= */
    public List<Object[]> thongKeTheoMon(int hocKy, String namHoc) {

        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT d.MaMon, mh.TenMon,
                   COUNT(*) AS SoSV,
                   AVG(d.DiemTong) AS DiemTB
            FROM Diem d
            JOIN MonHoc mh ON d.MaMon = mh.MaMon
            WHERE d.HocKy = ? AND d.NamHoc = ?
            GROUP BY d.MaMon, mh.TenMon
            ORDER BY mh.TenMon
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, hocKy);
            ps.setString(2, namHoc);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("MaMon"),
                        rs.getString("TenMon"),
                        rs.getInt("SoSV"),
                        Math.round(rs.getDouble("DiemTB") * 100.0) / 100.0
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
