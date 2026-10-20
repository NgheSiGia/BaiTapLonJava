/** Ngoại lệ tự định nghĩa: giá bán/giá nhập không hợp lệ (nhỏ hơn hoặc bằng 0). */
public class InvalidPriceException extends Exception {
    private final double giaTriSai;

    public InvalidPriceException(String message, double giaTriSai) {
        super(message);
        this.giaTriSai = giaTriSai;
    }

    public double getGiaTriSai() {
        return giaTriSai;
    }
}
