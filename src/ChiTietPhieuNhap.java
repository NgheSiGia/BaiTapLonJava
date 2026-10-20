public class ChiTietPhieuNhap {
    private final SanPham sanPham;
    private final int soLuongNhap;
    private final double giaNhap;

    public ChiTietPhieuNhap(SanPham sanPham, int soLuongNhap, double giaNhap) {
        this.sanPham = sanPham;
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
    }

    public SanPham getSanPham() { return sanPham; }
    public int getSoLuongNhap() { return soLuongNhap; }
    public double getGiaNhap() { return giaNhap; }
    public double getThanhTien() { return giaNhap * soLuongNhap; }
}
