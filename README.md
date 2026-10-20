# VPHONE - Ung dung quan ly ban dien thoai bang Java + MySQL (XAMPP)

Bai tap lon mon Cong nghe Java. Giao dien dong lenh (console). Du lieu doc len cac `ArrayList`
de xu ly (tim kiem, sap xep, thong ke) va **moi thay doi duoc ghi xuong MySQL** qua JDBC.

## Giao dien web (chay tren Chrome)
Ngoai giao dien console, chuong trinh co **giao dien web** dung may chu HTTP co san cua JDK (khong can cai them gi):
1. Bat MySQL trong XAMPP (khong bat cung duoc - chuong trinh se chay che do bo nho).
2. Chay: trong VS Code chon cau hinh **`VPHONE Web (mo bang Chrome)`** roi bam Run; hoac `run.bat web` / `sh run.sh web`;
   hoac go `java -cp "out;lib/*" Web` (them so cong neu can, vi du `Web 9000`).
3. Mo **Chrome**, vao `http://localhost:8080` (chuong trinh cung tu mo trinh duyet mac dinh).
4. Dang nhap bang tai khoan mau (co nut dien san trong hop dang nhap).

Tinh nang: xem / tim kiem / loc khoang gia / sap xep san pham; gio hang; dat hang; huy don; lich su don hang (khach hang).
Quan tri vien them / sua / xoa san pham, xu ly don hang, nha cung cap + lap phieu nhap, thong ke (bieu do), danh sach nguoi dung.
Giao dien goi cac API JSON trong `Web.java`, API goi lai cac lop `*Manager` / `*DAO` nen du lieu duoc luu vao MySQL nhu ban console.
May chu chi nhan ket noi tu chinh may ban (`127.0.0.1`). Cac thong bao loi tren web duoc hien thi tieng Viet co dau (`Vi.java`).

## Yeu cau
- JDK 11 tro len (khuyen dung 17 hoac 21).
- XAMPP, bat module **MySQL** trong XAMPP Control Panel (cong 3306, user `root`, mat khau rong).
- Driver JDBC da co san trong thu muc `lib/` (MariaDB Connector/J - XAMPP dung MariaDB, tuong thich MySQL).

## Cach chay
### Trong VS Code
1. Cai JDK va extension **Extension Pack for Java**.
2. *File -> Open Folder* -> chon thu muc `vphone` (thu muc chua `src`, `lib`, `.vscode`).
3. Bat MySQL trong XAMPP.
4. Mo **Run and Debug** (Ctrl+Shift+D), chon cau hinh va bam Run:
   - `VPHONE (MySQL - XAMPP)` - chay chuong trinh that, luu vao CSDL;
   - `VPHONE demo tu dong (bo nho)` - kich ban tu dong, khong dung CSDL;
   - `VPHONE reset CSDL` - xoa CSDL va tao lai.
   (Hoac mo `Main.java` va bam Run phia tren ham `main`.)

### Bang dong lenh
```
javac -encoding UTF-8 -cp "lib/*" -d out src/*.java
java -cp "out;lib/*" Main              # Windows (Linux/macOS dung dau : thay cho ;)
java -cp "out;lib/*" Main --demo       # kich ban tu dong (bo nho)
java -cp "out;lib/*" Main --reset-db   # xoa va tao lai CSDL
```
Hoac bam doi `run.bat` (Windows) / `sh run.sh` (Linux, macOS); them `demo` hoac `reset` sau ten script.

## Co so du lieu
- Lan chay dau tien ung dung **tu tao CSDL `vphone`, cac bang va nap du lieu mau** (neu CSDL trong).
- Muon tao bang thu cong: phpMyAdmin -> Import -> `sql/schema.sql` (va `sql/data_mau.sql` neu muon co du lieu mau).
- Doi cau hinh (host, cong, user, mat khau, ten CSDL) trong file `db.properties`.
- Khong bat duoc MySQL: chuong trinh bao loi va **tu chuyen sang che do bo nho** (du lieu mat khi tat).
- Dung driver MySQL Connector/J thay vi MariaDB: chep file `.jar` vao `lib/` va dat `db.driver=mysql`.

Cac bang: `nguoi_dung`, `san_pham`, `nha_cung_cap`, `don_hang`, `chi_tiet_don_hang`, `phieu_nhap`,
`chi_tiet_phieu_nhap` (7 bang, 7 khoa ngoai). Gio hang chi luu trong bo nho cho den khi dat hang.

## Tai khoan mau
| Vai tro | Email | Mat khau |
|---|---|---|
| Quan tri vien | admin@vphone.vn | admin123 |
| Khach hang | khach@vphone.vn | 123456 |

(Mat khau luu dang van ban cho don gian; huong phat trien: ma hoa mat khau.)

## Chuc nang
**Quan tri vien**: xem / them / sua / xoa san pham; tim kiem; sap xep theo gia; thong ke; quan ly nha cung cap;
lap phieu nhap (tu dong cong ton kho); quan ly don hang; doanh thu.
**Khach hang**: xem, tim kiem, sap xep san pham; gio hang; dat hang (tru ton kho); lich su; huy don (hoan ton kho).

## Kien truc
```
Main / Demo / Web (+ web/*.html, js, css)  ->  *Manager (ArrayList + logic nghiep vu)  ->  *DAO (JDBC)  ->  MySQL
                      \-> model: NguoiDung, KhachHang, QuanTriVien, SanPham, DienThoai, PhuKien,
                          GioHang, DonHang, PhieuNhap, NhaCungCap ... (IManager<T>)
```
- Ghi CSDL truoc, cap nhat ArrayList sau (loi thi bo nho khong doi).
- Dat hang / huy don / lap phieu nhap dung **giao dich (transaction)**: ghi don + chi tiet + ton kho cung luc,
  loi thi hoan tac (rollback) va tra lai ton kho trong bo nho. Loi CSDL duoc bao bang `DataAccessException`.
- Xoa san pham da co trong don hang/phieu nhap bi khoa ngoai chan va co thong bao ro rang.

## Anh xa voi bao cao
| Noi dung bao cao | Ma nguon |
|---|---|
| Ke thua `NguoiDung`, `SanPham`; da hinh | `NguoiDung`, `KhachHang`, `QuanTriVien`, `SanPham`, `DienThoai`, `PhuKien` |
| Interface | `IManager.java` va cac lop `*Manager` |
| `ArrayList`, `Comparator`, `Collections.sort` | `SanPhamManager` |
| Ngoai le tu dinh nghia | `InvalidPriceException`, `InvalidQuantityException`, `OutOfStockException`, `DataAccessException` |
| Luu do 2.2, 2.3, 2.4 | `Main.nhapSanPhamMoi()`, `DonHangManager.taoDonHang()`, `SanPhamManager.timKiem/sapXepTheoGia` |
| Ket noi MySQL, JDBC, DAO | `DBConnection`, `DatabaseInit`, `*DAO` |
| Giao dien web (Chrome) | `Web.java` (API), `Json.java`, `Vi.java`, thu muc `web/` |

Ten lop trong so do lop (muc 2.1.1) co the khac mot so ten o day (xem bao cao): `IGeneralService` ~ `IManager`,
`*Service` ~ `*Manager`, `HetHangException` ~ `OutOfStockException`.

## Quy uoc
- Thong bao khong dau de tranh loi font tren console.
- Phu kien giam 5% khi ban (`PhuKien.tinhGiaBan()`), de minh hoa da hinh.
- Doanh thu = tong tien cac don o trang thai Hoan thanh.
- Trang thai don: Cho xac nhan -> Dang giao -> Hoan thanh; chi huy duoc don dang Cho xac nhan.
