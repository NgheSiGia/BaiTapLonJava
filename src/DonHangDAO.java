import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/** DAO cho don_hang và chi_tiet_don_hang. Các thao tác ghi dùng giao dịch (transaction). */
public class DonHangDAO {

    public ArrayList<DonHang> layTatCa(SanPhamManager qlSanPham) {
        ArrayList<DonHang> ds = new ArrayList<>();
        try (Connection c = DBConnection.layKetNoi()) {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM don_hang ORDER BY ma_don_hang")) {
                while (rs.next()) {
                    ds.add(new DonHang(rs.getString("ma_don_hang"), rs.getString("ma_khach_hang"),
                            rs.getString("ten_khach_hang"), rs.getDate("ngay_dat").toLocalDate(),
                            TrangThaiDonHang.valueOf(rs.getString("trang_thai"))));
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT ma_sp, so_luong, don_gia FROM chi_tiet_don_hang WHERE ma_don_hang=? ORDER BY id")) {
                for (DonHang dh : ds) {
                    ps.setString(1, dh.getMaDonHang());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            SanPham sp = qlSanPham.timTheoMa(rs.getString("ma_sp"));
                            if (sp != null) {
                                dh.napChiTiet(new ChiTietDonHang(sp, rs.getInt("so_luong"), rs.getDouble("don_gia")));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi doc don hang: " + e.getMessage(), e);
        }
        return ds;
    }

    /** Lưu đơn hàng, chi tiết và số lượng tồn mới của các sản phẩm trong MỘT giao dịch. */
    public void them(DonHang dh) {
        try (Connection c = DBConnection.layKetNoi()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO don_hang (ma_don_hang, ma_khach_hang, ten_khach_hang, ngay_dat, trang_thai)"
                        + " VALUES (?,?,?,?,?)")) {
                    ps.setString(1, dh.getMaDonHang());
                    ps.setString(2, dh.getMaKhachHang());
                    ps.setString(3, dh.getTenKhachHang());
                    ps.setDate(4, Date.valueOf(dh.getNgayDat()));
                    ps.setString(5, dh.getTrangThai().name());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO chi_tiet_don_hang (ma_don_hang, ma_sp, so_luong, don_gia) VALUES (?,?,?,?)")) {
                    for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
                        ps.setString(1, dh.getMaDonHang());
                        ps.setString(2, ct.getSanPham().getMaSP());
                        ps.setInt(3, ct.getSoLuong());
                        ps.setDouble(4, ct.getDonGia());
                        ps.executeUpdate();
                        SanPhamDAO.capNhatTonKho(c, ct.getSanPham());
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi luu don hang (da hoan tac): " + e.getMessage(), e);
        }
    }

    /** Cập nhật trạng thái; nếu hủy đơn thì ghi luôn tồn kho đã được hoàn lại. */
    public void capNhatTrangThai(DonHang dh) {
        try (Connection c = DBConnection.layKetNoi()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE don_hang SET trang_thai=? WHERE ma_don_hang=?")) {
                    ps.setString(1, dh.getTrangThai().name());
                    ps.setString(2, dh.getMaDonHang());
                    ps.executeUpdate();
                }
                if (dh.getTrangThai() == TrangThaiDonHang.DA_HUY) {
                    for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
                        SanPhamDAO.capNhatTonKho(c, ct.getSanPham());
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi cap nhat trang thai don hang (da hoan tac): " + e.getMessage(), e);
        }
    }
}
