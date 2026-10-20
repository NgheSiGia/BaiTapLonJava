import java.util.ArrayList;

public class KhachHang extends NguoiDung {
    private String diaChi;
    private final GioHang gioHang = new GioHang();

    public KhachHang(String ma, String hoTen, String email, String matKhau,
                     String soDienThoai, String diaChi) {
        super(ma, hoTen, email, matKhau, soDienThoai);
        this.diaChi = diaChi == null ? "" : diaChi.trim();
    }

    public String getDiaChi() { return diaChi; }
    public GioHang getGioHang() { return gioHang; }

    /** Đặt hàng từ giỏ hàng hiện tại: kiểm tra tồn kho rồi trừ kho. */
    public DonHang datHang(DonHangManager qlDonHang) throws OutOfStockException {
        return qlDonHang.taoDonHang(this);
    }

    public ArrayList<DonHang> xemLichSuMuaHang(DonHangManager qlDonHang) {
        return qlDonHang.layTheoKhachHang(getMaNguoiDung());
    }

    @Override
    public String getVaiTro() { return "Khach hang"; }

    @Override
    public void hienThiThongTin() {
        System.out.printf("[Khach hang] %s | %s | %s | SDT: %s | Dia chi: %s%n",
                getMaNguoiDung(), getHoTen(), getEmail(), getSoDienThoai(), diaChi);
    }
}
