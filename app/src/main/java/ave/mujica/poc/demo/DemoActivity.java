package ave.mujica.poc.demo;

import android.os.*;
import ave.mujica.poc.*;

public class DemoActivity extends BasePocActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public void poc() {
        log(TAG, "Starting PoC...");
    }
}
