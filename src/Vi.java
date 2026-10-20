/** Chuyển thông báo lỗi (không dấu trong mã nguồn) sang tiếng Việt có dấu để hiển thị trên web. */
public final class Vi {
    private Vi() { }

    private static final String[][] BANG = {
        {"Gia ban phai lon hon 0, nhan duoc: (.*)", "Giá bán phải lớn hơn 0 (nhận được: $1)"},
        {"So luong ton khong duoc am, nhan duoc: (.*)", "Số lượng tồn không được âm (nhận được: $1)"},
        {"So luong them vao gio phai lon hon 0, nhan duoc: (.*)", "Số lượng thêm vào giỏ phải lớn hơn 0 (nhận được: $1)"},
        {"So luong dat phai lon hon 0, nhan duoc: (.*)", "Số lượng đặt phải lớn hơn 0 (nhận được: $1)"},
        {"So luong nhap phai lon hon 0, nhan duoc: (.*)", "Số lượng nhập phải lớn hơn 0 (nhận được: $1)"},
        {"Gia nhap phai lon hon 0, nhan duoc: (.*)", "Giá nhập phải lớn hơn 0 (nhận được: $1)"},
        {"So luong cong them phai lon hon 0, nhan duoc: (.*)", "Số lượng cộng thêm phải lớn hơn 0 (nhận được: $1)"},
        {"San pham '(.*)' khong du hang: con (\\d+), yeu cau (\\d+)", "Sản phẩm '$1' không đủ hàng: còn $2, yêu cầu $3"},
        {"Ma san pham da ton tai: (.*)", "Mã sản phẩm đã tồn tại: $1"},
        {"Ma, ten va hang san pham khong duoc de trong", "Mã, tên và hãng sản phẩm không được để trống"},
        {"RAM va bo nho phai lon hon 0", "RAM và bộ nhớ phải lớn hơn 0"},
        {"Ma hoac email da ton tai: (.*)", "Mã hoặc email đã tồn tại: $1"},
        {"Ma, ho ten, email, mat khau khong duoc de trong", "Mã, họ tên, email, mật khẩu không được để trống"},
        {"Ma nha cung cap da ton tai: (.*)", "Mã nhà cung cấp đã tồn tại: $1"},
        {"Ma va ten nha cung cap khong duoc de trong", "Mã và tên nhà cung cấp không được để trống"},
        {"Gio hang trong, khong the dat hang", "Giỏ hàng trống, không thể đặt hàng"},
        {"Khong the xoa san pham '(.*)' vi da co trong don hang hoac phieu nhap", "Không thể xóa sản phẩm '$1' vì đã có trong đơn hàng hoặc phiếu nhập"},
        {"Khong the xoa nha cung cap '(.*)' vi da co phieu nhap", "Không thể xóa nhà cung cấp '$1' vì đã có phiếu nhập"},
        {"Phieu nhap chua co san pham nao", "Phiếu nhập chưa có sản phẩm nào"},
        {"Khong tim thay don hang: (.*)", "Không tìm thấy đơn hàng: $1"},
    };

    private static String nhan(String t) {
        switch (t) {
            case "Cho xac nhan": return "Chờ xác nhận";
            case "Dang giao": return "Đang giao";
            case "Hoan thanh": return "Hoàn thành";
            case "Da huy": return "Đã hủy";
            default: return t;
        }
    }

    public static String thongBao(Exception e) {
        String m = e.getMessage();
        if (m == null) {
            return e.getClass().getSimpleName();
        }
        for (String[] r : BANG) {
            if (m.matches(r[0])) {
                return m.replaceAll(r[0], r[1]);
            }
        }
        java.util.regex.Matcher mt = java.util.regex.Pattern
                .compile("Khong the chuyen don hang tu '(.*)' sang '(.*)'").matcher(m);
        if (mt.matches()) {
            return "Không thể chuyển đơn hàng từ '" + nhan(mt.group(1)) + "' sang '" + nhan(mt.group(2)) + "'";
        }
        if (e instanceof DataAccessException) {
            return "Lỗi cơ sở dữ liệu: " + m;
        }
        return m;
    }
}
