import java.time.LocalDate;
import java.util.ArrayList;

public class DonHang {
    private String maDonHang;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayDat;
    private TrangThaiDonHang trangThai = TrangThaiDonHang.CHO_XAC_NHAN;
    private ArrayList<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    public DonHang(String maDonHang, String maKhachHang, String tenKhachHang) {
        this.maDonHang = maDonHang;
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.ngayDat = LocalDate.now();
    }

    /** Dùng khi nạp từ CSDL. */
    public DonHang(String maDonHang, String maKhachHang, String tenKhachHang,
                   LocalDate ngayDat, TrangThaiDonHang trangThai) {
        this.maDonHang = maDonHang;
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
    }

    public String getMaDonHang() { return maDonHang; }
    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public LocalDate getNgayDat() { return ngayDat; }
    public TrangThaiDonHang getTrangThai() { return trangThai; }
    public ArrayList<ChiTietDonHang> getDanhSachChiTiet() { return danhSachChiTiet; }

    /** Thêm sản phẩm vào đơn: kiểm tra và trừ tồn kho trước khi ghi nhận. */
    public void themChiTiet(SanPham sp, int soLuong)
            throws OutOfStockException, InvalidQuantityException {
        if (soLuong <= 0) {
            throw new InvalidQuantityException(
                "So luong dat phai lon hon 0, nhan duoc: " + soLuong);
        }
        sp.giamTonKho(soLuong);
        danhSachChiTiet.add(new ChiTietDonHang(sp, soLuong));
    }

    /** Dùng khi nạp từ CSDL: thêm chi tiết đã lưu, KHÔNG trừ tồn kho. */
    public void napChiTiet(ChiTietDonHang ct) {
        danhSachChiTiet.add(ct);
    }

    /** Hoàn tác trạng thái trong bộ nhớ khi ghi CSDL thất bại. */
    public void khoiPhucTrangThai(TrangThaiDonHang cu) {
        this.trangThai = cu;
    }

    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietDonHang ct : danhSachChiTiet) {
            tong += ct.getThanhTien();   // thành tiền = đơn giá x số lượng
        }
        return tong;
    }

    /** Đổi trạng thái theo quy tắc; hủy đơn sẽ hoàn lại tồn kho. */
    public void capNhatTrangThai(TrangThaiDonHang moi) {
        if (!trangThai.coTheChuyenSang(moi)) {
            throw new IllegalStateException("Khong the chuyen don hang tu '"
                    + trangThai.getNhan() + "' sang '" + moi.getNhan() + "'");
        }
        if (moi == TrangThaiDonHang.DA_HUY) {
            for (ChiTietDonHang ct : danhSachChiTiet) {
                try {
                    ct.getSanPham().tangTonKho(ct.getSoLuong());
                } catch (InvalidQuantityException e) {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
        trangThai = moi;
    }

    public void hienThi() {
        System.out.printf("Don %s | Khach: %s | Ngay: %s | Trang thai: %s%n",
                maDonHang, tenKhachHang, ngayDat, trangThai.getNhan());
        for (ChiTietDonHang ct : danhSachChiTiet) {
            System.out.printf("   - %-22s x%-3d %14s%n", ct.getSanPham().getTenSP(),
                    ct.getSoLuong(), Fmt.tien(ct.getThanhTien()));
        }
        System.out.println("   Tong tien: " + Fmt.tien(tinhTongTien()));
    }
}
