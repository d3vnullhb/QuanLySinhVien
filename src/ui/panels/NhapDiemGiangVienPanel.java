package ui.panels;


import dao.GiangVienDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NhapDiemGiangVienPanel extends JPanel {

    private String maGV;

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cboMon;

    private JTextField txtMaSV, txtHoTen, txtLop, txtCC, txtGK, txtCK;

    private GiangVienDB dao = new GiangVienDB();

    public NhapDiemGiangVienPanel(String maGV) {
        this.maGV = maGV;
        setLayout(new BorderLayout());
        initUI();
        loadMon();
    }

    private void initUI() {

        // ====== TOP ======
        JPanel top = new JPanel();
        top.add(new JLabel("Môn học: "));
        cboMon = new JComboBox<>();
        JButton btnLoad = new JButton("Tải danh sách");
        top.add(cboMon);
        top.add(btnLoad);

        // ====== TABLE ======
        model = new DefaultTableModel(
                new String[]{"Mã SV","Họ tên","Lớp","CC","GK","CK","Tổng","Chữ","KQ"}, 0
        );
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        // ====== FORM ======
        JPanel form = new JPanel(new GridLayout(3,4,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin điểm"));

        txtMaSV = new JTextField(); txtMaSV.setEnabled(false);
        txtHoTen = new JTextField(); txtHoTen.setEnabled(false);
        txtLop = new JTextField(); txtLop.setEnabled(false);
        txtCC = new JTextField();
        txtGK = new JTextField();
        txtCK = new JTextField();

        form.add(new JLabel("Mã SV")); form.add(txtMaSV);
        form.add(new JLabel("Họ tên")); form.add(txtHoTen);
        form.add(new JLabel("Lớp")); form.add(txtLop);
        form.add(new JLabel("Chuyên cần")); form.add(txtCC);
        form.add(new JLabel("Giữa kỳ")); form.add(txtGK);
        form.add(new JLabel("Cuối kỳ")); form.add(txtCK);

        // ====== BUTTON ======
        JPanel buttons = new JPanel();

        JButton btnThem = new JButton("Thêm / Lưu");
        JButton btnXoa = new JButton("Xóa");
        JButton btnMoi = new JButton("Làm mới");

        buttons.add(btnThem);
        buttons.add(btnXoa);
        buttons.add(btnMoi);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);

        // ====== ADD ======
        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // ====== EVENT ======
        btnLoad.addActionListener(e -> loadSinhVien());
        btnThem.addActionListener(e -> luuDiem());
        btnMoi.addActionListener(e -> clearForm());
        btnXoa.addActionListener(e -> xoaDong());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadMon() {
        cboMon.removeAllItems();
        for (String m : dao.getMonGiangDay(maGV)) {
            cboMon.addItem(m);
        }
    }

    private void loadSinhVien() {
        model.setRowCount(0);
        String maMon = (String) cboMon.getSelectedItem();
        if (maMon == null) return;

        for (Object[] row : dao.getSinhVienNhapDiem(maGV, maMon)) {
            model.addRow(row);
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        txtMaSV.setText(model.getValueAt(row,0).toString());
        txtHoTen.setText(model.getValueAt(row,1).toString());
        txtLop.setText(model.getValueAt(row,2).toString());
        txtCC.setText(model.getValueAt(row,3).toString());
        txtGK.setText(model.getValueAt(row,4).toString());
        txtCK.setText(model.getValueAt(row,5).toString());
    }

    private void luuDiem() {
        try {
            String maSV = txtMaSV.getText();
            String maMon = (String) cboMon.getSelectedItem();

            float cc = Float.parseFloat(txtCC.getText());
            float gk = Float.parseFloat(txtGK.getText());
            float ck = Float.parseFloat(txtCK.getText());

            if (dao.updateDiem(maSV, maMon, cc, gk, ck)) {
                JOptionPane.showMessageDialog(this, "Lưu điểm thành công");
                loadSinhVien();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Điểm không hợp lệ!");
        }
    }

    private void xoaDong() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        model.removeRow(row);
        clearForm();
    }

    private void clearForm() {
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtLop.setText("");
        txtCC.setText("");
        txtGK.setText("");
        txtCK.setText("");
        table.clearSelection();
    }
}