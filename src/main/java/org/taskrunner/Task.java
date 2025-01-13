package org.taskrunner;

import java.awt.*;
import java.util.concurrent.Callable;

public class Task implements Callable<String> {
    private final String taskType;
    private final int delay;

    // Access the singleton Logger
    private final Logger logger = Logger.getInstance();

    public Task(String taskType, int delay) {
        this.taskType = taskType;
        this.delay = delay;
    }

    public String getTaskType() {
        return taskType;
    }

    @Override
    public String call() throws Exception {
        // Log task start
        logger.log("Task of type " + taskType + " started...", Color.GREEN);

        // Simulate task execution with delay
        Thread.sleep(delay);

        // Log task completion
        String executionCompletionMessage = "Task of type " + taskType + " completed...";
        logger.log(executionCompletionMessage, Color.BLUE);

        return executionCompletionMessage;
    }
}
