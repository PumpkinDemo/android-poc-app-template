package ave.mujica.poc.bglog;

import java.util.ArrayList;
import java.util.List;

public final class BackgroundLogResult {
    public final String notificationTitle;
    public final String notificationText;
    public final String notificationBigText;
    public final List<String> logLines;

    public BackgroundLogResult(
            String notificationTitle,
            String notificationText,
            String notificationBigText,
            List<String> logLines
    ) {
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.notificationBigText = notificationBigText;
        this.logLines = logLines != null ? new ArrayList<>(logLines) : new ArrayList<>();
    }
}

