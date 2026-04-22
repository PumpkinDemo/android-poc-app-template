package ave.mujica.poc;

import android.os.IBinder;
import android.util.Log;

public class Utils {
    public static IBinder getServiceBinder(String name) {
        try {
            Class<?> smClass = Class.forName("android.os.ServiceManager");
            var getService = smClass.getDeclaredMethod("getService", String.class);
            return (IBinder) getService.invoke(null, name);
        } catch (Exception e) {
            Log.d("getService", "Exception: " + e.getMessage());
            return null;
        }
    }
}
