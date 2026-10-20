import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Chương trình VPHONE - giao diện dòng lệnh, lưu dữ liệu vào MySQL (XAMPP).
 *  Chạy: java Main | java Main --demo (bộ nhớ) | java Main --reset-db */
public class Main {
    private static final Scanner SC = new Scanner(System.in);
    private static CuaHang CH;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            CH = new CuaHang(false);          // kịch bản demo luôn chạy trong bộ nhớ, không đụng tới CSDL
            CH.napDuLieuMau();
            Demo.chay(CH);
            return;
        }
        if (args.length > 0 && args[0].equals("--reset-db")) {
            try {
                DatabaseInit.xoaVaTaoLai();
                System.out.println("Da xoa va tao lai CSDL '" + DBConnection.getTenCSDL()
                        + "'. Lan chay tiep theo se nap lai du lieu mau.");
            } catch (DataAccessException e) {
                System.out.println("[Loi CSDL] " + e.getMessage());
            }
            return;
        }
        System.out.println("=== UNG DUNG QUAN LY BAN DIEN THOAI VPHONE ===");
        CH = CuaHang.khoiTao();
        try {
            menuDauTien();
        } catch (NoSuchElementException e) {
            System.out.println("\n(Het du lieu nhap - thoat chuong trinh)");
        }
        System.out.println("Tam biet!");
    }

    // ------------------------------------------------------------ menu goc
    private static void menuDauTien() {
        while (true) {
            System.out.println("\n--- MENU CHINH ---");
            System.out.println("1. Dang nhap");
            System.out.println("2. Dang ky khach hang moi");
            System.out.println("0. Thoat");
            String chon = doc("Chon: ");
            try {
                if (chon.equals("0")) return;
                else if (chon.equals("1")) dangNhap();
                else if (chon.equals("2")) dangKy();
                else System.out.println("Lua chon khong hop le.");
            } catch (NumberFormatException e) {
                System.out.println("[Loi dinh dang] Du lieu nhap khong phai so: " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("[Loi CSDL] " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[Loi] " + e.getMessage());
            }
        }
    }

    private static void dangNhap() {
        String email = doc("Email: ");
        String mk = doc("Mat khau: ");
        NguoiDung nd = CH.nguoiDung.dangNhap(email, mk);
        if (nd == null) {
            System.out.println("Sai email hoac mat khau.");
            return;
        }
        System.out.println("Dang nhap thanh cong. Xin chao " + nd.getHoTen() + " (" + nd.getVaiTro() + ")");
        if (nd instanceof QuanTriVien) menuQuanTri((QuanTriVien) nd);
        else menuKhachHang((KhachHang) nd);
        nd.dangXuat();
    }

    private static void dangKy() {
        String ma = CH.nguoiDung.taoMaKhachHang();
        String ten = doc("Ho ten: ");
        String email = doc("Email: ");
        String mk = doc("Mat khau: ");
        String sdt = doc("So dien thoai: ");
        String dc = doc("Dia chi: ");
        CH.nguoiDung.them(new KhachHang(ma, ten, email, mk, sdt, dc));
        System.out.println("Dang ky thanh cong. Ma khach hang cua ban: " + ma);
    }

    // ------------------------------------------------------------ quản trị viên
    private static void menuQuanTri(QuanTriVien qt) {
        while (true) {
            System.out.println("\n--- MENU QUAN TRI ---");
            System.out.println("1. Hien thi san pham");
            System.out.println("2. Them san pham");
            System.out.println("3. Sua gia / so luong ton");
            System.out.println("4. Xoa san pham");
            System.out.println("5. Tim kiem san pham");
            System.out.println("6. Sap xep san pham theo gia");
            System.out.println("7. Thong ke");
            System.out.println("8. Nha cung cap");
            System.out.println("9. Lap phieu nhap hang");
            System.out.println("10. Quan ly don hang");
            System.out.println("11. Doanh thu");
            System.out.println("12. Danh sach nguoi dung");
            System.out.println("0. Dang xuat");
            String chon = doc("Chon: ");
            try {
                switch (chon) {
                    case "0": return;
                    case "1": CH.sanPham.hienThiDanhSach(); break;
                    case "2": nhapSanPhamMoi(); break;
                    case "3": suaSanPham(); break;
                    case "4": xoaSanPham(); break;
                    case "5": timKiem(); break;
                    case "6": sapXep(); break;
                    case "7": thongKe(); break;
                    case "8": quanLyNhaCungCap(); break;
                    case "9": lapPhieuNhap(qt); break;
                    case "10": quanLyDonHang(); break;
                    case "11": System.out.println("Doanh thu (don hoan thanh): "
                            + Fmt.tien(qt.thongKeDoanhThu(CH.donHang))); break;
                    case "12": CH.nguoiDung.hienThiDanhSach(); break;
                    default: System.out.println("Lua chon khong hop le.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Loi dinh dang] Du lieu nhap khong phai so: " + e.getMessage());
            } catch (InvalidPriceException e) {
                System.out.println("[Loi gia] " + e.getMessage());
            } catch (InvalidQuantityException e) {
                System.out.println("[Loi so luong] " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("[Loi CSDL] " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[Loi] " + e.getMessage());
            }
        }
    }

    /** Nhập sản phẩm mới: bắt lỗi bằng try-catch-finally như mô tả trong báo cáo. */
    private static void nhapSanPhamMoi() {
        String loai = doc("Loai (1 = Dien thoai, 2 = Phu kien): ");
        String ma = doc("Ma san pham: ");
        String ten = doc("Ten san pham: ");
        String hang = doc("Hang: ");
        String giaStr = doc("Gia ban: ");
        String slStr = doc("So luong ton: ");
        try {
            double gia = Double.parseDouble(giaStr);   // NumberFormatException
            int sl = Integer.parseInt(slStr);          // NumberFormatException
            SanPham sp;
            if (loai.equals("2")) {
                String lpk = doc("Loai phu kien: ");
                sp = new PhuKien(ma, ten, hang, gia, sl, lpk);
            } else {
                int ram = Integer.parseInt(doc("RAM (GB): "));
                int bn = Integer.parseInt(doc("Bo nho (GB): "));
                String mau = doc("Mau sac: ");
                sp = new DienThoai(ma, ten, hang, gia, sl, ram, bn, mau);
            }
            CH.sanPham.them(sp);                       // IllegalArgumentException khi trung ma
            System.out.println("Them san pham thanh cong.");
        } catch (NumberFormatException e) {
            System.out.println("[Loi dinh dang] Gia hoac so luong khong phai so: " + e.getMessage());
        } catch (InvalidPriceException e) {
            System.out.println("[Loi gia ban] " + e.getMessage());
        } catch (InvalidQuantityException e) {
            System.out.println("[Loi so luong] " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("[Loi CSDL] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[Loi du lieu] " + e.getMessage());
        } finally {
            System.out.println("-> Ket thuc xu ly nhap san pham " + ma);
        }
    }

    private static void suaSanPham() throws InvalidPriceException, InvalidQuantityException {
        SanPham sp = CH.sanPham.timTheoMa(doc("Ma san pham can sua: "));
        if (sp == null) { System.out.println("Khong tim thay san pham."); return; }
        String g = doc("Gia ban moi (Enter de giu nguyen): ");
        if (!g.isEmpty()) sp.setGiaBan(Double.parseDouble(g));
        String s = doc("So luong ton moi (Enter de giu nguyen): ");
        if (!s.isEmpty()) sp.setSoLuongTon(Integer.parseInt(s));
        CH.sanPham.capNhat(sp);                 // ghi xuong CSDL (neu dang dung MySQL)
        System.out.println("Da cap nhat:");
        SanPhamManager.inDanhSach(java.util.Collections.singletonList(sp));
    }

    private static void xoaSanPham() {
        String ma = doc("Ma san pham can xoa: ");
        System.out.println(CH.sanPham.xoa(ma) ? "Da xoa." : "Khong tim thay san pham.");
    }

    private static void thongKe() {
        System.out.println("Tong gia tri ton kho: " + Fmt.tien(CH.sanPham.tongGiaTriTonKho()));
        SanPham max = CH.sanPham.sanPhamGiaCaoNhat();
        System.out.println("San pham gia cao nhat: " + (max == null ? "-" : max.getTenSP()));
        System.out.println("So san pham theo hang: " + CH.sanPham.demTheoHang());
        ArrayList<SanPham> sapHet = CH.sanPham.sanPhamSapHet(5);
        System.out.println("San pham sap het hang (ton <= 5):");
        SanPhamManager.inDanhSach(sapHet);
        Map<TrangThaiDonHang, Integer> tt = CH.donHang.demTheoTrangThai();
        System.out.println("Don hang theo trang thai:");
        for (Map.Entry<TrangThaiDonHang, Integer> e : tt.entrySet()) {
            System.out.println("   " + e.getKey().getNhan() + ": " + e.getValue());
        }
    }

    private static void quanLyNhaCungCap() {
        CH.nhaCungCap.hienThiDanhSach();
        String c = doc("Them nha cung cap moi? (c/k): ");
        if (c.equalsIgnoreCase("c")) {
            CH.nhaCungCap.them(new NhaCungCap(doc("Ma NCC: "), doc("Ten NCC: "),
                    doc("Dia chi: "), doc("SDT: ")));
            System.out.println("Da them nha cung cap.");
        }
    }

    private static void lapPhieuNhap(QuanTriVien qt) throws InvalidPriceException, InvalidQuantityException {
        CH.nhaCungCap.hienThiDanhSach();
        NhaCungCap ncc = CH.nhaCungCap.timTheoMa(doc("Chon ma nha cung cap: "));
        if (ncc == null) { System.out.println("Khong tim thay nha cung cap."); return; }
        PhieuNhap pn = qt.lapPhieuNhap(CH.phieuNhap, ncc);
        while (true) {
            String ma = doc("Ma san pham nhap (Enter de ket thuc): ");
            if (ma.isEmpty()) break;
            SanPham sp = CH.sanPham.timTheoMa(ma);
            if (sp == null) { System.out.println("Khong tim thay san pham."); continue; }
            int sl = Integer.parseInt(doc("So luong nhap: "));
            double gia = Double.parseDouble(doc("Gia nhap: "));
            pn.themChiTiet(sp, sl, gia);
        }
        CH.phieuNhap.xacNhan(pn);
        System.out.println("Da lap phieu nhap va cap nhat ton kho:");
        pn.hienThi();
    }

    private static void quanLyDonHang() {
        CH.donHang.hienThiDanhSach();
        if (CH.donHang.layTatCa().isEmpty()) return;
        String ma = doc("Ma don can cap nhat trang thai (Enter de bo qua): ");
        if (ma.isEmpty()) return;
        DonHang dh = CH.donHang.timTheoMa(ma);
        if (dh == null) { System.out.println("Khong tim thay don hang."); return; }
        System.out.println("1. Dang giao  2. Hoan thanh  3. Huy don");
        String c = doc("Chon trang thai moi: ");
        TrangThaiDonHang moi = c.equals("1") ? TrangThaiDonHang.DANG_GIAO
                : c.equals("2") ? TrangThaiDonHang.HOAN_THANH
                : c.equals("3") ? TrangThaiDonHang.DA_HUY : null;
        if (moi == null) { System.out.println("Lua chon khong hop le."); return; }
        CH.donHang.capNhatTrangThai(dh.getMaDonHang(), moi);
        System.out.println("Da cap nhat trang thai don " + dh.getMaDonHang() + ": " + moi.getNhan());
    }

    // ------------------------------------------------------------ khách hàng
    private static void menuKhachHang(KhachHang kh) {
        while (true) {
            System.out.println("\n--- MENU KHACH HANG ---");
            System.out.println("1. Xem san pham");
            System.out.println("2. Tim kiem san pham");
            System.out.println("3. Sap xep san pham theo gia");
            System.out.println("4. Them san pham vao gio");
            System.out.println("5. Xem gio hang");
            System.out.println("6. Xoa san pham khoi gio");
            System.out.println("7. Dat hang");
            System.out.println("8. Lich su mua hang");
            System.out.println("9. Huy don (don dang cho xac nhan)");
            System.out.println("0. Dang xuat");
            String chon = doc("Chon: ");
            try {
                switch (chon) {
                    case "0": return;
                    case "1": CH.sanPham.hienThiDanhSach(); break;
                    case "2": timKiem(); break;
                    case "3": sapXep(); break;
                    case "4": themVaoGio(kh); break;
                    case "5": kh.getGioHang().hienThi(); break;
                    case "6": System.out.println(kh.getGioHang().xoaSanPham(doc("Ma san pham: "))
                            ? "Da xoa khoi gio." : "San pham khong co trong gio."); break;
                    case "7": datHang(kh); break;
                    case "8": lichSu(kh); break;
                    case "9": huyDon(kh); break;
                    default: System.out.println("Lua chon khong hop le.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Loi dinh dang] Du lieu nhap khong phai so: " + e.getMessage());
            } catch (InvalidQuantityException e) {
                System.out.println("[Loi so luong] " + e.getMessage());
            } catch (OutOfStockException e) {
                System.out.println("[Het hang] " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("[Loi CSDL] " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[Loi] " + e.getMessage());
            }
        }
    }

    private static void themVaoGio(KhachHang kh) throws InvalidQuantityException, OutOfStockException {
        SanPham sp = CH.sanPham.timTheoMa(doc("Ma san pham: "));
        if (sp == null) { System.out.println("Khong tim thay san pham."); return; }
        int sl = Integer.parseInt(doc("So luong: "));
        kh.getGioHang().themSanPham(sp, sl);
        System.out.println("Da them vao gio hang.");
    }

    private static void datHang(KhachHang kh) throws OutOfStockException {
        kh.getGioHang().hienThi();
        if (kh.getGioHang().isEmpty()) return;
        if (!doc("Xac nhan dat hang? (c/k): ").equalsIgnoreCase("c")) return;
        DonHang dh = kh.datHang(CH.donHang);
        System.out.println("Dat hang thanh cong! Ma don: " + dh.getMaDonHang());
        dh.hienThi();
    }

    private static void lichSu(KhachHang kh) {
        ArrayList<DonHang> ds = kh.xemLichSuMuaHang(CH.donHang);
        if (ds.isEmpty()) System.out.println("Ban chua co don hang nao.");
        for (DonHang dh : ds) dh.hienThi();
    }

    private static void huyDon(KhachHang kh) {
        DonHang dh = CH.donHang.timTheoMa(doc("Ma don can huy: "));
        if (dh == null || !dh.getMaKhachHang().equals(kh.getMaNguoiDung())) {
            System.out.println("Khong tim thay don hang cua ban.");
            return;
        }
        CH.donHang.capNhatTrangThai(dh.getMaDonHang(), TrangThaiDonHang.DA_HUY);
        System.out.println("Da huy don " + dh.getMaDonHang() + ", ton kho da duoc hoan lai.");
    }

    // ------------------------------------------------------------ dùng chung
    private static void timKiem() {
        System.out.println("1. Theo tu khoa (ten/hang)   2. Theo khoang gia");
        String c = doc("Chon: ");
        if (c.equals("2")) {
            double min = Double.parseDouble(doc("Gia tu: "));
            double max = Double.parseDouble(doc("Gia den: "));
            SanPhamManager.inDanhSach(CH.sanPham.timTheoKhoangGia(min, max));
        } else {
            ArrayList<SanPham> kq = CH.sanPham.timKiem(doc("Tu khoa: "));
            if (kq.isEmpty()) System.out.println("Khong tim thay san pham phu hop.");
            else SanPhamManager.inDanhSach(kq);
        }
    }

    private static void sapXep() {
        String c = doc("Sap xep gia: 1 = tang dan, 2 = giam dan: ");
        CH.sanPham.sapXepTheoGia(!c.equals("2"));
        CH.sanPham.hienThiDanhSach();
    }

    private static String doc(String nhac) {
        System.out.print(nhac);
        return SC.nextLine().trim();
    }
}
