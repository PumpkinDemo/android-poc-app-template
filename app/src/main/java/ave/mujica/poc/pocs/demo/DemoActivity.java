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
        addActionButton("read asset file", v -> readAssetFile());
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

    public void readAssetFile() {
        String content = AssetUtils.readText(this, "hello.txt");
        if (content != null) {
            log(TAG, "--- Asset content start ---\n" + content + "\n--- Asset content end ---");
        } else {
            log(TAG, "Failed to read asset file.");
        }
    }
}
