public class ChiTietDonHang {
    private final SanPham sanPham;
    private final int soLuong;
    private final double donGia;

    public ChiTietDonHang(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = sanPham.tinhGiaBan(); // chốt giá tại thời điểm đặt
    }

    /** Dùng khi nạp từ CSDL: giữ nguyên đơn giá đã chốt lúc đặt. */
    public ChiTietDonHang(SanPham sanPham, int soLuong, double donGia) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public SanPham getSanPham() { return sanPham; }
    public int getSoLuong() { return soLuong; }
    public double getDonGia() { return donGia; }
    public double getThanhTien() { return donGia * soLuong; }
}
