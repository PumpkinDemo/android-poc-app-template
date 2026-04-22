package ave.mujica.poc.pocs.bglogdemo;

import ave.mujica.poc.*;
import ave.mujica.poc.bglog.BackgroundLogController;
import ave.mujica.poc.bglog.BackgroundLogTaskRegistry;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.EditText;
import android.Manifest;

public class BgLogDemoActivity extends BasePocActivity {
    private static final String TAG = "BgLogDemoActivity";
    private static final int REQUEST_POST_NOTIFICATIONS = 1001;

    private EditText intervalInput;
    private boolean pendingStartBackgroudLogger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intervalInput = ViewHelper.createInputField(this, "bg interval ms", "1000", getLogTypeface());
        rootLayout.addView(intervalInput, 1);

        // addActionButton("test", v -> poc());
        addActionButton("start background log", v -> requestBackgroundLog());
        addActionButton("stop background log", v -> stopBackgroundLogger());

        BackgroundLogTaskRegistry.register(new DemoBgLogTask());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    private void requestBackgroundLog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU 
        && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            pendingStartBackgroudLogger = true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            log(TAG, "Requesting POST_NOTIFICATIONS for the background logger foreground service.");
            return;
        }
        startBackgroundLogger();
    }
 

    private void startBackgroundLogger() {
        log(TAG, "Background logger starts.");

        long intervalMs = 1000L;
        do {
            String raw = intervalInput.getText().toString().trim();
            if (raw.isEmpty()) {
                break;
            }
            try {
                intervalMs = Math.max(1000L, Long.parseLong(raw));
            } catch (NumberFormatException e) {
                log(TAG, "Invalid interval, falling back to 1000ms: " + raw);
                intervalMs = 1000L;
            }
        } while (false);

        BackgroundLogController.start(this, DemoBgLogTask.TASK_ID, intervalMs);
    }

    
    private void stopBackgroundLogger() {
        pendingStartBackgroudLogger = false;
        BackgroundLogController.stop(this);
        log(TAG, "Background logger stopped.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_POST_NOTIFICATIONS) {
            return;
        }

        log(TAG, "POST_NOTIFICATIONS permission result: " + (grantResults.length > 0 ? grantResults[0] : "none"));
        
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (pendingStartBackgroudLogger && granted) {
            pendingStartBackgroudLogger = false;
            startBackgroundLogger();
            return;
        }

        pendingStartBackgroudLogger = false;
        log(TAG, "POST_NOTIFICATIONS denied. Foreground notification visibility may be blocked, so the background logger was not started.");
    }
}
