package ave.mujica.poc;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AssetUtils {

    public static String readText(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;

            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            return bos.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}