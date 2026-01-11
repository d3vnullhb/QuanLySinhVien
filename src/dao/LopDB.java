package dao;

import model.Lop;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LopDB {

    public List<Lop> getAll() {
        List<Lop> list = new ArrayList<>();

        String sql = """
            SELECT l.MaLop, l.TenLop, k.MaKhoa, k.TenKhoa
            FROM Lop l
            JOIN Khoa k ON l.MaKhoa = k.MaKhoa
        """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Lop lop = new Lop();
                lop.setMaLop(rs.getString("MaLop"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setMaKhoa(rs.getString("MaKhoa"));
                lop.setTenKhoa(rs.getString("TenKhoa"));

                list.add(lop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
