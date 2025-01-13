package org.taskrunner;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Logger {
    private static Logger instance;
    private JTextPane outputPane;

    private Logger() {}

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void setOutputPane(JTextPane pane) {
        this.outputPane = pane;
    }

    public synchronized void log(String message, Color color) {
        if (outputPane == null) {
            System.out.println(message);
            return;
        }
        StyledDocument doc = outputPane.getStyledDocument();
        Style style = outputPane.addStyle("ConsoleStyle", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputPane.setCaretPosition(doc.getLength());
    }
}
