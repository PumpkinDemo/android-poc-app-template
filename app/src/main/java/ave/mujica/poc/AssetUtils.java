package ave.mujica.poc;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class AssetUtils {

    public static String readText(Context context, String fileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open(fileName), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[8192];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                content.append(buffer, 0, len);
            }
            return content.toString();
        } catch (IOException e) {
            // Java callers receive the read failure without a checked-exception requirement.
            throw new UncheckedIOException(e);
        }
    }
}
