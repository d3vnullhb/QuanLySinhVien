package ui;

import dao.StudentDB;
import util.Session;
import ui.panels.DashboardPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class StudentMainFrame extends JFrame {

    private JPanel sidebar, topPanel, contentPanel;
    private CardLayout cardLayout;

    private final Color SIDEBAR_COLOR = new Color(108, 92, 231);
    private final Color SIDEBAR_HOVER = new Color(84, 76, 200);

    private String currentMaSV, currentMaLop, currentHoTen;

    private JTable tblTKB, tblScore;
    private DefaultTableModel modelTKB, modelScore;
    private JPanel profilePanel;

    public StudentMainFrame() {
        loadStudentInfo();

        setTitle("Cổng thông tin Sinh viên - " + currentHoTen);
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
    private void loadStudentInfo() {
        StudentDB dao = new StudentDB();
        String[] info = dao.getThongTinSinhVien();
        if (info != null) {
            currentMaSV = info[0];
            currentMaLop = info[1];
            currentHoTen = info[2];
        }
    }

    /* ================= SIDEBAR ================= */
    private void initSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_COLOR);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR_COLOR);

        JButton btnHome = createMenuButton("Trang chủ", "home.png");
        JButton btnInfo = createMenuButton("Thông tin cá nhân", "student.png");
        JButton btnTKB = createMenuButton("Thời khóa biểu", "timetable.png");
        JButton btnScore = createMenuButton("Kết quả học tập", "score.png");

        menu.add(btnHome);
        menu.add(btnInfo);
        menu.add(btnTKB);
        menu.add(btnScore);

        JButton btnLogout = createMenuButton("Đăng xuất", "logout.png");

        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        btnHome.addActionListener(e -> cardLayout.show(contentPanel, "dashboard"));

        btnInfo.addActionListener(e -> {
            profilePanel = createProfilePanel();
            contentPanel.add(profilePanel, "info");
            cardLayout.show(contentPanel, "info");
        });

        btnTKB.addActionListener(e -> {
            cardLayout.show(contentPanel, "tkb");
            refreshTKB();
        });

        btnScore.addActionListener(e -> {
            cardLayout.show(contentPanel, "score");
            refreshScore();
        });

       btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất không?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
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
        topPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.LIGHT_GRAY));

        JLabel lblWelcome = new JLabel("Xin chào: " + currentHoTen);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        topPanel.add(lblWelcome, BorderLayout.WEST);
    }

    /* ================= CONTENT ================= */
    private void initContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new DashboardPanel(), "dashboard");

        profilePanel = createProfilePanel();
        contentPanel.add(profilePanel, "info");

        // ===== TKB =====
        JPanel pnlTKB = createCardPanel("THỜI KHÓA BIỂU - LỚP " + currentMaLop);
        modelTKB = new DefaultTableModel(
                new String[]{"Môn", "GV", "Ngày", "Thứ", "Tiết", "Phòng"}, 0
        );
        tblTKB = createTable(modelTKB);
        pnlTKB.add(new JScrollPane(tblTKB), BorderLayout.CENTER);
        contentPanel.add(pnlTKB, "tkb");

        // ===== SCORE =====
        JPanel pnlScore = createCardPanel("KẾT QUẢ HỌC TẬP");
        modelScore = new DefaultTableModel(
                new String[]{"Môn", "TC", "CC", "GK", "CK", "Tổng", "Chữ", "KQ"}, 0
        );
        tblScore = createTable(modelScore);
        pnlScore.add(new JScrollPane(tblScore), BorderLayout.CENTER);
        contentPanel.add(pnlScore, "score");
    }

    /* ================= PROFILE ================= */
    private JPanel createProfilePanel() {
        JPanel card = createCardPanel("HỒ SƠ SINH VIÊN");

        StudentDB dao = new StudentDB();
        String[] info = dao.getStudentProfile(currentMaSV);
        if (info == null) info = new String[]{"","","","","","","",""};

        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 12));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        grid.setBackground(Color.WHITE);

        String[] labels = {
                "Mã SV", "Họ tên", "Ngày sinh", "Giới tính",
                "SĐT", "Địa chỉ", "Lớp", "Trạng thái"
        };

        for (int i = 0; i < labels.length; i++) {
            grid.add(new JLabel(labels[i] + ":"));
            grid.add(new JLabel(info[i]));
        }

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    /* ================= DATA ================= */
    private void refreshTKB() {
        modelTKB.setRowCount(0);
        StudentDB dao = new StudentDB();
        Vector<Vector<String>> data = dao.getTKB(currentMaLop);
        for (Vector<String> row : data) modelTKB.addRow(row);
    }

    private void refreshScore() {
        modelScore.setRowCount(0);
        StudentDB dao = new StudentDB();
        Vector<Vector<String>> data = dao.getDiem(currentMaSV);
        for (Vector<String> row : data) modelScore.addRow(row);
    }

    /* ================= UI HELPERS ================= */
    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setDefaultEditor(Object.class, null);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        return t;
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/" + iconName));
        Image img = icon.getImage().getScaledInstance(22,22,Image.SCALE_SMOOTH);
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
        btn.setBorder(BorderFactory.createEmptyBorder(12,25,12,10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_COLOR); }
        });
        return btn;
    }
}
