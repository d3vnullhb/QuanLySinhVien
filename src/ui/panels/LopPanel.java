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

    private JTextField txtMaLop;
    private JTextField txtTenLop;
    private JComboBox<String> cboKhoa;

    private LopDB lopDB = new LopDB();

    public LopPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("QUẢN LÝ LỚP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lớp"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaLop = new JTextField(15);
        txtTenLop = new JTextField(15);
        cboKhoa = new JComboBox<>();

        loadKhoa(); 

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã lớp:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMaLop, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tên lớp:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTenLop, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Khoa:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboKhoa, gbc);

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
                new Object[]{"Mã lớp", "Tên lớp", "Khoa"}, 0
        ) {
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

        btnAdd.addActionListener(e -> addLop());
        btnUpdate.addActionListener(e -> updateLop());
        btnDelete.addActionListener(e -> deleteLop());
        btnReset.addActionListener(e -> clearForm());
    }

    // ===== LOAD DATA =====
    private void loadData() {
        tableModel.setRowCount(0);
        List<Lop> list = lopDB.getAllActive();

        for (Lop lop : list) {
            tableModel.addRow(new Object[]{
                    lop.getMaLop(),
                    lop.getTenLop(),
                    lop.getTenKhoa()
            });
        }
    }

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        cboKhoa.addItem("CNTT");
        cboKhoa.addItem("QTKD");
        cboKhoa.addItem("KT");
        cboKhoa.addItem("NN");
        cboKhoa.addItem("CK");
        cboKhoa.addItem("DL");
    }

    // ===== FILL FORM =====
    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaLop.setText(table.getValueAt(row, 0).toString());
            txtTenLop.setText(table.getValueAt(row, 1).toString());
            cboKhoa.setSelectedItem(table.getValueAt(row, 2).toString());
            txtMaLop.setEnabled(false);
        }
    }

    // ===== ADD =====
    private void addLop() {
        if (txtMaLop.getText().isEmpty() || txtTenLop.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống dữ liệu!");
            return;
        }

        Lop lop = new Lop();
        lop.setMaLop(txtMaLop.getText().trim());
        lop.setTenLop(txtTenLop.getText().trim());
        lop.setMaKhoa(cboKhoa.getSelectedItem().toString());

        if (lopDB.insert(lop)) {
            JOptionPane.showMessageDialog(this, "Thêm lớp thành công!");
            clearForm();
            loadData();
        }
    }

    // ===== UPDATE =====
    private void updateLop() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn lớp cần sửa!");
            return;
        }

        Lop lop = new Lop();
        lop.setMaLop(txtMaLop.getText());
        lop.setTenLop(txtTenLop.getText());
        lop.setMaKhoa(cboKhoa.getSelectedItem().toString());

        if (lopDB.update(lop)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadData();
        }
    }

    private void deleteLop() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn lớp cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa lớp này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String maLop = txtMaLop.getText();
            if (lopDB.Delete(maLop)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearForm();
                loadData();
            }
        }
    }

    private void clearForm() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        cboKhoa.setSelectedIndex(0);
        txtMaLop.setEnabled(true);
        table.clearSelection();
    }
}
