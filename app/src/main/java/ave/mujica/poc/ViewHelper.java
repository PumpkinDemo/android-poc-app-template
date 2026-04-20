package ave.mujica.poc;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHelper {
    public static LinearLayout makeLinearLayout(Context context, String title) {

        // var density = context.getResources().getDisplayMetrics().density;
        // var paddingX = (int)(density * 16);

        var rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        rootLayout.setFitsSystemWindows(true);
        rootLayout.setPadding(100, 100, 100, 100);

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
}