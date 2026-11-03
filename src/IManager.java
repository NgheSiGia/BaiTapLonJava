import java.util.ArrayList;

/** Interface quy định các thao tác quản lý chung cho mọi đối tượng kiểu T. */
public interface IManager<T> {
    void them(T doiTuong);
    boolean xoa(String ma);
    T timTheoMa(String ma);
    ArrayList<T> layTatCa();
    void hienThiDanhSach();
}
