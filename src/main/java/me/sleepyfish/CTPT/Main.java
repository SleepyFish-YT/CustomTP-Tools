package me.sleepyfish.CTPT;

import me.sleepyfish.CTPT.ui.Interface;
import me.sleepyfish.CTPT.ui.ToolManager;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.LogUtils;
import me.sleepyfish.CTPT.utils.ProjectUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;

import java.time.LocalTime;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is the main class of CTP-T
 */
public class Main {

    public static LocalTime startTime;
    public static Interface ui;
    public static ToolManager tools = new ToolManager();

    public static void main(String[] args) {

        // saving start time
        Main.startTime = LocalTime.now();

        // creating default directories and log file
        SettingUtils.changeInputPath(FileUtils.getDefaultPath(), false);
        FileUtils.createDirsAndLogFile();

        LogUtils.log("Starting " + ProjectUtils.title);

        ui = new Interface();
        ui.setVisible(true);
    }
    
}