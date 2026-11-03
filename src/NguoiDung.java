/** Lớp cha trừu tượng của KhachHang và QuanTriVien (tính kế thừa). */
public abstract class NguoiDung {
    private String maNguoiDung;
    private String hoTen;
    private String email;
    private String matKhau;
    private String soDienThoai;

    public NguoiDung(String maNguoiDung, String hoTen, String email,
                     String matKhau, String soDienThoai) {
        if (maNguoiDung == null || maNguoiDung.trim().isEmpty()
                || hoTen == null || hoTen.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || matKhau == null || matKhau.isEmpty()) {
            throw new IllegalArgumentException("Ma, ho ten, email, mat khau khong duoc de trong");
        }
        this.maNguoiDung = maNguoiDung.trim();
        this.hoTen = hoTen.trim();
        this.email = email.trim();
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
    }

    public String getMaNguoiDung() { return maNguoiDung; }
    public String getHoTen() { return hoTen; }
    public String getEmail() { return email; }
    /** Chỉ dùng để lưu xuống CSDL. */
    public String getMatKhau() { return matKhau; }
    public String getSoDienThoai() { return soDienThoai; }

    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public boolean dangNhap(String email, String matKhau) {
        return this.email.equalsIgnoreCase(email.trim()) && this.matKhau.equals(matKhau);
    }

    public void dangXuat() {
        System.out.println("Da dang xuat tai khoan: " + email);
    }

    /** Vai trò hiển thị: mỗi lớp con trả về giá trị riêng. */
    public abstract String getVaiTro();

    /** Đa hình: mỗi lớp con ghi đè để hiển thị thông tin riêng. */
    public abstract void hienThiThongTin();
}
