package ave.mujica.poc.pocs.bglogdemo

import android.content.Context
import android.os.SystemClock
import android.util.Log
import ave.mujica.poc.utils.NotificationUtils
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

object NonForegroundBgLogThread {

    private const val LOG_TAG = "DemoBgLogNoFgs"
    private const val THREAD_NAME = "BgLogDemoNoFgsThread"
    private const val MIN_INTERVAL_MS = 1000L

    private val lock = Any()
    private val running = AtomicBoolean(false)
    private var workerThread: Thread? = null

    fun start(context: Context, intervalMs: Long): Boolean {
        val appContext = context.applicationContext
        val actualIntervalMs = max(MIN_INTERVAL_MS, intervalMs)

        synchronized(lock) {
            if (workerThread?.isAlive == true) {
                return false
            }

            running.set(true)
            workerThread = Thread {
                runLoop(appContext, actualIntervalMs)
            }.apply {
                name = THREAD_NAME
                start()
            }
            return true
        }
    }

    fun stop() {
        synchronized(lock) {
            running.set(false)
            workerThread?.interrupt()
        }
    }

    fun isRunning(): Boolean {
        return workerThread?.isAlive == true
    }

    private fun runLoop(context: Context, intervalMs: Long) {
        val task = DemoBgLogTask()
        var sequence = 0L
        var previousElapsedMs = 0L

        Log.d(LOG_TAG, "no-FGS log thread started, interval=${intervalMs}ms")
        while (running.get()) {
            val elapsedMs = SystemClock.elapsedRealtime()
            val deltaMs = if (previousElapsedMs == 0L) 0L else elapsedMs - previousElapsedMs
            previousElapsedMs = elapsedMs
            sequence++

            try {
                val result = task.runOnce(context)
                Log.d(LOG_TAG, "no-FGS seq=$sequence elapsedMs=$elapsedMs deltaMs=$deltaMs")
                for (line in result.logLines) {
                    Log.d(LOG_TAG, line)
                }
                NotificationUtils.sendNotification(
                    context,
                    "No-FGS log thread",
                    "Current time: ${result.notificationText}"
                )
            } catch (t: Throwable) {
                Log.e(LOG_TAG, "no-FGS log task failed", t)
            }

            try {
                Thread.sleep(intervalMs)
            } catch (_: InterruptedException) {
                if (!running.get()) {
                    break
                }
            }
        }

        Log.d(LOG_TAG, "no-FGS log thread stopped")
        synchronized(lock) {
            if (Thread.currentThread() == workerThread) {
                workerThread = null
            }
            running.set(false)
        }
    }
}
