public class DienThoai extends SanPham {
    private int ram;       // GB
    private int boNho;     // GB
    private String mauSac;

    public DienThoai(String maSP, String tenSP, String hang, double giaBan, int soLuongTon,
                     int ram, int boNho, String mauSac)
            throws InvalidPriceException, InvalidQuantityException {
        super(maSP, tenSP, hang, giaBan, soLuongTon);
        if (ram <= 0 || boNho <= 0) {
            throw new IllegalArgumentException("RAM va bo nho phai lon hon 0");
        }
        this.ram = ram;
        this.boNho = boNho;
        this.mauSac = mauSac == null ? "" : mauSac.trim();
    }

    public int getRam() { return ram; }
    public int getBoNho() { return boNho; }
    public String getMauSac() { return mauSac; }

    @Override
    public void hienThiThongTin() {
        System.out.println(dongCoBan() + "  Dien thoai: " + ram + "GB RAM, "
                + boNho + "GB, mau " + mauSac);
    }
}
