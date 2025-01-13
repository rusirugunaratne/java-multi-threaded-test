package org.taskrunner;

import java.util.HashSet;
import java.util.Set;

public class Subscriber {
    private final String subscriberName;
    private final Set<String> taskTypes;

    public Subscriber(String subscriberName) {
        this.subscriberName = subscriberName;
        this.taskTypes = new HashSet<>();
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void addTaskType(String taskType) {
        taskTypes.add(taskType);
        Logger.getInstance().log(subscriberName + " subscribed to task type: " + taskType, java.awt.Color.BLUE);
    }

    public Set<String> getSubscribedTaskTypes() {
        return taskTypes;
    }

    public void update(Topic topic) {
        String message = subscriberName + " received result for task type " + topic.taskType() + ": " + topic.result();
        Logger.getInstance().log(message, java.awt.Color.GREEN);
        System.out.println(message);
    }
}
