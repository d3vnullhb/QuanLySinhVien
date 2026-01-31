package ui.panels;

import dao.SinhVienDB;
import dao.TaiKhoanDB;
import model.SinhVien;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SinhVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaSV, txtHoTen, txtDiaChi, txtSDT, txtUsername;
    private JPasswordField txtPassword, txtMatKhauMoi;
    private JComboBox<String> cboGioiTinh, cboLop;
    private JSpinner spNgaySinh;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnResetMK;

    private SinhVienDB sinhVienDB = new SinhVienDB();
    private TaiKhoanDB taiKhoanDB = new TaiKhoanDB();

    public SinhVienPanel() {
        setLayout(new BorderLayout());

        Dimension small = new Dimension(130, 24);

        /* ================= FORM ================= */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSV = new JTextField(); txtMaSV.setPreferredSize(small);
        txtHoTen = new JTextField(); txtHoTen.setPreferredSize(small);
        txtDiaChi = new JTextField(); txtDiaChi.setPreferredSize(small);
        txtSDT = new JTextField(); txtSDT.setPreferredSize(small);
        txtUsername = new JTextField(); txtUsername.setPreferredSize(small);
        txtPassword = new JPasswordField(); txtPassword.setPreferredSize(small);
        txtMatKhauMoi = new JPasswordField(); txtMatKhauMoi.setPreferredSize(small);

        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));

        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboLop = new JComboBox<>();
        loadLop();

        int r = 0;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Mã SV"), gbc);
        gbc.gridx=1; form.add(txtMaSV, gbc);
        gbc.gridx=2; form.add(new JLabel("Họ tên"), gbc);
        gbc.gridx=3; form.add(txtHoTen, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Ngày sinh"), gbc);
        gbc.gridx=1; form.add(spNgaySinh, gbc);
        gbc.gridx=2; form.add(new JLabel("Giới tính"), gbc);
        gbc.gridx=3; form.add(cboGioiTinh, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Địa chỉ"), gbc);
        gbc.gridx=1; form.add(txtDiaChi, gbc);
        gbc.gridx=2; form.add(new JLabel("SĐT"), gbc);
        gbc.gridx=3; form.add(txtSDT, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Lớp"), gbc);
        gbc.gridx=1; form.add(cboLop, gbc);

        JPanel tkPanel = new JPanel(new GridBagLayout());
        tkPanel.setBorder(BorderFactory.createTitledBorder("Tài khoản sinh viên"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2,4,2,4);
        g2.fill = GridBagConstraints.HORIZONTAL;

        g2.gridx=0; g2.gridy=0; tkPanel.add(new JLabel("Username"), g2);
        g2.gridx=1; tkPanel.add(txtUsername, g2);
        g2.gridx=0; g2.gridy=1; tkPanel.add(new JLabel("Password"), g2);
        g2.gridx=1; tkPanel.add(txtPassword, g2);

        JPanel resetPanel = new JPanel(new GridBagLayout());
        resetPanel.setBorder(BorderFactory.createTitledBorder("Reset mật khẩu"));
        GridBagConstraints g3 = new GridBagConstraints();
        g3.insets = new Insets(2,4,2,4);
        g3.fill = GridBagConstraints.HORIZONTAL;

        g3.gridx=0; g3.gridy=0; resetPanel.add(new JLabel("Mật khẩu mới"), g3);
        g3.gridx=1; resetPanel.add(txtMatKhauMoi, g3);
        btnResetMK = new JButton("Reset");
        g3.gridx=1; g3.gridy=1; resetPanel.add(btnResetMK, g3);

        JPanel right = new JPanel(new BorderLayout(5,5));
        right.setPreferredSize(new Dimension(280,1));
        right.add(tkPanel, BorderLayout.NORTH);
        right.add(resetPanel, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new BorderLayout(5,5));
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(right, BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(wrap, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        model = new DefaultTableModel(
            new Object[]{"Mã SV","Họ tên","Ngày sinh","Giới tính","Địa chỉ","SĐT","Lớp","Username"},0
        ){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, new JScrollPane(table));
        split.setResizeWeight(0.3);

        add(split, BorderLayout.CENTER);

        loadData();

        /* ================= EVENTS ================= */
        btnAdd.addActionListener(e -> addSV());
        btnUpdate.addActionListener(e -> updateSV());
        btnDelete.addActionListener(e -> deleteSV());
        btnClear.addActionListener(e -> clearForm());
        btnResetMK.addActionListener(e -> resetMatKhau());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
    }

    /* ================= LOGIC ================= */

    private boolean validateAddSV() {
        if (txtMaSV.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Mã sinh viên không được để trống");
            txtMaSV.requestFocus(); return false;
        }
        if (txtHoTen.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống");
            txtHoTen.requestFocus(); return false;
        }
        if (cboLop.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn lớp");
            cboLop.requestFocus(); return false;
        }
        if (txtUsername.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Phải nhập username");
            txtUsername.requestFocus(); return false;
        }
        if (txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Phải nhập password");
            txtPassword.requestFocus(); return false;
        }
        return true;
    }

    private SinhVien buildSV() {
        SinhVien sv = new SinhVien();
        sv.setMaSV(txtMaSV.getText().trim());
        sv.setHoTen(txtHoTen.getText().trim());
        sv.setNgaySinh((Date) spNgaySinh.getValue());
        sv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
        sv.setDiaChi(txtDiaChi.getText().trim());
        sv.setSoDienThoai(txtSDT.getText().trim());
        sv.setMaLop(cboLop.getSelectedItem().toString());
        String u = txtUsername.getText().trim();
        sv.setTenDangNhap(u.isEmpty() ? null : u);
        return sv;
    }

    private void addSV() {
        if (!validateAddSV()) return;

        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword());

        if (taiKhoanDB.exists(u)) {
            JOptionPane.showMessageDialog(this, "Username đã tồn tại!");
            return;
        }

        try (var con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            taiKhoanDB.insert(u, p, "SINHVIEN");
            sinhVienDB.insert(buildSV());
            con.commit();

            JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
            loadData();
            clearForm();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateSV() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        if (txtHoTen.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống");
            return;
        }

        try {
            sinhVienDB.update(buildSV());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void deleteSV() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        if (JOptionPane.showConfirmDialog(
                this, "Xóa sinh viên này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        sinhVienDB.delete(model.getValueAt(r,0).toString());
        loadData();
        clearForm();
    }

    private void resetMatKhau() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn sinh viên cần reset mật khẩu!");
            return;
        }

        String username = model.getValueAt(r, 7).toString();
        if (username.equals("CHƯA CÓ")) {
            JOptionPane.showMessageDialog(this, "Sinh viên chưa có tài khoản!");
            return;
        }

        String newPass = new String(txtMatKhauMoi.getPassword()).trim();
        if (newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mật khẩu mới!");
            return;
        }

        if (taiKhoanDB.resetPassword(username, newPass)) {
            JOptionPane.showMessageDialog(this, "Reset mật khẩu thành công!");
            txtMatKhauMoi.setText("");
        }
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaSV.setText(model.getValueAt(r,0).toString());
        txtMaSV.setEditable(false);
        txtHoTen.setText(model.getValueAt(r,1).toString());
        spNgaySinh.setValue(model.getValueAt(r,2));
        cboGioiTinh.setSelectedItem(model.getValueAt(r,3));
        txtDiaChi.setText(model.getValueAt(r,4).toString());
        txtSDT.setText(model.getValueAt(r,5).toString());
        cboLop.setSelectedItem(model.getValueAt(r,6));
        txtUsername.setText(model.getValueAt(r,7).equals("CHƯA CÓ")?"":model.getValueAt(r,7).toString());
    }

    private void loadLop() {
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement("SELECT MaLop FROM Lop WHERE TrangThai=1");
             var rs = ps.executeQuery()) {
            while (rs.next()) cboLop.addItem(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadData() {
        model.setRowCount(0);
        for (SinhVien sv : sinhVienDB.getAllActive()) {
            model.addRow(new Object[]{
                sv.getMaSV(), sv.getHoTen(), sv.getNgaySinh(),
                sv.getGioiTinh(), sv.getDiaChi(), sv.getSoDienThoai(),
                sv.getMaLop(), sv.getTenDangNhap()==null?"CHƯA CÓ":sv.getTenDangNhap()
            });
        }
    }

    private void clearForm() {
        txtMaSV.setText("");
        txtMaSV.setEditable(true);
        txtHoTen.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtMatKhauMoi.setText("");
        spNgaySinh.setValue(new Date());
        table.clearSelection();
    }
}
