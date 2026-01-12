package ui.panels;

import dao.GiangVienDB;
import dao.TaiKhoanDB;
import model.GiangVien;
import ui.dialogs.CreateTaiKhoanDialog;
import javax.swing.SwingUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiangVienPanel extends JPanel {

    private JTextField txtMaGV, txtHoTen, txtEmail;
    private JComboBox<String> cboKhoa, cboTaiKhoan;
    private JTable table;
    private DefaultTableModel model;
    private TaiKhoanDB taiKhoanDB = new TaiKhoanDB();
    private GiangVienDB db = new GiangVienDB();

    public GiangVienPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("QUẢN LÝ GIẢNG VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(title, BorderLayout.NORTH);

        /* ========== FORM ========== */
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));

        txtMaGV = new JTextField();
        txtHoTen = new JTextField();
        txtEmail = new JTextField();

        cboKhoa = new JComboBox<>();
        cboTaiKhoan = new JComboBox<>();

        loadKhoa();
        loadTaiKhoan();

        form.add(new JLabel("Mã GV"));
        form.add(txtMaGV);
        form.add(new JLabel("Họ tên"));
        form.add(txtHoTen);

        form.add(new JLabel("Email"));
        form.add(txtEmail);
        form.add(new JLabel("Khoa"));
        form.add(cboKhoa);

        form.add(new JLabel("Tài khoản"));
        form.add(cboTaiKhoan);
        form.add(new JLabel());
        form.add(new JLabel());

        /* ========== BUTTON ========== */
        JPanel buttons = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");
        JButton btnCreateTK = new JButton("+ Tạo TK");

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        buttons.add(btnCreateTK);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        /* ========== TABLE ========== */
        model = new DefaultTableModel(
            new Object[]{"STT","Mã GV","Họ tên","Email","Khoa","Tài khoản","Trạng thái"}, 0
        );
        table = new JTable(model);
        table.setRowHeight(26);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();

        /* ========== EVENTS ========== */
        btnAdd.addActionListener(e -> addGV());
        btnEdit.addActionListener(e -> updateGV());
        btnDelete.addActionListener(e -> deleteGV());
        btnClear.addActionListener(e -> clearForm());

        btnCreateTK.addActionListener(e -> {
            CreateTaiKhoanDialog dlg =
                new CreateTaiKhoanDialog((JFrame) SwingUtilities.getWindowAncestor(this));
            dlg.setVisible(true);

            if (dlg.isCreated()) {
                loadTaiKhoan();
                cboTaiKhoan.setSelectedItem(dlg.getTenDangNhap());
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
             fillForm();
         }
        });

    }

    /* ========== LOAD ========== */

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        for (String mk : db.getAllMaKhoa()) {
            cboKhoa.addItem(mk);
        }
    }

    private void loadTaiKhoan() {
     cboTaiKhoan.removeAllItems();
     for (String tk : taiKhoanDB.getAllTenDangNhapGV()) {
         cboTaiKhoan.addItem(tk);
        }
    }


    private void loadData() {
        model.setRowCount(0);
        List<GiangVien> list = db.getAll();
        int stt = 1;
        for (GiangVien gv : list) {
            model.addRow(new Object[]{
                stt++,
                gv.getMaGV(),
                gv.getHoTen(),
                gv.getEmail(),
                gv.getMaKhoa(),
                gv.getTenDangNhap(),
                gv.getTrangThai()
            });
        }
    }

    /* ========== CRUD ========== */

        private void addGV() {
        if (txtMaGV.getText().isBlank() || txtHoTen.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Không được để trống");
            return;
        }

        if (cboTaiKhoan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo hoặc chọn tài khoản");
            return;
        }

        boolean ok = db.insert(
            txtMaGV.getText().trim(),
            txtHoTen.getText().trim(),
            txtEmail.getText().trim(),
            cboKhoa.getSelectedItem().toString(),
            cboTaiKhoan.getSelectedItem().toString()
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm thành công");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại");
        }
    }


    private void updateGV() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String maGV = table.getValueAt(row, 1).toString();

        db.update(
            maGV,
            txtHoTen.getText().trim(),
            txtEmail.getText().trim(),
            cboKhoa.getSelectedItem().toString(),
            cboTaiKhoan.getSelectedItem().toString()
        );
        loadData();
    }

    private void deleteGV() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) return;

        if (JOptionPane.showConfirmDialog(this, "Xóa giảng viên?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        for (int r : rows) {
            db.softDelete(table.getValueAt(r, 1).toString());
        }
        loadData();
        clearForm();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        txtMaGV.setText(table.getValueAt(r, 1).toString());
        txtHoTen.setText(table.getValueAt(r, 2).toString());
        txtEmail.setText(table.getValueAt(r, 3).toString());
        cboKhoa.setSelectedItem(table.getValueAt(r, 4).toString());
        cboTaiKhoan.setSelectedItem(table.getValueAt(r, 5).toString());

        txtMaGV.setEditable(false);
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtMaGV.setEditable(true);
        if (cboKhoa.getItemCount() > 0) cboKhoa.setSelectedIndex(0);
        if (cboTaiKhoan.getItemCount() > 0) cboTaiKhoan.setSelectedIndex(0);
        table.clearSelection();
    }
}
