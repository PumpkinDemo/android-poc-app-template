package ave.mujica.poc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class BasePocActivity extends Activity {

    private static final String LOG_FONT_ASSET = "fonts/SpaceMono-Regular.ttf";

    private static final int CONTENT_HORIZONTAL_PADDING_DP = 24;
    private static final int CONTENT_VERTICAL_PADDING_DP = 16;
    private static final int CONTENT_ITEM_SPACING_DP = 4;

    private static final int LOG_HORIZONTAL_PADDING_DP = 12;
    private static final int LOG_VERTICAL_PADDING_DP = 12;
    private static final int LOG_BACKGROUND_COLOR = Color.parseColor("#F3F6FA");

    protected TextView logView;
    protected final StringBuilder logBuffer = new StringBuilder();
    protected LinearLayout rootLayout;

    private static Typeface logTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var root = ViewHelper.makeLinearLayout(this, getTag());
        this.rootLayout = root;
        root.setGravity(Gravity.NO_GRAVITY);
        root.setPadding(
                ViewHelper.dpToPx(this, CONTENT_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_VERTICAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_VERTICAL_PADDING_DP)
        );
        setContentView(root);

        logView = new TextView(this);
        logView.setTextSize(12f);
        logView.setTypeface(getLogTypeface());
        logView.setPadding(
                ViewHelper.dpToPx(this, LOG_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, 24),
                ViewHelper.dpToPx(this, LOG_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, LOG_VERTICAL_PADDING_DP)
        );
        logView.setHorizontallyScrolling(true);
        logView.setTextIsSelectable(true);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setFillViewport(false);
        horizontalScrollView.setBackgroundColor(LOG_BACKGROUND_COLOR);
        horizontalScrollView.addView(logView);

        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        sv.setBackgroundColor(LOG_BACKGROUND_COLOR);
        sv.addView(horizontalScrollView);
        root.addView(sv, defaultLayoutParams());
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

    protected Typeface loadLogTypeface() {
        try {
            return Typeface.createFromAsset(getAssets(), LOG_FONT_ASSET);
        } catch (RuntimeException e) {
            Log.w(getTag(), "Failed to load " + LOG_FONT_ASSET + ", falling back to monospace", e);
            return Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        }
    }

    protected Typeface getLogTypeface() {
        if (logTypeface == null) {
            logTypeface = loadLogTypeface();
        }
        return logTypeface;
    }

    protected LinearLayout.LayoutParams defaultLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = ViewHelper.dpToPx(this, CONTENT_ITEM_SPACING_DP);
        return params;
    }

    protected void addActionButton(String label, View.OnClickListener onClick) {
        if (rootLayout == null) {
            return;
        }
        rootLayout.addView(
                ViewHelper.makeButton(this, label, onClick),
                Math.max(1, rootLayout.getChildCount() - 1),
                defaultLayoutParams()
        );
    }
    
    public abstract String getTag();
}
