import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quản lý kết nối JDBC tới Oracle Database (bản Free 23ai/26ai, PDB mặc định FREEPDB1).
 * Cấu hình đọc từ file db.properties trong thư mục chạy chương trình; thiếu file thì dùng giá trị mặc định.
 * Cần file driver ojdbc11.jar (hoặc ojdbc8.jar) trong thư mục lib.
 */
public final class DBConnection {
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException e) {
            // không có file cấu hình: dùng giá trị mặc định
        }
        String host = p.getProperty("db.host", "localhost").trim();
        String port = p.getProperty("db.port", "1521").trim();
        String service = p.getProperty("db.service", "freepdb1").trim();
        String tuDat = p.getProperty("db.url", "").trim();     // cho phép ghi đè nguyên chuỗi JDBC
        url = tuDat.isEmpty() ? "jdbc:oracle:thin:@//" + host + ":" + port + "/" + service : tuDat;
        user = p.getProperty("db.user", "vphone").trim();
        password = p.getProperty("db.password", "vphone123");
    }

    private DBConnection() { }

    public static String moTa() {
        return url + " (user " + user + ")";
    }

    /** Mở một kết nối mới tới Oracle. */
    public static Connection layKetNoi() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DataAccessException("Khong ket noi duoc Oracle (" + url + "): "
                    + e.getMessage() + goiY(e), e);
        }
    }

    /** Thông tin máy chủ, phiên bản Oracle, driver và schema đang dùng (lệnh: java Main --check-db). */
    public static String thongTin() {
        try (Connection c = layKetNoi()) {
            java.sql.DatabaseMetaData md = c.getMetaData();
            StringBuilder sb = new StringBuilder();
            sb.append("Ket noi thanh cong den: ").append(moTa()).append('\n');
            sb.append("May chu : ").append(md.getDatabaseProductName()).append(" - ")
              .append(md.getDatabaseProductVersion().replace('\n', ' ')).append('\n');
            sb.append("Driver  : ").append(md.getDriverName()).append(' ').append(md.getDriverVersion()).append('\n');
            sb.append("Schema  : ").append(c.getSchema());
            return sb.toString();
        } catch (SQLException e) {
            throw new DataAccessException("Khong doc duoc thong tin may chu: " + e.getMessage(), e);
        }
    }

    /** Vi phạm ràng buộc khóa ngoại (ví dụ ORA-02292: xóa dòng cha còn dòng con). */
    public static boolean laLoiKhoaNgoai(SQLException e) {
        return e instanceof java.sql.SQLIntegrityConstraintViolationException
                || e.getErrorCode() == 2292 || e.getErrorCode() == 2291
                || (e.getSQLState() != null && e.getSQLState().startsWith("23"));
    }

    /** Gợi ý khắc phục các lỗi kết nối Oracle thường gặp (không dấu để hiện trên console). */
    private static String goiY(SQLException e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        switch (e.getErrorCode()) {
            case 1017:
                return "\n   Goi y: sai user/mat khau, hoac chua tao user - hay chay sql/01_tao_user_oracle.sql bang SYSDBA.";
            case 28000:
                return "\n   Goi y: tai khoan bi khoa - chay: ALTER USER vphone ACCOUNT UNLOCK;";
            case 12541:
                return "\n   Goi y: service TNSListener chua chay - start service nay roi doi khoang 1 phut.";
            case 12514:
                return "\n   Goi y: sai service name (kiem tra db.service, vi du freepdb1) hoac OracleServiceFREE chua Running.";
            case 12560:
                return "\n   Goi y: thieu Service Name / ORACLE_SID - kiem tra lai db.properties.";
            case 1034:
            case 27101:
                return "\n   Goi y: instance Oracle chua startup - start OracleServiceFREE.";
            default:
                if (m.contains("No suitable driver")) {
                    return "\n   Goi y: chua co driver JDBC - chep ojdbc11.jar vao thu muc lib (xem lib/HUONG_DAN_DRIVER.txt).";
                }
                return "";
        }
    }
}
