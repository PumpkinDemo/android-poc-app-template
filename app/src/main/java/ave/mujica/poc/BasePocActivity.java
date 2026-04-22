package ave.mujica.poc;

import android.app.Activity;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public abstract class BasePocActivity extends Activity {

    private static final String LOG_FONT_ASSET = "fonts/SpaceMono-Regular.ttf";

    private static final int CONTENT_HORIZONTAL_PADDING_DP = 24;
    private static final int CONTENT_VERTICAL_PADDING_DP = 16;
    private static final int CONTENT_ITEM_SPACING_DP = 10;
    private static final int PAGE_BACKGROUND_COLOR = Color.parseColor("#F4F7FB");

    private static final int LOG_SHEET_PEEK_HEIGHT_DP = 164;
    private static final int LOG_SHEET_EXPANDED_TOP_OFFSET_DP = 48;
    private static final int LOG_HORIZONTAL_PADDING_DP = 12;
    private static final int LOG_VERTICAL_PADDING_DP = 12;
    private static final int LOG_SHEET_CORNER_RADIUS_DP = 22;
    private static final int LOG_SHEET_ELEVATION_DP = 10;
    private static final int LOG_HEADER_HORIZONTAL_PADDING_DP = 16;
    private static final int LOG_HEADER_VERTICAL_PADDING_DP = 10;
    private static final int LOG_HEADER_BACKGROUND_COLOR = Color.parseColor("#DCE6F2");
    private static final int LOG_HEADER_TEXT_COLOR = Color.parseColor("#10243A");
    private static final int LOG_META_TEXT_COLOR = Color.parseColor("#58708A");
    private static final int LOG_CONSOLE_BACKGROUND_COLOR = Color.parseColor("#F8FBFF");
    private static final int LOG_SHEET_BACKGROUND_COLOR = Color.parseColor("#EEF4FA");
    private static final int LOG_SHEET_STROKE_COLOR = Color.parseColor("#B7C7D8");
    private static final int LOG_HANDLE_COLOR = Color.parseColor("#8AA0B8");
    private static final int LOG_DIVIDER_COLOR = Color.parseColor("#CBD8E5");
    private static final int LOG_TEXT_COLOR = Color.parseColor("#20354C");
    private static final int LOG_CLEAR_TEXT_COLOR = Color.parseColor("#0F548C");
    private static final int LOG_CLEAR_BACKGROUND_COLOR = Color.parseColor("#E8F2FB");
    private static final String LOG_EMPTY_TEXT = "No logs captured yet.";

    protected TextView logView;
    protected final StringBuilder logBuffer = new StringBuilder();
    protected LinearLayout rootLayout;

    private static Typeface logTypeface;
    private TextView logMetaView;
    private View inputSectionCard;
    private View actionSectionCard;
    private LinearLayout inputSectionContent;
    private LinearLayout actionSectionContent;
    private ScrollView contentScrollView;
    private LinearLayout logSheet;
    private float logSheetExpandedTranslationY;
    private float logSheetCollapsedTranslationY;
    private float logSheetDragStartRawY;
    private float logSheetDragStartTranslationY;
    private boolean logSheetPositioned;
    private boolean logHeaderDragging;
    private boolean logHeaderMoved;
    private int logHeaderTouchSlop;
    private ValueAnimator logSheetAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logHeaderTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        coordinatorLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        coordinatorLayout.setBackgroundColor(PAGE_BACKGROUND_COLOR);

        contentScrollView = new ScrollView(this);
        contentScrollView.setFillViewport(true);
        contentScrollView.setClipToPadding(false);
        contentScrollView.setPadding(0, 0, 0, 0);
        CoordinatorLayout.LayoutParams contentParams = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
        );
        contentScrollView.setLayoutParams(contentParams);

        var root = ViewHelper.makeLinearLayout(this, getTag());
        this.rootLayout = root;
        root.setGravity(Gravity.NO_GRAVITY);
        root.setPadding(
                ViewHelper.dpToPx(this, CONTENT_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_VERTICAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, CONTENT_VERTICAL_PADDING_DP)
        );
        root.setClipToPadding(false);

        contentScrollView.addView(root);
        coordinatorLayout.addView(contentScrollView);

        logView = new TextView(this);
        logView.setTextSize(12.5f);
        logView.setTextColor(LOG_TEXT_COLOR);
        logView.setTypeface(getLogTypeface());
        logView.setLineSpacing(0f, 1.18f);
        logView.setPadding(
                ViewHelper.dpToPx(this, LOG_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, 14),
                ViewHelper.dpToPx(this, LOG_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, LOG_VERTICAL_PADDING_DP)
        );
        logView.setHorizontallyScrolling(true);
        logView.setTextIsSelectable(true);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setFillViewport(false);
        horizontalScrollView.setBackgroundColor(LOG_CONSOLE_BACKGROUND_COLOR);
        horizontalScrollView.addView(logView);

        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        sv.setBackgroundColor(LOG_CONSOLE_BACKGROUND_COLOR);
        sv.addView(horizontalScrollView);

        View handleBar = new View(this);
        GradientDrawable handleDrawable = new GradientDrawable();
        handleDrawable.setColor(LOG_HANDLE_COLOR);
        handleDrawable.setCornerRadius(ViewHelper.dpToPx(this, 999));
        handleBar.setBackground(handleDrawable);
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(
                ViewHelper.dpToPx(this, 42),
                ViewHelper.dpToPx(this, 4)
        );
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.topMargin = ViewHelper.dpToPx(this, 8);
        handleParams.bottomMargin = ViewHelper.dpToPx(this, 8);

        TextView titleView = new TextView(this);
        titleView.setText("Log Console");
        titleView.setTextSize(14f);
        titleView.setTextColor(LOG_HEADER_TEXT_COLOR);
        titleView.setTypeface(Typeface.create(getLogTypeface(), Typeface.BOLD));

        logMetaView = new TextView(this);
        logMetaView.setTextSize(10.5f);
        logMetaView.setTextColor(LOG_META_TEXT_COLOR);
        logMetaView.setTypeface(getLogTypeface());

        LinearLayout titleColumn = new LinearLayout(this);
        titleColumn.setOrientation(LinearLayout.VERTICAL);
        titleColumn.setGravity(Gravity.CENTER_VERTICAL);
        titleColumn.addView(titleView);
        titleColumn.addView(logMetaView);

        TextView clearLogView = new TextView(this);
        clearLogView.setText("Clear");
        clearLogView.setTextSize(12f);
        clearLogView.setTextColor(LOG_CLEAR_TEXT_COLOR);
        clearLogView.setTypeface(Typeface.create(getLogTypeface(), Typeface.BOLD));
        clearLogView.setGravity(Gravity.CENTER);
        clearLogView.setPadding(
                ViewHelper.dpToPx(this, 10),
                ViewHelper.dpToPx(this, 6),
                ViewHelper.dpToPx(this, 10),
                ViewHelper.dpToPx(this, 6)
        );
        clearLogView.setBackground(makeRoundedRect(LOG_CLEAR_BACKGROUND_COLOR, 999, 0, Color.TRANSPARENT));
        clearLogView.setOnClickListener(v -> clearLog());

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.addView(titleColumn, titleParams);
        headerRow.addView(clearLogView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(
                ViewHelper.dpToPx(this, LOG_HEADER_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, LOG_HEADER_VERTICAL_PADDING_DP),
                ViewHelper.dpToPx(this, LOG_HEADER_HORIZONTAL_PADDING_DP),
                ViewHelper.dpToPx(this, LOG_HEADER_VERTICAL_PADDING_DP)
        );
        headerLayout.setBackgroundColor(LOG_HEADER_BACKGROUND_COLOR);
        headerLayout.addView(handleBar, handleParams);
        headerLayout.addView(headerRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        View dividerView = new View(this);
        dividerView.setBackgroundColor(LOG_DIVIDER_COLOR);

        logSheet = new LinearLayout(this);
        logSheet.setOrientation(LinearLayout.VERTICAL);
        logSheet.setBackground(makeSheetBackground());
        logSheet.setElevation(ViewHelper.dpToPx(this, LOG_SHEET_ELEVATION_DP));
        logSheet.addView(headerLayout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        logSheet.addView(dividerView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewHelper.dpToPx(this, 1)
        ));

        LinearLayout.LayoutParams logScrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
        );
        logScrollParams.weight = 1f;
        logSheet.addView(sv, logScrollParams);

        CoordinatorLayout.LayoutParams logSheetParams = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
        );
        logSheetParams.gravity = Gravity.BOTTOM;
        logSheet.setLayoutParams(logSheetParams);
        coordinatorLayout.addView(logSheet);
        logSheet.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                updateLogSheetBounds()
        );
        coordinatorLayout.post(this::updateLogSheetBounds);

        View.OnTouchListener logHeaderTouchListener = this::handleLogHeaderTouch;
        handleBar.setOnTouchListener(logHeaderTouchListener);
        titleColumn.setOnTouchListener(logHeaderTouchListener);
        titleView.setOnTouchListener(logHeaderTouchListener);
        logMetaView.setOnTouchListener(logHeaderTouchListener);

        setContentView(coordinatorLayout);
        renderLogBuffer();
    }

    protected void log(String tag, String msg) {
        Log.d(tag, msg);
        logBuffer.append(msg).append("\n");
        if (logView != null) {
            logView.post(this::renderLogBuffer);
        }
    }

    protected void clearLog() {
        logBuffer.setLength(0);
        renderLogBuffer();
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
        ensureActionSection();
        actionSectionContent.addView(ViewHelper.makeButton(this, label, onClick), defaultLayoutParams());
    }

    protected void addInputField(View view) {
        if (rootLayout == null || view == null) {
            return;
        }
        ensureInputSection();
        inputSectionContent.addView(view, defaultLayoutParams());
    }

    protected void toggleLogSheet() {
        if (logSheet == null) {
            return;
        }
        float midpoint = (logSheetExpandedTranslationY + logSheetCollapsedTranslationY) / 2f;
        if (logSheet.getTranslationY() <= midpoint) {
            animateLogSheetTo(logSheetCollapsedTranslationY);
            return;
        }
        animateLogSheetTo(logSheetExpandedTranslationY);
    }

    private void renderLogBuffer() {
        if (logView == null) {
            return;
        }
        String content = logBuffer.length() == 0 ? LOG_EMPTY_TEXT : logBuffer.toString();
        logView.setText(content);
        logView.setAlpha(logBuffer.length() == 0 ? 0.62f : 1f);
        if (logMetaView != null) {
            int entries = countLogEntries();
            String suffix = entries == 1 ? "entry" : "entries";
            logMetaView.setText(entries + " " + suffix + "  |  Drag title freely");
        }
    }

    private int countLogEntries() {
        if (logBuffer.length() == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < logBuffer.length(); i++) {
            if (logBuffer.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    private GradientDrawable makeSheetBackground() {
        GradientDrawable drawable = new GradientDrawable();
        float radius = ViewHelper.dpToPx(this, LOG_SHEET_CORNER_RADIUS_DP);
        drawable.setColor(LOG_SHEET_BACKGROUND_COLOR);
        drawable.setCornerRadii(new float[]{
                radius, radius,
                radius, radius,
                0f, 0f,
                0f, 0f
        });
        drawable.setStroke(ViewHelper.dpToPx(this, 1), LOG_SHEET_STROKE_COLOR);
        return drawable;
    }

    private boolean handleLogHeaderTouch(View view, MotionEvent event) {
        if (logSheet == null) {
            return false;
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            cancelLogSheetAnimation();
            logHeaderDragging = true;
            logHeaderMoved = false;
            logSheetDragStartRawY = event.getRawY();
            logSheetDragStartTranslationY = logSheet.getTranslationY();
            return true;
        }
        if (!logHeaderDragging) {
            return false;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            float deltaY = event.getRawY() - logSheetDragStartRawY;
            if (Math.abs(deltaY) > logHeaderTouchSlop) {
                logHeaderMoved = true;
            }
            setLogSheetTranslation(logSheetDragStartTranslationY + deltaY);
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            if (!logHeaderMoved) {
                toggleLogSheet();
            }
            logHeaderDragging = false;
            view.performClick();
            return true;
        }
        if (action == MotionEvent.ACTION_CANCEL) {
            logHeaderDragging = false;
            return true;
        }
        return false;
    }

    private void updateLogSheetBounds() {
        if (logSheet == null || logSheet.getHeight() == 0) {
            return;
        }
        logSheetExpandedTranslationY = ViewHelper.dpToPx(this, LOG_SHEET_EXPANDED_TOP_OFFSET_DP);
        logSheetCollapsedTranslationY = Math.max(
                logSheetExpandedTranslationY,
                logSheet.getHeight() - ViewHelper.dpToPx(this, LOG_SHEET_PEEK_HEIGHT_DP)
        );
        if (!logSheetPositioned) {
            logSheetPositioned = true;
            setLogSheetTranslation(logSheetCollapsedTranslationY);
            return;
        }
        setLogSheetTranslation(logSheet.getTranslationY());
    }

    private void setLogSheetTranslation(float translationY) {
        if (logSheet == null) {
            return;
        }
        float clamped = Math.max(logSheetExpandedTranslationY, Math.min(logSheetCollapsedTranslationY, translationY));
        logSheet.setTranslationY(clamped);
        updateContentBottomInset(clamped);
    }

    private void animateLogSheetTo(float targetTranslationY) {
        if (logSheet == null) {
            return;
        }
        cancelLogSheetAnimation();
        float start = logSheet.getTranslationY();
        float end = Math.max(logSheetExpandedTranslationY, Math.min(logSheetCollapsedTranslationY, targetTranslationY));
        if (Math.abs(start - end) < 1f) {
            setLogSheetTranslation(end);
            return;
        }
        logSheetAnimator = ValueAnimator.ofFloat(start, end);
        logSheetAnimator.setDuration(220L);
        logSheetAnimator.addUpdateListener(animation ->
                setLogSheetTranslation((float) animation.getAnimatedValue()));
        logSheetAnimator.start();
    }

    private void cancelLogSheetAnimation() {
        if (logSheetAnimator == null) {
            return;
        }
        logSheetAnimator.cancel();
        logSheetAnimator = null;
    }

    private void updateContentBottomInset(float logSheetTranslationY) {
        if (contentScrollView == null || logSheet == null) {
            return;
        }
        int visibleLogHeight = Math.max(0, Math.round(logSheet.getHeight() - logSheetTranslationY));
        int bottomInset = visibleLogHeight + ViewHelper.dpToPx(this, CONTENT_ITEM_SPACING_DP);
        if (contentScrollView.getPaddingBottom() == bottomInset) {
            return;
        }
        contentScrollView.setPadding(
                contentScrollView.getPaddingLeft(),
                contentScrollView.getPaddingTop(),
                contentScrollView.getPaddingRight(),
                bottomInset
        );
    }

    private void ensureInputSection() {
        if (inputSectionCard != null) {
            return;
        }
        SectionViews section = createSectionCard(
                "Inputs",
                "Adjust probe parameters before issuing binder calls."
        );
        inputSectionCard = section.card;
        inputSectionContent = section.content;
        rootLayout.addView(inputSectionCard, 1, sectionLayoutParams());
    }

    private void ensureActionSection() {
        if (actionSectionCard != null) {
            return;
        }
        SectionViews section = createSectionCard(
                "Actions",
                "Run binder probes and inspect the resulting output in the log console."
        );
        actionSectionCard = section.card;
        actionSectionContent = section.content;
        int index = inputSectionCard == null ? 1 : rootLayout.indexOfChild(inputSectionCard) + 1;
        rootLayout.addView(actionSectionCard, index, sectionLayoutParams());
    }

    private LinearLayout.LayoutParams sectionLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = ViewHelper.dpToPx(this, 14);
        return params;
    }

    private SectionViews createSectionCard(String title, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(ViewHelper.makeSurfaceBackground(this, 22));
        card.setElevation(ViewHelper.dpToPx(this, 2));
        card.setPadding(
                ViewHelper.dpToPx(this, 16),
                ViewHelper.dpToPx(this, 14),
                ViewHelper.dpToPx(this, 16),
                ViewHelper.dpToPx(this, 14)
        );

        TextView label = ViewHelper.makeSectionLabel(this, title);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.bottomMargin = ViewHelper.dpToPx(this, 4);
        card.addView(label, labelParams);

        TextView body = ViewHelper.makeSectionBody(this, subtitle);
        body.setTextSize(12.5f);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bodyParams.bottomMargin = ViewHelper.dpToPx(this, 12);
        card.addView(body, bodyParams);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        card.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return new SectionViews(card, content);
    }

    private GradientDrawable makeRoundedRect(int fillColor, int radiusDp, int strokeWidthDp, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(ViewHelper.dpToPx(this, radiusDp));
        if (strokeWidthDp > 0) {
            drawable.setStroke(ViewHelper.dpToPx(this, strokeWidthDp), strokeColor);
        }
        return drawable;
    }

    private static final class SectionViews {
        final LinearLayout card;
        final LinearLayout content;

        SectionViews(LinearLayout card, LinearLayout content) {
            this.card = card;
            this.content = content;
        }
    }
    
    public abstract String getTag();
}
