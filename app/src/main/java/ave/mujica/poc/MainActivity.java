package ave.mujica.poc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ave.mujica.poc.demo.DemoActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        var root = ViewHelper.makeLinearLayout(this, "Main");
        setContentView(root);

        root.addView(ViewHelper.makeButton(this, "Demo",  v -> {
            startActivity(new Intent(this, DemoActivity.class));
        }));
    }
}