package org.taskrunner;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Executor {
    private final ExecutorService executorService;
    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getInstance();

    public Executor(int threadPoolSize) {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void subscribe(String taskType, Subscriber subscriber) {
        subscribers.computeIfAbsent(taskType, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(subscriber);
        logger.log("Subscriber added for task type: " + taskType, Color.BLUE);
    }

    public void unsubscribe(String taskType, Subscriber subscriber) {
        List<Subscriber> subs = subscribers.get(taskType);
        if (subs != null) {
            subs.remove(subscriber);
            logger.log("Subscriber removed for task type: " + taskType, Color.ORANGE);
        }
    }

    public Future<String> submit(Task task) {
        return executorService.submit(() -> {
            try {
                logger.log("Executing task: " + task.getTaskType(), Color.GREEN);
                String result = task.call();
                logger.log("Task completed: " + task.getTaskType() + " with result: " + result, Color.GREEN);
                notifySubscribers(task.getTaskType(), result);
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
