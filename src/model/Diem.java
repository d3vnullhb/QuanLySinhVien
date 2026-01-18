package model;

public class Diem {

    private String maSV;
    private String hoTen;    
    private String maMon;
    private int hocKy;
    private String namHoc;
    private int lanThi;
    private String maGV;

    private Double diemChuyenCan;
    private Double diemGiuaKy;
    private Double diemCuoiKy;

    // computed từ DB
    private Double diemTong;
    private String diemChu;
    private String ketQua;

    // ===== GETTER / SETTER =====
    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public int getHocKy() { return hocKy; }
    public void setHocKy(int hocKy) { this.hocKy = hocKy; }

    public String getNamHoc() { return namHoc; }
    public void setNamHoc(String namHoc) { this.namHoc = namHoc; }

    public int getLanThi() { return lanThi; }
    public void setLanThi(int lanThi) { this.lanThi = lanThi; }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public Double getDiemChuyenCan() { return diemChuyenCan; }
    public void setDiemChuyenCan(Double diemChuyenCan) { this.diemChuyenCan = diemChuyenCan; }

    public Double getDiemGiuaKy() { return diemGiuaKy; }
    public void setDiemGiuaKy(Double diemGiuaKy) { this.diemGiuaKy = diemGiuaKy; }

    public Double getDiemCuoiKy() { return diemCuoiKy; }
    public void setDiemCuoiKy(Double diemCuoiKy) { this.diemCuoiKy = diemCuoiKy; }

    public Double getDiemTong() { return diemTong; }
    public void setDiemTong(Double diemTong) { this.diemTong = diemTong; }

    public String getDiemChu() { return diemChu; }
    public void setDiemChu(String diemChu) { this.diemChu = diemChu; }

    public String getKetQua() { return ketQua; }
    public void setKetQua(String ketQua) { this.ketQua = ketQua; }
}
