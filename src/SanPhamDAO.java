import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

/** Lớp truy cập dữ liệu (DAO) cho bảng san_pham. */
public class SanPhamDAO {

    public ArrayList<SanPham> layTatCa() {
        ArrayList<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM san_pham ORDER BY ma_sp";
        try (Connection c = DBConnection.layKetNoi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(doc(rs));
            }
        } catch (SQLException | InvalidPriceException | InvalidQuantityException e) {
            throw new DataAccessException("Loi doc san pham: " + e.getMessage(), e);
        }
        return ds;
    }

    public void them(SanPham sp) {
        String sql = "INSERT INTO san_pham (ma_sp, ten_sp, hang, gia_ban, so_luong_ton, loai,"
                + " ram, bo_nho, mau_sac, loai_phu_kien) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.layKetNoi(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setString(3, sp.getHang());
            ps.setDouble(4, sp.getGiaBan());
            ps.setInt(5, sp.getSoLuongTon());
            if (sp instanceof DienThoai) {
                DienThoai dt = (DienThoai) sp;
                ps.setString(6, "DIEN_THOAI");
                ps.setInt(7, dt.getRam());
                ps.setInt(8, dt.getBoNho());
                ps.setString(9, dt.getMauSac());
                ps.setNull(10, java.sql.Types.VARCHAR);
            } else if (sp instanceof PhuKien) {
                ps.setString(6, "PHU_KIEN");
                ps.setNull(7, java.sql.Types.INTEGER);
                ps.setNull(8, java.sql.Types.INTEGER);
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setString(10, ((PhuKien) sp).getLoaiPhuKien());
            } else {
                ps.setString(6, "KHAC");
                ps.setNull(7, java.sql.Types.INTEGER);
                ps.setNull(8, java.sql.Types.INTEGER);
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Loi them san pham: " + e.getMessage(), e);
        }
    }

    /** Cập nhật tên, hãng, giá bán, số lượng tồn. */
    public void capNhat(SanPham sp) {
        String sql = "UPDATE san_pham SET ten_sp=?, hang=?, gia_ban=?, so_luong_ton=? WHERE ma_sp=?";
        try (Connection c = DBConnection.layKetNoi(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getHang());
            ps.setDouble(3, sp.getGiaBan());
            ps.setInt(4, sp.getSoLuongTon());
            ps.setString(5, sp.getMaSP());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Loi cap nhat san pham: " + e.getMessage(), e);
        }
    }

    public void xoa(String ma) {
        try (Connection c = DBConnection.layKetNoi();
             PreparedStatement ps = c.prepareStatement("DELETE FROM san_pham WHERE ma_sp=?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Khong the xoa san pham '" + ma
                    + "' vi da co trong don hang hoac phieu nhap");
        } catch (SQLException e) {
            throw new DataAccessException("Loi xoa san pham: " + e.getMessage(), e);
        }
    }

    /** Ghi số lượng tồn hiện tại của sản phẩm, dùng chung kết nối của một giao dịch. */
    static void capNhatTonKho(Connection c, SanPham sp) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE san_pham SET so_luong_ton=? WHERE ma_sp=?")) {
            ps.setInt(1, sp.getSoLuongTon());
            ps.setString(2, sp.getMaSP());
            ps.executeUpdate();
        }
    }

    private SanPham doc(ResultSet rs) throws SQLException, InvalidPriceException, InvalidQuantityException {
        String ma = rs.getString("ma_sp");
        String ten = rs.getString("ten_sp");
        String hang = rs.getString("hang");
        double gia = rs.getDouble("gia_ban");
        int ton = rs.getInt("so_luong_ton");
        String loai = rs.getString("loai");
        if ("DIEN_THOAI".equals(loai)) {
            return new DienThoai(ma, ten, hang, gia, ton, rs.getInt("ram"), rs.getInt("bo_nho"),
                    rs.getString("mau_sac"));
        } else if ("PHU_KIEN".equals(loai)) {
            return new PhuKien(ma, ten, hang, gia, ton, rs.getString("loai_phu_kien"));
        }
        return new SanPham(ma, ten, hang, gia, ton);
    }
}
