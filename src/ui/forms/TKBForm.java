package ui.forms;

import dao.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class TKBForm extends JDialog {

    private boolean isEdit = false;
    private int editId;

    JComboBox<String> cbLop = new JComboBox<>();
    JComboBox<String> cbMon = new JComboBox<>();
    JComboBox<String> cbGV  = new JComboBox<>();

    JTextField txtNgay = new JTextField();
    JTextField txtThu = new JTextField();
    JTextField txtTiet = new JTextField();
    JTextField txtSoTiet = new JTextField();
    JTextField txtPhong = new JTextField();
    JTextField txtHK = new JTextField();
    JTextField txtNam = new JTextField();

    ThoiKhoaBieuDB db = new ThoiKhoaBieuDB();
    LopDB lopDB = new LopDB();
    MonHocDB monHocDB = new MonHocDB();
    GiangVienDB giangVienDB = new GiangVienDB();

    public TKBForm(ThoiKhoaBieu t) {
        setTitle(t == null ? "Thêm thời khóa biểu" : "Sửa thời khóa biểu");
        setSize(420, 430);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11,2,5,5));

        loadCombobox();

        add(new JLabel("Lớp")); add(cbLop);
        add(new JLabel("Môn")); add(cbMon);
        add(new JLabel("Giảng viên")); add(cbGV);
        add(new JLabel("Ngày (yyyy-MM-dd)")); add(txtNgay);
        add(new JLabel("Thứ")); add(txtThu);
        add(new JLabel("Tiết BD")); add(txtTiet);
        add(new JLabel("Số tiết")); add(txtSoTiet);
        add(new JLabel("Phòng")); add(txtPhong);
        add(new JLabel("Học kỳ")); add(txtHK);
        add(new JLabel("Năm học")); add(txtNam);

        JButton btnSave = new JButton(t == null ? "Lưu" : "Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        add(btnSave); add(btnCancel);

        if (t != null) fillForm(t);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillForm(ThoiKhoaBieu t) {
        isEdit = true;
        editId = t.getMaTKB();

        cbLop.setSelectedItem(t.getMaLop());
        cbMon.setSelectedItem(t.getMaMon());
        cbGV.setSelectedItem(t.getMaGV());
        txtNgay.setText(t.getNgayHoc().toString());
        txtThu.setText(String.valueOf(t.getThu()));
        txtTiet.setText(String.valueOf(t.getTietBatDau()));
        txtSoTiet.setText(String.valueOf(t.getSoTiet()));
        txtPhong.setText(t.getPhong());
        txtHK.setText(String.valueOf(t.getHocKy()));
        txtNam.setText(t.getNamHoc());
    }

    private void loadCombobox() {
        for (Lop l : lopDB.getAllActive()) cbLop.addItem(l.getMaLop());
        for (MonHoc m : monHocDB.getAllActive()) cbMon.addItem(m.getMaMon());
        for (GiangVien g : giangVienDB.getAllActive()) cbGV.addItem(g.getMaGV());
    }

    private void save() {
        try {
            ThoiKhoaBieu t = new ThoiKhoaBieu();
            t.setMaTKB(editId);
            t.setMaLop(cbLop.getSelectedItem().toString());
            t.setMaMon(cbMon.getSelectedItem().toString());
            t.setMaGV(cbGV.getSelectedItem().toString());
            t.setNgayHoc(Date.valueOf(txtNgay.getText()));
            t.setThu(Integer.parseInt(txtThu.getText()));
            t.setTietBatDau(Integer.parseInt(txtTiet.getText()));
            t.setSoTiet(Integer.parseInt(txtSoTiet.getText()));
            t.setPhong(txtPhong.getText());
            t.setHocKy(Integer.parseInt(txtHK.getText()));
            t.setNamHoc(txtNam.getText());

            if (isEdit) {
                db.update(t);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } else {
                db.insert(
                        t.getMaLop(), t.getMaMon(), t.getMaGV(),
                        t.getNgayHoc(), t.getThu(), t.getTietBatDau(),
                        t.getSoTiet(), t.getPhong(), t.getHocKy(), t.getNamHoc()
                );
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            }
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
