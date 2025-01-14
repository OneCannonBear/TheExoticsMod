package club.thom.tem.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberHelper {
    //Copied from TFM
    private static final String[] exponent_symbols = new String[] {"", "K", "M", "B", "T", "Q"};
    private static final Pattern REGEX = Pattern.compile("(-?\\d+(?:\\.\\d+)?)([KMBTQ]?)");

    public static String formatWithSuffix(double number) {
        int i = 0;
        boolean isNegative = number < 0;
        number = Math.abs(number);
        while (number >= 1000) {
            i++;
            if (i > 5) {
                i--;
                break;
            }
            number /= 1000;
        }
        if (isNegative) {
            number = -number;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        if (isNegative) {
            df.setRoundingMode(RoundingMode.CEILING);
        } else {
            df.setRoundingMode(RoundingMode.FLOOR);
        }
        return df.format(number) + exponent_symbols[i];
    }

    public static double parseDouble(String s) {
        s = s.toUpperCase();
        final Matcher m = REGEX.matcher(s);
        if (!m.matches()) throw new NumberFormatException("Invalid number format " + s);
        int i = 0;
        long scale = 1;
        while (!m.group(2).equals(exponent_symbols[i])) {
            i++;
            scale *= 1000;
        }
        return Double.parseDouble(m.group(1)) * scale;
    }
}
