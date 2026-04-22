package ave.mujica.poc.pocs.demo;

import android.os.*;
import ave.mujica.poc.*;

public class DemoActivity extends BasePocActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addActionButton("run poc", v -> poc());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public void poc() {
        log(TAG, "Starting PoC...");
    }
}
