package ave.mujica.poc.pocs.demo;


import android.os.*;
import android.widget.EditText;
import ave.mujica.poc.*;

public class DemoActivity extends BasePocActivity {
    private static final String TAG = "DemoActivity";

    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        input = ViewHelper.createInputField(this, "test input", "sakiko", getLogTypeface());

        addInputField(input);
        
        addActionButton("run poc", v -> poc());
        addActionButton("current time", v -> logCurrentTime());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public void poc() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            log(TAG, "Input is empty.");
            return; 
        } else {
            log(TAG, "Input: " + text);
        }
    }

    public void logCurrentTime() {
        log(TAG, "Current time: " + Utils.getCurrentTime());
    }
}
