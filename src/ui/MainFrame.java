package ui;

import util.Session;
import ui.panels.DashboardPanel;
import ui.panels.LopPanel;
import ui.panels.MonHocPanel;
import ui.panels.GiangVienPanel;
import ui.panels.PhanCongPanel;
import ui.panels.ThoiKhoaBieuPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebar;
    private JPanel topPanel;
    private JPanel contentPanel;
    private JLabel lblWelcome;
    private CardLayout cardLayout;

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
    sidebar = new JPanel(new GridLayout(10, 1));
    sidebar.setPreferredSize(new Dimension(220, 0));
    sidebar.setBackground(SIDEBAR_COLOR);

    JButton btnHome = createMenuButton("Trang chủ", "home.png");
    JButton btnSinhVien = createMenuButton("Quản lý sinh viên", "student.png");
    JButton btnLop = createMenuButton("Quản lý lớp", "class.png");
    JButton btnGiangVien = createMenuButton("Quản lý giảng viên", "teacher.png");
    JButton btnPhanCong = createMenuButton("Phân công giảng dạy", "assignment.png");
    JButton btnTKB = createMenuButton("Quản lý TKB", "timetable.png");
    JButton btnDiem = createMenuButton("Quản lý điểm", "score.png");
    JButton btnMonHoc = createMenuButton("Quản lý môn học", "subject.png");
    JButton btnThongKe = createMenuButton("Thống kê", "chart.png");

    sidebar.add(btnHome);
    sidebar.add(btnSinhVien);
    sidebar.add(btnLop);
    sidebar.add(btnGiangVien);
    sidebar.add(btnPhanCong); 
    sidebar.add(btnTKB);
    sidebar.add(btnDiem);
    sidebar.add(btnMonHoc);
    sidebar.add(btnThongKe);

    JButton btnLogout = createMenuButton("Đăng xuất", "logout.png");
    sidebar.add(btnLogout);

    btnHome.addActionListener(e -> cardLayout.show(contentPanel, "dashboard"));
    btnLop.addActionListener(e -> cardLayout.show(contentPanel, "lop"));
    btnMonHoc.addActionListener(e -> cardLayout.show(contentPanel, "monhoc"));
    btnGiangVien.addActionListener(e -> cardLayout.show(contentPanel, "giangvien"));
    btnPhanCong.addActionListener(e -> cardLayout.show(contentPanel, "phancong"));
    btnTKB.addActionListener(e -> cardLayout.show(contentPanel, "tkb"));

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
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setBackground(Color.WHITE);

        lblWelcome = new JLabel(
                "Xin chào: " + Session.currentUser.getTenDangNhap()
                        + " (" + Session.currentUser.getVaiTro() + ")"
        );
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        topPanel.add(lblWelcome, BorderLayout.NORTH);
    }

    private JPanel createCard(String text, String iconName, Color bg) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/" + iconName)
        );
        Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(img));
        lbl.setIconTextGap(10); 

       
        lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
        lbl.setVerticalTextPosition(SwingConstants.CENTER);

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

private void initContent() {
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);

   
    contentPanel.add(new DashboardPanel(), "dashboard");
    contentPanel.add(new LopPanel(), "lop");
    contentPanel.add(new MonHocPanel(), "monhoc");
    contentPanel.add(new GiangVienPanel(), "giangvien");
    contentPanel.add(new PhanCongPanel(), "phancong");
    contentPanel.add(new ThoiKhoaBieuPanel(), "tkb");


    cardLayout.show(contentPanel, "dashboard");
}
}