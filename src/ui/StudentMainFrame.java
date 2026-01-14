package ui;

import dao.StudentDB;
import util.Session;
import ui.panels.DashboardPanel; 

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class StudentMainFrame extends JFrame {

    private JPanel sidebar;
    private JPanel topPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private final Color SIDEBAR_COLOR = new Color(108, 92, 231);
    private final Color SIDEBAR_HOVER = new Color(84, 76, 200);

    // Lưu thông tin sinh viên hiện tại
    private String currentMaSV;
    private String currentMaLop;
    private String currentHoTen;

    public StudentMainFrame() {
        // 1. Load thông tin cơ bản trước
        loadStudentInfo();

        setTitle("Cổng thông tin Sinh viên - " + (currentHoTen != null ? currentHoTen : ""));
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

    private void loadStudentInfo() {
        StudentDB dao = new StudentDB();
        String[] info = dao.getThongTinSinhVien();
        if (info[0] != null) {
            currentMaSV = info[0];
            currentMaLop = info[1];
            currentHoTen = info[2];
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sinh viên liên kết!");
        }
    }

    // ================= SIDEBAR =================
    private void initSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_COLOR);

        // Menu
        JPanel menuPanel = new JPanel(new GridLayout(0, 1));
        menuPanel.setBackground(SIDEBAR_COLOR);

        JButton btnHome = createMenuButton("Trang chủ", "home.png");
        JButton btnInfo = createMenuButton("Thông tin cá nhân", "student.png");
        JButton btnTKB = createMenuButton("Thời khóa biểu", "timetable.png");
        JButton btnDiem = createMenuButton("Kết quả học tập", "score.png");

        menuPanel.add(btnHome);
        menuPanel.add(btnInfo);
        menuPanel.add(btnTKB);
        menuPanel.add(btnDiem);

        // Logout
        JButton btnLogout = createMenuButton("Đăng xuất", "logout.png");

        sidebar.add(menuPanel, BorderLayout.NORTH);
        sidebar.add(btnLogout, BorderLayout.SOUTH);

        // Events
        btnHome.addActionListener(e -> cardLayout.show(contentPanel, "dashboard"));
        
        // Khi bấm vào Info -> Load lại profile mới nhất
        btnInfo.addActionListener(e -> {
            contentPanel.remove(profilePanel); // Xóa panel cũ
            profilePanel = createProfilePanel(); // Tạo panel mới với dữ liệu mới
            contentPanel.add(profilePanel, "info"); // Add lại
            cardLayout.show(contentPanel, "info");
            contentPanel.revalidate();
        });

        btnTKB.addActionListener(e -> {
            cardLayout.show(contentPanel, "tkb");
            refreshTKB();
        });
        btnDiem.addActionListener(e -> {
            cardLayout.show(contentPanel, "score");
            refreshScore();
        });

        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Đăng xuất thiệt hả ní?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                Session.logout();
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    // ================= CONTENT PANEL =================
    private JTable tblTKB;
    private DefaultTableModel modelTKB;
    private JTable tblScore;
    private DefaultTableModel modelScore;
    private JPanel profilePanel; // Lưu biến này để refresh được

    private void initContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // 1. Dashboard
        contentPanel.add(new DashboardPanel(), "dashboard");

        // 2. Info Panel (GỌI HÀM MỚI Ở ĐÂY)
        profilePanel = createProfilePanel();
        contentPanel.add(profilePanel, "info");

        // 3. TKB Panel
        JPanel pnlTKB = new JPanel(new BorderLayout());
        JLabel lblTitleTKB = new JLabel("THỜI KHÓA BIỂU - LỚP: " + currentMaLop);
        lblTitleTKB.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitleTKB.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitleTKB.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        
        String[] colsTKB = {"Môn học", "Giảng viên", "Thứ", "Tiết BĐ", "Phòng"};
        modelTKB = new DefaultTableModel(colsTKB, 0);
        tblTKB = new JTable(modelTKB);
        tblTKB.setRowHeight(25);
        
        pnlTKB.add(lblTitleTKB, BorderLayout.NORTH);
        pnlTKB.add(new JScrollPane(tblTKB), BorderLayout.CENTER);
        contentPanel.add(pnlTKB, "tkb");

        // 4. Score Panel
        JPanel pnlScore = new JPanel(new BorderLayout());
        JLabel lblTitleScore = new JLabel("KẾT QUẢ HỌC TẬP");
        lblTitleScore.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitleScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitleScore.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        String[] colsScore = {"Môn học", "TC", "CC", "GK", "CK", "Tổng", "Chữ", "KQ"};
        modelScore = new DefaultTableModel(colsScore, 0);
        tblScore = new JTable(modelScore);
        tblScore.setRowHeight(25);

        pnlScore.add(lblTitleScore, BorderLayout.NORTH);
        pnlScore.add(new JScrollPane(tblScore), BorderLayout.CENTER);
        contentPanel.add(pnlScore, "score");
    }

    // ===== HÀM TẠO PANEL PROFILE XỊN XÒ =====
    private JPanel createProfilePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Lấy dữ liệu từ DB
        StudentDB dao = new StudentDB();
        String[] info = dao.getStudentProfile(currentMaSV);
        // info: [0]MaSV, [1]HoTen, [2]NgaySinh, [3]GioiTinh, [4]SDT, [5]DiaChi, [6]TenLop, [7]TrangThai
        if (info[0] == null) info = new String[]{"...", "...", "...", "...", "...", "...", "...", "..."};

        // Header Title
        JLabel lblTitle = new JLabel("HỒ SƠ SINH VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(SIDEBAR_COLOR);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Content (Dùng GridBagLayout căn cho thẳng)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Khoảng cách các dòng
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thêm từng dòng
        addProfileRow(formPanel, gbc, 0, "Mã sinh viên:", info[0]);
        addProfileRow(formPanel, gbc, 1, "Họ và tên:", info[1]);
        addProfileRow(formPanel, gbc, 2, "Lớp:", info[6]);
        addProfileRow(formPanel, gbc, 3, "Ngày sinh:", info[2]);
        addProfileRow(formPanel, gbc, 4, "Giới tính:", info[3]);
        addProfileRow(formPanel, gbc, 5, "Số điện thoại:", info[4]);
        addProfileRow(formPanel, gbc, 6, "Địa chỉ:", info[5]);
        addProfileRow(formPanel, gbc, 7, "Trạng thái:", info[7]);

        // Gom vào panel chính (cho nó nằm gọn ở giữa màn hình nếu rộng quá)
        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(formPanel);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        return mainPanel;
    }

    private void addProfileRow(JPanel p, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.GRAY);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        val.setForeground(Color.BLACK);
        p.add(val, gbc);
    }

    // --- Data Loading Helpers ---
    private void refreshTKB() {
        if (currentMaLop == null) return;
        modelTKB.setRowCount(0);
        StudentDB dao = new StudentDB();
        Vector<Vector<String>> data = dao.getTKB(currentMaLop);
        for (Vector<String> row : data) modelTKB.addRow(row);
    }

    private void refreshScore() {
        if (currentMaSV == null) return;
        modelScore.setRowCount(0);
        StudentDB dao = new StudentDB();
        Vector<Vector<String>> data = dao.getDiem(currentMaSV);
        for (Vector<String> row : data) modelScore.addRow(row);
    }

    // --- UI Helpers ---
    private void initTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 60));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel lblWelcome = new JLabel("Xin chào: " + (currentHoTen != null ? currentHoTen : Session.currentUser.getTenDangNhap()));
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(lblWelcome, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton btn = new JButton(text);
        java.net.URL imgURL = getClass().getResource("/images/" + iconName);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        }
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_HOVER);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });
        return btn;
    }
}