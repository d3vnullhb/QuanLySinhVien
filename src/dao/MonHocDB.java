package dao;

import model.MonHoc;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonHocDB {

    public List<MonHoc> getAllActive() {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT MaMon, TenMon, SoTinChi FROM MonHoc WHERE TrangThai = 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MonHoc mh = new MonHoc();
                mh.setMaMon(rs.getString("MaMon"));
                mh.setTenMon(rs.getString("TenMon"));
                mh.setSoTinChi(rs.getInt("SoTinChi"));
                list.add(mh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(MonHoc mh) {
        String sql = "INSERT INTO MonHoc (MaMon, TenMon, SoTinChi) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mh.getMaMon());
            ps.setString(2, mh.getTenMon());
            ps.setInt(3, mh.getSoTinChi());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(MonHoc mh) {
        String sql = "UPDATE MonHoc SET TenMon = ?, SoTinChi = ? WHERE MaMon = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mh.getTenMon());
            ps.setInt(2, mh.getSoTinChi());
            ps.setString(3, mh.getMaMon());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String maMon) {
        String sql = "UPDATE MonHoc SET TrangThai = 0 WHERE MaMon = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maMon);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
