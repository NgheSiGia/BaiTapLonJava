import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quản lý kết nối JDBC tới MySQL/MariaDB (XAMPP). Cấu hình đọc từ file db.properties
 * trong thư mục chạy chương trình; thiếu file thì dùng mặc định của XAMPP.
 */
public final class DBConnection {
    private static String driver = "mariadb";   // "mariadb" hoặc "mysql" (tùy file .jar trong thư mục lib)
    private static String host = "localhost";
    private static String port = "3306";
    private static String tenCSDL = "vphone";
    private static String user = "root";
    private static String password = "";

    static {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException e) {
            // không có file cấu hình: dùng giá trị mặc định
        }
        driver = p.getProperty("db.driver", driver).trim();
        host = p.getProperty("db.host", host).trim();
        port = p.getProperty("db.port", port).trim();
        tenCSDL = p.getProperty("db.name", tenCSDL).trim();
        user = p.getProperty("db.user", user).trim();
        password = p.getProperty("db.password", password);
    }

    private DBConnection() { }

    public static String getTenCSDL() { return tenCSDL; }

    public static String moTa() {
        return "jdbc:" + driver + "://" + host + ":" + port + "/" + tenCSDL + " (user " + user + ")";
    }

    /** Kết nối tới CSDL vphone. */
    public static Connection layKetNoi() {
        return mo(tenCSDL);
    }

    /** Kết nối tới máy chủ (chưa chọn CSDL) - dùng khi cần tạo CSDL. */
    public static Connection layKetNoiMayChu() {
        return mo("");
    }

    private static Connection mo(String db) {
        String url = "jdbc:" + driver + "://" + host + ":" + port + "/" + db;
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DataAccessException("Khong ket noi duoc MySQL (" + url + "): " + e.getMessage(), e);
        }
    }
}
