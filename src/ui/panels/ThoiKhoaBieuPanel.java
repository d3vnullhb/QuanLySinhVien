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
import java.text.SimpleDateFormat;


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
        new Object[]{"STT","ID","Lớp","Môn","GV","Ngày","Thứ","Tiết BD","Số tiết","Phòng","HK","Năm học"},0
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
        private void deleteTKB() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            if (JOptionPane.showConfirmDialog(
                    this, "Xóa thời khóa biểu?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) return;

            int id = Integer.parseInt(table.getValueAt(row, 1).toString());

            if (db.delete(id)) {
                loadData();
            }
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
                a[0],                         
                a[1],                        
                a[2],                         
                java.sql.Date.valueOf(a[3]),  
                Integer.parseInt(a[4]),       
                Integer.parseInt(a[5]),      
                Integer.parseInt(a[6]),       
                a[7],                         
                Integer.parseInt(a[8]),      
                a[9]                         
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
