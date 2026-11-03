import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SanPhamManager implements IManager<SanPham> {
    // Bộ nhớ tạm: toàn bộ sản phẩm được lưu trong một ArrayList
    private final ArrayList<SanPham> danhSach = new ArrayList<>();
    private final SanPhamDAO dao;   // null = chỉ lưu trong bộ nhớ

    public SanPhamManager() { this(null); }

    public SanPhamManager(SanPhamDAO dao) { this.dao = dao; }

    /** Nạp danh sách đã đọc từ CSDL. */
    public void napDanhSach(ArrayList<SanPham> ds) {
        danhSach.clear();
        danhSach.addAll(ds);
    }

    /** Ghi các thay đổi (giá, tồn, tên, hãng) của một sản phẩm xuống CSDL. */
    public void capNhat(SanPham sp) {
        if (dao != null) {
            dao.capNhat(sp);
        }
    }

    @Override
    public void them(SanPham sp) {
        if (timTheoMa(sp.getMaSP()) != null) {
            throw new IllegalArgumentException(
                "Ma san pham da ton tai: " + sp.getMaSP());
        }
        if (dao != null) {
            dao.them(sp);       // ghi CSDL trước; lỗi thì danh sách trong bộ nhớ không đổi
        }
        danhSach.add(sp);
    }

    @Override
    public boolean xoa(String ma) {
        SanPham sp = timTheoMa(ma);
        if (sp == null) {
            return false;
        }
        if (dao != null) {
            dao.xoa(sp.getMaSP());
        }
        danhSach.remove(sp);
        return true;
    }

    @Override
    public SanPham timTheoMa(String ma) {
        for (SanPham sp : danhSach) {
            if (sp.getMaSP().equalsIgnoreCase(ma)) {
                return sp;
            }
        }
        return null;
    }

    @Override
    public ArrayList<SanPham> layTatCa() { return danhSach; }

    @Override
    public void hienThiDanhSach() {
        inDanhSach(danhSach);
    }

    /** In một danh sách sản phẩm bất kỳ (toàn bộ hoặc kết quả tìm kiếm). */
    public static void inDanhSach(List<SanPham> ds) {
        if (ds.isEmpty()) {
            System.out.println("Danh sach san pham trong.");
            return;
        }
        System.out.printf("%-6s %-22s %-10s %14s %5s  %s%n",
                "Ma", "Ten san pham", "Hang", "Gia ban", "Ton", "Chi tiet");
        for (SanPham sp : ds) {
            sp.hienThiThongTin();
        }
    }

    // ---------- Tìm kiếm, sắp xếp, thống kê ----------

    /** Tìm theo từ khóa trong tên hoặc hãng, không phân biệt hoa/thường. */
    public ArrayList<SanPham> timKiem(String tuKhoa) {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        String kw = tuKhoa.trim().toLowerCase();
        for (SanPham sp : danhSach) {
            if (sp.getTenSP().toLowerCase().contains(kw)
                    || sp.getHang().toLowerCase().contains(kw)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    public ArrayList<SanPham> timTheoKhoangGia(double giaMin, double giaMax) {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : danhSach) {
            if (sp.getGiaBan() >= giaMin && sp.getGiaBan() <= giaMax) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    /** Sắp xếp danh sách theo giá bán tăng hoặc giảm dần. */
    public void sapXepTheoGia(boolean tangDan) {
        Comparator<SanPham> theoGia =
                Comparator.comparingDouble(SanPham::getGiaBan);
        Collections.sort(danhSach, tangDan ? theoGia : theoGia.reversed());
    }

    public double tongGiaTriTonKho() {
        double tong = 0;
        for (SanPham sp : danhSach) {
            tong += sp.getGiaBan() * sp.getSoLuongTon();
        }
        return tong;
    }

    public SanPham sanPhamGiaCaoNhat() {
        return danhSach.isEmpty() ? null
                : Collections.max(danhSach,
                      Comparator.comparingDouble(SanPham::getGiaBan));
    }

    public Map<String, Integer> demTheoHang() {
        Map<String, Integer> dem = new TreeMap<>();
        for (SanPham sp : danhSach) {
            dem.merge(sp.getHang(), 1, Integer::sum);
        }
        return dem;
    }

    /** Cảnh báo: sản phẩm có tồn kho nhỏ hơn hoặc bằng ngưỡng. */
    public ArrayList<SanPham> sanPhamSapHet(int nguong) {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : danhSach) {
            if (sp.getSoLuongTon() <= nguong) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }
}
