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

    /* ================= UI ================= */
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
                new String[]{"Mã SV","Họ tên","Lớp",
                             "CC","GK","CK","Tổng","Chữ","KQ"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        JScrollPane sp = new JScrollPane(table);

        // ====== FORM ======
        JPanel form = new JPanel(new GridLayout(3,4,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin điểm"));

        txtMaSV = new JTextField(); txtMaSV.setEnabled(false);
        txtHoTen = new JTextField(); txtHoTen.setEnabled(false);
        txtLop   = new JTextField(); txtLop.setEnabled(false);
        txtCC = new JTextField();
        txtGK = new JTextField();
        txtCK = new JTextField();

        form.add(new JLabel("Mã SV")); form.add(txtMaSV);
        form.add(new JLabel("Họ tên")); form.add(txtHoTen);
        form.add(new JLabel("Lớp"));   form.add(txtLop);
        form.add(new JLabel("Chuyên cần")); form.add(txtCC);
        form.add(new JLabel("Giữa kỳ"));    form.add(txtGK);
        form.add(new JLabel("Cuối kỳ"));    form.add(txtCK);

        // ====== BUTTON ======
        JPanel buttons = new JPanel();
        JButton btnLuu = new JButton("Lưu điểm");
        JButton btnXoa = new JButton("Bỏ chọn");
        JButton btnMoi = new JButton("Làm mới");

        buttons.add(btnLuu);
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
        btnLuu.addActionListener(e -> luuDiem());
        btnMoi.addActionListener(e -> clearForm());
        btnXoa.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    /* ================= DATA ================= */
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

    /* ================= SAVE SCORE ================= */
    private void luuDiem() {
        if (txtMaSV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn sinh viên");
            return;
        }

        String maMon = (String) cboMon.getSelectedItem();
        if (maMon == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn môn học");
            return;
        }

        try {
            float cc = Float.parseFloat(txtCC.getText());
            float gk = Float.parseFloat(txtGK.getText());
            float ck = Float.parseFloat(txtCK.getText());

            if (cc < 0 || cc > 10 || gk < 0 || gk > 10 || ck < 0 || ck > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10");
                return;
            }

           if (dao.updateDiem(
                txtMaSV.getText(),
                maMon,
                cc, gk, ck)) {


                JOptionPane.showMessageDialog(this, "Lưu điểm thành công");
                loadSinhVien();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm không hợp lệ");
        }
    }

    /* ================= CLEAR ================= */
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
