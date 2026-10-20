public enum TrangThaiDonHang {
    CHO_XAC_NHAN("Cho xac nhan"),
    DANG_GIAO("Dang giao"),
    HOAN_THANH("Hoan thanh"),
    DA_HUY("Da huy");

    private final String nhan;

    TrangThaiDonHang(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }

    /** Quy tắc chuyển trạng thái hợp lệ. */
    public boolean coTheChuyenSang(TrangThaiDonHang moi) {
        switch (this) {
            case CHO_XAC_NHAN: return moi == DANG_GIAO || moi == DA_HUY;
            case DANG_GIAO:    return moi == HOAN_THANH;
            default:           return false;
        }
    }
}
