public class ChiTietGioHang {
    private final SanPham sanPham;
    private int soLuong;

    public ChiTietGioHang(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }

    public SanPham getSanPham() { return sanPham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getThanhTien() { return sanPham.tinhGiaBan() * soLuong; }
}
