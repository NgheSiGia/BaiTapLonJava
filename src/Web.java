import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Giao diện web cho VPHONE: máy chủ HTTP có sẵn của JDK (com.sun.net.httpserver), trả về JSON cho thư mục web/.
 * Chạy: java -cp "out;lib/*" Web [cong]   rồi mở http://localhost:8080 bằng Chrome.
 * Toàn bộ nghiệp vụ dùng lại các lớp Manager/DAO đã có.
 */
public class Web {
    private static CuaHang CH;
    private static final Map<String, NguoiDung> PHIEN = new HashMap<>();   // token -> người dùng đang đăng nhập
    private static final Path THU_MUC_WEB = Paths.get("web").toAbsolutePath().normalize();

    /** Lỗi trả về mã HTTP tương ứng (401, 403, 404...). */
    private static class LoiHttp extends RuntimeException {
        final int ma;
        LoiHttp(int ma, String thongBao) { super(thongBao); this.ma = ma; }
    }

    public static void main(String[] args) throws IOException {
        int cong = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        CH = CuaHang.khoiTao();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", cong), 0);   // chỉ nhận kết nối từ máy này
        server.createContext("/api/", Web::api);
        server.createContext("/", Web::tinh);
        server.setExecutor(null);     // xử lý lần lượt từng yêu cầu: không cần đồng bộ hóa dữ liệu
        server.start();
        String url = "http://localhost:" + cong;
        System.out.println("VPHONE web dang chay tai " + url + "  (nhan Ctrl+C de dung)");
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception | Error e) {
            System.out.println("Hay mo Chrome va go dia chi tren de su dung.");
        }
    }

    // ================================================================ tệp tĩnh (HTML, CSS, JS)
    private static void tinh(HttpExchange ex) throws IOException {
        String duongDan = ex.getRequestURI().getPath();
        if (duongDan.equals("/")) duongDan = "/index.html";
        Path f = THU_MUC_WEB.resolve(duongDan.substring(1)).normalize();
        if (!f.startsWith(THU_MUC_WEB) || !Files.isRegularFile(f)) {
            byte[] b = "404 - Khong tim thay".getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            ex.sendResponseHeaders(404, b.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(b); }
            return;
        }
        String ten = f.getFileName().toString();
        String loai = ten.endsWith(".html") ? "text/html; charset=utf-8"
                : ten.endsWith(".css") ? "text/css; charset=utf-8"
                : ten.endsWith(".js") ? "application/javascript; charset=utf-8"
                : ten.endsWith(".svg") ? "image/svg+xml" : "application/octet-stream";
        byte[] b = Files.readAllBytes(f);
        ex.getResponseHeaders().set("Content-Type", loai);
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.sendResponseHeaders(200, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    // ================================================================ API JSON
    private static void api(HttpExchange ex) throws IOException {
        int ma = 200;
        Object kq;
        try {
            String[] p = ex.getRequestURI().getPath().substring("/api/".length()).split("/");
            String token = ex.getRequestHeaders().getFirst("X-Token");
            NguoiDung nd = token == null ? null : PHIEN.get(token);
            kq = xuLy(ex.getRequestMethod(), p, query(ex.getRequestURI()), Json.parseObject(docBody(ex)), token, nd);
        } catch (LoiHttp e) {
            ma = e.ma;
            kq = loi(e.getMessage(), e.getClass().getSimpleName());
        } catch (NumberFormatException e) {
            ma = 400;
            kq = loi("Giá, số lượng hoặc số tiền không phải là số hợp lệ", "NumberFormatException");
        } catch (InvalidPriceException | InvalidQuantityException | OutOfStockException
                 | IllegalArgumentException | IllegalStateException e) {
            ma = 400;
            kq = loi(Vi.thongBao(e), e.getClass().getSimpleName());
        } catch (DataAccessException e) {
            ma = 500;
            kq = loi(Vi.thongBao(e), "DataAccessException");
        } catch (Exception e) {
            ma = 500;
            kq = loi("Lỗi hệ thống: " + e, e.getClass().getSimpleName());
        }
        byte[] b = Json.toJson(kq).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(ma, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private static Map<String, Object> loi(String thongBao, String loai) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("loi", thongBao);
        m.put("loai", loai);
        return m;
    }

    private static Object xuLy(String method, String[] p, Map<String, String> q, Map<String, Object> b,
                               String token, NguoiDung nd) throws Exception {
        int n = p.length;
        String goc = p[0];
        boolean get = method.equals("GET"), post = method.equals("POST");

        if (goc.equals("info") && get) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("cheDo", CH.isDungCSDL() ? "MySQL" : "Bộ nhớ");
            m.put("csdl", CH.isDungCSDL() ? DBConnection.moTa() : "Dữ liệu chỉ lưu trong bộ nhớ (chưa kết nối MySQL)");
            return m;
        }
        if (goc.equals("login") && post) {
            NguoiDung u = CH.nguoiDung.dangNhap(chuoi(b, "email"), chuoi(b, "matKhau"));
            if (u == null) throw new LoiHttp(401, "Sai email hoặc mật khẩu");
            String tk = UUID.randomUUID().toString();
            PHIEN.put(tk, u);
            Map<String, Object> m = nguoiDungToMap(u);
            m.put("token", tk);
            return m;
        }
        if (goc.equals("register") && post) {
            String ma = CH.nguoiDung.taoMaKhachHang();
            KhachHang kh = new KhachHang(ma, chuoi(b, "hoTen"), chuoi(b, "email"), chuoi(b, "matKhau"),
                    chuoiHoacRong(b, "soDienThoai"), chuoiHoacRong(b, "diaChi"));
            CH.nguoiDung.them(kh);
            return nguoiDungToMap(kh);
        }
        if (goc.equals("logout") && post) {
            if (token != null) PHIEN.remove(token);
            return ok();
        }
        if (goc.equals("me") && get) {
            if (nd == null) throw new LoiHttp(401, "Chưa đăng nhập");
            return nguoiDungToMap(nd);
        }

        switch (goc) {
            // ---------------------------------------------------------------- sản phẩm
            case "sanpham": {
                if (get && n == 1) return layDanhSachSanPham(q);
                if (post && n == 1) {
                    quanTri(nd);
                    String loai = chuoiHoacRong(b, "loai");
                    SanPham sp;
                    if (loai.equals("PHU_KIEN")) {
                        sp = new PhuKien(chuoi(b, "ma"), chuoi(b, "ten"), chuoi(b, "hang"), so(b, "gia"),
                                nguyen(b, "ton"), chuoiHoacRong(b, "loaiPhuKien"));
                    } else {
                        sp = new DienThoai(chuoi(b, "ma"), chuoi(b, "ten"), chuoi(b, "hang"), so(b, "gia"),
                                nguyen(b, "ton"), nguyen(b, "ram"), nguyen(b, "boNho"), chuoiHoacRong(b, "mauSac"));
                    }
                    CH.sanPham.them(sp);
                    return spToMap(sp);
                }
                if (method.equals("PUT") && n == 2) {
                    quanTri(nd);
                    SanPham sp = timSanPham(p[1]);
                    if (coGiaTri(b, "ten")) sp.setTenSP(chuoi(b, "ten"));
                    if (coGiaTri(b, "hang")) sp.setHang(chuoi(b, "hang"));
                    if (coGiaTri(b, "gia")) sp.setGiaBan(so(b, "gia"));
                    if (coGiaTri(b, "ton")) sp.setSoLuongTon(nguyen(b, "ton"));
                    CH.sanPham.capNhat(sp);
                    return spToMap(sp);
                }
                if (method.equals("DELETE") && n == 2) {
                    quanTri(nd);
                    timSanPham(p[1]);
                    CH.sanPham.xoa(p[1]);
                    return ok();
                }
                break;
            }
            // ---------------------------------------------------------------- giỏ hàng
            case "gio": {
                KhachHang kh = khachHang(nd);
                if (get && n == 1) return gioToMap(kh);
                if (post && n == 1) {
                    kh.getGioHang().themSanPham(timSanPham(chuoi(b, "ma")), nguyen(b, "soLuong"));
                    return gioToMap(kh);
                }
                if (method.equals("DELETE") && n == 2) {
                    kh.getGioHang().xoaSanPham(p[1]);
                    return gioToMap(kh);
                }
                break;
            }
            // ---------------------------------------------------------------- đơn hàng
            case "donhang": {
                if (nd == null) throw new LoiHttp(401, "Vui lòng đăng nhập");
                if (get && n == 1) {
                    List<DonHang> ds = nd instanceof KhachHang
                            ? ((KhachHang) nd).xemLichSuMuaHang(CH.donHang)
                            : new ArrayList<>(CH.donHang.layTatCa());
                    Collections.reverse(ds);       // đơn mới nhất lên đầu
                    List<Object> kq = new ArrayList<>();
                    for (DonHang d : ds) kq.add(donHangToMap(d));
                    return kq;
                }
                if (post && n == 1) {
                    KhachHang kh = khachHang(nd);
                    return donHangToMap(kh.datHang(CH.donHang));
                }
                if (post && n == 3 && p[2].equals("trangthai")) {
                    DonHang d = CH.donHang.timTheoMa(p[1]);
                    if (d == null) throw new LoiHttp(404, "Không tìm thấy đơn hàng " + p[1]);
                    TrangThaiDonHang moi = TrangThaiDonHang.valueOf(chuoi(b, "trangThai"));
                    if (nd instanceof KhachHang) {
                        if (!d.getMaKhachHang().equals(nd.getMaNguoiDung())) throw new LoiHttp(403, "Đây không phải đơn hàng của bạn");
                        if (moi != TrangThaiDonHang.DA_HUY) throw new LoiHttp(403, "Khách hàng chỉ được hủy đơn");
                    }
                    CH.donHang.capNhatTrangThai(d.getMaDonHang(), moi);
                    return donHangToMap(d);
                }
                break;
            }
            // ---------------------------------------------------------------- quản trị
            case "thongke": {
                quanTri(nd);
                if (get) return thongKe();
                break;
            }
            case "nhacungcap": {
                quanTri(nd);
                if (get) {
                    List<Object> kq = new ArrayList<>();
                    for (NhaCungCap c : CH.nhaCungCap.layTatCa()) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("ma", c.getMaNCC()); m.put("ten", c.getTenNCC());
                        m.put("diaChi", c.getDiaChi()); m.put("sdt", c.getSoDienThoai());
                        kq.add(m);
                    }
                    return kq;
                }
                if (post) {
                    CH.nhaCungCap.them(new NhaCungCap(chuoi(b, "ma"), chuoi(b, "ten"),
                            chuoiHoacRong(b, "diaChi"), chuoiHoacRong(b, "sdt")));
                    return ok();
                }
                break;
            }
            case "phieunhap": {
                QuanTriVien qt = quanTri(nd);
                if (get) {
                    List<Object> kq = new ArrayList<>();
                    for (PhieuNhap pn : CH.phieuNhap.layTatCa()) kq.add(phieuNhapToMap(pn));
                    Collections.reverse(kq);
                    return kq;
                }
                if (post) {
                    NhaCungCap ncc = CH.nhaCungCap.timTheoMa(chuoi(b, "maNCC"));
                    if (ncc == null) throw new LoiHttp(404, "Không tìm thấy nhà cung cấp");
                    PhieuNhap pn = qt.lapPhieuNhap(CH.phieuNhap, ncc);
                    Object items = b.get("items");
                    if (items instanceof List) {
                        for (Object o : (List<?>) items) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> it = (Map<String, Object>) o;
                            pn.themChiTiet(timSanPham(chuoi(it, "ma")), nguyen(it, "soLuong"), so(it, "giaNhap"));
                        }
                    }
                    CH.phieuNhap.xacNhan(pn);
                    return phieuNhapToMap(pn);
                }
                break;
            }
            case "nguoidung": {
                quanTri(nd);
                if (get) {
                    List<Object> kq = new ArrayList<>();
                    for (NguoiDung u : CH.nguoiDung.layTatCa()) kq.add(nguoiDungToMap(u));
                    return kq;
                }
                break;
            }
            default:
        }
        throw new LoiHttp(404, "Không tìm thấy API: " + method + " /api/" + String.join("/", p));
    }

    // ================================================================ nghiệp vụ gọi lại các lớp Manager
    private static Object layDanhSachSanPham(Map<String, String> q) {
        String sx = q.getOrDefault("sort", "");
        if (sx.equals("gia_tang")) CH.sanPham.sapXepTheoGia(true);          // Collections.sort + Comparator
        else if (sx.equals("gia_giam")) CH.sanPham.sapXepTheoGia(false);
        String tuKhoa = q.getOrDefault("q", "").trim();
        List<SanPham> ds = tuKhoa.isEmpty() ? new ArrayList<>(CH.sanPham.layTatCa()) : CH.sanPham.timKiem(tuKhoa);
        if (sx.isEmpty()) {
            ds.sort((a, b) -> a.getMaSP().compareToIgnoreCase(b.getMaSP()));   // thứ tự mặc định: theo mã sản phẩm
        }
        double min = q.containsKey("min") && !q.get("min").isEmpty() ? Double.parseDouble(q.get("min")) : 0;
        double max = q.containsKey("max") && !q.get("max").isEmpty() ? Double.parseDouble(q.get("max")) : Double.MAX_VALUE;
        List<Object> kq = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getGiaBan() >= min && sp.getGiaBan() <= max) kq.add(spToMap(sp));
        }
        return kq;
    }

    private static Object thongKe() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tongGiaTriTon", CH.sanPham.tongGiaTriTonKho());
        m.put("doanhThu", CH.donHang.tongDoanhThu());
        SanPham max = CH.sanPham.sanPhamGiaCaoNhat();
        m.put("giaCaoNhat", max == null ? null : spToMap(max));
        m.put("theoHang", new LinkedHashMap<String, Object>(CH.sanPham.demTheoHang()));
        List<Object> sapHet = new ArrayList<>();
        for (SanPham sp : CH.sanPham.sanPhamSapHet(5)) sapHet.add(spToMap(sp));
        m.put("sapHet", sapHet);
        Map<String, Object> tt = new LinkedHashMap<>();
        for (Map.Entry<TrangThaiDonHang, Integer> e : CH.donHang.demTheoTrangThai().entrySet()) tt.put(e.getKey().name(), e.getValue());
        m.put("donTheoTrangThai", tt);
        m.put("soSanPham", CH.sanPham.layTatCa().size());
        m.put("soKhachHang", CH.nguoiDung.layTatCa().stream().filter(x -> x instanceof KhachHang).count());
        return m;
    }

    // ================================================================ chuyển đối tượng -> Map (để ghi JSON)
    private static Map<String, Object> spToMap(SanPham sp) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ma", sp.getMaSP()); m.put("ten", sp.getTenSP()); m.put("hang", sp.getHang());
        m.put("gia", sp.getGiaBan()); m.put("giaBan", sp.tinhGiaBan()); m.put("ton", sp.getSoLuongTon());
        if (sp instanceof DienThoai) {
            DienThoai d = (DienThoai) sp;
            m.put("loai", "DIEN_THOAI"); m.put("ram", d.getRam()); m.put("boNho", d.getBoNho()); m.put("mauSac", d.getMauSac());
        } else if (sp instanceof PhuKien) {
            m.put("loai", "PHU_KIEN"); m.put("loaiPhuKien", ((PhuKien) sp).getLoaiPhuKien());
        } else {
            m.put("loai", "KHAC");
        }
        return m;
    }

    private static Map<String, Object> nguoiDungToMap(NguoiDung u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ma", u.getMaNguoiDung()); m.put("hoTen", u.getHoTen()); m.put("email", u.getEmail());
        m.put("sdt", u.getSoDienThoai());
        m.put("vaiTro", u instanceof QuanTriVien ? "QUAN_TRI" : "KHACH_HANG");
        if (u instanceof KhachHang) m.put("diaChi", ((KhachHang) u).getDiaChi());
        if (u instanceof QuanTriVien) m.put("chucVu", ((QuanTriVien) u).getChucVu());
        return m;
    }

    private static Map<String, Object> gioToMap(KhachHang kh) {
        GioHang g = kh.getGioHang();
        List<Object> items = new ArrayList<>();
        int soLuong = 0;
        for (ChiTietGioHang ct : g.getDanhSachChiTiet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            SanPham sp = ct.getSanPham();
            m.put("ma", sp.getMaSP()); m.put("ten", sp.getTenSP()); m.put("soLuong", ct.getSoLuong());
            m.put("donGia", sp.tinhGiaBan()); m.put("thanhTien", ct.getThanhTien()); m.put("ton", sp.getSoLuongTon());
            items.add(m);
            soLuong += ct.getSoLuong();
        }
        Map<String, Object> kq = new LinkedHashMap<>();
        kq.put("items", items); kq.put("tong", g.tinhTongTien()); kq.put("soLuong", soLuong);
        return kq;
    }

    private static Map<String, Object> donHangToMap(DonHang d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ma", d.getMaDonHang()); m.put("maKhach", d.getMaKhachHang()); m.put("khach", d.getTenKhachHang());
        m.put("ngay", d.getNgayDat().toString()); m.put("trangThai", d.getTrangThai().name());
        m.put("tong", d.tinhTongTien());
        List<Object> ct = new ArrayList<>();
        for (ChiTietDonHang c : d.getDanhSachChiTiet()) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("ma", c.getSanPham().getMaSP()); x.put("ten", c.getSanPham().getTenSP());
            x.put("soLuong", c.getSoLuong()); x.put("donGia", c.getDonGia()); x.put("thanhTien", c.getThanhTien());
            ct.add(x);
        }
        m.put("chiTiet", ct);
        return m;
    }

    private static Map<String, Object> phieuNhapToMap(PhieuNhap pn) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ma", pn.getMaPhieu()); m.put("ncc", pn.getNhaCungCap().getTenNCC());
        m.put("ngay", pn.getNgayNhap().toString()); m.put("tong", pn.tinhTongTienNhap());
        List<Object> ct = new ArrayList<>();
        for (ChiTietPhieuNhap c : pn.getDanhSachChiTiet()) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("ma", c.getSanPham().getMaSP()); x.put("ten", c.getSanPham().getTenSP());
            x.put("soLuong", c.getSoLuongNhap()); x.put("giaNhap", c.getGiaNhap());
            ct.add(x);
        }
        m.put("chiTiet", ct);
        return m;
    }

    // ================================================================ tiện ích
    private static Map<String, Object> ok() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", true);
        return m;
    }

    private static SanPham timSanPham(String ma) {
        SanPham sp = CH.sanPham.timTheoMa(ma);
        if (sp == null) throw new LoiHttp(404, "Không tìm thấy sản phẩm " + ma);
        return sp;
    }

    private static QuanTriVien quanTri(NguoiDung nd) {
        if (nd == null) throw new LoiHttp(401, "Vui lòng đăng nhập");
        if (!(nd instanceof QuanTriVien)) throw new LoiHttp(403, "Chỉ quản trị viên mới được thực hiện thao tác này");
        return (QuanTriVien) nd;
    }

    private static KhachHang khachHang(NguoiDung nd) {
        if (nd == null) throw new LoiHttp(401, "Vui lòng đăng nhập để mua hàng");
        if (!(nd instanceof KhachHang)) throw new LoiHttp(403, "Thao tác này dành cho tài khoản khách hàng");
        return (KhachHang) nd;
    }

    private static boolean coGiaTri(Map<String, Object> b, String k) {
        Object v = b.get(k);
        return v != null && !(v instanceof String && ((String) v).trim().isEmpty());
    }

    private static String chuoi(Map<String, Object> b, String k) {
        if (!coGiaTri(b, k)) throw new IllegalArgumentException("Thieu du lieu: " + k);
        return String.valueOf(b.get(k)).trim();
    }

    private static String chuoiHoacRong(Map<String, Object> b, String k) {
        return coGiaTri(b, k) ? String.valueOf(b.get(k)).trim() : "";
    }

    private static double so(Map<String, Object> b, String k) {
        Object v = b.get(k);
        if (v == null || (v instanceof String && ((String) v).trim().isEmpty())) throw new IllegalArgumentException("Thieu du lieu: " + k);
        return v instanceof Number ? ((Number) v).doubleValue() : Double.parseDouble(String.valueOf(v).trim());
    }

    private static int nguyen(Map<String, Object> b, String k) {
        double d = so(b, k);
        if (d != Math.rint(d)) throw new NumberFormatException("Khong phai so nguyen: " + d);
        return (int) d;
    }

    private static String docBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody(); ByteArrayOutputStream bo = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = in.read(buf)) > 0) bo.write(buf, 0, r);
            return new String(bo.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> query(URI uri) {
        Map<String, String> m = new HashMap<>();
        String raw = uri.getRawQuery();
        if (raw == null) return m;
        for (String kv : raw.split("&")) {
            int i = kv.indexOf('=');
            String k = i < 0 ? kv : kv.substring(0, i);
            String v = i < 0 ? "" : kv.substring(i + 1);
            m.put(URLDecoder.decode(k, StandardCharsets.UTF_8), URLDecoder.decode(v, StandardCharsets.UTF_8));
        }
        return m;
    }
}
