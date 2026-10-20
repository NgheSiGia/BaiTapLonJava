/** Ngoại lệ tự định nghĩa: số lượng yêu cầu vượt quá số lượng tồn kho. */
public class OutOfStockException extends Exception {
    private final int soLuongTon;
    private final int soLuongYeuCau;

    public OutOfStockException(String tenSP, int soLuongTon, int soLuongYeuCau) {
        super("San pham '" + tenSP + "' khong du hang: con " + soLuongTon
                + ", yeu cau " + soLuongYeuCau);
        this.soLuongTon = soLuongTon;
        this.soLuongYeuCau = soLuongYeuCau;
    }

    public int getSoLuongTon() { return soLuongTon; }
    public int getSoLuongYeuCau() { return soLuongYeuCau; }
}
