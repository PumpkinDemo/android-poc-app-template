package ave.mujica.poc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHelper {
    
    public static LinearLayout makeLinearLayout(Context context, String title) {
        var rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setFitsSystemWindows(true);
        rootLayout.setPadding(100, 100, 100, 100);
        int paddingX = dpToPx(context, 24);
        int paddingY = dpToPx(context, 16);
        rootLayout.setPadding(paddingX, paddingY, paddingX, paddingY);

        rootLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        var text = new TextView(context);
        text.setText(title);
        text.setTextSize(20f);
        var titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = 24;
        text.setLayoutParams(titleParams);

        rootLayout.addView(text);

        return rootLayout;
    }

    public static Button makeButton(Context context, String label, View.OnClickListener onClick) {
        var button = new Button(context);
        button.setText(label);
        button.setOnClickListener(onClick);
        button.setAllCaps(false);
        return button;
    }

    public static EditText createInputField(Context context, String hint, String value, Typeface typeface) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        editText.setText(value);
        editText.setTypeface(typeface);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return editText;
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}