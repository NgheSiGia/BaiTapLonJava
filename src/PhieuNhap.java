import java.time.LocalDate;
import java.util.ArrayList;

public class PhieuNhap {
    private String maPhieu;
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;
    private String maQuanTri;
    private ArrayList<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<>();

    public PhieuNhap(String maPhieu, NhaCungCap nhaCungCap, String maQuanTri) {
        this.maPhieu = maPhieu;
        this.nhaCungCap = nhaCungCap;
        this.maQuanTri = maQuanTri;
        this.ngayNhap = LocalDate.now();
    }

    /** Dùng khi nạp từ CSDL. */
    public PhieuNhap(String maPhieu, NhaCungCap nhaCungCap, String maQuanTri, LocalDate ngayNhap) {
        this.maPhieu = maPhieu;
        this.nhaCungCap = nhaCungCap;
        this.maQuanTri = maQuanTri;
        this.ngayNhap = ngayNhap;
    }

    /** Dùng khi nạp từ CSDL: thêm chi tiết đã lưu. */
    public void napChiTiet(ChiTietPhieuNhap ct) {
        danhSachChiTiet.add(ct);
    }

    public String getMaPhieu() { return maPhieu; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public NhaCungCap getNhaCungCap() { return nhaCungCap; }
    public String getMaQuanTri() { return maQuanTri; }
    public ArrayList<ChiTietPhieuNhap> getDanhSachChiTiet() { return danhSachChiTiet; }

    public void themChiTiet(SanPham sp, int soLuongNhap, double giaNhap)
            throws InvalidQuantityException, InvalidPriceException {
        if (soLuongNhap <= 0) {
            throw new InvalidQuantityException(
                "So luong nhap phai lon hon 0, nhan duoc: " + soLuongNhap);
        }
        if (giaNhap <= 0) {
            throw new InvalidPriceException(
                "Gia nhap phai lon hon 0, nhan duoc: " + giaNhap, giaNhap);
        }
        danhSachChiTiet.add(new ChiTietPhieuNhap(sp, soLuongNhap, giaNhap));
    }

    public double tinhTongTienNhap() {
        double tong = 0;
        for (ChiTietPhieuNhap ct : danhSachChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    public void hienThi() {
        System.out.printf("Phieu %s | NCC: %s | Ngay: %s | Nguoi lap: %s%n",
                maPhieu, nhaCungCap.getTenNCC(), ngayNhap, maQuanTri);
        for (ChiTietPhieuNhap ct : danhSachChiTiet) {
            System.out.printf("   - %-22s x%-3d gia nhap %12s%n", ct.getSanPham().getTenSP(),
                    ct.getSoLuongNhap(), Fmt.tien(ct.getGiaNhap()));
        }
        System.out.println("   Tong tien nhap: " + Fmt.tien(tinhTongTienNhap()));
    }
}
