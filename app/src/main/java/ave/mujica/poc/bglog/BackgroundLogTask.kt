package ave.mujica.poc.bglog

import android.content.Context

interface BackgroundLogTask {
    fun getId(): String

    fun getLogTag(): String

    fun getDisplayName(): String

    fun getDefaultIntervalMs(): Long

    fun runOnce(context: Context): BackgroundLogResult
}