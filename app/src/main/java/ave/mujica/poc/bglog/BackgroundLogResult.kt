package ave.mujica.poc.bglog

class BackgroundLogResult(
    val notificationTitle: String,
    val notificationText: String,
    val notificationBigText: String,
    logLines: List<String>?
) {
    val logLines: List<String> = ArrayList(logLines ?: emptyList())
}