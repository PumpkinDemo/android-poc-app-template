package ave.mujica.poc.pocs.bglogdemo;

import java.text.SimpleDateFormat;
import java.util.*;
import android.content.Context;
import ave.mujica.poc.bglog.*;

public final class DemoBgLogTask implements BackgroundLogTask {
    public static final String TASK_ID = "bglogdemo.DemoBgLogTask";
    private static final String LOG_TAG = "DemoBgLog";

    @Override
    public String getId() {
        return TASK_ID;
    }

    @Override
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override
    public String getDisplayName() {
        return "Demo Background Log Task";
    }

    @Override
    public long getDefaultIntervalMs() {
        return 1000L;
    }

    @Override
    public BackgroundLogResult runOnce(Context context) throws Exception {
        
        String timestamp = new SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(new Date());

        List<String> logLines = new ArrayList<>();
        logLines.add(timestamp);
        String notificationText = timestamp;
        String notificationBigText = timestamp;;

        return new BackgroundLogResult(
                getDisplayName(),
                notificationText,
                notificationBigText,
                logLines
        );
    }
}
