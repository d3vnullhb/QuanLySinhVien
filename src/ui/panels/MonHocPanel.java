package ui.panels;

import dao.MonHocDB;
import model.MonHoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MonHocPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private JTextField txtSoTinChi;

    private MonHocDB monHocDB = new MonHocDB();

    public MonHocPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("QUẢN LÝ MÔN HỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin môn học"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaMon = new JTextField(15);
        txtTenMon = new JTextField(15);
        txtSoTinChi = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã môn:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMaMon, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên môn:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTenMon, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Số tín chỉ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtSoTinChi, gbc);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnReset = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(
                new Object[]{"Mã môn", "Tên môn", "Số tín chỉ"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ===== EVENTS =====
        loadData();

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        btnAdd.addActionListener(e -> addMonHoc());
        btnUpdate.addActionListener(e -> updateMonHoc());
        btnDelete.addActionListener(e -> deleteMonHoc());
        btnReset.addActionListener(e -> clearForm());
    }

    // ===== LOAD DATA =====
    private void loadData() {
        tableModel.setRowCount(0);
        List<MonHoc> list = monHocDB.getAllActive();

        for (MonHoc mh : list) {
            tableModel.addRow(new Object[]{
                    mh.getMaMon(),
                    mh.getTenMon(),
                    mh.getSoTinChi()
            });
        }
    }

    // ===== FILL FORM =====
    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaMon.setText(table.getValueAt(row, 0).toString());
            txtTenMon.setText(table.getValueAt(row, 1).toString());
            txtSoTinChi.setText(table.getValueAt(row, 2).toString());
            txtMaMon.setEnabled(false);
        }
    }

    // ===== ADD =====
    private void addMonHoc() {
        if (txtMaMon.getText().isEmpty()
                || txtTenMon.getText().isEmpty()
                || txtSoTinChi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống dữ liệu!");
            return;
        }

        int soTC;
        try {
            soTC = Integer.parseInt(txtSoTinChi.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số!");
            return;
        }

        MonHoc mh = new MonHoc();
        mh.setMaMon(txtMaMon.getText().trim());
        mh.setTenMon(txtTenMon.getText().trim());
        mh.setSoTinChi(soTC);

        if (monHocDB.insert(mh)) {
            JOptionPane.showMessageDialog(this, "Thêm môn học thành công!");
            clearForm();
            loadData();
        }
    }

    // ===== UPDATE =====
    private void updateMonHoc() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn môn học cần sửa!");
            return;
        }

        int soTC;
        try {
            soTC = Integer.parseInt(txtSoTinChi.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số!");
            return;
        }

        MonHoc mh = new MonHoc();
        mh.setMaMon(txtMaMon.getText());
        mh.setTenMon(txtTenMon.getText());
        mh.setSoTinChi(soTC);

        if (monHocDB.update(mh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadData();
        }
    }

    // ===== DELETE (SOFT) =====
    private void deleteMonHoc() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn môn học cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa môn học này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String maMon = txtMaMon.getText();
            if (monHocDB.Delete(maMon)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearForm();
                loadData();
            }
        }
    }

    // ===== CLEAR FORM =====
    private void clearForm() {
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtSoTinChi.setText("");
        txtMaMon.setEnabled(true);
        table.clearSelection();
    }
}
