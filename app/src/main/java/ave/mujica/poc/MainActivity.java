package ave.mujica.poc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ave.mujica.poc.pocs.demo.DemoActivity;
import ave.mujica.poc.pocs.bglogdemo.BgLogDemoActivity;


public class MainActivity extends Activity {
    private LinearLayout pocCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var root = ViewHelper.makeLinearLayout(this, "Main");

        ScrollView scrollView = new ScrollView(this);
        int horizontalPadding = ViewHelper.dpToPx(this, 20);
        scrollView.setClipToPadding(false);
        scrollView.setPadding(horizontalPadding, ViewHelper.dpToPx(this, 8), horizontalPadding, ViewHelper.dpToPx(this, 16));
        scrollView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
        scrollView.addView(root);
        setContentView(scrollView);

        LinearLayout.LayoutParams introParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        introParams.bottomMargin = ViewHelper.dpToPx(this, 18);
        root.addView(
                ViewHelper.makeInfoCard(
                        this,
                        "Use this launcher to open individual framework and OEM policy probes. " +
                                "Each page keeps its own action surface on top and an expandable log console at the bottom."
                ),
                1,
                introParams
        );

        pocCard = createPocCard();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = ViewHelper.dpToPx(this, 18);
        root.addView(pocCard, cardParams);

        addPocEntry("Simple Demo", DemoActivity.class);
        addPocEntry("Background Log Demo", BgLogDemoActivity.class);
    }

    private LinearLayout createPocCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(ViewHelper.makeSurfaceBackground(this, 22));
        card.setElevation(ViewHelper.dpToPx(this, 2));
        card.setPadding(
                ViewHelper.dpToPx(this, 18),
                ViewHelper.dpToPx(this, 18),
                ViewHelper.dpToPx(this, 18),
                ViewHelper.dpToPx(this, 18)
        );

        TextView cardTitle = ViewHelper.makeCardTitle(this, "Available PoCs");
        LinearLayout.LayoutParams cardTitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardTitleParams.bottomMargin = ViewHelper.dpToPx(this, 6);
        card.addView(cardTitle, cardTitleParams);

        TextView cardSubtitle = ViewHelper.makeSectionBody(
                this,
                "Each probe opens a dedicated test surface with inline controls and a persistent log console for raw output."
        );
        LinearLayout.LayoutParams cardSubtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardSubtitleParams.bottomMargin = ViewHelper.dpToPx(this, 14);
        card.addView(cardSubtitle, cardSubtitleParams);

        return card;
    }

    private void addPocEntry(String label, Class<? extends Activity> activityClass) {
        if (pocCard == null) {
            return;
        }
        pocCard.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.bottomMargin = ViewHelper.dpToPx(this, 10);
        pocCard.addView(
                ViewHelper.makeButton(this, label, v -> startActivity(new Intent(this, activityClass))),
                buttonParams
        );
    }
}
