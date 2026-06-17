package ave.mujica.poc.bglog

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ave.mujica.poc.MainActivity
import kotlin.math.max

class BackgroundLogService : Service() {

    companion object {
        const val ACTION_START = "ave.mujica.poc.backgroundlog.START"
        const val ACTION_STOP = "ave.mujica.poc.backgroundlog.STOP"
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_INTERVAL_MS = "interval_ms"

        private const val CHANNEL_ID = "background_log_heads_up"
        private const val NOTIFICATION_ID = 0x42474C
        private const val MIN_INTERVAL_MS = 1000L
    }

    private var workerThread: HandlerThread? = null
    private var workerHandler: Handler? = null
    private var currentTask: BackgroundLogTask? = null
    private var currentIntervalMs = 0L
    private val taskRunnable = Runnable { executeCurrentTask() }

    override fun onCreate() {
        super.onCreate()
        workerThread = HandlerThread("BackgroundLogService").also { it.start() }
        workerHandler = Handler(workerThread!!.looper)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }

        val action = intent.action
        if (ACTION_STOP == action) {
            stopLogging()
            return START_NOT_STICKY
        }

        if (ACTION_START != action) {
            return START_NOT_STICKY
        }

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val task = BackgroundLogTaskRegistry.get(taskId)
        if (task == null) {
            Log.w(CHANNEL_ID, "Unknown background log task: $taskId")
            stopLogging()
            return START_NOT_STICKY
        }

        currentTask = task
        currentIntervalMs = max(MIN_INTERVAL_MS, intent.getLongExtra(EXTRA_INTERVAL_MS, task.getDefaultIntervalMs()))

        val notification = buildNotification(
            task.getDisplayName(),
            "Starting background logger",
            "Task=${task.getId()}, interval=${currentIntervalMs}ms"
        )
        startForegroundCompat(notification)

        workerHandler?.removeCallbacks(taskRunnable)
        workerHandler?.post(taskRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        workerHandler?.removeCallbacksAndMessages(null)
        workerThread?.quitSafely()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun executeCurrentTask() {
        val task = currentTask
        if (task == null) {
            stopLogging()
            return
        }

        try {
            val result = task.runOnce(this)
            for (line in result.logLines) {
                Log.d(task.getLogTag(), line)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.notify(
                NOTIFICATION_ID,
                buildNotification(result.notificationTitle, result.notificationText, result.notificationBigText)
            )
        } catch (t: Throwable) {
            val message = "Background log task failed: $t"
            Log.e(task.getLogTag(), message, t)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.notify(
                NOTIFICATION_ID,
                buildNotification(task.getDisplayName(), "Task failed", message)
            )
        }

        if (workerHandler != null && currentTask != null) {
            workerHandler?.postDelayed(taskRunnable, currentIntervalMs)
        }
    }

    private fun stopLogging() {
        currentTask = null
        workerHandler?.removeCallbacksAndMessages(null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(title: String, text: String, bigText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val style = NotificationCompat.BigTextStyle().bigText(bigText)

        return NotificationCompat.Builder(this, CHANNEL_ID)
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
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val notificationManager = getSystemService(NotificationManager::class.java) ?: return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Background Log Heads Up",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Foreground notifications for reusable background log tasks"
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }
}