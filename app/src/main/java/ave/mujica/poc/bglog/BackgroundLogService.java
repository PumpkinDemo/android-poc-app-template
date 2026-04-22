package ave.mujica.poc.bglog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import ave.mujica.poc.MainActivity;

public class BackgroundLogService extends Service {
    public static final String ACTION_START = "ave.mujica.poc.backgroundlog.START";
    public static final String ACTION_STOP = "ave.mujica.poc.backgroundlog.STOP";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_INTERVAL_MS = "interval_ms";

    private static final String CHANNEL_ID = "background_log_heads_up";
    private static final int NOTIFICATION_ID = 0x42474C;
    private static final long MIN_INTERVAL_MS = 1000L;

    private HandlerThread workerThread;
    private Handler workerHandler;
    private BackgroundLogTask currentTask;
    private long currentIntervalMs;
    private final Runnable taskRunnable = new Runnable() {
        @Override
        public void run() {
            executeCurrentTask();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        workerThread = new HandlerThread("BackgroundLogService");
        workerThread.start();
        workerHandler = new Handler(workerThread.getLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopLogging();
            return START_NOT_STICKY;
        }

        if (!ACTION_START.equals(action)) {
            return START_NOT_STICKY;
        }

        String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        BackgroundLogTask task = BackgroundLogTaskRegistry.get(taskId);
        if (task == null) {
            Log.w(CHANNEL_ID, "Unknown background log task: " + taskId);
            stopLogging();
            return START_NOT_STICKY;
        }

        currentTask = task;
        currentIntervalMs = Math.max(MIN_INTERVAL_MS, intent.getLongExtra(EXTRA_INTERVAL_MS, task.getDefaultIntervalMs()));

        Notification notification = buildNotification(
                task.getDisplayName(),
                "Starting background logger",
                "Task=" + task.getId() + ", interval=" + currentIntervalMs + "ms"
        );
        startForegroundCompat(notification);

        workerHandler.removeCallbacks(taskRunnable);
        workerHandler.post(taskRunnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }
        if (workerThread != null) {
            workerThread.quitSafely();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void executeCurrentTask() {
        BackgroundLogTask task = currentTask;
        if (task == null) {
            stopLogging();
            return;
        }

        try {
            BackgroundLogResult result = task.runOnce(this);
            for (String line : result.logLines) {
                Log.d(task.getLogTag(), line);
            }

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(
                        NOTIFICATION_ID,
                        buildNotification(result.notificationTitle, result.notificationText, result.notificationBigText)
                );
            }
        } catch (Throwable t) {
            String message = "Background log task failed: " + t;
            Log.e(task.getLogTag(), message, t);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(
                        NOTIFICATION_ID,
                        buildNotification(task.getDisplayName(), "Task failed", message)
                );
            }
        }

        if (workerHandler != null && currentTask != null) {
            workerHandler.postDelayed(taskRunnable, currentIntervalMs);
        }
    }

    private void stopLogging() {
        currentTask = null;
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void startForegroundCompat(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private Notification buildNotification(String title, String text, String bigText) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .bigText(bigText);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(style)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager == null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
            "Background Log Heads Up",
            NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Foreground notifications for reusable background log tasks");
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
    }
}
