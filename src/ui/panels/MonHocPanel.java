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
    private JComboBox<String> cboTrangThai;

    private MonHocDB monHocDB = new MonHocDB();

    public MonHocPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        /* ===== TITLE ===== */
        JLabel lblTitle = new JLabel("QUẢN LÝ MÔN HỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        /* ===== CENTER ===== */
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        /* ===== FORM ===== */
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

        /* ===== BUTTONS ===== */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRestore = new JButton("Khôi phục");
        JButton btnReset = new JButton("Làm mới");

        cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Đã xóa"});

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRestore);
        btnPanel.add(btnReset);
        btnPanel.add(new JLabel("Xem:"));
        btnPanel.add(cboTrangThai);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        tableModel = new DefaultTableModel(
                new Object[]{"Mã môn", "Tên môn", "Số tín chỉ"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        /* ===== EVENTS ===== */
        loadData();

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        cboTrangThai.addActionListener(e -> {
            clearForm();
            loadData();
        });

        btnAdd.addActionListener(e -> addMonHoc());
        btnUpdate.addActionListener(e -> updateMonHoc());
        btnDelete.addActionListener(e -> softDelete());
        btnRestore.addActionListener(e -> restoreMonHoc());
        btnReset.addActionListener(e -> clearForm());
    }

    /* ================= LOAD DATA ================= */
    private void loadData() {
        tableModel.setRowCount(0);

        List<MonHoc> list = cboTrangThai.getSelectedItem().equals("Đang hoạt động")
                ? monHocDB.getAllActive()
                : monHocDB.getAllDeleted();

        for (MonHoc mh : list) {
            tableModel.addRow(new Object[]{
                    mh.getMaMon(),
                    mh.getTenMon(),
                    mh.getSoTinChi()
            });
        }
    }

    /* ================= FILL FORM ================= */
    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaMon.setText(table.getValueAt(r, 0).toString());
        txtTenMon.setText(table.getValueAt(r, 1).toString());
        txtSoTinChi.setText(table.getValueAt(r, 2).toString());
        txtMaMon.setEnabled(false);
    }

    /* ================= ADD ================= */
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số!");
            return;
        }

        MonHoc mh = new MonHoc();
        mh.setMaMon(txtMaMon.getText().trim());
        mh.setTenMon(txtTenMon.getText().trim());
        mh.setSoTinChi(soTC);

        if (monHocDB.insert(mh)) {
            loadData();
            clearForm();
        }
    }

    /* ================= UPDATE ================= */
    private void updateMonHoc() {
        if (table.getSelectedRow() < 0) return;

        int soTC;
        try {
            soTC = Integer.parseInt(txtSoTinChi.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số!");
            return;
        }

        MonHoc mh = new MonHoc();
        mh.setMaMon(txtMaMon.getText());
        mh.setTenMon(txtTenMon.getText());
        mh.setSoTinChi(soTC);

        monHocDB.update(mh);
        loadData();
    }

    /* ================= SOFT DELETE ================= */
    private void softDelete() {
        if (table.getSelectedRow() < 0) return;

        if (JOptionPane.showConfirmDialog(
                this,
                "Xóa môn học này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            monHocDB.softDelete(txtMaMon.getText());
            loadData();
            clearForm();
        }
    }

    /* ================= RESTORE ================= */
    private void restoreMonHoc() {
        if (!cboTrangThai.getSelectedItem().equals("Đã xóa")) {
            JOptionPane.showMessageDialog(this, "Chuyển sang chế độ 'Đã xóa' để khôi phục!");
            return;
        }

        int r = table.getSelectedRow();
        if (r < 0) return;

        String maMon = table.getValueAt(r, 0).toString();
        monHocDB.restore(maMon);
        loadData();
        clearForm();
    }

    /* ================= CLEAR ================= */
    private void clearForm() {
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtSoTinChi.setText("");
        txtMaMon.setEnabled(true);
        table.clearSelection();
    }
}
