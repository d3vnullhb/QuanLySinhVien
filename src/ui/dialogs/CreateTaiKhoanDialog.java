package ui.dialogs;

import dao.TaiKhoanDB;

import javax.swing.*;
import java.awt.*;

public class CreateTaiKhoanDialog extends JDialog {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private boolean created = false;

    public CreateTaiKhoanDialog(JFrame parent) {
        super(parent, "Tạo tài khoản giảng viên", true);
        setSize(320, 180);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        txtUser = new JTextField();
        txtPass = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Tên đăng nhập"));
        form.add(txtUser);
        form.add(new JLabel("Mật khẩu"));
        form.add(txtPass);

        JButton btnCreate = new JButton("Tạo tài khoản");
        btnCreate.addActionListener(e -> create());

        add(form, BorderLayout.CENTER);
        add(btnCreate, BorderLayout.SOUTH);
    }

    private void create() {
        if (txtUser.getText().isBlank() || txtPass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Không được để trống");
            return;
        }

        TaiKhoanDB db = new TaiKhoanDB();
        boolean ok = db.insert(
            txtUser.getText().trim(),
            new String(txtPass.getPassword()),
             "GIANGVIEN"
        );

        if (ok) {
            created = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại (trùng tên?)");
        }
    }

    public boolean isCreated() {
        return created;
    }

    public String getTenDangNhap() {
        return txtUser.getText().trim();
    }
}
