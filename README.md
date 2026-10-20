# VPHONE - Ứng dụng quản lý bán điện thoại bằng Java + MySQL (XAMPP)

Bài tập lớn môn Công nghệ Java. Chương trình có giao diện dòng lệnh (console). Dữ liệu được đọc vào các `ArrayList` để xử lý (tìm kiếm, sắp xếp, thống kê) và **mọi thay đổi đều được ghi xuống MySQL thông qua JDBC**.

## Giao diện web (chạy trên Chrome)

Ngoài giao diện console, chương trình còn có **giao diện web** sử dụng máy chủ HTTP có sẵn của JDK (không cần cài đặt thêm thư viện máy chủ):

1. Bật MySQL trong XAMPP (nếu không bật, chương trình vẫn có thể chạy ở chế độ bộ nhớ).
2. Chạy chương trình bằng một trong các cách sau:
   - Trong VS Code, chọn cấu hình **`VPHONE Web (mở bằng Chrome)`** rồi nhấn Run.
   - Chạy lệnh `run.bat web` hoặc `sh run.sh web`.
   - Hoặc chạy trực tiếp: `java -cp "out;lib/*" Web` (có thể thêm số cổng, ví dụ: `Web 9000`).
3. Mở Chrome và truy cập `http://localhost:8080` (chương trình cũng tự mở trình duyệt mặc định).
4. Đăng nhập bằng tài khoản mẫu (có nút điền sẵn thông tin trong hộp đăng nhập).

### Tính năng giao diện web

- **Khách hàng:** Xem, tìm kiếm, lọc sản phẩm theo khoảng giá, sắp xếp sản phẩm, quản lý giỏ hàng, đặt hàng, hủy đơn hàng và xem lịch sử đơn hàng.
- **Quản trị viên:** Thêm, sửa, xóa sản phẩm; xử lý đơn hàng; quản lý nhà cung cấp; lập phiếu nhập; xem thống kê bằng biểu đồ và quản lý danh sách người dùng.

Giao diện web gọi các API JSON trong `Web.java`. Các API này gọi lại những lớp `*Manager` và `*DAO`, vì vậy dữ liệu được lưu vào MySQL tương tự như khi sử dụng giao diện console.

Máy chủ chỉ chấp nhận kết nối từ chính máy tính đang chạy chương trình (`127.0.0.1`). Các thông báo lỗi trên giao diện web được hiển thị bằng tiếng Việt có dấu thông qua `Vi.java`.

## Yêu cầu hệ thống

- JDK 11 trở lên (khuyến nghị JDK 17 hoặc 21).
- XAMPP, bật mô-đun **MySQL** trong XAMPP Control Panel.
- Cấu hình mặc định của MySQL: cổng `3306`, tài khoản `root`, mật khẩu để trống.
- Trình điều khiển JDBC đã có sẵn trong thư mục `lib/` (MariaDB Connector/J, tương thích với MariaDB đi kèm XAMPP).

## Cách chạy chương trình

### 1. Chạy trong VS Code

1. Cài đặt JDK và extension **Extension Pack for Java**.
2. Chọn **File → Open Folder**, sau đó mở thư mục `vphone` (thư mục chứa `src`, `lib`, `.vscode`).
3. Bật MySQL trong XAMPP.
4. Mở **Run and Debug** bằng tổ hợp phím `Ctrl + Shift + D`, chọn cấu hình và nhấn Run:
   - `VPHONE (MySQL - XAMPP)`: Chạy chương trình thực tế và lưu dữ liệu vào cơ sở dữ liệu.
   - `VPHONE demo tự động (bộ nhớ)`: Chạy kịch bản tự động, không sử dụng cơ sở dữ liệu.
   - `VPHONE reset CSDL`: Xóa cơ sở dữ liệu và tạo lại từ đầu.

Bạn cũng có thể mở `Main.java` và nhấn Run phía trên hàm `main`.

### 2. Chạy bằng dòng lệnh

Biên dịch chương trình:

```bash
javac -encoding UTF-8 -cp "lib/*" -d out src/*.java
```

Chạy chương trình thông thường:

```bash
java -cp "out;lib/*" Main
```

Chạy kịch bản tự động ở chế độ bộ nhớ:

```bash
java -cp "out;lib/*" Main --demo
```

Xóa và tạo lại cơ sở dữ liệu:

```bash
java -cp "out;lib/*" Main --reset-db
```

**Lưu ý:** Các lệnh trên sử dụng dấu chấm phẩy (`;`) để phân tách classpath trên Windows. Trên Linux hoặc macOS, hãy thay bằng dấu hai chấm (`:`).

Bạn cũng có thể chạy bằng các tập lệnh có sẵn:

- Windows: `run.bat`
- Linux/macOS: `sh run.sh`

Có thể thêm tham số `demo` hoặc `reset` sau tên tập lệnh để chạy chế độ tương ứng.

## Cơ sở dữ liệu

Trong lần chạy đầu tiên, ứng dụng sẽ tự tạo cơ sở dữ liệu `vphone`, các bảng cần thiết và nạp dữ liệu mẫu nếu cơ sở dữ liệu chưa có dữ liệu.

Nếu muốn tạo bảng thủ công:

1. Mở phpMyAdmin.
2. Chọn chức năng **Import**.
3. Nhập tệp `sql/schema.sql`.
4. Nếu muốn có dữ liệu mẫu, nhập thêm tệp `sql/data_mau.sql`.

### Cấu hình kết nối

Có thể thay đổi thông tin kết nối trong tệp `db.properties`, bao gồm:

- Máy chủ (host).
- Cổng kết nối.
- Tên người dùng.
- Mật khẩu.
- Tên cơ sở dữ liệu.

Nếu không kết nối được MySQL, chương trình sẽ thông báo lỗi và tự chuyển sang chế độ bộ nhớ. Trong chế độ này, dữ liệu chỉ tồn tại khi chương trình đang chạy và sẽ mất khi tắt chương trình.

Nếu muốn sử dụng MySQL Connector/J thay cho MariaDB Connector/J, hãy sao chép tệp `.jar` tương ứng vào thư mục `lib/` và đặt `db.driver=mysql`.

### Các bảng trong cơ sở dữ liệu

Cơ sở dữ liệu gồm 7 bảng và 7 khóa ngoại:

- `nguoi_dung`
- `san_pham`
- `nha_cung_cap`
- `don_hang`
- `chi_tiet_don_hang`
- `phieu_nhap`
- `chi_tiet_phieu_nhap`

Giỏ hàng chỉ được lưu trong bộ nhớ cho đến khi khách hàng đặt hàng.

## Tài khoản mẫu

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Quản trị viên | `admin@vphone.vn` | `admin123` |
| Khách hàng | `khach@vphone.vn` | `123456` |

**Lưu ý:** Mật khẩu hiện được lưu dưới dạng văn bản thuần để đơn giản hóa bài tập. Trong thực tế, nên sử dụng các thuật toán băm mật khẩu an toàn.

## Chức năng chính

### Quản trị viên

- Xem, thêm, sửa và xóa sản phẩm.
- Tìm kiếm sản phẩm.
- Sắp xếp sản phẩm theo giá.
- Xem thống kê.
- Quản lý nhà cung cấp.
- Lập phiếu nhập hàng và tự động cập nhật tồn kho.
- Quản lý, xử lý đơn hàng.
- Theo dõi doanh thu.

### Khách hàng

- Xem danh sách sản phẩm.
- Tìm kiếm và sắp xếp sản phẩm.
- Thêm sản phẩm vào giỏ hàng.
- Đặt hàng và tự động trừ tồn kho.
- Xem lịch sử mua hàng.
- Hủy đơn hàng và hoàn lại tồn kho theo quy định của chương trình.

## Kiến trúc chương trình

```text
Main / Demo / Web (+ web/*.html, js, css)
                  |
                  v
        *Manager (ArrayList
        + logic nghiệp vụ)
                  |
                  v
             *DAO (JDBC)
                  |
                  v
                MySQL

Các lớp mô hình:
NguoiDung, KhachHang, QuanTriVien,
SanPham, DienThoai, PhuKien, GioHang,
DonHang, PhieuNhap, NhaCungCap, ...
(IManager<T>)
```

### Nguyên tắc xử lý dữ liệu

- Ghi dữ liệu vào cơ sở dữ liệu trước, sau đó mới cập nhật `ArrayList`. Nếu thao tác ghi thất bại, dữ liệu trong bộ nhớ không bị thay đổi.
- Các thao tác đặt hàng, hủy đơn hàng và lập phiếu nhập sử dụng **giao dịch (transaction)** để bảo đảm tính nhất quán giữa dữ liệu đơn hàng, chi tiết đơn hàng và tồn kho.
- Nếu xảy ra lỗi, giao dịch được hoàn tác bằng `rollback`; tồn kho trong bộ nhớ cũng được khôi phục.
- Lỗi truy cập cơ sở dữ liệu được biểu diễn bằng `DataAccessException`.
- Không thể xóa sản phẩm đã được sử dụng trong đơn hàng hoặc phiếu nhập nếu bị ràng buộc khóa ngoại. Chương trình sẽ hiển thị thông báo lỗi phù hợp.

## Đối chiếu với nội dung báo cáo

| Nội dung báo cáo | Mã nguồn tương ứng |
|---|---|
| Kế thừa `NguoiDung`, `SanPham`; đa hình | `NguoiDung`, `KhachHang`, `QuanTriVien`, `SanPham`, `DienThoai`, `PhuKien` |
| Interface | `IManager.java` và các lớp `*Manager` |
| `ArrayList`, `Comparator`, `Collections.sort` | `SanPhamManager` |
| Ngoại lệ tự định nghĩa | `InvalidPriceException`, `InvalidQuantityException`, `OutOfStockException`, `DataAccessException` |
| Lưu đồ 2.2, 2.3, 2.4 | `Main.nhapSanPhamMoi()`, `DonHangManager.taoDonHang()`, `SanPhamManager.timKiem()` / `sapXepTheoGia()` |
| Kết nối MySQL, JDBC, DAO | `DBConnection`, `DatabaseInit`, các lớp `*DAO` |
| Giao diện web chạy trên Chrome | `Web.java` (API), `Json.java`, `Vi.java`, thư mục `web/` |

**Lưu ý khi đối chiếu báo cáo:** Tên lớp trong sơ đồ lớp ở mục 2.1.1 có thể khác một số tên trong mã nguồn. Ví dụ:

- `IGeneralService` tương ứng với `IManager`.
- Các lớp `*Service` tương ứng với các lớp `*Manager`.
- `HetHangException` tương ứng với `OutOfStockException`.

## Quy ước và nghiệp vụ

- Các thông báo trên console không dấu nhằm hạn chế lỗi hiển thị font chữ.
- Phụ kiện được giảm giá 5% khi bán thông qua phương thức `PhuKien.tinhGiaBan()`, nhằm minh họa tính đa hình.
- Doanh thu được tính bằng tổng tiền của các đơn hàng ở trạng thái **Hoàn thành**.
- Quy trình trạng thái đơn hàng: **Chờ xác nhận → Đang giao → Hoàn thành**.
- Chỉ cho phép hủy đơn hàng đang ở trạng thái **Chờ xác nhận**.
