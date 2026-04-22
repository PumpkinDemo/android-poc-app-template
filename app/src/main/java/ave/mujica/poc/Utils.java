package ave.mujica.poc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;


public class Utils {
    private static final String TAG = "Utils";
    private static final int BINDER_EXTENSION_TRANSACTION = 0x5F455854;

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(new Date());
    }

    public static IBinder getServiceBinder(String name) {
        try {
            Class<?> smClass = Class.forName("android.os.ServiceManager");
            var getService = smClass.getDeclaredMethod("getService", String.class);
            return (IBinder) getService.invoke(null, name);
        } catch (Exception e) {
            Log.d(TAG, "getServiceBinder(" + name + ") failed: " + e.getMessage());
            return null;
        }
    }

    public static IBinder getExtensionBinder(IBinder baseBinder) throws Exception {
        if (baseBinder == null) {
            return null;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            boolean ok = baseBinder.transact(BINDER_EXTENSION_TRANSACTION, data, reply, 0);
            if (!ok) {
                return null;
            }
            return reply.readStrongBinder();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public static Parcel transactForReply(IBinder binder, String descriptor, int code, ParcelWriter writer) throws Exception {
        if (binder == null) {
            throw new IllegalStateException("binder is null");
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(descriptor);
            if (writer != null) {
                writer.write(data);
            }
            boolean ok = binder.transact(code, data, reply, 0);
            if (!ok) {
                throw new IllegalStateException("transact returned false for code " + code);
            }
            reply.readException();
            return reply;
        } finally {
            data.recycle();
        }
    }

    @SuppressWarnings("deprecation")
    public static String formatBundle(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        List<String> keys = new ArrayList<>(bundle.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        sb.append("Bundle{");
        if (!keys.isEmpty()) {
            sb.append('\n');
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = bundle.get(key);
            sb.append("  ").append(key).append(" = ").append(formatValue(value));
            if (i + 1 < keys.size()) {
                sb.append('\n');
            }
        }
        if (!keys.isEmpty()) {
            sb.append('\n');
        }
        sb.append('}');
        return sb.toString();
    }

    public static String formatStringList(List<String> values) {
        if (values == null) {
            return "null";
        }
        if (values.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append('[').append(i).append("] ").append(values.get(i));
            if (i + 1 < values.size()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Bundle nestedBundle) {
            return formatBundle(nestedBundle);
        }
        if (value instanceof ArrayList<?> list) {
            return list.toString();
        }
        if (value instanceof Parcelable[] parcelables) {
            List<String> values = new ArrayList<>();
            for (Parcelable parcelable : parcelables) {
                values.add(String.valueOf(parcelable));
            }
            return values.toString();
        }
        if (value instanceof String[] strings) {
            List<String> values = new ArrayList<>();
            Collections.addAll(values, strings);
            return values.toString();
        }
        return String.valueOf(value);
    }

    public interface ParcelWriter {
        void write(Parcel data) throws Exception;
    }
}
