package ui;

import util.Session;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Quản lý sinh viên");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblWelcome = new JLabel(
                "Xin chào: " + Session.currentUser.getTenDangNhap() +
                " (" + Session.currentUser.getVaiTro() + ")",
                SwingConstants.CENTER
        );
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));

        add(lblWelcome, BorderLayout.CENTER);
    }
}
