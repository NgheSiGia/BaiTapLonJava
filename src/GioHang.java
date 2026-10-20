import java.util.ArrayList;

public class GioHang {
    private final ArrayList<ChiTietGioHang> danhSachChiTiet = new ArrayList<>();

    public ArrayList<ChiTietGioHang> getDanhSachChiTiet() { return danhSachChiTiet; }
    public boolean isEmpty() { return danhSachChiTiet.isEmpty(); }

    /** Thêm sản phẩm; cộng dồn nếu đã có; không cho vượt quá tồn kho. */
    public void themSanPham(SanPham sp, int soLuong)
            throws InvalidQuantityException, OutOfStockException {
        if (soLuong <= 0) {
            throw new InvalidQuantityException(
                "So luong them vao gio phai lon hon 0, nhan duoc: " + soLuong);
        }
        ChiTietGioHang ct = timChiTiet(sp.getMaSP());
        int tong = soLuong + (ct == null ? 0 : ct.getSoLuong());
        if (tong > sp.getSoLuongTon()) {
            throw new OutOfStockException(sp.getTenSP(), sp.getSoLuongTon(), tong);
        }
        if (ct == null) {
            danhSachChiTiet.add(new ChiTietGioHang(sp, soLuong));
        } else {
            ct.setSoLuong(tong);
        }
    }

    public boolean xoaSanPham(String maSP) {
        return danhSachChiTiet.removeIf(ct -> ct.getSanPham().getMaSP().equalsIgnoreCase(maSP));
    }

    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietGioHang ct : danhSachChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    public void lamTrong() { danhSachChiTiet.clear(); }

    public void hienThi() {
        if (danhSachChiTiet.isEmpty()) {
            System.out.println("Gio hang trong.");
            return;
        }
        System.out.printf("%-6s %-22s %5s %14s %16s%n", "Ma", "Ten san pham", "SL", "Don gia", "Thanh tien");
        for (ChiTietGioHang ct : danhSachChiTiet) {
            SanPham sp = ct.getSanPham();
            System.out.printf("%-6s %-22s %5d %14s %16s%n", sp.getMaSP(), sp.getTenSP(),
                    ct.getSoLuong(), Fmt.tien(sp.tinhGiaBan()), Fmt.tien(ct.getThanhTien()));
        }
        System.out.println("Tong tien: " + Fmt.tien(tinhTongTien()));
    }

    private ChiTietGioHang timChiTiet(String maSP) {
        for (ChiTietGioHang ct : danhSachChiTiet) {
            if (ct.getSanPham().getMaSP().equalsIgnoreCase(maSP)) {
                return ct;
            }
        }
        return null;
    }
}
