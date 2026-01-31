package ui.panels;

import dao.SinhVienDB;
import model.SinhVien;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class SinhVienPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaSV, txtHoTen, txtDiaChi, txtSDT;
    private JComboBox<String> cboGioiTinh, cboLop, cboTrangThai;
    private JSpinner spNgaySinh;

    private JButton btnAdd, btnUpdate, btnDelete, btnRestore, btnClear;

    private SinhVienDB sinhVienDB = new SinhVienDB();

    public SinhVienPanel() {
        setLayout(new BorderLayout());

        /* ===== FORM ===== */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin sinh viên"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSV = new JTextField(12);
        txtHoTen = new JTextField(12);
        txtDiaChi = new JTextField(12);
        txtSDT = new JTextField(12);

        spNgaySinh = new JSpinner(new SpinnerDateModel());
        spNgaySinh.setEditor(new JSpinner.DateEditor(spNgaySinh,"dd/MM/yyyy"));

        cboGioiTinh = new JComboBox<>(new String[]{"Nam","Nữ"});
        cboLop = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"Đang học","Đã xóa"});

        loadLop();

        int r = 0;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Mã SV"),gbc);
        gbc.gridx=1; form.add(txtMaSV,gbc);
        gbc.gridx=2; form.add(new JLabel("Họ tên"),gbc);
        gbc.gridx=3; form.add(txtHoTen,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Ngày sinh"),gbc);
        gbc.gridx=1; form.add(spNgaySinh,gbc);
        gbc.gridx=2; form.add(new JLabel("Giới tính"),gbc);
        gbc.gridx=3; form.add(cboGioiTinh,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Địa chỉ"),gbc);
        gbc.gridx=1; form.add(txtDiaChi,gbc);
        gbc.gridx=2; form.add(new JLabel("SĐT"),gbc);
        gbc.gridx=3; form.add(txtSDT,gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(new JLabel("Lớp"),gbc);
        gbc.gridx=1; form.add(cboLop,gbc);

        /* ===== BUTTONS ===== */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRestore = new JButton("Khôi phục");
        btnClear = new JButton("Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRestore);
        btnPanel.add(new JLabel("Xem:"));
        btnPanel.add(cboTrangThai);
        btnPanel.add(btnClear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form,BorderLayout.CENTER);
        top.add(btnPanel,BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new Object[]{"Mã SV","Họ tên","Ngày sinh","Giới tính","Địa chỉ","SĐT","Lớp"},0
        ){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, new JScrollPane(table));
        split.setResizeWeight(0.35);

        add(split,BorderLayout.CENTER);

        loadData();

        /* ===== EVENTS ===== */
        btnAdd.addActionListener(e -> addSV());
        btnUpdate.addActionListener(e -> updateSV());
        btnDelete.addActionListener(e -> deleteSV());
        btnRestore.addActionListener(e -> restoreSV());
        btnClear.addActionListener(e -> clearForm());
        cboTrangThai.addActionListener(e -> { clearForm(); loadData(); });
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadData() {
        model.setRowCount(0);
        boolean active = cboTrangThai.getSelectedItem().equals("Đang học");
        var list = active ? sinhVienDB.getAllActive() : sinhVienDB.getAllDeleted();

        for (SinhVien sv : list) {
            model.addRow(new Object[]{
                sv.getMaSV(), sv.getHoTen(), sv.getNgaySinh(),
                sv.getGioiTinh(), sv.getDiaChi(), sv.getSoDienThoai(),
                sv.getMaLop()
            });
        }
    }

    private void addSV() {
        SinhVien sv = buildSV();
        if (sinhVienDB.insert(sv)) {
            loadData();
            clearForm();
        }
    }

    private void updateSV() {
        if (table.getSelectedRow()<0) return;
        sinhVienDB.update(buildSV());
        loadData();
        clearForm();
    }

    private void deleteSV() {
        if (table.getSelectedRow()<0) return;
        sinhVienDB.softDelete(model.getValueAt(table.getSelectedRow(),0).toString());
        loadData();
        clearForm();
    }

    private void restoreSV() {
        if (table.getSelectedRow()<0) return;
        sinhVienDB.restore(model.getValueAt(table.getSelectedRow(),0).toString());
        loadData();
        clearForm();
    }

    private SinhVien buildSV() {
        SinhVien sv = new SinhVien();
        sv.setMaSV(txtMaSV.getText());
        sv.setHoTen(txtHoTen.getText());
        sv.setNgaySinh((Date) spNgaySinh.getValue());
        sv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
        sv.setDiaChi(txtDiaChi.getText());
        sv.setSoDienThoai(txtSDT.getText());
        sv.setMaLop(cboLop.getSelectedItem().toString());
        return sv;
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r<0) return;

        txtMaSV.setText(model.getValueAt(r,0).toString());
        txtMaSV.setEditable(false);
        txtHoTen.setText(model.getValueAt(r,1).toString());
        spNgaySinh.setValue(model.getValueAt(r,2));
        cboGioiTinh.setSelectedItem(model.getValueAt(r,3));
        txtDiaChi.setText(model.getValueAt(r,4).toString());
        txtSDT.setText(model.getValueAt(r,5).toString());
        cboLop.setSelectedItem(model.getValueAt(r,6));
    }

    private void loadLop() {
        try (var c = DBConnection.getConnection();
             var ps = c.prepareStatement("SELECT MaLop FROM Lop WHERE TrangThai=1");
             var rs = ps.executeQuery()) {
            while (rs.next()) cboLop.addItem(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearForm() {
        txtMaSV.setText("");
        txtMaSV.setEditable(true);
        txtHoTen.setText("");
        txtDiaChi.setText("");
        txtSDT.setText("");
        spNgaySinh.setValue(new Date());
        table.clearSelection();
    }
}
