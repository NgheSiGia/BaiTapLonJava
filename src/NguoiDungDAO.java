import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/** DAO cho bảng nguoi_dung (khách hàng và quản trị viên dùng chung một bảng, phân biệt bằng vai_tro). */
public class NguoiDungDAO {

    public ArrayList<NguoiDung> layTatCa() {
        ArrayList<NguoiDung> ds = new ArrayList<>();
        try (Connection c = DBConnection.layKetNoi();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM nguoi_dung ORDER BY ma_nguoi_dung")) {
            while (rs.next()) {
                String ma = rs.getString("ma_nguoi_dung");
                String ten = rs.getString("ho_ten");
                String email = rs.getString("email");
                String mk = rs.getString("mat_khau");
                String sdt = rs.getString("so_dien_thoai");
                if ("QUAN_TRI".equals(rs.getString("vai_tro"))) {
                    ds.add(new QuanTriVien(ma, ten, email, mk, sdt, rs.getString("chuc_vu")));
                } else {
                    ds.add(new KhachHang(ma, ten, email, mk, sdt, rs.getString("dia_chi")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Loi doc nguoi dung: " + e.getMessage(), e);
        }
        return ds;
    }

    public void them(NguoiDung nd) {
        String sql = "INSERT INTO nguoi_dung (ma_nguoi_dung, ho_ten, email, mat_khau, so_dien_thoai,"
                + " vai_tro, dia_chi, chuc_vu) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.layKetNoi(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nd.getMaNguoiDung());
            ps.setString(2, nd.getHoTen());
            ps.setString(3, nd.getEmail());
            ps.setString(4, nd.getMatKhau());
            ps.setString(5, nd.getSoDienThoai());
            if (nd instanceof QuanTriVien) {
                ps.setString(6, "QUAN_TRI");
                ps.setString(7, "");
                ps.setString(8, ((QuanTriVien) nd).getChucVu());
            } else {
                ps.setString(6, "KHACH_HANG");
                ps.setString(7, ((KhachHang) nd).getDiaChi());
                ps.setString(8, "");
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Loi them nguoi dung: " + e.getMessage(), e);
        }
    }
}
