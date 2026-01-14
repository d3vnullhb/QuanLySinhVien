package model;

public class GiangVien {

    private String maGV;          // PK
    private String hoTen;
    private String email;
    private String maKhoa;
    private String tenDangNhap;
    private String trangThai;

    public GiangVien() {}

    public GiangVien(String maGV, String hoTen, String email,
                     String maKhoa, String tenDangNhap, String trangThai) {
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.email = email;
        this.maKhoa = maKhoa;
        this.tenDangNhap = tenDangNhap;
        this.trangThai = trangThai;
    }

    public String getMaGV() {
        return maGV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
