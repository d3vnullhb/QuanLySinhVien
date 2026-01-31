package ui.panels;

import dao.ThoiKhoaBieuDB;
import model.ThoiKhoaBieu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
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
                new Object[]{
                        "STT","ID","Lớp","Môn","GV",
                        "Ngày","Thứ","Tiết BD","Số tiết",
                        "Phòng","HK","Năm học"
                }, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false; 
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Ẩn cột ID
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

    /* ================= LOAD DATA ================= */
    private void loadData() {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<ThoiKhoaBieu> list = db.getAllActive();
        int stt = 1;

        for (ThoiKhoaBieu t : list) {
            model.addRow(new Object[]{
                    stt++,
                    t.getMaTKB(),
                    t.getMaLop(),
                    t.getTenMon(),
                    t.getTenGV(),
                    sdf.format(t.getNgayHoc()),
                    t.getThu(),
                    t.getTietBatDau(),
                    t.getSoTiet(),
                    t.getPhong(),
                    t.getHocKy(),
                    t.getNamHoc()
            });
        }
    }

    /* ================= DELETE ================= */
    private void deleteTKB() {
        int[] rows = table.getSelectedRows();

        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Chọn ít nhất 1 dòng để xóa");
            return;
        }

        if (JOptionPane.showConfirmDialog(
                this,
                "Xóa " + rows.length + " dòng thời khóa biểu?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        for (int r : rows) {
            int id = Integer.parseInt(table.getValueAt(r, 1).toString());
            db.delete(id);
        }

        loadData();
    }

    /* ================= IMPORT CSV ================= */
    private void importCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) { skipHeader = false; continue; }

                String[] a = line.split(",");

                if (a.length < 10) continue; // ⚠ validate CSV

                db.insert(
                        a[0],                          // MaLop
                        a[1],                          // MaMon
                        a[2],                          // MaGV
                        java.sql.Date.valueOf(a[3]),  // NgayHoc (yyyy-MM-dd)
                        Integer.parseInt(a[4]),       // Thu
                        Integer.parseInt(a[5]),       // TietBatDau
                        Integer.parseInt(a[6]),       // SoTiet
                        a[7],                          // Phong
                        Integer.parseInt(a[8]),       // HocKy
                        a[9]                           // NamHoc
                );
            }

            JOptionPane.showMessageDialog(this, "Import CSV thành công!");
            loadData();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Import thất bại:\n" + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
