package dao;

import model.Diem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiemDB {

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
}
