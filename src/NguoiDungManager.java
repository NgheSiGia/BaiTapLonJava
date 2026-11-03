import java.util.ArrayList;

public class NguoiDungManager implements IManager<NguoiDung> {
    private final ArrayList<NguoiDung> danhSach = new ArrayList<>();
    private final NguoiDungDAO dao;

    public NguoiDungManager() { this(null); }

    public NguoiDungManager(NguoiDungDAO dao) { this.dao = dao; }

    public void napDanhSach(ArrayList<NguoiDung> ds) {
        danhSach.clear();
        danhSach.addAll(ds);
    }

    @Override
    public void them(NguoiDung nd) {
        if (timTheoMa(nd.getMaNguoiDung()) != null || timTheoEmail(nd.getEmail()) != null) {
            throw new IllegalArgumentException(
                "Ma hoac email da ton tai: " + nd.getMaNguoiDung() + " / " + nd.getEmail());
        }
        if (dao != null) {
            dao.them(nd);
        }
        danhSach.add(nd);
    }

    @Override
    public boolean xoa(String ma) {
        return danhSach.removeIf(nd -> nd.getMaNguoiDung().equalsIgnoreCase(ma));
    }

    @Override
    public NguoiDung timTheoMa(String ma) {
        for (NguoiDung nd : danhSach) {
            if (nd.getMaNguoiDung().equalsIgnoreCase(ma)) {
                return nd;
            }
        }
        return null;
    }

    public NguoiDung timTheoEmail(String email) {
        for (NguoiDung nd : danhSach) {
            if (nd.getEmail().equalsIgnoreCase(email.trim())) {
                return nd;
            }
        }
        return null;
    }

    @Override
    public ArrayList<NguoiDung> layTatCa() { return danhSach; }

    @Override
    public void hienThiDanhSach() {
        for (NguoiDung nd : danhSach) {
            nd.hienThiThongTin();   // đa hình: gọi đúng phiên bản của lớp con
        }
    }

    /** Trả về người dùng nếu đúng email + mật khẩu, ngược lại trả về null. */
    public NguoiDung dangNhap(String email, String matKhau) {
        for (NguoiDung nd : danhSach) {
            if (nd.dangNhap(email, matKhau)) {
                return nd;
            }
        }
        return null;
    }

    public String taoMaKhachHang() {
        int dem = 0;
        for (NguoiDung nd : danhSach) {
            if (nd instanceof KhachHang) dem++;
        }
        return String.format("KH%03d", dem + 1);
    }
}
