package ui.forms;

import dao.ThoiKhoaBieuDB;
import dao.LopDB;
import dao.MonHocDB;
import dao.GiangVienDB;
import model.Lop;
import model.MonHoc;
import model.GiangVien;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class TKBForm extends JDialog {

    JComboBox<String> cbLop = new JComboBox<>();
    JComboBox<String> cbMon = new JComboBox<>();
    JComboBox<String> cbGV  = new JComboBox<>();

    JTextField txtNgay = new JTextField("2026-01-30");
    JTextField txtThu = new JTextField();
    JTextField txtTiet = new JTextField();
    JTextField txtSoTiet = new JTextField();
    JTextField txtPhong = new JTextField();
    JTextField txtHK = new JTextField();
    JTextField txtNam = new JTextField("2025-2026");

    ThoiKhoaBieuDB db = new ThoiKhoaBieuDB();
    LopDB lopDB = new LopDB();
    MonHocDB monHocDB = new MonHocDB();
    GiangVienDB giangVienDB = new GiangVienDB();

    public TKBForm() {
        setTitle("Nhập thời khóa biểu");
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

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        add(btnSave); add(btnCancel);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadCombobox() {
        for (Lop l : lopDB.getAllActive()) {
            cbLop.addItem(l.getMaLop());
        }

        for (MonHoc m : monHocDB.getAllActive()) {
            cbMon.addItem(m.getMaMon());
        }

        for (GiangVien g : giangVienDB.getAllActive()) {
            cbGV.addItem(g.getMaGV());
        }
    }

    private void save() {
        try {
            db.insert(
                cbLop.getSelectedItem().toString(),
                cbMon.getSelectedItem().toString(),
                cbGV.getSelectedItem().toString(),
                Date.valueOf(txtNgay.getText()),
                Integer.parseInt(txtThu.getText()),
                Integer.parseInt(txtTiet.getText()),
                Integer.parseInt(txtSoTiet.getText()),
                txtPhong.getText(),
                Integer.parseInt(txtHK.getText()),
                txtNam.getText()
            );

            JOptionPane.showMessageDialog(this, "Thêm TKB thành công!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
