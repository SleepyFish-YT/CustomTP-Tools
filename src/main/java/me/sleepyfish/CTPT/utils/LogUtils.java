package me.sleepyfish.CTPT.utils;

import javax.swing.JOptionPane;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for logging calls from CTP-T
 */
public final class LogUtils {

    public static final String prefix = "[" + ProjectUtils.nameShort + "]: ";
    public static String currentError = "";

    public static void log(String msg) {
        final String message = prefix + msg;

        if (SettingUtils.logConsoleToFile)
            FileUtils.logToFile(message);

        System.out.println(message);
    }

    public static void showPopup(final String message) {
        LogUtils.log("Popup: " + message);
        JOptionPane.showMessageDialog(null, message, ProjectUtils.titleShort, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void setNextError(final String msg) {
        LogUtils.currentError = msg;
    }
    
}