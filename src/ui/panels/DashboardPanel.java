package ui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(
                "Hệ thống quản lý sinh viên",
                SwingConstants.CENTER
        );
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/dashboard.png")
        );
        Image originalImage = icon.getImage();

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                Image scaled = originalImage.getScaledInstance(
                        w - 80, h - 120, Image.SCALE_SMOOTH
                );
                lblImage.setIcon(new ImageIcon(scaled));
            }
        });

        add(lblTitle, BorderLayout.NORTH);
        add(lblImage, BorderLayout.CENTER);
    }
}
