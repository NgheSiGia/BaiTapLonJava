/** Ngoại lệ tự định nghĩa: số lượng không hợp lệ (số âm hoặc bằng 0 khi mua/nhập). */
public class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
