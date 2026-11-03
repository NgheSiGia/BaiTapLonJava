/** Lỗi khi làm việc với cơ sở dữ liệu (bọc SQLException để không phải khai báo throws khắp nơi). */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
