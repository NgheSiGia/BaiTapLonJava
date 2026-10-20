import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

/** DAO cho bảng nha_cung_cap. */
public class NhaCungCapDAO {

    public ArrayList<NhaCungCap> layTatCa() {
        ArrayList<NhaCungCap> ds = new ArrayList<>();
        try (Connection c = DBConnection.layKetNoi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM nha_cung_cap ORDER BY ma_ncc")) {
            while (rs.next()) {
                ds.add(new NhaCungCap(rs.getString("ma_ncc"), rs.getString("ten_ncc"),
                        rs.getString("dia_chi"), rs.getString("so_dien_thoai")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi doc nha cung cap: " + e.getMessage(), e);
        }
        return ds;
    }

    public void them(NhaCungCap n) {
        String sql = "INSERT INTO nha_cung_cap (ma_ncc, ten_ncc, dia_chi, so_dien_thoai) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.layKetNoi(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, n.getMaNCC());
            ps.setString(2, n.getTenNCC());
            ps.setString(3, n.getDiaChi());
            ps.setString(4, n.getSoDienThoai());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Loi them nha cung cap: " + e.getMessage(), e);
        }
    }

    public void xoa(String ma) {
        try (Connection c = DBConnection.layKetNoi();
             PreparedStatement ps = c.prepareStatement("DELETE FROM nha_cung_cap WHERE ma_ncc=?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Khong the xoa nha cung cap '" + ma + "' vi da co phieu nhap");
        } catch (SQLException e) {
            throw new DataAccessException("Loi xoa nha cung cap: " + e.getMessage(), e);
        }
    }
}
