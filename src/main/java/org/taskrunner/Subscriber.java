package org.taskrunner;

public class Subscriber {
    private final String subscriberName;

    public Subscriber(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public void update(Topic topic) {
        System.out.println(subscriberName + " received result for task type " + topic.getTaskType() + ": " + topic.getResult());
    }
}
