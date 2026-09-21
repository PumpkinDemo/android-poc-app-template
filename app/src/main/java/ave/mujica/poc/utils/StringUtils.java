package ave.mujica.poc.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String trimInput(String value) {
        // Match Kotlin's trim(), including Unicode space characters such as NBSP.
        int start = 0;
        int end = value.length();
        while (start < end && isWhitespace(value.charAt(start))) {
            start++;
        }
        while (end > start && isWhitespace(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(start, end);
    }

    private static boolean isWhitespace(char value) {
        return Character.isWhitespace(value) || Character.isSpaceChar(value);
    }
}
