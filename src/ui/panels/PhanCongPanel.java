package ui.panels;

import dao.PhanCongDB;
import model.PhanCong;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class PhanCongPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cboGiangVien;
    private JComboBox<String> cboMonHoc;
    private JComboBox<String> cboLop;
    private JComboBox<Integer> cboHocKy;
    private JTextField txtNamHoc;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnViewDeleted, btnRestore;

    private PhanCongDB phanCongDB = new PhanCongDB();

    public PhanCongPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        /* ===== TITLE ===== */
        JLabel lblTitle = new JLabel("PHÂN CÔNG GIẢNG DẠY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        /* ===== CENTER ===== */
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        /* ===== FORM ===== */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phân công"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboGiangVien = new JComboBox<>();
        cboMonHoc = new JComboBox<>();
        cboLop = new JComboBox<>();
        cboHocKy = new JComboBox<>(new Integer[]{1, 2, 3});
        txtNamHoc = new JTextField("2024-2025", 15);

        loadGiangVien();
        loadMonHoc();
        loadLop();

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Giảng viên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboGiangVien, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Môn học:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboMonHoc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboLop, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Học kỳ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cboHocKy, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Năm học:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNamHoc, gbc);

        /* ===== BUTTONS ===== */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        btnViewDeleted = new JButton("Đã xóa");
        btnRestore = new JButton("Khôi phục");

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnRestore.setEnabled(false); 

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnViewDeleted);
        btnPanel.add(btnRestore);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        /* ===== TABLE ===== */
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Giảng viên", "Môn học", "Lớp", "Học kỳ", "Năm học"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        /* ===== LOAD DATA ===== */
        loadData();

        /* ===== EVENTS ===== */
        btnAdd.addActionListener(e -> addPhanCong());
        btnUpdate.addActionListener(e -> updatePhanCong());
        btnDelete.addActionListener(e -> deletePhanCong());

        btnClear.addActionListener(e -> {
            loadData();
            clearForm();
            btnRestore.setEnabled(false);
        });

        btnViewDeleted.addActionListener(e -> loadDeletedData());

        btnRestore.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;

            int maPC = (int) table.getValueAt(row, 0);
            if (phanCongDB.restore(maPC)) {
                JOptionPane.showMessageDialog(this, "Khôi phục thành công!");
                loadData();
                clearForm();
                btnRestore.setEnabled(false);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
    }

    /* ================= LOAD ACTIVE ================= */
    private void loadData() {
        tableModel.setRowCount(0);
        List<PhanCong> list = phanCongDB.getAllActive();
        for (PhanCong pc : list) {
            tableModel.addRow(new Object[]{
                    pc.getMaPC(),
                    pc.getTenGV(),
                    pc.getTenMon(),
                    pc.getTenLop(),
                    pc.getHocKy(),
                    pc.getNamHoc()
            });
        }
    }

    /* ================= LOAD DELETED ================= */
    private void loadDeletedData() {
        tableModel.setRowCount(0);
        List<PhanCong> list = phanCongDB.getDeleted();
        for (PhanCong pc : list) {
            tableModel.addRow(new Object[]{
                    pc.getMaPC(),
                    pc.getTenGV(),
                    pc.getTenMon(),
                    pc.getTenLop(),
                    pc.getHocKy(),
                    pc.getNamHoc()
            });
        }
        btnRestore.setEnabled(true);
    }

    /* ================= ADD ================= */
    private void addPhanCong() {
        String namHoc = txtNamHoc.getText().trim();
        if (!namHoc.matches("\\d{4}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Năm học phải có dạng yyyy-yyyy");
            return;
        }

        String maGV = cboGiangVien.getSelectedItem().toString().split(" - ")[0];
        String maMon = cboMonHoc.getSelectedItem().toString().split(" - ")[0];
        String maLop = cboLop.getSelectedItem().toString();
        int hocKy = (int) cboHocKy.getSelectedItem();

        if (phanCongDB.insert(maGV, maMon, maLop, hocKy, namHoc)) {
            JOptionPane.showMessageDialog(this, "Thêm phân công thành công!");
            loadData();
            clearForm();
        }
    }

    /* ================= UPDATE ================= */
    private void updatePhanCong() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int maPC = (int) table.getValueAt(row, 0);

        String namHoc = txtNamHoc.getText().trim();
        if (!namHoc.matches("\\d{4}-\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Năm học phải có dạng yyyy-yyyy");
            return;
        }

        String maGV = cboGiangVien.getSelectedItem().toString().split(" - ")[0];
        String maMon = cboMonHoc.getSelectedItem().toString().split(" - ")[0];
        String maLop = cboLop.getSelectedItem().toString();
        int hocKy = (int) cboHocKy.getSelectedItem();

        if (phanCongDB.update(maPC, maGV, maMon, maLop, hocKy, namHoc)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            clearForm();
        }
    }

    /* ================= DELETE ================= */
    private void deletePhanCong() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int maPC = (int) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(
                this, "Xóa phân công này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            if (phanCongDB.Delete(maPC)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
                clearForm();
            }
        }
    }

    /* ================= FORM ================= */
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String tenGV = table.getValueAt(row, 1).toString();
        String tenMon = table.getValueAt(row, 2).toString();
        String lop = table.getValueAt(row, 3).toString();
        int hocKy = Integer.parseInt(table.getValueAt(row, 4).toString());
        String namHoc = table.getValueAt(row, 5).toString();

        for (int i = 0; i < cboGiangVien.getItemCount(); i++)
            if (cboGiangVien.getItemAt(i).contains(tenGV))
                cboGiangVien.setSelectedIndex(i);

        for (int i = 0; i < cboMonHoc.getItemCount(); i++)
            if (cboMonHoc.getItemAt(i).contains(tenMon))
                cboMonHoc.setSelectedIndex(i);

        cboLop.setSelectedItem(lop);
        cboHocKy.setSelectedItem(hocKy);
        txtNamHoc.setText(namHoc);
    }

    private void clearForm() {
        cboGiangVien.setSelectedIndex(0);
        cboMonHoc.setSelectedIndex(0);
        cboLop.setSelectedIndex(0);
        cboHocKy.setSelectedIndex(0);
        txtNamHoc.setText("2024-2025");
        table.clearSelection();
    }

    /* ================= LOAD COMBO ================= */
    private void loadGiangVien() {
        String sql = "SELECT MaGV, HoTen FROM GiangVien WHERE TrangThai = N'Đang công tác'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cboGiangVien.addItem(rs.getString("MaGV") + " - " + rs.getString("HoTen"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMonHoc() {
        String sql = "SELECT MaMon, TenMon FROM MonHoc WHERE TrangThai = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cboMonHoc.addItem(rs.getString("MaMon") + " - " + rs.getString("TenMon"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLop() {
        String sql = "SELECT MaLop FROM Lop WHERE TrangThai = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cboLop.addItem(rs.getString("MaLop"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
