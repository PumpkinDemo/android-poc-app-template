package ave.mujica.poc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(new Date());
    }
}
