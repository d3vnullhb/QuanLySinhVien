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
    private JComboBox<String> cboTrangThai;

    private LopDB lopDB = new LopDB();

    public LopPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("QUẢN LÝ LỚP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lớp"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaLop = new JTextField(15);
        txtTenLop = new JTextField(15);
        cboKhoa = new JComboBox<>();
        loadKhoa();

        gbc.gridx=0; gbc.gridy=0; formPanel.add(new JLabel("Mã lớp:"), gbc);
        gbc.gridx=1; formPanel.add(txtMaLop, gbc);

        gbc.gridx=0; gbc.gridy=1; formPanel.add(new JLabel("Tên lớp:"), gbc);
        gbc.gridx=1; formPanel.add(txtTenLop, gbc);

        gbc.gridx=0; gbc.gridy=2; formPanel.add(new JLabel("Khoa:"), gbc);
        gbc.gridx=1; formPanel.add(cboKhoa, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
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

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        formPanel.add(btnPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Mã lớp","Tên lớp","Khoa"},0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadData();

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        cboTrangThai.addActionListener(e -> { clearForm(); loadData(); });

        btnAdd.addActionListener(e -> addLop());
        btnUpdate.addActionListener(e -> updateLop());
        btnDelete.addActionListener(e -> deleteLop());
        btnRestore.addActionListener(e -> restoreLop());
        btnReset.addActionListener(e -> clearForm());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Lop> list = cboTrangThai.getSelectedItem().equals("Đang hoạt động")
                ? lopDB.getAllActive()
                : lopDB.getAllDeleted();

        for (Lop l : list) {
            tableModel.addRow(new Object[]{
                    l.getMaLop(), l.getTenLop(), l.getMaKhoa()
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

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaLop.setText(table.getValueAt(r,0).toString());
        txtTenLop.setText(table.getValueAt(r,1).toString());
        cboKhoa.setSelectedItem(table.getValueAt(r,2).toString());
        txtMaLop.setEnabled(false);
    }

    private void addLop() {
        if (txtMaLop.getText().isEmpty() || txtTenLop.getText().isEmpty()) return;

        Lop lop = new Lop();
        lop.setMaLop(txtMaLop.getText());
        lop.setTenLop(txtTenLop.getText());
        lop.setMaKhoa(cboKhoa.getSelectedItem().toString());

        if (lopDB.insert(lop)) {
            loadData();
            clearForm();
        }
    }

    private void updateLop() {
        if (table.getSelectedRow() < 0) return;

        Lop lop = new Lop();
        lop.setMaLop(txtMaLop.getText());
        lop.setTenLop(txtTenLop.getText());
        lop.setMaKhoa(cboKhoa.getSelectedItem().toString());

        lopDB.update(lop);
        loadData();
    }

    private void deleteLop() {
        if (table.getSelectedRow() < 0) return;
        lopDB.softDelete(txtMaLop.getText());
        loadData();
        clearForm();
    }

    private void restoreLop() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        String maLop = table.getValueAt(r,0).toString();
        lopDB.restore(maLop);
        loadData();
        clearForm();
    }

    private void clearForm() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        cboKhoa.setSelectedIndex(0);
        txtMaLop.setEnabled(true);
        table.clearSelection();
    }
}
