package ui.panels;

import dao.DiemDB;
import model.Diem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyDiemPanel extends JPanel {

    private JComboBox<String> cboLop, cboMon, cboNamHoc;
    private JComboBox<Integer> cboHocKy;

    private JTable table;
    private DefaultTableModel model;

    private JButton btnLoad, btnSave;

    private DiemDB diemDB = new DiemDB();

    public QuanLyDiemPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        /* ================= FILTER ================= */
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setBorder(BorderFactory.createTitledBorder("Chọn lớp - môn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboLop = new JComboBox<>();
        cboMon = new JComboBox<>();
        cboHocKy = new JComboBox<>(new Integer[]{1, 2, 3});
        cboNamHoc = new JComboBox<>();

        loadLop();
        loadMon();
        loadNamHoc();

        gbc.gridx = 0; gbc.gridy = 0; filter.add(new JLabel("Lớp"), gbc);
        gbc.gridx = 1; filter.add(cboLop, gbc);

        gbc.gridx = 2; filter.add(new JLabel("Môn"), gbc);
        gbc.gridx = 3; filter.add(cboMon, gbc);

        gbc.gridx = 0; gbc.gridy = 1; filter.add(new JLabel("Học kỳ"), gbc);
        gbc.gridx = 1; filter.add(cboHocKy, gbc);

        gbc.gridx = 2; filter.add(new JLabel("Năm học"), gbc);
        gbc.gridx = 3; filter.add(cboNamHoc, gbc);

        btnLoad = new JButton("Tải danh sách");
        gbc.gridx = 3; gbc.gridy = 2;
        filter.add(btnLoad, gbc);

        add(filter, BorderLayout.NORTH);

        /* ================= TABLE ================= */
        model = new DefaultTableModel(
                new Object[]{
                        "Mã SV", "Họ tên",
                        "CC", "GK", "CK",
                        "Tổng", "Điểm chữ", "Kết quả"
                }, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return col >= 2 && col <= 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setDefaultEditor(Object.class, null);
        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ================= BUTTON ================= */
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu điểm");
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);

        /* ================= EVENTS ================= */
        btnLoad.addActionListener(e -> loadDiem());
        btnSave.addActionListener(e -> saveDiem());
    }

    /* ================= LOAD DATA ================= */

    private void loadDiem() {
        model.setRowCount(0);

        String maLop = cboLop.getSelectedItem().toString();
        String maMon = cboMon.getSelectedItem().toString().split(" - ")[0];
        int hocKy = (int) cboHocKy.getSelectedItem();
        String namHoc = cboNamHoc.getSelectedItem().toString();

        List<Diem> list = diemDB.getByLopMon(maLop, maMon, hocKy, namHoc);

        for (Diem d : list) {
            model.addRow(new Object[]{
                    d.getMaSV(),
                    d.getHoTen(),
                    d.getDiemChuyenCan(),
                    d.getDiemGiuaKy(),
                    d.getDiemCuoiKy(),
                    d.getDiemTong(),
                    d.getDiemChu(),
                    d.getKetQua()
            });
        }
    }

    private void saveDiem() {
        String maMon = cboMon.getSelectedItem().toString().split(" - ")[0];
        int hocKy = (int) cboHocKy.getSelectedItem();
        String namHoc = cboNamHoc.getSelectedItem().toString();

        for (int i = 0; i < model.getRowCount(); i++) {
            Diem d = new Diem();
            d.setMaSV(model.getValueAt(i, 0).toString());
            d.setMaMon(maMon);
            d.setHocKy(hocKy);
            d.setNamHoc(namHoc);

            d.setDiemChuyenCan(parse(model.getValueAt(i, 2)));
            d.setDiemGiuaKy(parse(model.getValueAt(i, 3)));
            d.setDiemCuoiKy(parse(model.getValueAt(i, 4)));

            diemDB.updateDiem(d);
        }

        JOptionPane.showMessageDialog(this, "Lưu điểm thành công");
        loadDiem();
    }

    private double parse(Object o) {
        if (o == null || o.toString().isBlank()) return 0;
        return Double.parseDouble(o.toString());
    }

    /* ================= COMBO LOAD ================= */

    private void loadLop() {
        cboLop.removeAllItems();
        diemDB.getAllLop().forEach(cboLop::addItem);
    }

    private void loadMon() {
        cboMon.removeAllItems();
        diemDB.getAllMon().forEach(cboMon::addItem);
    }

    private void loadNamHoc() {
        cboNamHoc.removeAllItems();
        diemDB.getAllNamHoc().forEach(cboNamHoc::addItem);
    }
}
