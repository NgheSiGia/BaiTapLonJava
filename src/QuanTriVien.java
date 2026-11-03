public class QuanTriVien extends NguoiDung {
    private String chucVu;

    public QuanTriVien(String ma, String hoTen, String email, String matKhau,
                       String soDienThoai, String chucVu) {
        super(ma, hoTen, email, matKhau, soDienThoai);
        this.chucVu = chucVu == null ? "" : chucVu.trim();
    }

    public String getChucVu() { return chucVu; }

    /** Tạo phiếu nhập mới (chưa có chi tiết) do quản trị viên này lập. */
    public PhieuNhap lapPhieuNhap(PhieuNhapManager qlPhieuNhap, NhaCungCap ncc) {
        return qlPhieuNhap.taoPhieu(ncc, getMaNguoiDung());
    }

    public double thongKeDoanhThu(DonHangManager qlDonHang) {
        return qlDonHang.tongDoanhThu();
    }

    @Override
    public String getVaiTro() { return "Quan tri vien"; }

    @Override
    public void hienThiThongTin() {
        System.out.printf("[Quan tri vien] %s | %s | %s | Chuc vu: %s%n",
                getMaNguoiDung(), getHoTen(), getEmail(), chucVu);
    }
}
