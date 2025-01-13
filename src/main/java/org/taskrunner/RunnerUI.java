package org.taskrunner;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

public class RunnerUI extends JFrame {
    private JPanel panel1;
    private JTextField txtThreadPoolSize;
    private JTextField txtMaxRandomDelay;
    private JList<String> lstTaskTypes;
    private JList<String> lstSubscribers;
    private JButton btnAddTaskType;
    private JButton btnAddSubscriber;
    private JTextField txtInitialTaskCount;
    private JButton btnStartExecution;
    private JButton btnStopExecution;
    private JButton btnForceStopExecution;
    private JTextPane txtConsoleOutput;
    private JButton btnAddTask;
    private JButton btnUnsubscribe;
    private JRadioButton radioPlatformThreads;
    private JRadioButton radioVirtualThreads;

    private TaskExecutor executor;
    private final DefaultListModel<String> taskTypeListModel;
    private final DefaultListModel<String> subscriberListModel;
    private boolean isExecuting = false;
    private final List<Subscriber> pendingSubscribers = new ArrayList<>();

    public RunnerUI() {
        executor = new TaskExecutor();
        taskTypeListModel = new DefaultListModel<>();
        subscriberListModel = new DefaultListModel<>();

        lstTaskTypes.setModel(taskTypeListModel);
        lstSubscribers.setModel(subscriberListModel);

        taskTypeListModel.addElement("TSK_01");
        taskTypeListModel.addElement("TSK_02");

        Logger.getInstance().setOutputPane(txtConsoleOutput);

        addDefaultSubscriber();

        ButtonGroup threadGroup = new ButtonGroup();
        threadGroup.add(radioPlatformThreads);
        threadGroup.add(radioVirtualThreads);

        btnAddTaskType.addActionListener(e -> {
            String taskType = JOptionPane.showInputDialog("Enter Task Type:");
            if (taskType != null && !taskType.trim().isEmpty()) {
                taskTypeListModel.addElement(taskType.trim());
                Logger.getInstance().log("Added task type: " + taskType, Color.BLUE);
            }
        });

        btnAddSubscriber.addActionListener(e -> {
            String subscriberName = JOptionPane.showInputDialog("Enter Subscriber Name:");
            if (subscriberName != null && !subscriberName.trim().isEmpty()) {
                List<String> selectedTasks = selectTasksForSubscriber();
                if (!selectedTasks.isEmpty()) {
                    Subscriber newSubscriber = new Subscriber(subscriberName.trim());
                    for (String taskType : selectedTasks) {
                        newSubscriber.addTaskType(taskType);
                        if (isExecuting) {
                            executor.subscribe(taskType, newSubscriber);
                        } else {
                            pendingSubscribers.add(newSubscriber);
                        }
                    }
                    subscriberListModel.addElement(subscriberName.trim());
                    Logger.getInstance().log("Subscriber " + subscriberName.trim() + " added with tasks: " + String.join(", ", selectedTasks), Color.BLUE);
                } else {
                    Logger.getInstance().log("No tasks selected for subscriber " + subscriberName, Color.RED);
                }
            }
        });


        btnStopExecution.addActionListener(e -> {
            executor.stop();
            isExecuting = false;
            Logger.getInstance().log("Executor will shutdown after execution", Color.RED);
        });

        btnForceStopExecution.addActionListener(e -> {
            executor.forceStop();
            isExecuting = false;
            Logger.getInstance().log("Executor shutdown", Color.RED);
        });

        btnStartExecution.addActionListener(e -> {
            try {
                Random random = new Random();
                int threadPoolSize = Integer.parseInt(txtThreadPoolSize.getText());
                int maxRandomDelay = Integer.parseInt(txtMaxRandomDelay.getText());
                int initialTaskCount = Integer.parseInt(txtInitialTaskCount.getText());

                if (radioVirtualThreads.isSelected()) {
                    executor.setExecutorService(Executors.newVirtualThreadPerTaskExecutor());
                    Logger.getInstance().log("Starting execution with virtual threads for " + initialTaskCount + " tasks.", Color.GREEN);
                } else if (radioPlatformThreads.isSelected()) {
                    executor.setExecutorService(Executors.newFixedThreadPool(threadPoolSize));
                    Logger.getInstance().log("Starting execution with platform threads for " + initialTaskCount + " tasks.", Color.GREEN);
                } else {
                    Logger.getInstance().log("Please select a thread type (Platform or Virtual) before starting execution.", Color.RED);
                    return;
                }

                isExecuting = true;

                for (Subscriber subscriber : pendingSubscribers) {
                    for (String taskType : subscriber.getSubscribedTaskTypes()) {
                        executor.subscribe(taskType, subscriber);
                    }
                }
                pendingSubscribers.clear();

                for (int i = 0; i < initialTaskCount; i++) {
                    int randomDelay = random.nextInt(maxRandomDelay + 1);
                    String randomTaskType = taskTypeListModel.get(random.nextInt(taskTypeListModel.size()));
                    executor.submit(new Task(randomTaskType, "Task No: " + i, randomDelay));
                }
            } catch (NumberFormatException ex) {
                Logger.getInstance().log("Invalid input for thread pool size, max delay, or task count.", Color.RED);
            }
        });


        btnAddTask.addActionListener(e -> {
            String taskName = JOptionPane.showInputDialog("Enter Task Name:");
            if (taskName != null && !taskName.trim().isEmpty()) {
                String selectedTaskType = selectTaskTypeForNewTask();

                if (selectedTaskType != null && !selectedTaskType.trim().isEmpty()) {
                    Random random = new Random();
                    int randomDelay = random.nextInt(Integer.parseInt(txtMaxRandomDelay.getText()) + 1);
                    Task newTask = new Task(selectedTaskType, taskName.trim(), randomDelay);
                    executor.submit(newTask);
                    Logger.getInstance().log("Added task: " + taskName.trim() + " with type: " + selectedTaskType, Color.BLUE);
                } else {
                    Logger.getInstance().log("No task type selected for task " + taskName, Color.RED);
                }
            }
        });

        btnUnsubscribe.addActionListener(e -> {
            String subscriberName = selectSubscriberForUnsubscription();
            if (subscriberName != null && !subscriberName.trim().isEmpty()) {
                String taskType = selectTaskTypeForUnsubscription();
                if (taskType != null && !taskType.trim().isEmpty()) {
                    Subscriber subscriber = findSubscriber(subscriberName);
                    if (subscriber != null) {
                        executor.unsubscribe(taskType, subscriber);
                        Logger.getInstance().log("Subscriber " + subscriberName.trim() + " unsubscribed from task type: " + taskType, Color.RED);
                    }
                }
            }
        });
    }

    private String selectSubscriberForUnsubscription() {
        JComboBox<String> comboBox = new JComboBox<>();
        for (int i = 0; i < subscriberListModel.size(); i++) {
            comboBox.addItem(subscriberListModel.get(i));
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Subscriber to Unsubscribe:"));
        panel.add(comboBox);

        int option = JOptionPane.showConfirmDialog(this, panel, "Select Subscriber", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return (String) comboBox.getSelectedItem();
        }
        return null;
    }

    private String selectTaskTypeForUnsubscription() {
        JComboBox<String> comboBox = new JComboBox<>();
        for (int i = 0; i < taskTypeListModel.size(); i++) {
            comboBox.addItem(taskTypeListModel.get(i));
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Task Type to Unsubscribe:"));
        panel.add(comboBox);

        int option = JOptionPane.showConfirmDialog(this, panel, "Select Task Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return (String) comboBox.getSelectedItem();
        }
        return null;
    }

    private Subscriber findSubscriber(String subscriberName) {
        for (int i = 0; i < subscriberListModel.size(); i++) {
            String name = subscriberListModel.get(i);
            if (name.equals(subscriberName)) {
                return new Subscriber(name);
            }
        }
        return null;
    }


    private void addDefaultSubscriber() {
        String defaultSubscriberName = "DFLT_SUB_01";
        List<String> defaultTasks = Arrays.asList("TSK_01", "TSK_02");
        Subscriber defaultSubscriber = new Subscriber(defaultSubscriberName);
        for (String taskType : defaultTasks) {
            defaultSubscriber.addTaskType(taskType);
            if (isExecuting) {
                executor.subscribe(taskType, defaultSubscriber);
            } else {
                pendingSubscribers.add(defaultSubscriber);
            }
        }
        subscriberListModel.addElement(defaultSubscriberName);
        Logger.getInstance().log("Added default subscriber: " + defaultSubscriberName + " with tasks: " + String.join(", ", defaultTasks), Color.BLUE);
    }

    private String selectTaskTypeForNewTask() {
        JComboBox<String> comboBox = new JComboBox<>();
        for (int i = 0; i < taskTypeListModel.size(); i++) {
            comboBox.addItem(taskTypeListModel.get(i));
        }
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Task Type for New Task:"));
        panel.add(comboBox);
        int option = JOptionPane.showConfirmDialog(this, panel, "Select Task Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return (String) comboBox.getSelectedItem();
        }
        return null;
    }

    private List<String> selectTasksForSubscriber() {
        List<String> selectedTasks = new ArrayList<>();
        JCheckBox[] checkBoxes = new JCheckBox[taskTypeListModel.size()];
        for (int i = 0; i < taskTypeListModel.size(); i++) {
            checkBoxes[i] = new JCheckBox(taskTypeListModel.get(i));
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (JCheckBox checkBox : checkBoxes) {
            panel.add(checkBox);
        }
        int option = JOptionPane.showConfirmDialog(this, panel, "Select Tasks to Subscribe To", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedTasks.add(checkBox.getText());
                }
            }
        }
        return selectedTasks;
    }

    public void showUI() {
        JFrame frame = new JFrame("Task Runner UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel1);
        frame.setSize(600, 500);
        frame.setVisible(true);
    }
}
