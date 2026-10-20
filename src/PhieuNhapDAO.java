import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/** DAO cho phieu_nhap và chi_tiet_phieu_nhap (ghi trong một giao dịch, kèm cập nhật tồn kho). */
public class PhieuNhapDAO {

    public ArrayList<PhieuNhap> layTatCa(SanPhamManager qlSanPham, NhaCungCapManager qlNCC) {
        ArrayList<PhieuNhap> ds = new ArrayList<>();
        try (Connection c = DBConnection.layKetNoi()) {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM phieu_nhap ORDER BY ma_phieu")) {
                while (rs.next()) {
                    NhaCungCap ncc = qlNCC.timTheoMa(rs.getString("ma_ncc"));
                    ds.add(new PhieuNhap(rs.getString("ma_phieu"), ncc, rs.getString("ma_quan_tri"),
                            rs.getDate("ngay_nhap").toLocalDate()));
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT ma_sp, so_luong_nhap, gia_nhap FROM chi_tiet_phieu_nhap WHERE ma_phieu=? ORDER BY id")) {
                for (PhieuNhap pn : ds) {
                    ps.setString(1, pn.getMaPhieu());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            SanPham sp = qlSanPham.timTheoMa(rs.getString("ma_sp"));
                            if (sp != null) {
                                pn.napChiTiet(new ChiTietPhieuNhap(sp, rs.getInt("so_luong_nhap"),
                                        rs.getDouble("gia_nhap")));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi doc phieu nhap: " + e.getMessage(), e);
        }
        return ds;
    }

    public void them(PhieuNhap pn) {
        try (Connection c = DBConnection.layKetNoi()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO phieu_nhap (ma_phieu, ma_ncc, ma_quan_tri, ngay_nhap) VALUES (?,?,?,?)")) {
                    ps.setString(1, pn.getMaPhieu());
                    ps.setString(2, pn.getNhaCungCap().getMaNCC());
                    ps.setString(3, pn.getMaQuanTri());
                    ps.setDate(4, Date.valueOf(pn.getNgayNhap()));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO chi_tiet_phieu_nhap (ma_phieu, ma_sp, so_luong_nhap, gia_nhap) VALUES (?,?,?,?)")) {
                    for (ChiTietPhieuNhap ct : pn.getDanhSachChiTiet()) {
                        ps.setString(1, pn.getMaPhieu());
                        ps.setString(2, ct.getSanPham().getMaSP());
                        ps.setInt(3, ct.getSoLuongNhap());
                        ps.setDouble(4, ct.getGiaNhap());
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
            throw new DataAccessException("Loi luu phieu nhap (da hoan tac): " + e.getMessage(), e);
        }
    }
}
