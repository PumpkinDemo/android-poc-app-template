package ave.mujica.poc.bglog;

import java.util.LinkedHashMap;
import java.util.Map;


public final class BackgroundLogTaskRegistry {
    private static final Map<String, BackgroundLogTask> TASKS = new LinkedHashMap<>();

    

    private BackgroundLogTaskRegistry() {
    }

    public static void register(BackgroundLogTask task) {
        TASKS.put(task.getId(), task);
    }

    public static BackgroundLogTask get(String taskId) {
        return TASKS.get(taskId);
    }
}
