package ave.mujica.poc.pocs.demo

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

import ave.mujica.poc.BasePocActivity
import ave.mujica.poc.utils.NotificationUtils.sendNotification
import ave.mujica.poc.utils.readAssetText
import ave.mujica.poc.utils.getCurrentTime
import ave.mujica.poc.utils.ViewUtils

import android.os.Bundle
import android.widget.EditText


class DemoActivity : BasePocActivity() {
    
    companion object {
        private const val TAG = "DemoActivity"
    }
    
    private lateinit var input: EditText
    
    override fun tag() = TAG 
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        input = ViewUtils.createInputField(this, "test input", "sakiko")

        addInputField(input)
        
        addActionButton("run poc") { poc() }
        addActionButton("current time"){ logCurrentTime() }
        addActionButton("read asset file") { readAssetFile() }
        addActionButton("notify") { sendNotification() }
    }
    
    private fun poc() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) {
            log(TAG, "Input is empty.")
        } else {
            log(TAG, "Input: $text")
        }
    }
    
    private fun logCurrentTime() {
        val currentTime = SimpleDateFormat("HH:mm:ss:SSS", Locale.US).format(Date())
        log(TAG, "Current time: $currentTime")
    }
    
    private fun readAssetFile() {
        val content = readAssetText(this, "hello.txt")
        log(TAG, "--- Asset content start ---\n$content\n--- Asset content end ---")
    }
    
    private fun sendNotification() {
        sendNotification(this, "Hello", "Current time: " + getCurrentTime())
    }
}