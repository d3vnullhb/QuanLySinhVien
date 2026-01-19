package ui.panels;

import dao.GiangVienDB;
import dao.TaiKhoanDB;
import model.GiangVien;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiangVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaGV, txtHoTen, txtEmail, txtUsername;
    private JPasswordField txtPassword, txtNewPass;
    private JComboBox<String> cboKhoa, cboTaiKhoan;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnTaoTK, btnResetMK;

    private GiangVienDB gvDB = new GiangVienDB();
    private TaiKhoanDB tkDB = new TaiKhoanDB();

    public GiangVienPanel() {
        setLayout(new BorderLayout(5,5));
        setBackground(Color.WHITE);

        Dimension small = new Dimension(130, 24);

        /* ================= FORM ================= */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,5,3,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaGV = new JTextField(); txtMaGV.setPreferredSize(small);
        txtHoTen = new JTextField(); txtHoTen.setPreferredSize(small);
        txtEmail = new JTextField(); txtEmail.setPreferredSize(small);

        cboKhoa = new JComboBox<>(); cboKhoa.setPreferredSize(small);
        cboTaiKhoan = new JComboBox<>(); cboTaiKhoan.setPreferredSize(small);

        loadKhoa();
        loadTaiKhoan();

        int r = 0;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Mã GV"), gbc);
        gbc.gridx=1; form.add(txtMaGV, gbc);
        gbc.gridx=2; form.add(new JLabel("Họ tên"), gbc);
        gbc.gridx=3; form.add(txtHoTen, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Email"), gbc);
        gbc.gridx=1; form.add(txtEmail, gbc);
        gbc.gridx=2; form.add(new JLabel("Khoa"), gbc);
        gbc.gridx=3; form.add(cboKhoa, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Tài khoản"), gbc);
        gbc.gridx=1; form.add(cboTaiKhoan, gbc);

        /* ================= TẠO TK ================= */
        JPanel tkPanel = new JPanel(new GridBagLayout());
        tkPanel.setBorder(BorderFactory.createTitledBorder("Tạo tài khoản"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(3,5,3,5);
        g2.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(); txtUsername.setPreferredSize(small);
        txtPassword = new JPasswordField(); txtPassword.setPreferredSize(small);

        g2.gridx=0; g2.gridy=0; tkPanel.add(new JLabel("Username"), g2);
        g2.gridx=1; tkPanel.add(txtUsername, g2);
        g2.gridx=0; g2.gridy=1; tkPanel.add(new JLabel("Password"), g2);
        g2.gridx=1; tkPanel.add(txtPassword, g2);

        btnTaoTK = new JButton("+ Tạo");
        g2.gridx=1; g2.gridy=2;
        tkPanel.add(btnTaoTK, g2);

        /* ================= RESET MK ================= */
        JPanel resetPanel = new JPanel(new GridBagLayout());
        resetPanel.setBorder(BorderFactory.createTitledBorder("Reset mật khẩu"));
        GridBagConstraints g3 = new GridBagConstraints();
        g3.insets = new Insets(3,5,3,5);
        g3.fill = GridBagConstraints.HORIZONTAL;

        txtNewPass = new JPasswordField(); txtNewPass.setPreferredSize(small);
        btnResetMK = new JButton("Reset");

        g3.gridx=0; g3.gridy=0; resetPanel.add(new JLabel("Mật khẩu mới"), g3);
        g3.gridx=1; resetPanel.add(txtNewPass, g3);
        g3.gridx=1; g3.gridy=1; resetPanel.add(btnResetMK, g3);

        /* ================= RIGHT ================= */
        JPanel right = new JPanel(new BorderLayout(5,5));
        right.setPreferredSize(new Dimension(260,1));
        right.add(tkPanel, BorderLayout.NORTH);
        right.add(resetPanel, BorderLayout.SOUTH);

        JPanel formWrap = new JPanel(new BorderLayout(5,5));
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(right, BorderLayout.EAST);

        /* ================= BUTTON ================= */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formWrap, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        /* ================= TABLE ================= */
        model = new DefaultTableModel(
                new Object[]{"STT","Mã GV","Họ tên","Email","Khoa","Tài khoản","Trạng thái"},0
        ){
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topPanel,
                new JScrollPane(table)
        );
        split.setResizeWeight(0.30);
        split.setDividerSize(6);

        add(split, BorderLayout.CENTER);

        loadData();

        /* ================= EVENTS ================= */
        btnAdd.addActionListener(e -> addGV());
        btnUpdate.addActionListener(e -> updateGV());
        btnDelete.addActionListener(e -> deleteGV());
        btnClear.addActionListener(e -> clearForm());
        btnTaoTK.addActionListener(e -> taoTaiKhoan());
        btnResetMK.addActionListener(e -> resetMK());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    /* ================= LOGIC ================= */

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        for (String k : gvDB.getAllMaKhoa()) cboKhoa.addItem(k);
    }

    private void loadTaiKhoan() {
        cboTaiKhoan.removeAllItems();
        for (String t : tkDB.getAllTenDangNhapGV()) cboTaiKhoan.addItem(t);
    }

    private void loadData() {
        model.setRowCount(0);
        int stt = 1;
        for (GiangVien gv : gvDB.getAll()) {
            model.addRow(new Object[]{
                    stt++, gv.getMaGV(), gv.getHoTen(),
                    gv.getEmail(), gv.getMaKhoa(),
                    gv.getTenDangNhap(), gv.getTrangThai()
            });
        }
    }

    private void addGV() {
        gvDB.insert(
                txtMaGV.getText(),
                txtHoTen.getText(),
                txtEmail.getText(),
                cboKhoa.getSelectedItem().toString(),
                null
        );
        loadData(); clearForm();
    }

    private void updateGV() {
        gvDB.update(
                txtMaGV.getText(),
                txtHoTen.getText(),
                txtEmail.getText(),
                cboKhoa.getSelectedItem().toString(),
                cboTaiKhoan.getSelectedItem()==null?null:cboTaiKhoan.getSelectedItem().toString()
        );
        loadData();
    }

    private void deleteGV() {
        if (table.getSelectedRow() < 0) return;
        gvDB.Delete(txtMaGV.getText());
        loadData(); clearForm();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaGV.setText(model.getValueAt(r,1).toString());
        txtHoTen.setText(model.getValueAt(r,2).toString());
        txtEmail.setText(model.getValueAt(r,3).toString());
        cboKhoa.setSelectedItem(model.getValueAt(r,4));
        txtUsername.setText(
                model.getValueAt(r,5)==null?"":model.getValueAt(r,5).toString()
        );
        txtMaGV.setEditable(false);
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtMaGV.setEditable(true);
        txtHoTen.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtNewPass.setText("");
        table.clearSelection();
    }

    private void taoTaiKhoan() {
        if (tkDB.insert(txtUsername.getText(),
                new String(txtPassword.getPassword()),
                "GIANGVIEN")) {

            gvDB.updateTaiKhoanGV(txtMaGV.getText(), txtUsername.getText());
            loadTaiKhoan();
            loadData();
        }
    }

    private void resetMK() {
        if (tkDB.resetPassword(txtUsername.getText(),
                new String(txtNewPass.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Reset thành công");
        }
    }
}
