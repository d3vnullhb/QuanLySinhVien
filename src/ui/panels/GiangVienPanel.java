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
    private JComboBox<String> cboKhoa;

    private GiangVienDB gvDB = new GiangVienDB();
    private TaiKhoanDB tkDB = new TaiKhoanDB();

    public GiangVienPanel() {
        setLayout(new BorderLayout(5,5));

        /* ================= FORM ================= */
        JPanel form = new JPanel(new GridLayout(3,4,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));

        txtMaGV = new JTextField();
        txtHoTen = new JTextField();
        txtEmail = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

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
        form.add(new JLabel("Password"));
        form.add(txtPassword);

        /* ================= BUTTON ================= */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        /* ================= TOP (FIX UI) ================= */
        JPanel top = new JPanel(new BorderLayout(5,5));
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        /* ================= TABLE ================= */
        model = new DefaultTableModel(
                new Object[]{"STT","Mã GV","Họ tên","Email","Khoa","Username","Trạng thái"},0
        ) {
            @Override
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);

        /* ================= ADD ================= */
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadData();

        /* ================= EVENT ================= */
        btnAdd.addActionListener(e -> addGV());
        btnUpdate.addActionListener(e -> updateGV());
        btnDelete.addActionListener(e -> deleteGV());
        btnClear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    /* ================= LOGIC ================= */

    private boolean validEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        for (String k : gvDB.getAllMaKhoa()) {
            cboKhoa.addItem(k);
        }
    }

    private void loadData() {
        model.setRowCount(0);
        int stt = 1;
        for (GiangVien gv : gvDB.getAll()) {
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
    }

    /* ================= ADD ================= */
    private void addGV() {
        String maGV = txtMaGV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (maGV.isEmpty() || hoTen.isEmpty() || email.isEmpty()
                || user.isEmpty() || pass.isEmpty()) {
            msg("Không được để trống dữ liệu");
            return;
        }

        if (!validEmail(email)) {
            msg("Email không hợp lệ");
            return;
        }

        if (gvDB.existsMaGV(maGV)) { msg("Mã GV đã tồn tại"); return; }
        if (gvDB.existsEmail(email)) { msg("Email đã tồn tại"); return; }
        if (tkDB.exists(user)) { msg("Username đã tồn tại"); return; }

        if (!tkDB.insert(user, pass, "GIANGVIEN")) {
            msg("Tạo tài khoản thất bại");
            return;
        }

        if (gvDB.insert(maGV, hoTen, email,
                cboKhoa.getSelectedItem().toString(), user)) {

            msg("Thêm giảng viên thành công");
            loadData();
            clearForm();
        }
    }

    /* ================= UPDATE ================= */
    private void updateGV() {
        if (table.getSelectedRow() < 0) {
            msg("Chọn giảng viên");
            return;
        }

        String maGV = txtMaGV.getText().trim();
        String email = txtEmail.getText().trim();

        if (!validEmail(email)) {
            msg("Email không hợp lệ");
            return;
        }

        if (gvDB.existsEmailExceptMaGV(email, maGV)) {
            msg("Email đã được dùng");
            return;
        }

        gvDB.update(
                maGV,
                txtHoTen.getText(),
                email,
                cboKhoa.getSelectedItem().toString(),
                null
        );

        msg("Cập nhật thành công");
        loadData();
    }

    /* ================= DELETE ================= */
    private void deleteGV() {
        if (table.getSelectedRow() < 0) return;

        if (JOptionPane.showConfirmDialog(
                this,
                "Xóa giảng viên?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) return;

        String user = txtUsername.getText();
        gvDB.delete(txtMaGV.getText());
        tkDB.delete(user);

        loadData();
        clearForm();
    }
    private String getValue(int row, int col) {
        Object v = model.getValueAt(row, col);
        if (v == null) return "";
        return v.toString();
    }


    /* ================= FORM ================= */
   private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaGV.setText(getValue(r, 1));
        txtHoTen.setText(getValue(r, 2));
        txtEmail.setText(getValue(r, 3));
        cboKhoa.setSelectedItem(getValue(r, 4));
        txtUsername.setText(getValue(r, 5));

        txtMaGV.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setText("");
    }


    private void clearForm() {
        txtMaGV.setText("");
        txtMaGV.setEditable(true);
        txtHoTen.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        table.clearSelection();
    }

    private void msg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }
}
