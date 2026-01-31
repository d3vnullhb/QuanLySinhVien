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
            SELECT tkb.MaTKB, tkb.MaLop, tkb.MaMon, tkb.MaGV,
                   mh.TenMon, gv.HoTen,
                   tkb.NgayHoc, tkb.Thu, tkb.TietBatDau, tkb.SoTiet,
                   tkb.Phong, tkb.HocKy, tkb.NamHoc
            FROM ThoiKhoaBieu tkb
            JOIN MonHoc mh ON tkb.MaMon = mh.MaMon
            JOIN GiangVien gv ON tkb.MaGV = gv.MaGV
            WHERE tkb.TrangThai = 1
            ORDER BY tkb.NgayHoc, tkb.Thu, tkb.TietBatDau
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ThoiKhoaBieu t = new ThoiKhoaBieu();
                t.setMaTKB(rs.getInt("MaTKB"));
                t.setMaLop(rs.getString("MaLop"));
                t.setMaMon(rs.getString("MaMon"));
                t.setMaGV(rs.getString("MaGV"));
                t.setTenMon(rs.getString("TenMon"));
                t.setTenGV(rs.getString("HoTen"));
                t.setNgayHoc(rs.getDate("NgayHoc"));
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

    private boolean isConflict(
            Connection con,
            String maLop, String maGV, String phong,
            Date ngayHoc, int tietBD, int soTiet, Integer ignoreId
    ) throws Exception {

        String sql = """
            SELECT COUNT(*) FROM ThoiKhoaBieu
            WHERE TrangThai = 1
              AND NgayHoc = ?
              AND (MaLop = ? OR MaGV = ? OR Phong = ?)
              AND (
                    TietBatDau < ? + ?
                AND ? < TietBatDau + SoTiet
              )
        """ + (ignoreId != null ? " AND MaTKB <> ?" : "");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setDate(i++, ngayHoc);
            ps.setString(i++, maLop);
            ps.setString(i++, maGV);
            ps.setString(i++, phong);
            ps.setInt(i++, tietBD);
            ps.setInt(i++, soTiet);
            ps.setInt(i++, tietBD);
            if (ignoreId != null) ps.setInt(i, ignoreId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void insert(
            String maLop, String maMon, String maGV,
            Date ngayHoc, int thu, int tietBatDau,
            int soTiet, String phong, int hocKy, String namHoc
    ) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            if (isConflict(con, maLop, maGV, phong, ngayHoc, tietBatDau, soTiet, null))
                throw new Exception("Trùng lịch");

            String sql = """
                INSERT INTO ThoiKhoaBieu
                (MaLop, MaMon, MaGV, NgayHoc, Thu, TietBatDau, SoTiet, Phong, HocKy, NamHoc, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
            """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maLop);
                ps.setString(2, maMon);
                ps.setString(3, maGV);
                ps.setDate(4, ngayHoc);
                ps.setInt(5, thu);
                ps.setInt(6, tietBatDau);
                ps.setInt(7, soTiet);
                ps.setString(8, phong);
                ps.setInt(9, hocKy);
                ps.setString(10, namHoc);
                ps.executeUpdate();
            }
        }
    }

    public void update(ThoiKhoaBieu t) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            if (isConflict(con,
                    t.getMaLop(), t.getMaGV(), t.getPhong(),
                    t.getNgayHoc(), t.getTietBatDau(), t.getSoTiet(), t.getMaTKB()
            ))
                throw new Exception("Trùng lịch");

            String sql = """
                UPDATE ThoiKhoaBieu
                SET MaLop=?, MaMon=?, MaGV=?, NgayHoc=?, Thu=?,
                    TietBatDau=?, SoTiet=?, Phong=?, HocKy=?, NamHoc=?
                WHERE MaTKB=?
            """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, t.getMaLop());
                ps.setString(2, t.getMaMon());
                ps.setString(3, t.getMaGV());
                ps.setDate(4, t.getNgayHoc());
                ps.setInt(5, t.getThu());
                ps.setInt(6, t.getTietBatDau());
                ps.setInt(7, t.getSoTiet());
                ps.setString(8, t.getPhong());
                ps.setInt(9, t.getHocKy());
                ps.setString(10, t.getNamHoc());
                ps.setInt(11, t.getMaTKB());
                ps.executeUpdate();
            }
        }
    }

    public void softDelete(int maTKB) throws Exception {
        String sql = "UPDATE ThoiKhoaBieu SET TrangThai = 0 WHERE MaTKB = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maTKB);
            ps.executeUpdate();
        }
    }

    public void restore(int maTKB) throws Exception {
        String sql = "UPDATE ThoiKhoaBieu SET TrangThai = 1 WHERE MaTKB = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maTKB);
            ps.executeUpdate();
        }
    }

    public void importCSV(List<String[]> rows) throws Exception {

        String sql = """
            INSERT INTO ThoiKhoaBieu
            (MaLop, MaMon, MaGV, NgayHoc, Thu, TietBatDau, SoTiet, Phong, HocKy, NamHoc, TrangThai)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (String[] a : rows) {
                    if (isConflict(con, a[0], a[2], a[7], Date.valueOf(a[3]),
                            Integer.parseInt(a[5]), Integer.parseInt(a[6]), null))
                        throw new Exception("CSV có dòng trùng lịch");

                    ps.setString(1, a[0]);
                    ps.setString(2, a[1]);
                    ps.setString(3, a[2]);
                    ps.setDate(4, Date.valueOf(a[3]));
                    ps.setInt(5, Integer.parseInt(a[4]));
                    ps.setInt(6, Integer.parseInt(a[5]));
                    ps.setInt(7, Integer.parseInt(a[6]));
                    ps.setString(8, a[7]);
                    ps.setInt(9, Integer.parseInt(a[8]));
                    ps.setString(10, a[9]);
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }
}
