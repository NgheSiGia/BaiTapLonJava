import java.util.ArrayList;

public class PhieuNhapManager implements IManager<PhieuNhap> {
    private final ArrayList<PhieuNhap> danhSach = new ArrayList<>();
    private final PhieuNhapDAO dao;
    private int boDem = 0;

    public PhieuNhapManager() { this(null); }

    public PhieuNhapManager(PhieuNhapDAO dao) { this.dao = dao; }

    public void napDanhSach(ArrayList<PhieuNhap> ds) {
        danhSach.clear();
        danhSach.addAll(ds);
        boDem = 0;
        for (PhieuNhap p : ds) {
            try {
                boDem = Math.max(boDem, Integer.parseInt(p.getMaPhieu().replaceAll("\\D", "")));
            } catch (NumberFormatException e) {
                // bỏ qua mã không theo dạng PNxxx
            }
        }
    }

    @Override
    public void them(PhieuNhap pn) {
        if (timTheoMa(pn.getMaPhieu()) != null) {
            throw new IllegalArgumentException("Ma phieu nhap da ton tai: " + pn.getMaPhieu());
        }
        danhSach.add(pn);
    }

    @Override
    public boolean xoa(String ma) {
        return danhSach.removeIf(p -> p.getMaPhieu().equalsIgnoreCase(ma));
    }

    @Override
    public PhieuNhap timTheoMa(String ma) {
        for (PhieuNhap p : danhSach) {
            if (p.getMaPhieu().equalsIgnoreCase(ma)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public ArrayList<PhieuNhap> layTatCa() { return danhSach; }

    @Override
    public void hienThiDanhSach() {
        if (danhSach.isEmpty()) {
            System.out.println("Chua co phieu nhap nao.");
            return;
        }
        for (PhieuNhap p : danhSach) {
            p.hienThi();
        }
    }

    /** Tạo phiếu nhập trống (chưa lưu vào danh sách cho tới khi xác nhận). */
    public PhieuNhap taoPhieu(NhaCungCap ncc, String maQuanTri) {
        boDem++;
        return new PhieuNhap(String.format("PN%03d", boDem), ncc, maQuanTri);
    }

    /** Xác nhận phiếu: cộng tồn kho cho từng sản phẩm rồi lưu phiếu. */
    public void xacNhan(PhieuNhap pn) {
        if (pn.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalStateException("Phieu nhap chua co san pham nao");
        }
        capNhatTonKho(pn);
        if (dao != null) {
            try {
                dao.them(pn);            // một giao dịch: phiếu + chi tiết + tồn kho
            } catch (DataAccessException e) {
                for (ChiTietPhieuNhap ct : pn.getDanhSachChiTiet()) {   // hoàn tác tồn kho trong bộ nhớ
                    try {
                        ct.getSanPham().giamTonKho(ct.getSoLuongNhap());
                    } catch (OutOfStockException ex) {
                        // không xảy ra: vừa cộng xong
                    }
                }
                throw e;
            }
        }
        danhSach.add(pn);
    }

    public void capNhatTonKho(PhieuNhap pn) {
        for (ChiTietPhieuNhap ct : pn.getDanhSachChiTiet()) {
            try {
                ct.getSanPham().tangTonKho(ct.getSoLuongNhap());
            } catch (InvalidQuantityException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
    }
}
