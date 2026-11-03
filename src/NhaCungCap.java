public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String soDienThoai;

    public NhaCungCap(String maNCC, String tenNCC, String diaChi, String soDienThoai) {
        if (maNCC == null || maNCC.trim().isEmpty() || tenNCC == null || tenNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma va ten nha cung cap khong duoc de trong");
        }
        this.maNCC = maNCC.trim();
        this.tenNCC = tenNCC.trim();
        this.diaChi = diaChi == null ? "" : diaChi.trim();
        this.soDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
    }

    public String getMaNCC() { return maNCC; }
    public String getTenNCC() { return tenNCC; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
}
