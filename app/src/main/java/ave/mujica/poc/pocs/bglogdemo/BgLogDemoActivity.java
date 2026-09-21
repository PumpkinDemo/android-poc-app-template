package ave.mujica.poc.pocs.bglogdemo;

import ave.mujica.poc.*;
import ave.mujica.poc.bglog.BackgroundLogController;
import ave.mujica.poc.bglog.BackgroundLogTaskRegistry;
import ave.mujica.poc.utils.StringUtils;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.EditText;
import android.Manifest;

public class BgLogDemoActivity extends BasePocActivity {
    private static final String TAG = "BgLogDemoActivity";
    private static final int REQUEST_POST_NOTIFICATIONS = 1001;
    private static final long NO_FGS_INTERVAL_MS = 1000L;

    private EditText intervalInput;
    private boolean pendingStartBackgroundLogger = false;
    private boolean pendingStartNoFgsLogThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intervalInput = ViewHelper.createInputField(this, "bg interval ms", "1000");
        addInputField(intervalInput);

        addActionButton("start foreground-service log", v -> requestBackgroundLog());
        addActionButton("stop foreground-service log", v -> stopBackgroundLogger());
        addActionButton("start no-FGS log thread", v -> requestNonForegroundLogThread());
        addActionButton("stop no-FGS log thread", v -> stopNonForegroundLogThread());

        BackgroundLogTaskRegistry.register(new DemoBgLogTask());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    private void requestBackgroundLog() {
        if (!hasPostNotificationsPermission()) {
            pendingStartBackgroundLogger = true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            log(TAG, "Requesting POST_NOTIFICATIONS for the background logger foreground service.");
            return;
        }
        startBackgroundLogger();
    }
 

    private void startBackgroundLogger() {
        long intervalMs = readIntervalMs();
        log(TAG, "Foreground-service background logger starts, interval=" + intervalMs + "ms.");
        BackgroundLogController.start(this, DemoBgLogTask.TASK_ID, intervalMs);
    }

    
    private void stopBackgroundLogger() {
        pendingStartBackgroundLogger = false;
        BackgroundLogController.stop(this);
        log(TAG, "Foreground-service background logger stopped.");
    }

    private void requestNonForegroundLogThread() {
        if (!hasPostNotificationsPermission()) {
            pendingStartNoFgsLogThread = true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            log(TAG, "Requesting POST_NOTIFICATIONS for the no-FGS log thread notification.");
            return;
        }
        startNonForegroundLogThread();
    }

    private void startNonForegroundLogThread() {
        long intervalMs = NO_FGS_INTERVAL_MS;
        if (NonForegroundBgLogThread.start(this, intervalMs)) {
            log(TAG, "No-FGS log thread starts. It updates a current-time notification every "
                    + intervalMs + "ms. Watch logcat tag DemoBgLogNoFgs.");
            return;
        }
        log(TAG, "No-FGS log thread is already running.");
    }

    private void stopNonForegroundLogThread() {
        NonForegroundBgLogThread.stop();
        log(TAG, "No-FGS log thread stop requested.");
    }

    private long readIntervalMs() {
        String raw = StringUtils.trimInput(intervalInput.getText().toString());
        if (raw.isEmpty()) {
            return 1000L;
        }
        try {
            return Math.max(1000L, Long.parseLong(raw));
        } catch (NumberFormatException e) {
            log(TAG, "Invalid interval, falling back to 1000ms: " + raw);
            return 1000L;
        }
    }

    private boolean hasPostNotificationsPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_POST_NOTIFICATIONS) {
            return;
        }

        log(TAG, "POST_NOTIFICATIONS permission result: " + (grantResults.length > 0 ? grantResults[0] : "none"));
        
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (pendingStartBackgroundLogger && granted) {
            pendingStartBackgroundLogger = false;
            startBackgroundLogger();
            return;
        }

        if (pendingStartNoFgsLogThread && granted) {
            pendingStartNoFgsLogThread = false;
            startNonForegroundLogThread();
            return;
        }

        pendingStartBackgroundLogger = false;
        pendingStartNoFgsLogThread = false;
        log(TAG, "POST_NOTIFICATIONS denied. Notification-based background log tests were not started.");
    }
}
