package ui.panels;

import dao.LopDB;
import model.Lop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LopPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public LopPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ LỚP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Mã lớp", "Tên lớp", "Khoa"}, 0
        );
        table = new JTable(tableModel);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);

        LopDB db = new LopDB();
        List<Lop> list = db.getAll();

        for (Lop lop : list) {
            tableModel.addRow(new Object[]{
                    lop.getMaLop(),
                    lop.getTenLop(),
                    lop.getTenKhoa()
            });
        }
    }
}
