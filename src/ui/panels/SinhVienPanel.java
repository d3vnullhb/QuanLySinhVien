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
    private JPasswordField txtPassword;
    private JComboBox<String> cboGioiTinh, cboLop, cboTrangThai;
    private JSpinner spNgaySinh;
    private JButton btnAdd, btnUpdate, btnDelete, btnRestore, btnClear, btnReset;

    private SinhVienDB sinhVienDB = new SinhVienDB();
    private TaiKhoanDB taiKhoanDB = new TaiKhoanDB();

    public SinhVienPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSV = new JTextField(12);
        txtHoTen = new JTextField(12);
        txtDiaChi = new JTextField(12);
        txtSDT = new JTextField(12);
        txtUsername = new JTextField(12);
        txtPassword = new JPasswordField(12);
        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh,"dd/MM/yyyy"));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam","Nữ"});
        cboLop = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"Đang học","Đã xóa"});

        loadLop();

        int r=0;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Mã SV"),gbc);
        gbc.gridx=1; form.add(txtMaSV,gbc);
        gbc.gridx=2; form.add(new JLabel("Họ tên"),gbc);
        gbc.gridx=3; form.add(txtHoTen,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Ngày sinh"),gbc);
        gbc.gridx=1; form.add(spNgaySinh,gbc);
        gbc.gridx=2; form.add(new JLabel("Giới tính"),gbc);
        gbc.gridx=3; form.add(cboGioiTinh,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Địa chỉ"),gbc);
        gbc.gridx=1; form.add(txtDiaChi,gbc);
        gbc.gridx=2; form.add(new JLabel("SĐT"),gbc);
        gbc.gridx=3; form.add(txtSDT,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Lớp"),gbc);
        gbc.gridx=1; form.add(cboLop,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Username"),gbc);
        gbc.gridx=1; form.add(txtUsername,gbc);
        gbc.gridx=2; form.add(new JLabel("Mật khẩu"),gbc);
        gbc.gridx=3; form.add(txtPassword,gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRestore = new JButton("Khôi phục");
        btnClear = new JButton("Làm mới");
        btnReset = new JButton("Reset mật khẩu");

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        btnPanel.add(btnRestore); btnPanel.add(btnReset);
        btnPanel.add(new JLabel("Xem:")); btnPanel.add(cboTrangThai); btnPanel.add(btnClear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form,BorderLayout.CENTER);
        top.add(btnPanel,BorderLayout.SOUTH);

        model = new DefaultTableModel(new Object[]{"Mã SV","Họ tên","Ngày sinh","Giới tính","Địa chỉ","SĐT","Lớp","Username"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setRowHeight(26);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, new JScrollPane(table));
        split.setResizeWeight(0.35);
        add(split,BorderLayout.CENTER);

        loadData();

        btnAdd.addActionListener(e -> addSV());
        btnUpdate.addActionListener(e -> updateSV());
        btnDelete.addActionListener(e -> deleteSV());
        btnRestore.addActionListener(e -> restoreSV());
        btnReset.addActionListener(e -> resetPass());
        btnClear.addActionListener(e -> clearForm());
        cboTrangThai.addActionListener(e -> { clearForm(); loadData(); });
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadData() {
        model.setRowCount(0);
        boolean active = cboTrangThai.getSelectedItem().equals("Đang học");
        var list = active ? sinhVienDB.getAllActive() : sinhVienDB.getAllDeleted();
        for (SinhVien sv : list) {
            model.addRow(new Object[]{sv.getMaSV(), sv.getHoTen(), sv.getNgaySinh(), sv.getGioiTinh(), sv.getDiaChi(), sv.getSoDienThoai(), sv.getMaLop(), sv.getTenDangNhap()});
        }
    }

    private boolean msg(String s, JComponent c) {
        JOptionPane.showMessageDialog(this, s);
        if(c != null) c.requestFocus();
        return false;
    }

    private boolean validateForm(boolean isAdd) {
        if (txtMaSV.getText().trim().isEmpty()) return msg("Chưa nhập mã SV", txtMaSV);
        if (txtHoTen.getText().trim().isEmpty()) return msg("Chưa nhập họ tên", txtHoTen);
        if (!txtSDT.getText().matches("0\\d{9,10}")) return msg("SĐT không hợp lệ", txtSDT);
        if (isAdd) {
            if (txtUsername.getText().trim().isEmpty()) return msg("Chưa nhập username", txtUsername);
            if (txtPassword.getPassword().length < 6) return msg("Mật khẩu phải ≥ 6 ký tự", txtPassword);
        }
        return true;
    }

       private void addSV() {
    if (!validateForm(true)) return;
    
    String maSV = txtMaSV.getText().trim();
    String user = txtUsername.getText().trim();
    String pass = new String(txtPassword.getPassword()).trim();

    // 1. Kiểm tra Mã SV tồn tại (Xử lý Khôi phục)
    if (sinhVienDB.existsMaSV(maSV)) {
        SinhVien svCu = sinhVienDB.getByMaSV(maSV);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Mã SV này đã tồn tại trong danh sách đã xóa. Bạn có muốn khôi phục không?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            // KIỂM TRA USERNAME MỚI: Nếu đổi username khác, phải check xem có ai dùng chưa
            if (!user.equals(svCu.getTenDangNhap()) && taiKhoanDB.exists(user)) {
                msg("Username mới này đã có người khác sử dụng!", txtUsername);
                return;
            }

            // BƯỚC A: XỬ LÝ BẢNG TAIKHOAN TRƯỚC (QUAN TRỌNG ĐỂ FIX LỖI FK)
            if (svCu.getTenDangNhap() == null) {
                // Nếu bản ghi cũ không có account, tạo mới luôn
                taiKhoanDB.insert(user, pass, "SINHVIEN");
            } else {
                // Nếu đã có account, cập nhật username TRƯỚC ở bảng TaiKhoan
                taiKhoanDB.updateUsername(svCu.getTenDangNhap(), user);
                taiKhoanDB.restore(user);
                taiKhoanDB.resetPassword(user, pass);
            }

            // BƯỚC B: SAU KHI BẢNG TAIKHOAN ĐÃ CÓ 'user', MỚI UPDATE BẢNG SINHVIEN
            SinhVien svMoi = buildSV();
            svMoi.setTenDangNhap(user);
            
            if (sinhVienDB.update(svMoi)) { // Lúc này sẽ KHÔNG lỗi FK nữa
                sinhVienDB.restore(maSV);
                msg("Khôi phục và cập nhật thành công!", null);
                loadData(); clearForm();
            } else {
                msg("Lỗi khi cập nhật Sinh viên!", null);
            }
        }
        return; 
    }

    // 2. Thêm mới hoàn toàn (Mã SV chưa từng tồn tại)
    if (taiKhoanDB.exists(user)) {
        msg("Username đã tồn tại!", txtUsername);
        return;
    }

    // Luôn Insert TaiKhoan trước, SinhVien sau
    if (taiKhoanDB.insert(user, pass, "SINHVIEN")) {
        SinhVien sv = buildSV();
        sv.setTenDangNhap(user);
        if (sinhVienDB.insert(sv)) {
            msg("Thêm thành công!", null);
            loadData(); clearForm();
        } else {
            msg("Lỗi khi lưu Sinh viên!", null);
        }
    } else {
        msg("Lỗi khi tạo Tài khoản!", null);
    }
}

    private void updateSV() {
        int r = table.getSelectedRow();
        if (r < 0) { msg("Chọn sinh viên để sửa", null); return; }
        if (!validateForm(false)) return;

        String oldUser = (String) model.getValueAt(r,7);
        String newUser = txtUsername.getText().trim();

        if (!newUser.equals(oldUser) && taiKhoanDB.exists(newUser)) {
            msg("Username mới đã tồn tại", txtUsername); return;
        }

        SinhVien sv = buildSV();
        sv.setMaSV(model.getValueAt(r,0).toString());
        sv.setTenDangNhap(newUser);

        if (sinhVienDB.update(sv)) {
            if (oldUser != null && !newUser.equals(oldUser)) {
                taiKhoanDB.updateUsername(oldUser, newUser);
            }
            msg("Cập nhật thành công!", null);
            loadData(); clearForm();
        } else { msg("Cập nhật thất bại!", null); }
    }

    private void deleteSV() {
        int r = table.getSelectedRow(); if (r<0) return;
        String maSV = model.getValueAt(r,0).toString();
        String u = (String) model.getValueAt(r,7);
        if(sinhVienDB.softDelete(maSV)) {
            if (u!=null) taiKhoanDB.softDelete(u);
            loadData(); clearForm();
        }
    }

    private void restoreSV() {
        int r = table.getSelectedRow(); if (r<0) return;
        String maSV = model.getValueAt(r,0).toString();
        String u = (String) model.getValueAt(r,7);
        if(sinhVienDB.restore(maSV)) {
            if (u!=null) taiKhoanDB.restore(u);
            loadData(); clearForm();
        }
    }

        private void resetPass() {
         int r = table.getSelectedRow();
         if (r < 0) return;

         String user = (String) model.getValueAt(r, 7);
         if (user == null || user.isEmpty()) return;

         int c = JOptionPane.showConfirmDialog(
                 this,
                 "Reset mật khẩu cho tài khoản: " + user +
                 "\nMật khẩu mới: 123456",
                 "Xác nhận",
                 JOptionPane.YES_NO_OPTION
         );

         if (c == JOptionPane.YES_OPTION) {
             taiKhoanDB.resetPassword(user, "123456");
             JOptionPane.showMessageDialog(this, "Reset mật khẩu thành công!");
         }
     }


    private SinhVien buildSV() {
        SinhVien sv = new SinhVien();
        sv.setMaSV(txtMaSV.getText());
        sv.setHoTen(txtHoTen.getText());
        sv.setNgaySinh((Date) spNgaySinh.getValue());
        sv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
        sv.setDiaChi(txtDiaChi.getText());
        sv.setSoDienThoai(txtSDT.getText());
        sv.setMaLop(cboLop.getSelectedItem() == null ? "" : cboLop.getSelectedItem().toString());
        return sv;
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r<0) return;
        txtMaSV.setText(model.getValueAt(r,0).toString());
        txtMaSV.setEditable(false);
        txtHoTen.setText(model.getValueAt(r,1).toString());
        spNgaySinh.setValue(model.getValueAt(r,2));
        cboGioiTinh.setSelectedItem(model.getValueAt(r,3));
        txtDiaChi.setText(model.getValueAt(r,4).toString());
        txtSDT.setText(model.getValueAt(r,5).toString());
        cboLop.setSelectedItem(model.getValueAt(r,6));
        txtUsername.setText(model.getValueAt(r,7)==null?"":model.getValueAt(r,7).toString());
        txtPassword.setEnabled(false);      
    }

    private void clearForm() {
        txtMaSV.setText(""); txtMaSV.setEditable(true);
        txtHoTen.setText(""); txtDiaChi.setText(""); txtSDT.setText("");
        txtUsername.setText(""); txtPassword.setText(""); txtPassword.setEnabled(true);
        spNgaySinh.setValue(new Date());
        table.clearSelection();
    }

    private void loadLop() {
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement("SELECT MaLop FROM Lop WHERE TrangThai=1");
             var rs = ps.executeQuery()) {
            cboLop.removeAllItems();
            while (rs.next()) cboLop.addItem(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
    }
}