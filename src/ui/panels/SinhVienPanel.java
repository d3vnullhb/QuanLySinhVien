package ui.panels;

import dao.SinhVienDB;
import dao.TaiKhoanDB;
import model.SinhVien;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class SinhVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaSV, txtHoTen, txtDiaChi, txtSDT, txtUsername;
    private JPasswordField txtPassword, txtMatKhauMoi;
    private JComboBox<String> cboGioiTinh, cboLop;
    private JSpinner spNgaySinh;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnTaoTK, btnResetMK;

    private SinhVienDB sinhVienDB = new SinhVienDB();
    private TaiKhoanDB taiKhoanDB = new TaiKhoanDB();

    public SinhVienPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Dimension small = new Dimension(120, 24);

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

        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh, "dd/MM/yyyy"));
        spNgaySinh.setPreferredSize(small);

        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setPreferredSize(small);

        cboLop = new JComboBox<>();
        cboLop.setPreferredSize(small);

        txtUsername = new JTextField(); txtUsername.setPreferredSize(small);
        txtPassword = new JPasswordField(); txtPassword.setPreferredSize(small);
        txtMatKhauMoi = new JPasswordField(); txtMatKhauMoi.setPreferredSize(small);

        loadLop();

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Mã SV"), gbc);
        gbc.gridx = 1; form.add(txtMaSV, gbc);
        gbc.gridx = 2; form.add(new JLabel("Họ tên"), gbc);
        gbc.gridx = 3; form.add(txtHoTen, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Ngày sinh"), gbc);
        gbc.gridx = 1; form.add(spNgaySinh, gbc);
        gbc.gridx = 2; form.add(new JLabel("Giới tính"), gbc);
        gbc.gridx = 3; form.add(cboGioiTinh, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Địa chỉ"), gbc);
        gbc.gridx = 1; form.add(txtDiaChi, gbc);
        gbc.gridx = 2; form.add(new JLabel("SĐT"), gbc);
        gbc.gridx = 3; form.add(txtSDT, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; form.add(new JLabel("Lớp"), gbc);
        gbc.gridx = 1; form.add(cboLop, gbc);

        /* ===== TẠO TÀI KHOẢN ===== */
        JPanel tkPanel = new JPanel(new GridBagLayout());
        tkPanel.setBorder(BorderFactory.createTitledBorder("Tạo tài khoản"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(2, 4, 2, 4);
        g2.fill = GridBagConstraints.HORIZONTAL;

        g2.gridx = 0; g2.gridy = 0; tkPanel.add(new JLabel("Username"), g2);
        g2.gridx = 1; tkPanel.add(txtUsername, g2);
        g2.gridx = 0; g2.gridy = 1; tkPanel.add(new JLabel("Password"), g2);
        g2.gridx = 1; tkPanel.add(txtPassword, g2);

        btnTaoTK = new JButton("+ Tạo");
        g2.gridx = 1; g2.gridy = 2; tkPanel.add(btnTaoTK, g2);

        /* ===== RESET MK ===== */
        JPanel resetPanel = new JPanel(new GridBagLayout());
        resetPanel.setBorder(BorderFactory.createTitledBorder("Reset mật khẩu"));
        GridBagConstraints g3 = new GridBagConstraints();
        g3.insets = new Insets(2, 4, 2, 4);
        g3.fill = GridBagConstraints.HORIZONTAL;

        g3.gridx = 0; g3.gridy = 0; resetPanel.add(new JLabel("Mật khẩu mới"), g3);
        g3.gridx = 1; resetPanel.add(txtMatKhauMoi, g3);

        btnResetMK = new JButton("Reset");
        g3.gridx = 1; g3.gridy = 1; resetPanel.add(btnResetMK, g3);

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setPreferredSize(new Dimension(260, 1));
        right.add(tkPanel, BorderLayout.NORTH);
        right.add(resetPanel, BorderLayout.SOUTH);

        JPanel formWrap = new JPanel(new BorderLayout(5, 5));
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(right, BorderLayout.EAST);

        /* ===== BUTTONS ===== */
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
                new Object[]{"Mã SV", "Họ tên", "Lớp", "Username"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        JScrollPane tableScroll = new JScrollPane(table);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tableScroll);
        split.setResizeWeight(0.28); // bảng cao hơn
        split.setDividerSize(6);

        add(split, BorderLayout.CENTER);

        loadData();

        /* ================= EVENTS ================= */
        btnAdd.addActionListener(e -> addSV());
        btnUpdate.addActionListener(e -> updateSV());
        btnDelete.addActionListener(e -> deleteSV());
        btnClear.addActionListener(e -> clearForm());
        btnTaoTK.addActionListener(e -> taoTaiKhoan());
        btnResetMK.addActionListener(e -> resetMatKhau());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
    }

    /* ================= DATA ================= */
    private void loadLop() {
        cboLop.removeAllItems();
        try (var con = DBConnection.getConnection();
             var ps = con.prepareStatement("SELECT MaLop FROM Lop WHERE TrangThai = 1");
             var rs = ps.executeQuery()) {
            while (rs.next()) cboLop.addItem(rs.getString("MaLop"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadData() {
        model.setRowCount(0);
        for (SinhVien sv : sinhVienDB.getAllActive()) {
            model.addRow(new Object[]{
                    sv.getMaSV(),
                    sv.getHoTen(),
                    sv.getMaLop(),
                    sv.getTenDangNhap() == null ? "CHƯA CÓ" : sv.getTenDangNhap()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtMaSV.setText(model.getValueAt(row, 0).toString());
        txtMaSV.setEditable(false);
        txtHoTen.setText(model.getValueAt(row, 1).toString());
        cboLop.setSelectedItem(model.getValueAt(row, 2));
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
        return sv;
    }

    private void addSV() {
        if (sinhVienDB.insert(buildSV())) {
            loadData(); clearForm();
        }
    }

    private void updateSV() {
        if (sinhVienDB.update(buildSV())) loadData();
    }

    private void deleteSV() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        if (sinhVienDB.delete(model.getValueAt(row, 0).toString())) {
            loadData(); clearForm();
        }
    }

    private void taoTaiKhoan() {}
    private void resetMatKhau() {}

    private void clearForm() {
        txtMaSV.setText("");
        txtMaSV.setEditable(true);
        txtHoTen.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        table.clearSelection();
    }
}
