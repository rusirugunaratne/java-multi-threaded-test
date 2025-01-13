package org.taskrunner;

import java.awt.*;
import java.util.concurrent.Callable;

public class Task implements Callable<String> {
    private final String type;
    private final int delay;
    private final String name;

    private final Logger logger = Logger.getInstance();

    public Task(String type, String name, int delay) {
        this.type = type;
        this.name = name;
        this.delay = delay;
    }

    public String getType() {
        return type;
    }

    @Override
    public String call() throws Exception {
        logger.log("Task " + type + " : " + name + " started...", Color.GREEN);
        Thread.sleep(delay);
        String executionCompletionMessage = "Task " + type + " : " + name + " completed...";
        logger.log(executionCompletionMessage, Color.BLUE);
        return executionCompletionMessage;
    }
}
