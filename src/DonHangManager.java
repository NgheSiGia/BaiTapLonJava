import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class DonHangManager implements IManager<DonHang> {
    private final ArrayList<DonHang> danhSach = new ArrayList<>();
    private final DonHangDAO dao;
    private int boDem = 0;

    public DonHangManager() { this(null); }

    public DonHangManager(DonHangDAO dao) { this.dao = dao; }

    /** Nạp danh sách từ CSDL và đặt lại bộ đếm mã đơn (DH001, DH002...). */
    public void napDanhSach(ArrayList<DonHang> ds) {
        danhSach.clear();
        danhSach.addAll(ds);
        boDem = 0;
        for (DonHang dh : ds) {
            try {
                boDem = Math.max(boDem, Integer.parseInt(dh.getMaDonHang().replaceAll("\\D", "")));
            } catch (NumberFormatException e) {
                // mã không theo dạng DHxxx: bỏ qua
            }
        }
    }

    @Override
    public void them(DonHang dh) {
        if (timTheoMa(dh.getMaDonHang()) != null) {
            throw new IllegalArgumentException("Ma don hang da ton tai: " + dh.getMaDonHang());
        }
        if (dao != null) {
            dao.them(dh);
        }
        danhSach.add(dh);
    }

    @Override
    public boolean xoa(String ma) {
        return danhSach.removeIf(dh -> dh.getMaDonHang().equalsIgnoreCase(ma));
    }

    @Override
    public DonHang timTheoMa(String ma) {
        for (DonHang dh : danhSach) {
            if (dh.getMaDonHang().equalsIgnoreCase(ma)) {
                return dh;
            }
        }
        return null;
    }

    @Override
    public ArrayList<DonHang> layTatCa() { return danhSach; }

    @Override
    public void hienThiDanhSach() {
        if (danhSach.isEmpty()) {
            System.out.println("Chua co don hang nao.");
            return;
        }
        for (DonHang dh : danhSach) {
            dh.hienThi();
        }
    }

    /**
     * Đặt hàng từ giỏ của khách: kiểm tra đủ hàng cho TẤT CẢ mặt hàng trước,
     * chỉ khi đủ mới trừ kho, tạo đơn (CHO_XAC_NHAN) và làm trống giỏ hàng.
     */
    public DonHang taoDonHang(KhachHang kh) throws OutOfStockException {
        GioHang gio = kh.getGioHang();
        if (gio.isEmpty()) {
            throw new IllegalStateException("Gio hang trong, khong the dat hang");
        }
        for (ChiTietGioHang ct : gio.getDanhSachChiTiet()) {          // bước 1: kiểm tra
            SanPham sp = ct.getSanPham();
            if (ct.getSoLuong() > sp.getSoLuongTon()) {
                throw new OutOfStockException(sp.getTenSP(), sp.getSoLuongTon(), ct.getSoLuong());
            }
        }
        boDem++;
        DonHang dh = new DonHang(String.format("DH%03d", boDem), kh.getMaNguoiDung(), kh.getHoTen());
        for (ChiTietGioHang ct : gio.getDanhSachChiTiet()) {          // bước 2: trừ kho
            try {
                dh.themChiTiet(ct.getSanPham(), ct.getSoLuong());
            } catch (InvalidQuantityException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        if (dao != null) {
            try {
                dao.them(dh);            // một giao dịch: đơn + chi tiết + tồn kho
            } catch (DataAccessException e) {
                hoanKho(dh);             // ghi CSDL lỗi: trả lại tồn kho trong bộ nhớ
                boDem--;
                throw e;
            }
        }
        danhSach.add(dh);
        gio.lamTrong();
        return dh;
    }

    private void hoanKho(DonHang dh) {
        for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
            try {
                ct.getSanPham().tangTonKho(ct.getSoLuong());
            } catch (InvalidQuantityException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
    }

    /** Đổi trạng thái đơn hàng (hủy đơn sẽ hoàn tồn kho) và lưu xuống CSDL. */
    public void capNhatTrangThai(String ma, TrangThaiDonHang moi) {
        DonHang dh = timTheoMa(ma);
        if (dh == null) {
            throw new IllegalArgumentException("Khong tim thay don hang: " + ma);
        }
        TrangThaiDonHang cu = dh.getTrangThai();
        dh.capNhatTrangThai(moi);
        if (dao != null) {
            try {
                dao.capNhatTrangThai(dh);
            } catch (DataAccessException e) {
                if (moi == TrangThaiDonHang.DA_HUY) {
                    for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
                        try {
                            ct.getSanPham().giamTonKho(ct.getSoLuong());
                        } catch (OutOfStockException ex) {
                            // không xảy ra: vừa hoàn kho xong
                        }
                    }
                }
                dh.khoiPhucTrangThai(cu);
                throw e;
            }
        }
    }

    public ArrayList<DonHang> layTheoKhachHang(String maKhachHang) {
        ArrayList<DonHang> ketQua = new ArrayList<>();
        for (DonHang dh : danhSach) {
            if (dh.getMaKhachHang().equalsIgnoreCase(maKhachHang)) {
                ketQua.add(dh);
            }
        }
        return ketQua;
    }

    /** Doanh thu = tổng tiền các đơn đã HOAN_THANH. */
    public double tongDoanhThu() {
        double tong = 0;
        for (DonHang dh : danhSach) {
            if (dh.getTrangThai() == TrangThaiDonHang.HOAN_THANH) {
                tong += dh.tinhTongTien();
            }
        }
        return tong;
    }

    public Map<TrangThaiDonHang, Integer> demTheoTrangThai() {
        Map<TrangThaiDonHang, Integer> dem = new EnumMap<>(TrangThaiDonHang.class);
        for (TrangThaiDonHang tt : TrangThaiDonHang.values()) {
            dem.put(tt, 0);
        }
        for (DonHang dh : danhSach) {
            dem.merge(dh.getTrangThai(), 1, Integer::sum);
        }
        return dem;
    }
}
