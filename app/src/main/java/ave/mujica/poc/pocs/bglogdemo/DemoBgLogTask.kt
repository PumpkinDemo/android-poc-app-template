package ave.mujica.poc.pocs.bglogdemo

import android.content.Context

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import ave.mujica.poc.bglog.BackgroundLogResult
import ave.mujica.poc.bglog.BackgroundLogTask


class DemoBgLogTask : BackgroundLogTask {

    companion object {
        const val TASK_ID = "bglogdemo.DemoBgLogTask"
        private const val LOG_TAG = "DemoBgLog"
    }

    override fun getId(): String {
        return TASK_ID
    }

    override fun getLogTag(): String {
        return LOG_TAG
    }

    override fun getDisplayName(): String {
        return "Demo Background Log Task"
    }

    override fun getDefaultIntervalMs(): Long {
        return 1000L
    }

    override fun runOnce(context: Context): BackgroundLogResult {
        val timestamp = SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(Date())

        val logLines = arrayListOf<String>()
        logLines.add(timestamp)

        return BackgroundLogResult(
            getDisplayName(),
            timestamp,
            timestamp,
            logLines
        )
    }
}