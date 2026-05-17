package org.example.client;

import org.example.enums.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utils {
    public static String convertBigDecimalToString(BigDecimal amount, String type) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,###.00", symbols);
        String formatted = formatter.format(amount.abs());

        if (type.equals("EXPENSE")) {
            return "-" + formatted;
        }
        return formatted;
    }

    public static String convertBigDecimalToString(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,###.00", symbols);

        return formatter.format(amount.abs());
    }

    public static String convertBigDecimalToString(BigDecimal amount, String type, Currency currency) {
        return convertBigDecimalToString(amount, type) + " " + currency.getCode();
    }

    public static String convertBigDecimalToString(BigDecimal amount, Currency currency) {
        return convertBigDecimalToString(amount) + " " + currency.getCode();
    }

    public static String convertIntToString(int value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("# ###", symbols);
        return formatter.format(value);
    }
}
