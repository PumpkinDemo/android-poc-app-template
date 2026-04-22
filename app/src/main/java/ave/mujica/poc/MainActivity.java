package ave.mujica.poc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ScrollView;

import ave.mujica.poc.pocs.demo.DemoActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        var root = ViewHelper.makeLinearLayout(this, "Main");
        root.setGravity(Gravity.TOP);

        ScrollView scrollView = new ScrollView(this);
        int horizontalPadding = ViewHelper.dpToPx(this, 20);
        scrollView.setClipToPadding(false);
        scrollView.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        scrollView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
        scrollView.addView(root);
        setContentView(scrollView);


        // @Todo: this is an example PoC entry, replace it with real ones
        root.addView(ViewHelper.makeButton(this, "Demo",  v -> {
            startActivity(new Intent(this, DemoActivity.class));
        }));

        // @Todo: this is an example PoC entry, replace it with real ones
        root.addView(ViewHelper.makeButton(this, "Background Log Demo",  v -> {
            startActivity(new Intent(this, ave.mujica.poc.pocs.bglogdemo.BgLogDemoActivity.class));
        }));
    }
}