package ui.panels;

import dao.GiangVienDB;
import dao.TaiKhoanDB;
import model.GiangVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GiangVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaGV, txtHoTen, txtEmail, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboKhoa, cboTrangThai;

    private JButton btnAdd, btnUpdate, btnDelete, btnRestore, btnClear, btnReset;

    private GiangVienDB gvDB = new GiangVienDB();
    private TaiKhoanDB tkDB = new TaiKhoanDB();

    public GiangVienPanel() {
        setLayout(new BorderLayout(5, 5));
        initUI();
        initEvents();
        loadData();
    }

    /* ================= UI ================= */

    private void initUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaGV = new JTextField(15);
        txtHoTen = new JTextField(15);
        txtEmail = new JTextField(15);
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);

        cboKhoa = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"Đang công tác", "Đã xóa"});
        loadKhoa();

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Mã GV"), gbc);
        gbc.gridx = 1; form.add(txtMaGV, gbc);
        gbc.gridx = 2; form.add(new JLabel("Họ tên"), gbc);
        gbc.gridx = 3; form.add(txtHoTen, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1; form.add(txtEmail, gbc);
        gbc.gridx = 2; form.add(new JLabel("Khoa"), gbc);
        gbc.gridx = 3; form.add(cboKhoa, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; form.add(txtUsername, gbc);
        gbc.gridx = 2; form.add(new JLabel("Mật khẩu"), gbc);
        gbc.gridx = 3; form.add(txtPassword, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRestore = new JButton("Khôi phục");
        btnReset = new JButton("Reset mật khẩu");
        btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRestore);
        btnPanel.add(btnReset);
        btnPanel.add(new JLabel("Xem:"));
        btnPanel.add(cboTrangThai);
        btnPanel.add(btnClear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new Object[]{"STT", "Mã GV", "Họ tên", "Email", "Khoa", "Username", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /* ================= EVENTS ================= */

    private void initEvents() {
        btnAdd.addActionListener(e -> addGV());
        btnUpdate.addActionListener(e -> updateGV());
        btnDelete.addActionListener(e -> deleteGV());
        btnRestore.addActionListener(e -> restoreGV());
        btnReset.addActionListener(e -> resetPassword());
        btnClear.addActionListener(e -> clearForm());
        cboTrangThai.addActionListener(e -> { clearForm(); loadData(); });

        table.getSelectionModel().addListSelectionListener(e -> {
            fillForm();
        });
    }

    /* ================= ADD ================= */

    private void addGV() {
        String ma = txtMaGV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String khoa = cboKhoa.getSelectedItem().toString();
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (ma.isEmpty() || hoTen.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            msg("Nhập đầy đủ thông tin!");
            return;
        }

        // ==== KHÔI PHỤC ====
        if (gvDB.existsMaGV(ma)) {
            GiangVien gvCu = gvDB.getByMaGV(ma);
            int c = JOptionPane.showConfirmDialog(
                    this,
                    "Giảng viên đã tồn tại (đã xóa). Khôi phục?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (c == JOptionPane.YES_OPTION) {

                if (!user.equals(gvCu.getTenDangNhap()) && tkDB.exists(user)) {
                    msg("Username đã tồn tại!");
                    return;
                }

                if (gvCu.getTenDangNhap() == null) {
                    tkDB.insert(user, pass, "GIANGVIEN");
                } else {
                    tkDB.updateUsername(gvCu.getTenDangNhap(), user);
                    tkDB.restore(user);
                    tkDB.resetPassword(user, pass);
                }

                if (gvDB.update(ma, hoTen, email, khoa, user)) {
                    gvDB.restore(ma);
                    msg("Khôi phục thành công!");
                    loadData(); clearForm();
                }
            }
            return;
        }

        // ==== THÊM MỚI ====
        if (tkDB.exists(user)) {
            msg("Username đã tồn tại!");
            return;
        }

        if (tkDB.insert(user, pass, "GIANGVIEN")) {
            if (gvDB.insert(ma, hoTen, email, khoa, user)) {
                msg("Thêm thành công!");
                loadData(); clearForm();
            }
        }
    }

    /* ================= UPDATE ================= */

    private void updateGV() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        String oldUser = model.getValueAt(r, 5).toString();
        String newUser = txtUsername.getText().trim();

        if (!newUser.equals(oldUser) && tkDB.exists(newUser)) {
            msg("Username mới đã tồn tại!");
            return;
        }

        if (gvDB.update(
                txtMaGV.getText(),
                txtHoTen.getText(),
                txtEmail.getText(),
                cboKhoa.getSelectedItem().toString(),
                newUser
        )) {
            if (!oldUser.equals(newUser)) {
                tkDB.updateUsername(oldUser, newUser);
            }
            msg("Cập nhật thành công!");
            loadData(); clearForm();
        }
    }

    /* ================= RESET PASSWORD ================= */

    private void resetPassword() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        String user = model.getValueAt(r, 5).toString();

        int c = JOptionPane.showConfirmDialog(
                this,
                "Reset mật khẩu cho tài khoản: " + user + "\nMật khẩu mới: 123456",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (c == JOptionPane.YES_OPTION) {
            tkDB.resetPassword(user, "123456");
            msg("Reset mật khẩu thành công!");
        }
    }

    /* ================= DELETE / RESTORE ================= */

    private void deleteGV() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        String ma = model.getValueAt(r, 1).toString();
        String user = model.getValueAt(r, 5).toString();

        if (confirm("Xác nhận xóa giảng viên?")) {
            gvDB.softDelete(ma);
            if (user != null) tkDB.softDelete(user);
            loadData(); clearForm();
        }
    }

    private void restoreGV() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        String ma = model.getValueAt(r, 1).toString();
        String user = model.getValueAt(r, 5).toString();

        gvDB.restore(ma);
        if (user != null) tkDB.restore(user);
        loadData(); clearForm();
    }

    /* ================= LOAD / FORM ================= */

    private void loadData() {
        model.setRowCount(0);
        int stt = 1;
        boolean active = cboTrangThai.getSelectedItem().equals("Đang công tác");

        var list = active ? gvDB.getAllActive() : gvDB.getAllDeleted();
        for (GiangVien gv : list) {
            model.addRow(new Object[]{
                    stt++, gv.getMaGV(), gv.getHoTen(), gv.getEmail(),
                    gv.getMaKhoa(), gv.getTenDangNhap(), gv.getTrangThai()
            });
        }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaGV.setText(model.getValueAt(r, 1).toString());
        txtHoTen.setText(model.getValueAt(r, 2).toString());
        txtEmail.setText(model.getValueAt(r, 3).toString());
        cboKhoa.setSelectedItem(model.getValueAt(r, 4));
        txtUsername.setText(model.getValueAt(r, 5).toString());

        txtMaGV.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEnabled(false);
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPassword.setText("");

        txtMaGV.setEditable(true);
        txtUsername.setEditable(true);
        txtPassword.setEnabled(true);

        table.clearSelection();
    }

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        for (String k : gvDB.getAllMaKhoa()) cboKhoa.addItem(k);
    }

    /* ================= UTIL ================= */

    private void msg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    private boolean confirm(String s) {
        return JOptionPane.showConfirmDialog(this, s, "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
