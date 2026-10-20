import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Bộ đọc/ghi JSON tối giản (không cần thư viện ngoài) cho giao diện web. */
public final class Json {
    private final String s;
    private int i = 0;

    private Json(String s) { this.s = s; }

    /** Đọc JSON thành Map, List, String, Double, Boolean hoặc null. */
    public static Object parse(String text) {
        Json p = new Json(text);
        p.boQuaKhoangTrang();
        Object v = p.giaTri();
        p.boQuaKhoangTrang();
        if (p.i != text.length()) {
            throw new IllegalArgumentException("JSON khong hop le tai vi tri " + p.i);
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }
        Object o = parse(text);
        if (!(o instanceof Map)) {
            throw new IllegalArgumentException("Du lieu gui len phai la mot doi tuong JSON");
        }
        return (Map<String, Object>) o;
    }

    // ---------------- đọc ----------------
    private void boQuaKhoangTrang() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private Object giaTri() {
        if (i >= s.length()) throw new IllegalArgumentException("JSON bi cat ngang");
        char c = s.charAt(i);
        if (c == '{') return doiTuong();
        if (c == '[') return mang();
        if (c == '"') return chuoi();
        if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        if (s.startsWith("null", i)) { i += 4; return null; }
        return so();
    }

    private Map<String, Object> doiTuong() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++; // {
        boQuaKhoangTrang();
        if (s.charAt(i) == '}') { i++; return m; }
        while (true) {
            boQuaKhoangTrang();
            String k = chuoi();
            boQuaKhoangTrang();
            if (s.charAt(i++) != ':') throw new IllegalArgumentException("Thieu dau : trong JSON");
            boQuaKhoangTrang();
            m.put(k, giaTri());
            boQuaKhoangTrang();
            char c = s.charAt(i++);
            if (c == '}') return m;
            if (c != ',') throw new IllegalArgumentException("Thieu dau , trong JSON");
        }
    }

    private List<Object> mang() {
        List<Object> l = new ArrayList<>();
        i++; // [
        boQuaKhoangTrang();
        if (s.charAt(i) == ']') { i++; return l; }
        while (true) {
            boQuaKhoangTrang();
            l.add(giaTri());
            boQuaKhoangTrang();
            char c = s.charAt(i++);
            if (c == ']') return l;
            if (c != ',') throw new IllegalArgumentException("Thieu dau , trong mang JSON");
        }
    }

    private String chuoi() {
        if (s.charAt(i) != '"') throw new IllegalArgumentException("Can mot chuoi JSON tai vi tri " + i);
        i++;
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = s.charAt(i++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            char e = s.charAt(i++);
            switch (e) {
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u': sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                default: sb.append(e);
            }
        }
    }

    private Double so() {
        int j = i;
        while (j < s.length() && "+-0123456789.eE".indexOf(s.charAt(j)) >= 0) j++;
        if (j == i) throw new IllegalArgumentException("JSON khong hop le tai vi tri " + i);
        Double d = Double.valueOf(s.substring(i, j));
        i = j;
        return d;
    }

    // ---------------- ghi ----------------
    public static String toJson(Object o) {
        StringBuilder sb = new StringBuilder();
        ghi(sb, o);
        return sb.toString();
    }

    private static void ghi(StringBuilder sb, Object o) {
        if (o == null) {
            sb.append("null");
        } else if (o instanceof String) {
            ghiChuoi(sb, (String) o);
        } else if (o instanceof Boolean) {
            sb.append(o);
        } else if (o instanceof Number) {
            double d = ((Number) o).doubleValue();
            if (d == Math.rint(d) && Math.abs(d) < 1e15) sb.append((long) d); else sb.append(d);
        } else if (o instanceof Map) {
            sb.append('{');
            boolean dau = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!dau) sb.append(',');
                dau = false;
                ghiChuoi(sb, String.valueOf(e.getKey()));
                sb.append(':');
                ghi(sb, e.getValue());
            }
            sb.append('}');
        } else if (o instanceof Collection) {
            sb.append('[');
            boolean dau = true;
            for (Object x : (Collection<?>) o) {
                if (!dau) sb.append(',');
                dau = false;
                ghi(sb, x);
            }
            sb.append(']');
        } else {
            ghiChuoi(sb, String.valueOf(o));
        }
    }

    private static void ghiChuoi(StringBuilder sb, String t) {
        sb.append('"');
        for (int k = 0; k < t.length(); k++) {
            char c = t.charAt(k);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
            }
        }
        sb.append('"');
    }
}
