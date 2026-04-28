package ave.mujica.poc;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "PoC Test";
    private static final String CHANNEL_DESC = "PoC Test Channel";

    public static void createChannel(Context context) {
        var channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(CHANNEL_DESC);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
    
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void checkAndSend(Context context, String title, String content) {
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) 
        == PackageManager.PERMISSION_GRANTED) {
            createChannel(context);
            send(context, title, content);
        } else {
            String errMsg = "Permission POST_NOTIFICATIONS is not granted.";
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show();
            Log.d(TAG, errMsg);
        }
    }

    public static void send(Context context, String title, String content){
        // Intent notificationIntent = new Intent(context, VmTestActivity.class);
        // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // var piFlags  = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        // var pi = PendingIntent.getActivity(context, 0, notificationIntent, piFlags);
        var largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_claude);
        var notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_claude)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(content)
            // .setContentIntent(pi)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build();
        NotificationManagerCompat.from(context).notify(1001, notification);
    }
}
