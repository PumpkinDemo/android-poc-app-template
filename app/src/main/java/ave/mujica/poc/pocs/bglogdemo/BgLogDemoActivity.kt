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
    }

    private lateinit var intervalInput: EditText
    private var pendingStartBackgroudLogger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intervalInput = ViewUtils.createInputField(this, "bg interval ms", "1000", getLogTypeface())
        addInputField(intervalInput)

        addActionButton("start background log") { requestBackgroundLog() }
        addActionButton("stop background log") { stopBackgroundLogger() }

        BackgroundLogTaskRegistry.register(DemoBgLogTask())
    }

    override fun getTag(): String {
        return TAG
    }

    private fun requestBackgroundLog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingStartBackgroudLogger = true
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_POST_NOTIFICATIONS)
            log(TAG, "Requesting POST_NOTIFICATIONS for the background logger foreground service.")
            return
        }
        startBackgroundLogger()
    }

    private fun startBackgroundLogger() {
        log(TAG, "Background logger starts.")

        var intervalMs = 1000L
        do {
            val raw = intervalInput.text.toString().trim()
            if (raw.isEmpty()) {
                break
            }
            try {
                intervalMs = kotlin.math.max(1000L, raw.toLong())
            } catch (_: NumberFormatException) {
                log(TAG, "Invalid interval, falling back to 1000ms: $raw")
                intervalMs = 1000L
            }
        } while (false)

        BackgroundLogController.start(this, DemoBgLogTask.TASK_ID, intervalMs)
    }

    private fun stopBackgroundLogger() {
        pendingStartBackgroudLogger = false
        BackgroundLogController.stop(this)
        log(TAG, "Background logger stopped.")
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
        if (pendingStartBackgroudLogger && granted) {
            pendingStartBackgroudLogger = false
            startBackgroundLogger()
            return
        }

        pendingStartBackgroudLogger = false
        log(
            TAG,
            "POST_NOTIFICATIONS denied. Foreground notification visibility may be blocked, so the background logger was not started."
        )
    }
}