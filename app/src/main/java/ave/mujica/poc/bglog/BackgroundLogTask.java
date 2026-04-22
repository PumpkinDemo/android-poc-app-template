package ave.mujica.poc.bglog;

import android.content.Context;

public interface BackgroundLogTask {
    String getId();

    String getLogTag();

    String getDisplayName();

    long getDefaultIntervalMs();

    BackgroundLogResult runOnce(Context context) throws Exception;
}
