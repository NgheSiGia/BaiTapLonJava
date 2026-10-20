/** Gom các lớp quản lý lại một chỗ; nạp dữ liệu từ MySQL (XAMPP) hoặc dữ liệu mẫu trong bộ nhớ. */
public class CuaHang {
    public final SanPhamManager sanPham;
    public final NguoiDungManager nguoiDung;
    public final DonHangManager donHang;
    public final NhaCungCapManager nhaCungCap;
    public final PhieuNhapManager phieuNhap;

    private final boolean dungCSDL;
    private final SanPhamDAO sanPhamDAO;
    private final NguoiDungDAO nguoiDungDAO;
    private final NhaCungCapDAO nhaCungCapDAO;
    private final DonHangDAO donHangDAO;
    private final PhieuNhapDAO phieuNhapDAO;

    /** dungCSDL = true: ghi mọi thay đổi xuống MySQL; false: chỉ lưu trong bộ nhớ (ArrayList). */
    public CuaHang(boolean dungCSDL) {
        this.dungCSDL = dungCSDL;
        sanPhamDAO = dungCSDL ? new SanPhamDAO() : null;
        nguoiDungDAO = dungCSDL ? new NguoiDungDAO() : null;
        nhaCungCapDAO = dungCSDL ? new NhaCungCapDAO() : null;
        donHangDAO = dungCSDL ? new DonHangDAO() : null;
        phieuNhapDAO = dungCSDL ? new PhieuNhapDAO() : null;
        sanPham = new SanPhamManager(sanPhamDAO);
        nguoiDung = new NguoiDungManager(nguoiDungDAO);
        nhaCungCap = new NhaCungCapManager(nhaCungCapDAO);
        donHang = new DonHangManager(donHangDAO);
        phieuNhap = new PhieuNhapManager(phieuNhapDAO);
    }

    public boolean isDungCSDL() { return dungCSDL; }

    /**
     * Thử kết nối MySQL: tạo CSDL/bảng nếu chưa có, đọc dữ liệu lên ArrayList, nạp dữ liệu mẫu nếu CSDL trống.
     * Không kết nối được thì báo và chuyển sang chế độ bộ nhớ.
     */
    public static CuaHang khoiTao() {
        try {
            DatabaseInit.taoCSDLVaBang();
            CuaHang ch = new CuaHang(true);
            ch.napTuCSDL();
            if (ch.sanPham.layTatCa().isEmpty() && ch.nguoiDung.layTatCa().isEmpty()) {
                ch.napDuLieuMau();
                System.out.println("(CSDL con trong: da nap du lieu mau)");
            }
            System.out.println("Che do: MySQL - " + DBConnection.moTa());
            return ch;
        } catch (DataAccessException e) {
            System.out.println("[Canh bao] " + e.getMessage());
            System.out.println("-> Chuyen sang che do BO NHO (du lieu se mat khi tat chuong trinh).");
            System.out.println("   Hay bat MySQL trong XAMPP Control Panel roi chay lai de luu vao CSDL.");
            CuaHang ch = new CuaHang(false);
            ch.napDuLieuMau();
            return ch;
        }
    }

    /** Đọc toàn bộ dữ liệu từ MySQL vào các ArrayList (theo thứ tự phụ thuộc khóa ngoại). */
    private void napTuCSDL() {
        sanPham.napDanhSach(sanPhamDAO.layTatCa());
        nguoiDung.napDanhSach(nguoiDungDAO.layTatCa());
        nhaCungCap.napDanhSach(nhaCungCapDAO.layTatCa());
        donHang.napDanhSach(donHangDAO.layTatCa(sanPham));
        phieuNhap.napDanhSach(phieuNhapDAO.layTatCa(sanPham, nhaCungCap));
    }

    public void napDuLieuMau() {
        try {
            sanPham.them(new DienThoai("SP01", "iPhone 15", "Apple", 22990000, 10, 6, 128, "Den"));
            sanPham.them(new DienThoai("SP02", "Galaxy S24", "Samsung", 19990000, 5, 8, 256, "Xam"));
            sanPham.them(new DienThoai("SP03", "Redmi Note 13", "Xiaomi", 5990000, 20, 8, 128, "Xanh"));
            sanPham.them(new DienThoai("SP04", "Galaxy A55", "Samsung", 9990000, 8, 8, 256, "Tim"));
            sanPham.them(new PhuKien("PK01", "Cap sac Type-C 1m", "Anker", 250000, 50, "Cap sac"));
            sanPham.them(new PhuKien("PK02", "Op lung iPhone 15", "Spigen", 450000, 3, "Op lung"));

            nhaCungCap.them(new NhaCungCap("NCC01", "Cong ty FPT Trading", "Ha Noi", "0241234567"));
            nhaCungCap.them(new NhaCungCap("NCC02", "Cong ty Viettel Store", "Ha Noi", "0247654321"));

            nguoiDung.them(new QuanTriVien("QT001", "Quan tri VPHONE", "admin@vphone.vn",
                    "admin123", "0900000001", "Chu cua hang"));
            nguoiDung.them(new KhachHang("KH001", "Nguyen Van A", "khach@vphone.vn",
                    "123456", "0900000002", "Ba Dinh, Ha Noi"));
        } catch (InvalidPriceException | InvalidQuantityException e) {
            throw new IllegalStateException("Du lieu mau khong hop le: " + e.getMessage());
        }
    }
}
