package ui.panels;

import dao.ThoiKhoaBieuDB;
import model.ThoiKhoaBieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ThoiKhoaBieuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ThoiKhoaBieuDB db = new ThoiKhoaBieuDB();

    public ThoiKhoaBieuPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("THỜI KHÓA BIỂU", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
           new Object[]{"STT","ID","Lớp","Môn","GV","Thứ","Tiết BD","Số tiết","Phòng","HK","Năm học"},0
        );
        table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnImport = new JButton("Import CSV");
        JButton btnDelete = new JButton("Xóa");

        bottom.add(btnImport);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        loadData();

        btnImport.addActionListener(e -> importCSV());
        btnDelete.addActionListener(e -> deleteTKB());
    }

    private void loadData() {
        model.setRowCount(0);
        List<ThoiKhoaBieu> list = db.getAllActive();
        int stt = 1;
        for (ThoiKhoaBieu t : list) {
            model.addRow(new Object[]{
                stt++,                 
                t.getMaTKB(),           
                t.getMaLop(),
                t.getTenMon(),
                t.getTenGV(),
                t.getThu(),
                t.getTietBatDau(),
                t.getSoTiet(),
                t.getPhong(),
                t.getHocKy(),
                t.getNamHoc()
    });
    }
}

    private void deleteTKB() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) table.getValueAt(row, 1);       
        if (db.softDelete(id)) loadData();
    }

    private void importCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean skip = true;

            while ((line = br.readLine()) != null) {
                if (skip) { skip = false; continue; }
                String[] a = line.split(",");

                db.insert(
                    a[0], a[1], a[2],
                    Integer.parseInt(a[3]),
                    Integer.parseInt(a[4]),
                    Integer.parseInt(a[5]),
                    a[6],
                    Integer.parseInt(a[7]),
                    a[8]
                );
            }
            JOptionPane.showMessageDialog(this, "Import CSV thành công!");
            loadData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
