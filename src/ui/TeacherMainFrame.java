package ui;

import javax.swing.*;
import java.awt.*;

public class TeacherMainFrame extends JFrame {

    public TeacherMainFrame() {
        setTitle("Giảng viên");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("GIAO DIỆN GIẢNG VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblInfo = new JLabel(
                "Đăng nhập với vai trò GIANGVIEN thành công",
                SwingConstants.CENTER
        );
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(lblInfo, BorderLayout.CENTER);

        add(mainPanel);
    }
}
