package ave.mujica.poc.pocs.bglogdemo;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import ave.mujica.poc.NotificationHelper;
import ave.mujica.poc.bglog.BackgroundLogResult;

public final class NonForegroundBgLogThread {
    private static final String LOG_TAG = "DemoBgLogNoFgs";
    private static final String THREAD_NAME = "BgLogDemoNoFgsThread";
    private static final long MIN_INTERVAL_MS = 1000L;

    private static final Object lock = new Object();
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static Thread workerThread;

    private NonForegroundBgLogThread() {
    }

    public static boolean start(Context context, long intervalMs) {
        Context appContext = context.getApplicationContext();
        long actualIntervalMs = Math.max(MIN_INTERVAL_MS, intervalMs);
        synchronized (lock) {
            if (workerThread != null && workerThread.isAlive()) {
                return false;
            }

            running.set(true);
            workerThread = new Thread(() -> runLoop(appContext, actualIntervalMs), THREAD_NAME);
            workerThread.start();
            return true;
        }
    }

    public static void stop() {
        synchronized (lock) {
            running.set(false);
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    public static boolean isRunning() {
        Thread thread = workerThread;
        return thread != null && thread.isAlive();
    }

    private static void runLoop(Context context, long intervalMs) {
        DemoBgLogTask task = new DemoBgLogTask();
        long sequence = 0L;
        long previousElapsedMs = 0L;

        Log.d(LOG_TAG, "no-FGS log thread started, interval=" + intervalMs + "ms");
        while (running.get()) {
            long elapsedMs = SystemClock.elapsedRealtime();
            long deltaMs = previousElapsedMs == 0L ? 0L : elapsedMs - previousElapsedMs;
            previousElapsedMs = elapsedMs;
            sequence++;

            try {
                BackgroundLogResult result = task.runOnce(context);
                Log.d(LOG_TAG, "no-FGS seq=" + sequence + " elapsedMs=" + elapsedMs + " deltaMs=" + deltaMs);
                for (String line : result.logLines) {
                    Log.d(LOG_TAG, line);
                }
                NotificationHelper.checkAndSend(context, "No-FGS log thread", "Current time: " + result.notificationText);
            } catch (Throwable t) {
                Log.e(LOG_TAG, "no-FGS log task failed", t);
            }

            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                if (!running.get()) {
                    break;
                }
            }
        }

        Log.d(LOG_TAG, "no-FGS log thread stopped");
        synchronized (lock) {
            if (Thread.currentThread() == workerThread) {
                workerThread = null;
            }
            running.set(false);
        }
    }
}
