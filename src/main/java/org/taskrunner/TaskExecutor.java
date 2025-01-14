package org.taskrunner;

import java.awt.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.Map;

public class TaskExecutor {
    private ExecutorService executorService;
    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getInstance();

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void subscribe(String taskType, Subscriber subscriber) {
        subscribers.computeIfAbsent(taskType, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(subscriber);
        logger.log("Subscriber added for task type: " + taskType, Color.BLUE);
    }

    public void submit(Task task) {
        executorService.submit(() -> {
            try {
                logger.log("Executing task: " + task.getType(), Color.GREEN);
                String result = task.call();
                logger.log("Task completed: " + task.getType() + " with result: " + result, Color.GREEN);
                notifySubscribers(task.getType(), result);
                return result;
            } catch (Exception e) {
                logger.log("Error executing task: " + e.getMessage(), Color.RED);
                return null;
            }
        });
    }

    public void stop() {
        executorService.shutdown();
    }

    public void forceStop() {
        executorService.shutdownNow();
    }

    private void notifySubscribers(String taskType, String result) {
        List<Subscriber> subs = subscribers.get(taskType);
        if (subs != null) {
            logger.log("Notifying subscribers for task type: " + taskType, Color.CYAN);
            for (Subscriber subscriber : subs) {
                subscriber.update(new Topic(taskType, result));
            }
        }
    }
}
