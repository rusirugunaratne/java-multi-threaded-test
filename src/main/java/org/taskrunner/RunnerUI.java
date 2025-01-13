package org.taskrunner;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RunnerUI extends JFrame {
    private JPanel panel1;
    private JTextField txtThreadPoolSize;
    private JTextField txtMaxRandomDelay;
    private JList<String> lstTaskTypes;
    private JList lstSubscribers;
    private JButton btnAddTaskType;
    private JButton btnAddSubscriber;
    private JTextField txtInitialTaskCount;
    private JButton btnStartExecution;
    private JButton btnStopExecution;
    private JButton btnForceStopExecution;
    private JTextPane txtConsoleOutput;

    Executor executor;
    private final DefaultListModel<String> taskTypeListModel;

    public RunnerUI() {
        taskTypeListModel = new DefaultListModel<>();
        lstTaskTypes.setModel(taskTypeListModel);
        taskTypeListModel.addElement("TSK_01");
        taskTypeListModel.addElement("TSK_02");

        Logger.getInstance().setOutputPane(txtConsoleOutput);

        btnAddTaskType.addActionListener(e -> {
            String taskType = JOptionPane.showInputDialog("Enter Task Type:");
            if (taskType != null && !taskType.trim().isEmpty()) {
                taskTypeListModel.addElement(taskType.trim());
                Logger.getInstance().log("Added task type: " + taskType, Color.BLUE);
            }
        });

        btnStopExecution.addActionListener(e -> {
            executor.stop();
            Logger.getInstance().log("Executor will shutdown after execution", Color.RED);
        });

        btnForceStopExecution.addActionListener(e -> {
            executor.forceStop();
            Logger.getInstance().log("Executor shutdown", Color.RED);
        });

        btnStartExecution.addActionListener(e -> {
            try {
                Random random = new Random();
                int threadPoolSize = Integer.parseInt(txtThreadPoolSize.getText());
                int maxRandomDelay = Integer.parseInt(txtMaxRandomDelay.getText());
                int initialTaskCount = Integer.parseInt(txtInitialTaskCount.getText());

                executor = new Executor(threadPoolSize);

                Logger.getInstance().log("Starting execution with " + initialTaskCount + " tasks.", Color.GREEN);

                for (int i = 0; i < initialTaskCount; i++) {
                    int randomDelay = random.nextInt(maxRandomDelay + 1);
                    String randomTaskType = taskTypeListModel.get(random.nextInt(taskTypeListModel.size()));
                    executor.submit(new Task(randomTaskType, randomDelay));
                }
            } catch (NumberFormatException ex) {
                Logger.getInstance().log("Invalid input for thread pool size, max delay, or task count.", Color.RED);
            }
        });
    }

    public void showUI() {
        JFrame frame = new JFrame("Task Runner UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel1);
        frame.setSize(600, 500);
        frame.setVisible(true);
    }
}
