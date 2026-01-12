package dao;

import model.PhanCong;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class PhanCongDB {

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
        }
        return list;
    }

    public boolean insert(String maGV, String maMon, String maLop, int hocKy, String namHoc) {
        String sql = """
            INSERT INTO PhanCong (MaGV, MaMon, MaLop, HocKy, NamHoc)
            VALUES (?, ?, ?, ?, ?)
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
                    "Phân công này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean softDelete(int maPC) {
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

    public boolean update(int maPC, String maGV, String maMon, String maLop, int hocKy, String namHoc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
