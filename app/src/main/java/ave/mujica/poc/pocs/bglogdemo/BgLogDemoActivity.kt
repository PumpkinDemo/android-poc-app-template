package ave.mujica.poc.pocs.bglogdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import ave.mujica.poc.BasePocActivity
import ave.mujica.poc.bglog.BackgroundLogController
import ave.mujica.poc.bglog.BackgroundLogTaskRegistry
import ave.mujica.poc.utils.ViewUtils

class BgLogDemoActivity : BasePocActivity() {

    companion object {
        private const val TAG = "BgLogDemoActivity"
        private const val REQUEST_POST_NOTIFICATIONS = 1001
        private const val NO_FGS_INTERVAL_MS = 1000L
    }

    private lateinit var intervalInput: EditText
    private var pendingStartBackgroundLogger = false
    private var pendingStartNoFgsLogThread = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intervalInput = ViewUtils.createInputField(this, "bg interval ms", "1000")
        addInputField(intervalInput)

        addActionButton("start foreground-service log") { requestBackgroundLog() }
        addActionButton("stop foreground-service log") { stopBackgroundLogger() }
        addActionButton("start no-FGS log thread") { requestNonForegroundLogThread() }
        addActionButton("stop no-FGS log thread") { stopNonForegroundLogThread() }

        BackgroundLogTaskRegistry.register(DemoBgLogTask())
    }

    override fun tag(): String = TAG

    private fun requestBackgroundLog() {
        if (!hasPostNotificationsPermission()) {
            pendingStartBackgroundLogger = true
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_POST_NOTIFICATIONS)
            log(TAG, "Requesting POST_NOTIFICATIONS for the background logger foreground service.")
            return
        }
        startBackgroundLogger()
    }

    private fun startBackgroundLogger() {
        val intervalMs = readIntervalMs()
        log(TAG, "Foreground-service background logger starts, interval=${intervalMs}ms.")
        BackgroundLogController.start(this, DemoBgLogTask.TASK_ID, intervalMs)
    }

    private fun stopBackgroundLogger() {
        pendingStartBackgroundLogger = false
        BackgroundLogController.stop(this)
        log(TAG, "Foreground-service background logger stopped.")
    }

    private fun requestNonForegroundLogThread() {
        if (!hasPostNotificationsPermission()) {
            pendingStartNoFgsLogThread = true
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_POST_NOTIFICATIONS)
            log(TAG, "Requesting POST_NOTIFICATIONS for the no-FGS log thread notification.")
            return
        }
        startNonForegroundLogThread()
    }

    private fun startNonForegroundLogThread() {
        val intervalMs = NO_FGS_INTERVAL_MS
        if (NonForegroundBgLogThread.start(this, intervalMs)) {
            log(
                TAG,
                "No-FGS log thread starts. It updates a current-time notification every ${intervalMs}ms. Watch logcat tag DemoBgLogNoFgs."
            )
            return
        }

        log(TAG, "No-FGS log thread is already running.")
    }

    private fun stopNonForegroundLogThread() {
        NonForegroundBgLogThread.stop()
        log(TAG, "No-FGS log thread stop requested.")
    }

    private fun readIntervalMs(): Long {
        val raw = intervalInput.text.toString().trim()
        if (raw.isEmpty()) {
            return 1000L
        }

        return try {
            kotlin.math.max(1000L, raw.toLong())
        } catch (_: NumberFormatException) {
            log(TAG, "Invalid interval, falling back to 1000ms: $raw")
            1000L
        }
    }

    private fun hasPostNotificationsPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_POST_NOTIFICATIONS) {
            return
        }

        log(TAG, "POST_NOTIFICATIONS permission result: ${if (grantResults.isNotEmpty()) grantResults[0] else "none"}")

        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (pendingStartBackgroundLogger && granted) {
            pendingStartBackgroundLogger = false
            startBackgroundLogger()
            return
        }

        if (pendingStartNoFgsLogThread && granted) {
            pendingStartNoFgsLogThread = false
            startNonForegroundLogThread()
            return
        }

        pendingStartBackgroundLogger = false
        pendingStartNoFgsLogThread = false
        log(
            TAG,
            "POST_NOTIFICATIONS denied. Notification-based background log tests were not started."
        )
    }
}
