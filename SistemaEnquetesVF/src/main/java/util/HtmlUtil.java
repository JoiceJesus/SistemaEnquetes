package util;

public final class HtmlUtil {

    private HtmlUtil() {
    }

    public static String escape(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
