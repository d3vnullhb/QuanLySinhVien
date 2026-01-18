package ui;

import dao.GiangVienDB;
import util.Session;
import ui.panels.DashboardPanel;
import ui.panels.NhapDiemGiangVienPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TeacherMainFrame extends JFrame {

    private JPanel sidebar, topPanel, contentPanel;
    private CardLayout cardLayout;

    private final Color SIDEBAR_COLOR = new Color(108, 92, 231);
    private final Color SIDEBAR_HOVER = new Color(84, 76, 200);

    private String maGV, hoTen, email, maKhoa;

    private JTable tblTKB;
    private DefaultTableModel modelTKB;
    private JPanel profilePanel;

    public TeacherMainFrame() {
        loadGiangVienInfo();

        setTitle("Cổng thông tin Giảng viên - " + hoTen);
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

    /* ================= LOAD INFO ================= */
    private void loadGiangVienInfo() {
        GiangVienDB dao = new GiangVienDB();
        String tenDN = Session.currentUser.getTenDangNhap();
        String[] info = dao.getGiangVienByTenDangNhap(tenDN);

        if (info != null) {
            maGV = info[0];
            hoTen = info[1];
            email = info[2];
            maKhoa = info[3];
        }
    }

    /* ================= SIDEBAR ================= */
    private void initSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_COLOR);

        JPanel menu = new JPanel(new GridLayout(8, 1));
        menu.setBackground(SIDEBAR_COLOR);

        JButton btnHome = createMenuButton("Trang chủ", "home.png");
        JButton btnInfo = createMenuButton("Thông tin cá nhân", "teacher.png");
        JButton btnTKB = createMenuButton("Thời khóa biểu", "timetable.png");
        JButton btnNhapDiem = createMenuButton("Nhập điểm", "score.png");
        JButton btnLogout = createMenuButton("Đăng xuất", "logout.png");

        menu.add(btnHome);
        menu.add(btnInfo);
        menu.add(btnTKB);
        menu.add(btnNhapDiem);

        sidebar.add(menu, BorderLayout.NORTH);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        btnHome.addActionListener(e -> cardLayout.show(contentPanel, "dashboard"));

        btnInfo.addActionListener(e -> {
            contentPanel.remove(profilePanel);
            profilePanel = createProfilePanel();
            contentPanel.add(profilePanel, "info");
            cardLayout.show(contentPanel, "info");
        });

        btnTKB.addActionListener(e -> {
            cardLayout.show(contentPanel, "tkb");
            loadTKB();
        });

        btnNhapDiem.addActionListener(e -> cardLayout.show(contentPanel, "diem"));

        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Session.logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    /* ================= TOP ================= */
    private void initTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 55));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel lblWelcome = new JLabel("Xin chào giảng viên: " + hoTen);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        topPanel.add(lblWelcome, BorderLayout.WEST);
    }

    /* ================= CONTENT ================= */
    private void initContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new DashboardPanel(), "dashboard");

        profilePanel = createProfilePanel();
        contentPanel.add(profilePanel, "info");

        // ===== TKB =====
        JPanel pnlTKB = new JPanel(new BorderLayout());
        JLabel lblTKB = new JLabel("THỜI KHÓA BIỂU GIẢNG VIÊN", SwingConstants.CENTER);
        lblTKB.setFont(new Font("Segoe UI", Font.BOLD, 18));

        modelTKB = new DefaultTableModel(
                new String[]{"Môn", "Lớp", "Ngày", "Thứ", "Tiết BĐ", "Số tiết", "Phòng"}, 0
        );
        tblTKB = new JTable(modelTKB);
        tblTKB.setRowHeight(26);
        tblTKB.setDefaultEditor(Object.class, null);

        pnlTKB.add(lblTKB, BorderLayout.NORTH);
        pnlTKB.add(new JScrollPane(tblTKB), BorderLayout.CENTER);
        contentPanel.add(pnlTKB, "tkb");

        // ===== NHẬP ĐIỂM =====
        NhapDiemGiangVienPanel pnlNhapDiem = new NhapDiemGiangVienPanel(maGV);
        contentPanel.add(pnlNhapDiem, "diem");
    }

    /* ================= PROFILE ================= */
    private JPanel createProfilePanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(500, 300));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel avatar = new JLabel(new ImageIcon(
                new ImageIcon(getClass().getResource("/images/teacher.png"))
                        .getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)
        ));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel(new GridLayout(5, 1, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        info.add(makeBold("Họ tên: " + hoTen));
        info.add(makeLabel("Mã GV: " + maGV));
        info.add(makeLabel("Email: " + email));
        info.add(makeLabel("Khoa: " + maKhoa));
        info.add(makeLabel("Vai trò: Giảng viên"));

        card.add(avatar, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        main.add(card);

        return main;
    }

    private JLabel makeBold(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        return lbl;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    /* ================= DATA ================= */
    private void loadTKB() {
        modelTKB.setRowCount(0);
        GiangVienDB dao = new GiangVienDB();
        for (Object[] row : dao.getTKBGiangVien(maGV)) {
            modelTKB.addRow(row);
        }
    }

    /* ================= BUTTON ================= */
    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + iconName));
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
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });
        return btn;
    }
}
