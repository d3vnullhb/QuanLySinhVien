package ui;

import dao.TaiKhoanDB;
import model.TaiKhoan;
import util.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    private JCheckBox chkRemember;
    private JButton btnForgot;

    private JLabel lblImage;
    private Image originalImage;

    public LoginFrame() {
        setTitle("Đăng nhập - Quản lý sinh viên");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== PANEL TRÁI: ẢNH LOGIN =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(450, 0));

        originalImage = new ImageIcon(
                getClass().getResource("/images/login.png")
        ).getImage();

        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        leftPanel.add(lblImage, BorderLayout.CENTER);

        leftPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scaleImage();
            }
        });

        // ===== PANEL PHẢI: FORM LOGIN =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSub = new JLabel("Đăng nhập để tiếp tục");
        lblSub.setForeground(Color.GRAY);

        JLabel lblUser = new JLabel("Tài khoản");
        txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(250, 30));

        JLabel lblPass = new JLabel("Mật khẩu");
        txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(250, 30));

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(111, 99, 221)); // tím login
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(140, 35));

        chkRemember = new JCheckBox("Ghi nhớ đăng nhập");
        chkRemember.setBackground(Color.WHITE);

        btnForgot = new JButton("Quên mật khẩu?");
        btnForgot.setBorderPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setForeground(new Color(111, 99, 221));
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ===== ADD COMPONENTS =====
        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(lblTitle, gbc);

        gbc.gridy++;
        rightPanel.add(lblSub, gbc);

        gbc.gridy++;
        rightPanel.add(lblUser, gbc);

        gbc.gridy++;
        rightPanel.add(txtUser, gbc);

        gbc.gridy++;
        rightPanel.add(lblPass, gbc);

        gbc.gridy++;
        rightPanel.add(txtPass, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        rightPanel.add(chkRemember, gbc);

        gbc.gridy++;
        rightPanel.add(btnForgot, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(btnLogin, gbc);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // ===== LOGIN EVENT =====
        btnLogin.addActionListener(e -> {
            TaiKhoanDB db = new TaiKhoanDB();
            TaiKhoan tk = db.login(
                    txtUser.getText(),
                    new String(txtPass.getPassword())
            );

            if (tk != null) {
                Session.currentUser = tk;

                Preferences prefs = Preferences.userRoot().node("QLSV");
                if (chkRemember.isSelected()) {
                    prefs.put("username", tk.getTenDangNhap());
                } else {
                    prefs.remove("username");
                }

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công");
                MainFrame main = new MainFrame();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            }
        });

        // ===== QUÊN MẬT KHẨU =====
        btnForgot.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng liên hệ Phòng Quản lý sinh viên \nđể được cấp lại mật khẩu.",
                    "Quên mật khẩu",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // ===== GHI NHỚ USERNAME =====
        Preferences prefs = Preferences.userRoot().node("QLSV");
        String savedUser = prefs.get("username", null);
        if (savedUser != null) {
            txtUser.setText(savedUser);
            chkRemember.setSelected(true);
        }

        getRootPane().setDefaultButton(btnLogin);
        scaleImage();
    }

    private void scaleImage() {
        if (originalImage == null || lblImage.getWidth() == 0 || lblImage.getHeight() == 0)
            return;

        int panelW = lblImage.getWidth();
        int panelH = lblImage.getHeight();

        int imgW = originalImage.getWidth(null);
        int imgH = originalImage.getHeight(null);

        double scale = Math.min(
                (double) panelW / imgW,
                (double) panelH / imgH
        );

        int newW = (int) (imgW * scale);
        int newH = (int) (imgH * scale);

        Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        lblImage.setIcon(new ImageIcon(scaled));
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}
