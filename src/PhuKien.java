public class PhuKien extends SanPham {
    private static final double TY_LE_GIAM = 0.05; // phụ kiện được giảm 5% khi bán
    private String loaiPhuKien;

    public PhuKien(String maSP, String tenSP, String hang, double giaBan, int soLuongTon,
                   String loaiPhuKien)
            throws InvalidPriceException, InvalidQuantityException {
        super(maSP, tenSP, hang, giaBan, soLuongTon);
        this.loaiPhuKien = loaiPhuKien == null ? "" : loaiPhuKien.trim();
    }

    public String getLoaiPhuKien() { return loaiPhuKien; }

    /** Ghi đè (đa hình): phụ kiện được giảm 5% so với giá niêm yết. */
    @Override
    public double tinhGiaBan() {
        return Math.round(getGiaBan() * (1 - TY_LE_GIAM));
    }

    @Override
    public void hienThiThongTin() {
        System.out.println(dongCoBan() + "  Phu kien: " + loaiPhuKien
                + " (ban " + Fmt.tien(tinhGiaBan()) + ")");
    }
}
