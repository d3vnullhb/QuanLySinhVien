CREATE DATABASE QuanLySinhVien;
GO
USE QuanLySinhVien;
GO
CREATE TABLE Khoa (
    MaKhoa VARCHAR(10) PRIMARY KEY,
    TenKhoa NVARCHAR(100) NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1
);
CREATE TABLE Lop (
    MaLop VARCHAR(10) PRIMARY KEY,
    TenLop NVARCHAR(100) NOT NULL,
    MaKhoa VARCHAR(10) NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_Lop_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);
CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(255) NOT NULL,     
    Salt VARCHAR(50) NOT NULL,
    VaiTro NVARCHAR(20) NOT NULL
        CHECK (VaiTro IN (N'ADMIN', N'GIANGVIEN', N'SINHVIEN')),
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Hoạt động'
        CHECK (TrangThai IN (N'Hoạt động', N'Bị khóa', N'Vô hiệu hóa')),
    NgayTao DATETIME DEFAULT GETDATE(),
    LastLogin DATETIME
);
CREATE TABLE SinhVien (
    MaSV VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(10)
        CHECK (GioiTinh IN (N'Nam', N'Nữ')),
    DiaChi NVARCHAR(255),
    SoDienThoai VARCHAR(15),

    MaLop VARCHAR(10) NOT NULL,
    TenDangNhap VARCHAR(50) NULL, 

    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang học'
        CHECK (TrangThai IN (N'Đang học', N'Bảo lưu', N'Thôi học', N'Tốt nghiệp')),

    CONSTRAINT FK_SV_Lop FOREIGN KEY (MaLop) REFERENCES Lop(MaLop),
    CONSTRAINT FK_SV_TaiKhoan FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoan(TenDangNhap)
);
CREATE TABLE GiangVien (
    MaGV VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE,
    MaKhoa VARCHAR(10) NOT NULL,
    TenDangNhap VARCHAR(50) NULL,

    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Đang công tác'
        CHECK (TrangThai IN (N'Đang công tác', N'Nghỉ phép', N'Nghỉ việc')),

    CONSTRAINT FK_GV_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa),
    CONSTRAINT FK_GV_TaiKhoan FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoan(TenDangNhap)
);
CREATE TABLE MonHoc (
    MaMon VARCHAR(10) PRIMARY KEY,
    TenMon NVARCHAR(100) NOT NULL,
    SoTinChi INT NOT NULL CHECK (SoTinChi > 0),
    TrangThai BIT NOT NULL DEFAULT 1
);
CREATE TABLE PhanCong (
    MaPC INT IDENTITY(1,1) PRIMARY KEY,
    MaGV VARCHAR(10) NOT NULL,
    MaMon VARCHAR(10) NOT NULL,
    MaLop VARCHAR(10) NOT NULL,
    HocKy INT NOT NULL CHECK (HocKy IN (1,2,3)),
    NamHoc VARCHAR(9) NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_PC_GV  FOREIGN KEY (MaGV) REFERENCES GiangVien(MaGV),
    CONSTRAINT FK_PC_Mon FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon),
    CONSTRAINT FK_PC_Lop FOREIGN KEY (MaLop) REFERENCES Lop(MaLop),

    CONSTRAINT UQ_PhanCong UNIQUE (MaGV, MaMon, MaLop, HocKy, NamHoc)
);
CREATE TABLE ThoiKhoaBieu (
    MaTKB INT IDENTITY(1,1) PRIMARY KEY,
    MaLop VARCHAR(10) NOT NULL,
    MaMon VARCHAR(10) NOT NULL,
    MaGV  VARCHAR(10) NOT NULL,
    NgayHoc DATE NOT NULL,  

    Thu INT NOT NULL CHECK (Thu BETWEEN 2 AND 8),
    TietBatDau INT NOT NULL CHECK (TietBatDau BETWEEN 1 AND 15),
    SoTiet INT NOT NULL CHECK (SoTiet BETWEEN 1 AND 5),
    Phong NVARCHAR(20),

    HocKy INT NOT NULL CHECK (HocKy IN (1,2,3)),
    NamHoc VARCHAR(9) NOT NULL,

    TrangThai BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_TKB_Lop FOREIGN KEY (MaLop) REFERENCES Lop(MaLop),
    CONSTRAINT FK_TKB_Mon FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon),
    CONSTRAINT FK_TKB_GV  FOREIGN KEY (MaGV)  REFERENCES GiangVien(MaGV)
);

CREATE TABLE Diem (
    MaSV VARCHAR(10) NOT NULL,
    MaMon VARCHAR(10) NOT NULL,
    HocKy INT NOT NULL CHECK (HocKy IN (1, 2, 3)),
    NamHoc VARCHAR(9) NOT NULL,
    LanThi INT NOT NULL DEFAULT 1 CHECK (LanThi >= 1),
    MaGV VARCHAR(10) NOT NULL,

    DiemChuyenCan FLOAT CHECK (DiemChuyenCan BETWEEN 0 AND 10),
    DiemGiuaKy FLOAT CHECK (DiemGiuaKy BETWEEN 0 AND 10),
    DiemCuoiKy FLOAT CHECK (DiemCuoiKy BETWEEN 0 AND 10),

    -- 1. Tính điểm tổng kết hệ 10
    DiemTong AS (
        CAST(
            DiemChuyenCan * 0.1 + 
            DiemGiuaKy   * 0.3 + 
            DiemCuoiKy  * 0.6 
        AS FLOAT)
    ) PERSISTED,

    -- 2. Chuyển đổi sang Điểm Chữ (A, B, C, D, F)
    DiemChu AS (
        CASE 
            WHEN (DiemChuyenCan * 0.1 + DiemGiuaKy * 0.3 + DiemCuoiKy * 0.6) >= 8.5 THEN 'A'
            WHEN (DiemChuyenCan * 0.1 + DiemGiuaKy * 0.3 + DiemCuoiKy * 0.6) >= 7.0 THEN 'B'
            WHEN (DiemChuyenCan * 0.1 + DiemGiuaKy * 0.3 + DiemCuoiKy * 0.6) >= 5.5 THEN 'C'
            WHEN (DiemChuyenCan * 0.1 + DiemGiuaKy * 0.3 + DiemCuoiKy * 0.6) >= 4.0 THEN 'D'
            ELSE 'F'
        END
    ),

    -- 3. Xác định Kết quả (Đạt hoặc Học lại)
    KetQua AS (
        CASE 
            WHEN (DiemChuyenCan * 0.1 + DiemGiuaKy * 0.3 + DiemCuoiKy * 0.6) >= 4.0 THEN N'Đạt'
            ELSE N'Học lại'
        END
    ),

    CONSTRAINT PK_Diem PRIMARY KEY (MaSV, MaMon, HocKy, NamHoc, LanThi),
    CONSTRAINT FK_Diem_SV FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV),
    CONSTRAINT FK_Diem_Mon FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon),
    CONSTRAINT FK_Diem_GV FOREIGN KEY (MaGV) REFERENCES GiangVien(MaGV)
);
INSERT INTO Khoa (MaKhoa, TenKhoa)
VALUES
('CNTT', N'Công nghệ thông tin'),
('QTKD', N'Quản trị kinh doanh'),
('KT',   N'Kế toán'),
('NN',   N'Ngôn ngữ Anh'),
('CK',   N'Cơ khí'),
('DL',   N'Du lịch');

INSERT INTO Lop (MaLop, TenLop, MaKhoa)
VALUES
('CTT1', N'CNTT 1', 'CNTT'),
('CTT2', N'CNTT 2', 'CNTT'),
('KD1',  N'QTKD 1', 'QTKD'),
('KT1',  N'Kế toán 1', 'KT'),
('NN1',  N'Ngôn ngữ Anh 1', 'NN'),
('DL1',  N'Du lịch 1', 'DL');


INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Salt, VaiTro)
VALUES
('admin01', '123456', 's1', 'ADMIN'),
('admin02', '123456', 's2', 'ADMIN'),
('gv01',    '123456', 's3', 'GIANGVIEN'),
('gv02',    '123456', 's4', 'GIANGVIEN'),
('gv03',    '123456', 's5', 'GIANGVIEN'),
('gv04',    '123456', 's6', 'GIANGVIEN'),
('gv05',    '123456', 's7', 'GIANGVIEN'),
('gv06',    '123456', 's8', 'GIANGVIEN'),
('bao.ch',  '123456', 's9', 'SINHVIEN'),
('bao.qh',  '123456', 's10','SINHVIEN'),
('khang.qv','123456', 's11','SINHVIEN'),
('lan.nt',  '123456', 's12','SINHVIEN'),
('thimai',  '123456', 's13','SINHVIEN'),
('levannam','123456', 's14','SINHVIEN');


INSERT INTO GiangVien (MaGV, HoTen, Email, MaKhoa, TenDangNhap, TrangThai)
VALUES
('GV01', N'Nguyễn Văn An',  'an@edu.vn',    'CNTT', 'gv01', N'Đang công tác'),
('GV02', N'Trần Thị Bình',  'binh@edu.vn',  'QTKD', 'gv02', N'Đang công tác'),
('GV03', N'Lê Văn Cường',   'cuong@edu.vn', 'KT',   'gv03', N'Đang công tác'),
('GV04', N'Phạm Thị Dung',  'dung@edu.vn',  'NN',   'gv04', N'Đang công tác'),
('GV05', N'Hoàng Văn Em',   'em@edu.vn',    'CK',   'gv05', N'Đang công tác'),
('GV06', N'Võ Thị Hoa',     'hoa@edu.vn',   'DL',   'gv06', N'Đang công tác');

INSERT INTO SinhVien
(MaSV, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, MaLop, TenDangNhap, TrangThai)
VALUES
('SV01', N'CAO HOÀI BẢO',   '2004-06-10', N'Nam', N'Kiên Giang', '0901111111', 'CTT1', 'bao.ch',  N'Đang học'),
('SV02', N'QUÁCH HOÀNG BẢO','2004-08-20', N'Nam', N'Cà Mau',    '0902222222', 'CTT1', 'bao.qh',  N'Đang học'),
('SV03', N'QUÁCH VĨ KHANG','2026-01-15', N'Nam', N'Kiên Giang','0911040031', 'CTT1', 'khang.qv',N'Đang học'),
('SV04', N'NGUYỄN THỊ LAN', '2026-01-18', N'Nam', N'Cà Mau',    '0912456987', 'CTT1', 'lan.nt',  N'Đang học'),
('SV05', N'TRẦN THỊ MAI',   '2026-01-23', N'Nữ',  N'Cần Thơ',   '091949911',  'CTT1', 'thimai',  N'Đang học'),
('SV06', N'LÊ VĂN NAM',     '2026-01-18', N'Nam', N'Cần Thơ',   '0911878123', 'CTT1', 'levannam',N'Đang học');

INSERT INTO MonHoc (MaMon, TenMon, SoTinChi)
VALUES
('JAVA', N'Lập trình Java', 3),
('CSDL', N'Cơ sở dữ liệu', 3),
('WEB',  N'Lập trình Web', 3),
('KTCT', N'Kinh tế chính trị', 2),
('TA',   N'Tiếng Anh', 2),
('MARK', N'Marketing căn bản', 2);

INSERT INTO PhanCong (MaGV, MaMon, MaLop, HocKy, NamHoc)
VALUES
('GV01','JAVA','CTT1',1,'2024-2025'),
('GV01','CSDL','CTT2',1,'2024-2025'),
('GV02','MARK','KD1',1,'2024-2025'),
('GV03','KTCT','KT1',1,'2024-2025'),
('GV04','TA','NN1',1,'2024-2025'),
('GV06','TA','DL1',1,'2024-2025');

INSERT INTO ThoiKhoaBieu
(MaLop, MaMon, MaGV, NgayHoc, Thu, TietBatDau, SoTiet, Phong, HocKy, NamHoc, TrangThai)
VALUES
('CTT1', 'JAVA', 'GV01', '2026-01-26', 2, 1, 3, 'I304', 1, '2025-2026', 1),
('CTT2', 'CSDL', 'GV01', '2026-01-27', 3, 4, 3, 'I601', 1, '2025-2026', 1),
('KD1',  'MARK', 'GV02', '2026-01-28', 4, 1, 2, 'I205', 1, '2025-2026', 1),
('KT1',  'KTCT', 'GV03', '2026-01-29', 5, 7, 3, 'I403', 1, '2025-2026', 1),
('NN1',  'TA',   'GV04', '2026-01-30', 6, 1, 2, 'I401', 1, '2025-2026', 1),
('DL1',  'TA',   'GV06', '2026-01-31', 7, 4, 2, 'I406', 1, '2025-2026', 1);

INSERT INTO Diem
(MaSV, MaMon, HocKy, NamHoc, LanThi, MaGV,
 DiemChuyenCan, DiemGiuaKy, DiemCuoiKy)
VALUES
('SV01','JAVA',1,'2024-2025',1,'GV01',8,7,9),
('SV02','JAVA',1,'2024-2025',1,'GV01',9,8,8),
('SV03','CSDL',1,'2024-2025',1,'GV01',7,7,8),

('SV04','KTCT',1,'2024-2025',1,'GV03',8,8,7),
('SV05','TA',1,'2024-2025',1,'GV04',9,9,9),
('SV06','TA',1,'2024-2025',1,'GV06',6,7,7);

