public class SanPham {
    // Đóng gói: thuộc tính private, chỉ truy cập qua getter/setter
    private String maSP;
    private String tenSP;
    private String hang;
    private double giaBan;
    private int soLuongTon;

    public SanPham(String maSP, String tenSP, String hang,
                   double giaBan, int soLuongTon)
            throws InvalidPriceException, InvalidQuantityException {
        if (maSP == null || maSP.trim().isEmpty() || tenSP == null || tenSP.trim().isEmpty()
                || hang == null || hang.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma, ten va hang san pham khong duoc de trong");
        }
        this.maSP = maSP.trim();
        this.tenSP = tenSP.trim();
        this.hang = hang.trim();
        setGiaBan(giaBan);           // tái sử dụng bước kiểm tra trong setter
        setSoLuongTon(soLuongTon);
    }

    public String getMaSP()     { return maSP; }
    public String getTenSP()    { return tenSP; }
    public String getHang()     { return hang; }
    public double getGiaBan()   { return giaBan; }
    public int getSoLuongTon()  { return soLuongTon; }

    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public void setHang(String hang) { this.hang = hang; }

    public void setGiaBan(double giaBan) throws InvalidPriceException {
        if (giaBan <= 0) {
            throw new InvalidPriceException(
                "Gia ban phai lon hon 0, nhan duoc: " + giaBan, giaBan);
        }
        this.giaBan = giaBan;
    }

    public void setSoLuongTon(int soLuongTon) throws InvalidQuantityException {
        if (soLuongTon < 0) {
            throw new InvalidQuantityException(
                "So luong ton khong duoc am, nhan duoc: " + soLuongTon);
        }
        this.soLuongTon = soLuongTon;
    }

    /** Trừ tồn kho khi bán; ném OutOfStockException nếu không đủ hàng. */
    public void giamTonKho(int soLuong) throws OutOfStockException {
        if (soLuong > soLuongTon) {
            throw new OutOfStockException(tenSP, soLuongTon, soLuong);
        }
        soLuongTon -= soLuong;
    }

    /** Cộng tồn kho khi nhập hàng hoặc khi hủy đơn. */
    public void tangTonKho(int soLuong) throws InvalidQuantityException {
        if (soLuong <= 0) {
            throw new InvalidQuantityException("So luong cong them phai lon hon 0, nhan duoc: " + soLuong);
        }
        soLuongTon += soLuong;
    }

    /** Giá thực tế khi bán. Lớp con có thể ghi đè (đa hình). */
    public double tinhGiaBan() {
        return giaBan;
    }

    protected String dongCoBan() {
        return String.format("%-6s %-22s %-10s %14s %5d",
                maSP, tenSP, hang, Fmt.tien(giaBan), soLuongTon);
    }

    /** Đa hình: DienThoai và PhuKien ghi đè để hiển thị thêm thông tin riêng. */
    public void hienThiThongTin() {
        System.out.println(dongCoBan());
    }
}
