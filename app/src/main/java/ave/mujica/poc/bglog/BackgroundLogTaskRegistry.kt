package ave.mujica.poc.bglog

object BackgroundLogTaskRegistry {
    private val tasks: MutableMap<String, BackgroundLogTask> = LinkedHashMap()

    fun register(task: BackgroundLogTask) {
        tasks[task.getId()] = task
    }

    fun get(taskId: String?): BackgroundLogTask? {
        return tasks[taskId]
    }
}