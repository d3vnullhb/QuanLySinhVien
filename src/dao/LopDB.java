package dao;

import model.Lop;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LopDB {

    public List<Lop> getAllActive() {
        List<Lop> list = new ArrayList<>();
        String sql = """
            SELECT l.MaLop, l.TenLop, k.TenKhoa
            FROM Lop l
            JOIN Khoa k ON l.MaKhoa = k.MaKhoa
            WHERE l.TrangThai = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Lop lop = new Lop();
                lop.setMaLop(rs.getString("MaLop"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setTenKhoa(rs.getString("TenKhoa"));
                list.add(lop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(Lop lop) {
        String sql = "INSERT INTO Lop (MaLop, TenLop, MaKhoa) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lop.getMaLop());
            ps.setString(2, lop.getTenLop());
            ps.setString(3, lop.getMaKhoa());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Lop lop) {
        String sql = "UPDATE Lop SET TenLop = ?, MaKhoa = ? WHERE MaLop = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lop.getTenLop());
            ps.setString(2, lop.getMaKhoa());
            ps.setString(3, lop.getMaLop());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

        public boolean Delete(String maLop) {

         String sql = "DELETE FROM Lop WHERE MaLop = ?";

         try (Connection con = DBConnection.getConnection();
              PreparedStatement ps = con.prepareStatement(sql)) {

             ps.setString(1, maLop.trim());
             return ps.executeUpdate() > 0;

         } catch (Exception e) {
             e.printStackTrace();
         }
         return false;
     }

}
