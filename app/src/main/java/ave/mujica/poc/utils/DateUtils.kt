package ave.mujica.poc.utils

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

fun getCurrentTime(): String = SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(Date())