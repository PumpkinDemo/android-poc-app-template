package ave.mujica.poc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class BasePocActivity extends Activity {

    protected TextView logView;
    protected final StringBuilder logBuffer = new StringBuilder();
    protected LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var root = ViewHelper.makeLinearLayout(this, getTag());
        this.rootLayout = root;
        root.setGravity(Gravity.NO_GRAVITY);
        setContentView(root);

        root.addView(ViewHelper.makeButton(this, "run poc", v -> poc()));

        // TextView hint = new TextView(this);
        // hint.setText("\nOutput goes to logcat:\n  adb logcat -s " + getTag());
        // hint.setTextSize(12f);
        // hint.setTypeface(android.graphics.Typeface.MONOSPACE);
        // root.addView(hint);

        logView = new TextView(this);
        logView.setTextSize(12f);
        logView.setTypeface(android.graphics.Typeface.MONOSPACE);
        logView.setPadding(0, 24, 0, 0);
        logView.setTextIsSelectable(true);
        ScrollView sv = new ScrollView(this);
        sv.addView(logView);
        root.addView(sv);
    }

    protected void log(String tag, String msg) {
        Log.d(tag, msg);
        logBuffer.append(msg).append("\n");
        if (logView != null) {
            logView.post(() -> logView.setText(logBuffer.toString()));
        }
    }

    protected void clearLog() {
        logBuffer.setLength(0);
        if (logView != null) {
            logView.setText("");
        }
    }

    public abstract void poc();
    public abstract String getTag();
}
