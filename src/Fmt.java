import java.util.Locale;

/** Tiện ích định dạng số tiền (dùng Locale.US để ra dạng 22,990,000 trên mọi máy). */
public final class Fmt {
    private Fmt() { }

    public static String tien(double v) {
        return String.format(Locale.US, "%,.0f", v);
    }
}
