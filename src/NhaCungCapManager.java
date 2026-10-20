import java.util.ArrayList;

public class NhaCungCapManager implements IManager<NhaCungCap> {
    private final ArrayList<NhaCungCap> danhSach = new ArrayList<>();
    private final NhaCungCapDAO dao;

    public NhaCungCapManager() { this(null); }

    public NhaCungCapManager(NhaCungCapDAO dao) { this.dao = dao; }

    public void napDanhSach(ArrayList<NhaCungCap> ds) {
        danhSach.clear();
        danhSach.addAll(ds);
    }

    @Override
    public void them(NhaCungCap ncc) {
        if (timTheoMa(ncc.getMaNCC()) != null) {
            throw new IllegalArgumentException("Ma nha cung cap da ton tai: " + ncc.getMaNCC());
        }
        if (dao != null) {
            dao.them(ncc);
        }
        danhSach.add(ncc);
    }

    @Override
    public boolean xoa(String ma) {
        NhaCungCap n = timTheoMa(ma);
        if (n == null) {
            return false;
        }
        if (dao != null) {
            dao.xoa(n.getMaNCC());
        }
        danhSach.remove(n);
        return true;
    }

    @Override
    public NhaCungCap timTheoMa(String ma) {
        for (NhaCungCap n : danhSach) {
            if (n.getMaNCC().equalsIgnoreCase(ma)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public ArrayList<NhaCungCap> layTatCa() { return danhSach; }

    @Override
    public void hienThiDanhSach() {
        if (danhSach.isEmpty()) {
            System.out.println("Chua co nha cung cap nao.");
            return;
        }
        System.out.printf("%-6s %-24s %-22s %s%n", "Ma", "Ten nha cung cap", "Dia chi", "SDT");
        for (NhaCungCap n : danhSach) {
            System.out.printf("%-6s %-24s %-22s %s%n", n.getMaNCC(), n.getTenNCC(),
                    n.getDiaChi(), n.getSoDienThoai());
        }
    }
}
