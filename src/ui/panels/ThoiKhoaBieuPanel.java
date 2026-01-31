package ui.panels;

import dao.ThoiKhoaBieuDB;
import ui.forms.TKBForm;
import model.ThoiKhoaBieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ThoiKhoaBieuPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ThoiKhoaBieuDB db = new ThoiKhoaBieuDB();

    public ThoiKhoaBieuPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("THỜI KHÓA BIỂU", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"STT","ID","Lớp","Môn","GV","Ngày","Thứ","Tiết","Số tiết","Phòng","HK","Năm"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Thêm TKB");
        JButton btnImport = new JButton("Import CSV");
        JButton btnDelete = new JButton("Xóa");

        bottom.add(btnAdd);
        bottom.add(btnImport);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            new TKBForm().setVisible(true);
            loadData();
        });

        btnImport.addActionListener(e -> importCSV());
        btnDelete.addActionListener(e -> deleteTKB());

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<ThoiKhoaBieu> list = db.getAllActive();
        int stt = 1;

        for (ThoiKhoaBieu t : list) {
            model.addRow(new Object[]{
                    stt++, t.getMaTKB(), t.getMaLop(), t.getTenMon(), t.getTenGV(),
                    sdf.format(t.getNgayHoc()), t.getThu(), t.getTietBatDau(),
                    t.getSoTiet(), t.getPhong(), t.getHocKy(), t.getNamHoc()
            });
        }
    }

    private void deleteTKB() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) return;

        if (JOptionPane.showConfirmDialog(this, "Xóa dòng đã chọn?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        for (int r : rows) {
            try {
                int id = Integer.parseInt(table.getValueAt(r,1).toString());
                db.softDelete(id);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        loadData();
    }

        private void importCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        int success = 0;
        int fail = 0;
        StringBuilder log = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
            String line;
            boolean skip = true;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                if (skip) { skip = false; continue; }

                String[] a = line.split(",");
                if (a.length < 10) {
                    fail++;
                    log.append("Dòng ").append(lineNo).append(": thiếu cột\n");
                    continue;
                }

                try {
                    db.insert(
                            a[0], a[1], a[2],
                            java.sql.Date.valueOf(a[3]),
                            Integer.parseInt(a[4]),
                            Integer.parseInt(a[5]),
                            Integer.parseInt(a[6]),
                            a[7],
                            Integer.parseInt(a[8]),
                            a[9]
                    );
                    success++;
                } catch (Exception ex) {
                    fail++;
                    log.append("Dòng ").append(lineNo)
                       .append(": ").append(ex.getMessage()).append("\n");
                }
            }

            loadData();

            JOptionPane.showMessageDialog(this,
                "Import hoàn tất!\n" +
                "Thành công: " + success + "\n" +
                "Bỏ qua: " + fail + "\n\n" +
                log.toString()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi đọc file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

}
