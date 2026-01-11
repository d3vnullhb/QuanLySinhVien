package ui;

import util.Session;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebar;
    private JPanel topPanel;
    private JPanel contentPanel;
    private JLabel lblWelcome;

    // ===== COLORS =====
    private final Color SIDEBAR_COLOR = new Color(108, 92, 231); 
    private final Color SIDEBAR_HOVER = new Color(84, 76, 200);

    public MainFrame() {
        setTitle("Quản lý sinh viên");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initSidebar();
        initTopPanel();
        initContent();

        add(sidebar, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= SIDEBAR =================
    private void initSidebar() {
        sidebar = new JPanel(new GridLayout(8, 1));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_COLOR);

        sidebar.add(createMenuButton("Trang chủ", "home.png"));
        sidebar.add(createMenuButton("Quản lý sinh viên", "student.png"));
        sidebar.add(createMenuButton("Quản lý lớp", "class.png"));
        sidebar.add(createMenuButton("Quản lý TKB", "timetable.png"));
        sidebar.add(createMenuButton("Quản lý điểm", "score.png"));
        sidebar.add(createMenuButton("Quản lý môn học", "subject.png"));
        sidebar.add(createMenuButton("Thống kê", "chart.png"));

        JButton btnLogout = createMenuButton("Đăng xuất", "logout.png");
        sidebar.add(btnLogout);

        // LOGOUT EVENT
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (c == JOptionPane.YES_OPTION) {
                Session.logout();
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    // ===== BUTTON WITH ICON =====
    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);

        // LOAD ICON
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/" + iconName)
        );
        Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(img));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });

        return btn;
    }

    // ================= TOP PANEL =================
    private void initTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 120));
        topPanel.setBackground(Color.WHITE);

        lblWelcome = new JLabel(
                "Xin chào: " + Session.currentUser.getTenDangNhap()
                        + " (" + Session.currentUser.getVaiTro() + ")"
        );
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cardPanel.setBackground(Color.WHITE);

      
        cardPanel.add(createCard("Thêm danh sách lớp", new Color(108, 92, 231)));
        cardPanel.add(createCard("Xem danh sách lớp", new Color(108, 92, 231)));
        cardPanel.add(createCard("Thêm sinh viên", new Color(108, 92, 231)));

        topPanel.add(lblWelcome, BorderLayout.NORTH);
        topPanel.add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String text, Color bg) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // ================= CONTENT =================
    private void initContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));

        JLabel lbl = new JLabel("Dashboard", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));

        contentPanel.add(lbl, BorderLayout.CENTER);
    }
}
