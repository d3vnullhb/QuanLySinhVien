package model;

import java.util.Date;

public class ThoiKhoaBieu {
    private int maTKB;
    private String maLop;
    private String tenMon;
    private String tenGV;
    private Date ngayHoc;
    private int thu;
    private int tietBatDau;
    private int soTiet;
    private String phong;
    private int hocKy;
    private String namHoc;

    public int getMaTKB() { return maTKB; }
    public void setMaTKB(int maTKB) { this.maTKB = maTKB; }

    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public String getTenGV() { return tenGV; }
    public void setTenGV(String tenGV) { this.tenGV = tenGV; }
    
    public Date getNgayHoc() { return ngayHoc; }
    public void setNgayHoc(Date ngayHoc) { this.ngayHoc = ngayHoc; }


    public int getThu() { return thu; }
    public void setThu(int thu) { this.thu = thu; }

    public int getTietBatDau() { return tietBatDau; }
    public void setTietBatDau(int tietBatDau) { this.tietBatDau = tietBatDau; }

    public int getSoTiet() { return soTiet; }
    public void setSoTiet(int soTiet) { this.soTiet = soTiet; }

    public String getPhong() { return phong; }
    public void setPhong(String phong) { this.phong = phong; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public String getNamHoc() { return namHoc; }
    public void setNamHoc(String namHoc) { this.namHoc = namHoc; }
}
