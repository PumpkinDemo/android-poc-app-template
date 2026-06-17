package ave.mujica.poc.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import android.graphics.BitmapFactory

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import ave.mujica.poc.R

object NotificationUtils {
    private const val TAG = "NotificationHelper"

    private const val CHANNEL_ID = "default"
    private const val CHANNEL_NAME = "PoC Test"
    private const val CHANNEL_DESC = "PoC Test Channel"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CHANNEL_DESC
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
    
    fun sendNotification(context: Context, title: String, content: String) {
        if (context.checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            createChannel(context)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_claude)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_claude))
                .setContentTitle(title)
                .setContentText(content)
                // .setContentIntent(pi)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()
            NotificationManagerCompat.from(context).notify(1001, notification)
        } else {
            val errMsg = "Permission POST_NOTIFICATIONS is not granted."
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            Log.d(TAG, errMsg)
        }
    }
}