package org.example.client;

import org.example.enums.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utils {

    public static String convertBigDecimalToString(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return "0,00";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,###.00", symbols);

        return formatter.format(amount);
    }

    public static String convertBigDecimalToString(BigDecimal amount, String type) {
        String converted = convertBigDecimalToString(amount);

        if (type.equals("EXPENSE")) {
            return "-" + converted;
        }
        return converted;
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
