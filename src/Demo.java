import java.util.Map;

/** Kịch bản chạy tự động (không cần nhập): java Main --demo */
public class Demo {
    private static void tieuDe(String s) {
        System.out.println("\n==== " + s + " ====");
    }

    public static void chay(CuaHang ch) {
        System.out.println("##### VPHONE - CHAY THU TU DONG #####");

        tieuDe("1. Hien thi danh sach san pham");
        ch.sanPham.hienThiDanhSach();

        tieuDe("2. Tim kiem tu khoa 'samsung'");
        SanPhamManager.inDanhSach(ch.sanPham.timKiem("samsung"));

        tieuDe("3. Sap xep theo gia tang dan / giam dan");
        ch.sanPham.sapXepTheoGia(true);
        ch.sanPham.hienThiDanhSach();
        System.out.println("--- giam dan ---");
        ch.sanPham.sapXepTheoGia(false);
        ch.sanPham.hienThiDanhSach();

        tieuDe("4. Thong ke");
        System.out.println("Tong gia tri ton kho: " + Fmt.tien(ch.sanPham.tongGiaTriTonKho()));
        System.out.println("Gia cao nhat: " + ch.sanPham.sanPhamGiaCaoNhat().getTenSP());
        System.out.println("So SP theo hang: " + ch.sanPham.demTheoHang());
        System.out.println("San pham sap het hang (ton <= 5):");
        SanPhamManager.inDanhSach(ch.sanPham.sanPhamSapHet(5));

        tieuDe("5. Nhap san pham sai du lieu (try-catch-finally)");
        String[][] du = {
            {"SP07", "Vivo V30", "Vivo", "10990000", "12"},
            {"SP08", "Pixel 8", "Google", "abc", "3"},
            {"SP09", "Oppo Reno 11", "Oppo", "-500000", "4"},
            {"SP10", "Realme 12", "Realme", "7990000", "-2"},
            {"SP01", "iPhone 15 (trung ma)", "Apple", "22990000", "1"},
        };
        for (String[] r : du) {
            nhapThu(ch, r[0], r[1], r[2], r[3], r[4]);
        }

        tieuDe("6. Khach hang them vao gio va dat hang");
        KhachHang kh = (KhachHang) ch.nguoiDung.timTheoMa("KH001");
        try {
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("SP01"), 2);
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("PK01"), 3);
            kh.getGioHang().hienThi();
            DonHang dh = kh.datHang(ch.donHang);
            System.out.println("Dat hang thanh cong:");
            dh.hienThi();
            System.out.println("Ton kho SP01 sau khi dat: " + ch.sanPham.timTheoMa("SP01").getSoLuongTon());
        } catch (OutOfStockException | InvalidQuantityException e) {
            System.out.println("[Loi] " + e.getMessage());
        }

        tieuDe("7. Dat vuot ton kho (Galaxy S24 chi con 5)");
        try {
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("SP02"), 9);
        } catch (OutOfStockException e) {
            System.out.println("[Het hang] " + e.getMessage());
        } catch (InvalidQuantityException e) {
            System.out.println("[Loi so luong] " + e.getMessage());
        }
        try {
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("SP02"), -1);
        } catch (OutOfStockException | InvalidQuantityException e) {
            System.out.println("[Loi so luong] " + e.getMessage());
        }

        tieuDe("8. Dat 2 mat hang, mot mat hang het hang -> khong tru kho mat hang nao");
        try {
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("SP03"), 4);
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("PK02"), 3);   // ton PK02 = 3
            ch.sanPham.timTheoMa("PK02").setSoLuongTon(1);                  // gia lap co nguoi khac mua truoc
            kh.datHang(ch.donHang);
        } catch (OutOfStockException e) {
            System.out.println("[Het hang] " + e.getMessage());
            System.out.println("Ton kho SP03 van la: " + ch.sanPham.timTheoMa("SP03").getSoLuongTon() + " (khong bi tru)");
        } catch (InvalidQuantityException e) {
            System.out.println("[Loi] " + e.getMessage());
        }
        kh.getGioHang().lamTrong();

        tieuDe("9. Cap nhat trang thai don hang, huy don hoan kho");
        DonHang don1 = ch.donHang.timTheoMa("DH001");
        don1.capNhatTrangThai(TrangThaiDonHang.DANG_GIAO);
        don1.capNhatTrangThai(TrangThaiDonHang.HOAN_THANH);
        System.out.println("DH001 -> " + don1.getTrangThai().getNhan());
        try {
            don1.capNhatTrangThai(TrangThaiDonHang.DA_HUY);
        } catch (IllegalStateException e) {
            System.out.println("[Loi] " + e.getMessage());
        }
        try {
            kh.getGioHang().themSanPham(ch.sanPham.timTheoMa("SP04"), 2);
            DonHang don2 = kh.datHang(ch.donHang);
            System.out.println("Ton SP04 sau khi dat " + don2.getMaDonHang() + ": " + ch.sanPham.timTheoMa("SP04").getSoLuongTon());
            don2.capNhatTrangThai(TrangThaiDonHang.DA_HUY);
            System.out.println("Ton SP04 sau khi huy: " + ch.sanPham.timTheoMa("SP04").getSoLuongTon());
        } catch (OutOfStockException | InvalidQuantityException e) {
            System.out.println("[Loi] " + e.getMessage());
        }

        tieuDe("10. Quan tri vien lap phieu nhap hang");
        QuanTriVien qt = (QuanTriVien) ch.nguoiDung.timTheoMa("QT001");
        try {
            PhieuNhap pn = qt.lapPhieuNhap(ch.phieuNhap, ch.nhaCungCap.timTheoMa("NCC01"));
            pn.themChiTiet(ch.sanPham.timTheoMa("SP02"), 10, 17500000);
            pn.themChiTiet(ch.sanPham.timTheoMa("PK02"), 20, 300000);
            ch.phieuNhap.xacNhan(pn);
            pn.hienThi();
            System.out.println("Ton SP02 sau nhap: " + ch.sanPham.timTheoMa("SP02").getSoLuongTon());
        } catch (InvalidPriceException | InvalidQuantityException e) {
            System.out.println("[Loi] " + e.getMessage());
        }

        tieuDe("11. Doanh thu va thong ke don hang");
        System.out.println("Doanh thu: " + Fmt.tien(qt.thongKeDoanhThu(ch.donHang)));
        for (Map.Entry<TrangThaiDonHang, Integer> e : ch.donHang.demTheoTrangThai().entrySet()) {
            System.out.println("   " + e.getKey().getNhan() + ": " + e.getValue());
        }

        tieuDe("12. Danh sach nguoi dung (da hinh)");
        ch.nguoiDung.hienThiDanhSach();
    }

    private static void nhapThu(CuaHang ch, String ma, String ten, String hang, String giaStr, String slStr) {
        try {
            double gia = Double.parseDouble(giaStr);
            int sl = Integer.parseInt(slStr);
            ch.sanPham.them(new DienThoai(ma, ten, hang, gia, sl, 8, 128, "Den"));
            System.out.println("Them thanh cong: " + ma);
        } catch (NumberFormatException e) {
            System.out.println("[Loi dinh dang] Gia hoac so luong khong phai so: " + e.getMessage());
        } catch (InvalidPriceException e) {
            System.out.println("[Loi gia ban] " + e.getMessage());
        } catch (InvalidQuantityException e) {
            System.out.println("[Loi so luong] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[Loi du lieu] " + e.getMessage());
        } finally {
            System.out.println("-> Ket thuc xu ly nhap san pham " + ma);
        }
    }
}
