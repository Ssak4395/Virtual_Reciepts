package app_.smartreceipt.digitaldocs.model;

import java.util.Locale;

public class Currency {
    private static Locale locale = Locale.getDefault();
    private static java.util.Currency cur1 = java.util.Currency.getInstance(locale);
    private static String symbol = cur1.getSymbol(locale);

    public static String getSymbol() {
        return symbol;
    }
}
