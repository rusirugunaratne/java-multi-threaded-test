package org.taskrunner;

public class Topic {
    private final String taskType;
    private final String result;

    public Topic(String taskType, String result) {
        this.taskType = taskType;
        this.result = result;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getResult() {
        return result;
    }
}
