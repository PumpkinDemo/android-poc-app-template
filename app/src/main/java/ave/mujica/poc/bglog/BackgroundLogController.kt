package ave.mujica.poc.bglog

import android.content.Context
import android.content.Intent
import android.os.Build

object BackgroundLogController {

    fun start(context: Context, taskId: String, intervalMs: Long) {
        val intent = Intent(context, BackgroundLogService::class.java).apply {
            action = BackgroundLogService.ACTION_START
            putExtra(BackgroundLogService.EXTRA_TASK_ID, taskId)
            putExtra(BackgroundLogService.EXTRA_INTERVAL_MS, intervalMs)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stop(context: Context) {
        val intent = Intent(context, BackgroundLogService::class.java).apply {
            action = BackgroundLogService.ACTION_STOP
        }
        context.startService(intent)
    }
}