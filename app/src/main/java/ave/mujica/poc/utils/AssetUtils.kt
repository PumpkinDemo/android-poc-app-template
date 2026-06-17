package ave.mujica.poc.utils

import android.content.Context

fun readAssetText(context: Context, fileName: String): String {
    return context.assets.open(fileName).bufferedReader().use { it.readText() }
}