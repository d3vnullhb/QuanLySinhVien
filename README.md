# 🎓 HỆ THỐNG QUẢN LÝ SINH VIÊN (JAVA SWING + SQL SERVER)

## 📌 Giới thiệu
Đây là đồ án môn học xây dựng hệ thống **Quản lý sinh viên** bằng **Java Swing** kết nối **SQL Server**.  
Hệ thống mô phỏng quy trình quản lý sinh viên của một trường học với đầy đủ các nghiệp vụ quản lý cơ bản và nâng cao.

---

## 🛠️ Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 8+)
- **Giao diện:** Java Swing
- **IDE:** NetBeans
- **Cơ sở dữ liệu:** Microsoft SQL Server
- **Kết nối DB:** JDBC
- **Quản lý mã nguồn:** Git & GitHub

---

## ⚙️ Hướng dẫn cài đặt & chạy chương trình

### 1️⃣ Tạo cơ sở dữ liệu
Mở **SQL Server Management Studio** và chạy file:

database/QLSV.sql


File này bao gồm:
- Tạo bảng dữ liệu
- Ràng buộc **FOREIGN KEY**, **CHECK**
- Dữ liệu mẫu ban đầu

---

### 2️⃣ Cấu hình kết nối database
Mở file:

src/util/DBConnection.java


Sửa thông tin cho đúng máy:
```java
private static final String URL =
    "jdbc:sqlserver://localhost:1433;"
  + "databaseName=QuanLySinhVien;"
  + "encrypt=true;trustServerCertificate=true";

private static final String USER = "sa";
private static final String PASS = "106204";
3️⃣ Chạy chương trình
Mở project bằng NetBeans

Run file:
src/ui/LoginFrame.java

Tài khoản demo
Username: admin01
Password: 123456
