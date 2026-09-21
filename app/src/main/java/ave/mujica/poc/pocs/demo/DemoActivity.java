package ave.mujica.poc.pocs.demo;


import android.os.*;
import android.widget.EditText;
import ave.mujica.poc.*;
import ave.mujica.poc.utils.DateUtils;
import ave.mujica.poc.utils.StringUtils;

public class DemoActivity extends BasePocActivity {
    private static final String TAG = "DemoActivity";

    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        input = ViewHelper.createInputField(this, "test input", "sakiko");

        addInputField(input);
        
        addActionButton("run poc", v -> poc());
        addActionButton("current time", v -> logCurrentTime());
        addActionButton("read asset file", v -> readAssetFile());
        addActionButton("notify", v -> sendNotification());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public void poc() {
        String text = StringUtils.trimInput(input.getText().toString());
        if (text.isEmpty()) {
            log(TAG, "Input is empty.");
            return; 
        } else {
            log(TAG, "Input: " + text);
        }
    }

    public void logCurrentTime() {
        log(TAG, "Current time: " + DateUtils.getCurrentTime());
    }

    public void readAssetFile() {
        String content = AssetUtils.readText(this, "hello.txt");
        log(TAG, "--- Asset content start ---\n" + content + "\n--- Asset content end ---");
    }

    public void sendNotification() {
        NotificationHelper.checkAndSend(this, "Hello", "Current time: " + DateUtils.getCurrentTime());
    }
}
