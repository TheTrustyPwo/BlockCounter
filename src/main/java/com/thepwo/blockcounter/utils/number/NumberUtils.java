package com.thepwo.blockcounter.utils.number;

import com.thepwo.blockcounter.utils.number.enums.NumberFormatType;

import java.text.NumberFormat;

public class NumberUtils {
    public static boolean isInteger(final String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(final String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(final String string) {
        try {
            Long.parseLong(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String format(final double number, final NumberFormatType numberFormatType) {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormatType == NumberFormatType.MULTIPLIER) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.COMMAS) {
            numberFormat.setGroupingUsed(true);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.LETTERS) {
            if (number < 1000) {
                return String.valueOf(number);
            }
            final String[] units = new String[]{"k", "m", "b", "t", "q", "qt"};
            final int exp = (int) (Math.log(number) / 3);
            return String.format("%8s%n", String.format("%.2f %s", (number / Math.pow(1000, exp)), units[exp - 1]));
        }
        return String.valueOf(number);
    }
}
