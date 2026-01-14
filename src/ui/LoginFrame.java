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

    // ===== EYE ICON =====
    private JLabel lblEye;
    private boolean showPassword = false;

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

        // Load ảnh (ní nhớ check file login.png nha)
        java.net.URL imgURL = getClass().getResource("/images/login.png");
        if (imgURL != null) {
            originalImage = new ImageIcon(imgURL).getImage();
        }

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
        txtUser.addActionListener(e -> txtPass.requestFocus());

        JLabel lblPass = new JLabel("Mật khẩu");
        txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(250, 30));

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(111, 99, 221));
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

        // ===== PASSWORD + EYE ICON =====
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(Color.WHITE);
        passPanel.add(txtPass, BorderLayout.CENTER);

        // Load Icon mắt
        if (getClass().getResource("/images/eye.png") != null) {
            ImageIcon eyeIcon = new ImageIcon(getClass().getResource("/images/eye.png"));
            Image eyeImg = eyeIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            lblEye = new JLabel(new ImageIcon(eyeImg));
        } else {
            lblEye = new JLabel("Show");
        }
        
        lblEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblEye.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        passPanel.add(lblEye, BorderLayout.EAST);

        gbc.gridy++;
        rightPanel.add(passPanel, gbc);

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

        // ===== SỰ KIỆN CLICK MẮT (HIỆN/ẨN PASS) =====
        char defaultEcho = txtPass.getEchoChar();
        lblEye.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!showPassword) {
                    txtPass.setEchoChar((char) 0);
                    updateEyeIcon("eye_off.png");
                    showPassword = true;
                } else {
                    txtPass.setEchoChar(defaultEcho);
                    updateEyeIcon("eye.png");
                    showPassword = false;
                }
            }
        });
//sự kiện đăng nhập
        btnLogin.addActionListener(e -> {
            TaiKhoanDB db = new TaiKhoanDB();
            TaiKhoan tk = db.login(
                    txtUser.getText(),
                    new String(txtPass.getPassword())
            );

            if (tk != null) {
                // 1. Lưu vào Session
                Session.currentUser = tk;

                // 2. Lưu tên đăng nhập (nếu tích chọn)
                Preferences prefs = Preferences.userRoot().node("QLSV");
                if (chkRemember.isSelected()) {
                    prefs.put("username", tk.getTenDangNhap());
                } else {
                    prefs.remove("username");
                }

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

                // 3. ĐIỀU HƯỚNG THEO VAI TRÒ
                // Vì chung package ui nên gọi thẳng tên class luôn
                String vaiTro = tk.getVaiTro(); 

                if (vaiTro.equalsIgnoreCase("ADMIN")) {
                    new MainFrame().setVisible(true); // Vào trang Admin
                    this.dispose();

                } else if (vaiTro.equalsIgnoreCase("SINHVIEN")) {
                    new StudentMainFrame().setVisible(true); // Vào trang Sinh viên
                    this.dispose();

                } else if (vaiTro.equalsIgnoreCase("GIANGVIEN")) {
                    // Tạm thời chưa có Frame giảng viên
                    JOptionPane.showMessageDialog(this, 
                        "Chào Giảng viên! Chức năng này đang cập nhật.", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    // Sau này có thì: new LecturerMainFrame().setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Vai trò không xác định!");
                }

            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ===== SỰ KIỆN QUÊN MẬT KHẨU =====
        btnForgot.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng liên hệ Phòng Quản lý sinh viên để cấp lại mật khẩu.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // ===== LOAD LẠI TÊN ĐĂNG NHẬP CŨ =====
        Preferences prefs = Preferences.userRoot().node("QLSV");
        String savedUser = prefs.get("username", null);
        if (savedUser != null) {
            txtUser.setText(savedUser);
            chkRemember.setSelected(true);
        }

        getRootPane().setDefaultButton(btnLogin);
        scaleImage();
    }

    // Helper đổi icon mắt
    private void updateEyeIcon(String iconName) {
        if (getClass().getResource("/images/" + iconName) != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + iconName));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            lblEye.setIcon(new ImageIcon(img));
        }
    }

    // Helper scale ảnh
    private void scaleImage() {
        if (originalImage == null || lblImage.getWidth() == 0 || lblImage.getHeight() == 0)
            return;
        int panelW = lblImage.getWidth();
        int panelH = lblImage.getHeight();
        int imgW = originalImage.getWidth(null);
        int imgH = originalImage.getHeight(null);
        double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);
        int newW = (int) (imgW * scale);
        int newH = (int) (imgH * scale);
        Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        lblImage.setIcon(new ImageIcon(scaled));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}