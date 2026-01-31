package ui.panels;

import dao.GiangVienDB;
import model.GiangVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GiangVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaGV, txtHoTen, txtEmail, txtUsername;
    private JComboBox<String> cboKhoa, cboTrangThai;

    private JButton btnAdd, btnUpdate, btnDelete, btnRestore, btnClear;

    private GiangVienDB gvDB = new GiangVienDB();

    public GiangVienPanel() {
        setLayout(new BorderLayout(5,5));

        /* ===== FORM ===== */
        JPanel form = new JPanel(new GridLayout(3,4,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));

        txtMaGV = new JTextField();
        txtHoTen = new JTextField();
        txtEmail = new JTextField();
        txtUsername = new JTextField();
        txtUsername.setEnabled(false);

        cboKhoa = new JComboBox<>();
        loadKhoa();

        form.add(new JLabel("Mã GV"));
        form.add(txtMaGV);
        form.add(new JLabel("Họ tên"));
        form.add(txtHoTen);

        form.add(new JLabel("Email"));
        form.add(txtEmail);
        form.add(new JLabel("Khoa"));
        form.add(cboKhoa);

        form.add(new JLabel("Username"));
        form.add(txtUsername);

        /* ===== BUTTON ===== */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRestore = new JButton("Khôi phục");
        btnClear = new JButton("Làm mới");

        cboTrangThai = new JComboBox<>(new String[]{
                "Đang công tác",
                "Đã xóa"
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRestore);
        btnPanel.add(btnClear);
        btnPanel.add(new JLabel("Xem:"));
        btnPanel.add(cboTrangThai);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        /* ===== TABLE ===== */
        model = new DefaultTableModel(
                new Object[]{"STT","Mã GV","Họ tên","Email","Khoa","Username","Trạng thái"},0
        ) {
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        /* ===== EVENTS ===== */
        cboTrangThai.addActionListener(e -> {
            clearForm();
            loadData();
        });

        btnDelete.addActionListener(e -> deleteGV());
        btnRestore.addActionListener(e -> restoreGV());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    /* ================= LOGIC ================= */

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        for (String k : gvDB.getAllMaKhoa())
            cboKhoa.addItem(k);
    }

    private void loadData() {
        model.setRowCount(0);
        int stt = 1;

        boolean active = cboTrangThai.getSelectedItem().equals("Đang công tác");
        var list = active ? gvDB.getAllActive() : gvDB.getAllDeleted();

        for (GiangVien gv : list) {
            model.addRow(new Object[]{
                    stt++,
                    gv.getMaGV(),
                    gv.getHoTen(),
                    gv.getEmail(),
                    gv.getMaKhoa(),
                    gv.getTenDangNhap(),
                    gv.getTrangThai()
            });
        }

        btnRestore.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void deleteGV() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        if (JOptionPane.showConfirmDialog(
                this,
                "Chuyển giảng viên sang Nghỉ việc?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        gvDB.softDelete(model.getValueAt(r,1).toString());
        loadData();
        clearForm();
    }

    private void restoreGV() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn giảng viên cần khôi phục");
            return;
        }

        if (!cboTrangThai.getSelectedItem().equals("Đã xóa")) {
            JOptionPane.showMessageDialog(this, "Chỉ khôi phục giảng viên đã xóa");
            return;
        }

        gvDB.restore(model.getValueAt(r,1).toString());
        JOptionPane.showMessageDialog(this, "Khôi phục thành công!");
        loadData();
        clearForm();
    }


    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaGV.setText(model.getValueAt(r,1).toString());
        txtHoTen.setText(model.getValueAt(r,2).toString());
        txtEmail.setText(model.getValueAt(r,3).toString());
        cboKhoa.setSelectedItem(model.getValueAt(r,4));
        txtUsername.setText(
                model.getValueAt(r,5) == null ? "" : model.getValueAt(r,5).toString()
        );

        txtMaGV.setEditable(false);
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtMaGV.setEditable(true);
        table.clearSelection();
    }
}
