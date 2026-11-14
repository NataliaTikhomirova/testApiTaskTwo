package api.utils;

import java.math.BigDecimal;
import java.util.List;

public class Utils {

    public static void checkField(List<String> discrepancies, String fieldName, Object expected, Object actual) {
        if (!isEqual(expected, actual)) {
            discrepancies.add(String.format("Поле '%s' -> Ожидалось: '%s', Фактически: '%s'", fieldName, expected, actual));
        }
    }

    private static boolean isEqual(Object expected, Object actual) {
        if (expected == null || actual == null) return expected == actual;
        try {
            return new BigDecimal(expected.toString().trim()).compareTo(new BigDecimal(actual.toString().trim())) == 0;
        } catch (NumberFormatException e) {
            return expected.toString().trim().equals(actual.toString().trim());
        }
    }
}
