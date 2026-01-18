package ui.panels;

import dao.ThongKeDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ThongKePanel extends JPanel {

    private JComboBox<Integer> cboHocKy;
    private JComboBox<String> cboNamHoc;
    private JButton btnThongKe;

    private JTable tblLop, tblMon;
    private DefaultTableModel modelLop, modelMon;

    private ThongKeDB thongKeDB = new ThongKeDB();

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        /* ================= FILTER ================= */
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cboHocKy = new JComboBox<>(new Integer[]{1, 2, 3});
        cboNamHoc = new JComboBox<>(new String[]{"2023-2024", "2024-2025"});
        btnThongKe = new JButton("Thống kê");

        filter.add(new JLabel("Học kỳ"));
        filter.add(cboHocKy);
        filter.add(new JLabel("Năm học"));
        filter.add(cboNamHoc);
        filter.add(btnThongKe);

        add(filter, BorderLayout.NORTH);

        /* ================= TABLE LỚP ================= */
        modelLop = new DefaultTableModel(
                new Object[]{"Lớp", "Tổng SV", "Đậu", "Rớt", "Tỷ lệ (%)"}, 0
        );

        tblLop = new JTable(modelLop);
        tblLop.setRowHeight(26);

        /* ================= TABLE MÔN ================= */
        modelMon = new DefaultTableModel(
                new Object[]{"Mã môn", "Tên môn", "Số SV", "Điểm TB"}, 0
        );

        tblMon = new JTable(modelMon);
        tblMon.setRowHeight(26);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblLop),
                new JScrollPane(tblMon)
        );
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        /* ================= EVENT ================= */
        btnThongKe.addActionListener(e -> thongKe());
    }

    private void thongKe() {
        int hocKy = (int) cboHocKy.getSelectedItem();
        String namHoc = cboNamHoc.getSelectedItem().toString();

        modelLop.setRowCount(0);
        modelMon.setRowCount(0);

        thongKeDB.thongKeTheoLop(hocKy, namHoc)
                .forEach(modelLop::addRow);

        thongKeDB.thongKeTheoMon(hocKy, namHoc)
                .forEach(modelMon::addRow);
    }
}
