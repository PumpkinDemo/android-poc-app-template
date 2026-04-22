package ave.mujica.poc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHelper {
    private static final int PAGE_BACKGROUND_COLOR = Color.parseColor("#F4F7FB");
    private static final int SURFACE_COLOR = Color.parseColor("#FFFFFF");
    private static final int SURFACE_STROKE_COLOR = Color.parseColor("#D7E1EC");
    private static final int TITLE_COLOR = Color.parseColor("#10243A");
    private static final int SUBTITLE_COLOR = Color.parseColor("#5E7389");
    private static final int ACCENT_COLOR = Color.parseColor("#0F5F9A");
    private static final int ACCENT_DARK_COLOR = Color.parseColor("#0A4C7B");
    private static final int INPUT_BACKGROUND_COLOR = Color.parseColor("#FBFDFF");
    private static final int INPUT_TEXT_COLOR = Color.parseColor("#183046");
    private static final int INPUT_HINT_COLOR = Color.parseColor("#73879D");

    public static LinearLayout makeLinearLayout(Context context, String title) {
        var rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.TOP);
        rootLayout.setFitsSystemWindows(true);
        rootLayout.setBackgroundColor(PAGE_BACKGROUND_COLOR);
        int paddingX = dpToPx(context, 20);
        int paddingY = dpToPx(context, 16);
        rootLayout.setPadding(paddingX, paddingY, paddingX, paddingY);

        rootLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout titleCard = new LinearLayout(context);
        titleCard.setOrientation(LinearLayout.VERTICAL);
        titleCard.setBackground(makeRoundedRect(SURFACE_COLOR, dpToPx(context, 22), dpToPx(context, 1), SURFACE_STROKE_COLOR));
        titleCard.setElevation(dpToPx(context, 2));
        titleCard.setPadding(
                dpToPx(context, 18),
                dpToPx(context, 18),
                dpToPx(context, 18),
                dpToPx(context, 18)
        );

        TextView eyebrow = new TextView(context);
        eyebrow.setText("OnePlus Framework");
        eyebrow.setTextSize(11f);
        eyebrow.setTextColor(ACCENT_COLOR);
        eyebrow.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        TextView text = new TextView(context);
        text.setText(title);
        text.setTextSize(24f);
        text.setTextColor(TITLE_COLOR);
        text.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        TextView subtitle = new TextView(context);
        subtitle.setText(defaultSubtitleForTitle(title));
        subtitle.setTextSize(13f);
        subtitle.setTextColor(SUBTITLE_COLOR);
        subtitle.setLineSpacing(0f, 1.12f);

        LinearLayout.LayoutParams eyebrowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        eyebrowParams.bottomMargin = dpToPx(context, 10);
        titleCard.addView(eyebrow, eyebrowParams);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = dpToPx(context, 6);
        titleCard.addView(text, titleParams);
        titleCard.addView(subtitle);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dpToPx(context, 18);
        rootLayout.addView(titleCard, cardParams);

        return rootLayout;
    }

    public static Button makeButton(Context context, String label, View.OnClickListener onClick) {
        var button = new Button(context);
        button.setText(label);
        button.setOnClickListener(onClick);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14f);
        button.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dpToPx(context, 46));
        button.setPadding(
                dpToPx(context, 14),
                dpToPx(context, 10),
                dpToPx(context, 14),
                dpToPx(context, 10)
        );
        button.setBackground(makeRoundedRect(ACCENT_COLOR, dpToPx(context, 16), dpToPx(context, 1), ACCENT_DARK_COLOR));
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return button;
    }

    public static EditText createInputField(Context context, String hint, String value, Typeface typeface) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        editText.setText(value);
        editText.setTypeface(typeface);
        editText.setTextSize(13.5f);
        editText.setTextColor(INPUT_TEXT_COLOR);
        editText.setHintTextColor(INPUT_HINT_COLOR);
        editText.setBackground(makeRoundedRect(INPUT_BACKGROUND_COLOR, dpToPx(context, 16), dpToPx(context, 1), SURFACE_STROKE_COLOR));
        editText.setPadding(
                dpToPx(context, 14),
                dpToPx(context, 10),
                dpToPx(context, 14),
                dpToPx(context, 10)
        );
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return editText;
    }

    public static TextView makeInfoCard(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(13.5f);
        view.setTextColor(SUBTITLE_COLOR);
        view.setLineSpacing(0f, 1.12f);
        view.setBackground(makeRoundedRect(SURFACE_COLOR, dpToPx(context, 18), dpToPx(context, 1), SURFACE_STROKE_COLOR));
        view.setPadding(
                dpToPx(context, 16),
                dpToPx(context, 14),
                dpToPx(context, 16),
                dpToPx(context, 14)
        );
        return view;
    }

    public static TextView makeSectionLabel(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(12f);
        view.setTextColor(ACCENT_COLOR);
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        return view;
    }

    public static TextView makeCardTitle(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(18f);
        view.setTextColor(TITLE_COLOR);
        view.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        return view;
    }

    public static TextView makeSectionBody(Context context, String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(13.5f);
        view.setTextColor(SUBTITLE_COLOR);
        view.setLineSpacing(0f, 1.12f);
        return view;
    }

    public static GradientDrawable makeSurfaceBackground(Context context, int radiusDp) {
        return makeRoundedRect(SURFACE_COLOR, dpToPx(context, radiusDp), dpToPx(context, 1), SURFACE_STROKE_COLOR);
    }

    private static String defaultSubtitleForTitle(String title) {
        if ("Main".equals(title)) {
            return "Binder probes and OEM policy surfaces collected in a single launcher.";
        }
        return "Interactive probe surface for " + title + ".";
    }

    private static GradientDrawable makeRoundedRect(int fillColor, int radiusPx, int strokeWidthPx, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(radiusPx);
        if (strokeWidthPx > 0) {
            drawable.setStroke(strokeWidthPx, strokeColor);
        }
        return drawable;
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
