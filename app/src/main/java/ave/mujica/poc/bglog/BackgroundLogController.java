package ave.mujica.poc.bglog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public final class BackgroundLogController {
    private BackgroundLogController() {
    }

    public static void start(Context context, String taskId, long intervalMs) {
        Intent intent = new Intent(context, BackgroundLogService.class);
        intent.setAction(BackgroundLogService.ACTION_START);
        intent.putExtra(BackgroundLogService.EXTRA_TASK_ID, taskId);
        intent.putExtra(BackgroundLogService.EXTRA_INTERVAL_MS, intervalMs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, BackgroundLogService.class);
        intent.setAction(BackgroundLogService.ACTION_STOP);
        context.startService(intent);
    }
}

