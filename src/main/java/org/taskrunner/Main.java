package org.taskrunner;

public class Main {
    public static void main(String[] args) {

        RunnerUI runnerUI = new RunnerUI();
        runnerUI.showUI();

//        Executor executor = new Executor(3);
//
//        // Create subscribers
//        Subscriber subscriber1 = new Subscriber("Subscriber 1");
//        Subscriber subscriber2 = new Subscriber("Subscriber 2");
//
//        // Subscribe to task types
//        executor.subscribe("Task 0", subscriber1);
//        executor.subscribe("Task 0", subscriber2); // Multiple subscribers for the same task type
//        executor.subscribe("Task 5", subscriber1); // One subscriber for multiple task types
//
//        // Submit tasks
//        for (int i = 0; i < 10; i++) {
//            executor.submit(new Task("Task " + i, 1000));
//        }
    }
}
