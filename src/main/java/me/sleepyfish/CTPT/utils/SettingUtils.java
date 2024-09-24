package me.sleepyfish.CTPT.utils;

import me.sleepyfish.CTPT.ui.language.Language;
import me.sleepyfish.CTPT.ui.settings.impl.ModeSetting;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to store settings and paths
 */
public final class SettingUtils {

    // Global settings
    public static boolean logConsoleToFile = true;
    public static boolean logEveryFileEach = true;
    public static boolean openOutputPathWhenDone = true;

    // Global paths
    public static File inputDir = new File("input");
    public static File outputPath = new File(inputDir, "output");
    public static File logFile = new File(outputPath, "!latest.log");
    public static File inputConfig = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\CustomTP-Tools\\input.cfg");

    public static void changeInputPath(final String inputPath, final boolean updateConfig) {
        if (updateConfig) {
            SettingUtils.inputConfig.delete();
        }

        SettingUtils.inputDir = new File(inputPath);
        SettingUtils.outputPath = new File(inputPath, "output");
        SettingUtils.logFile = new File(SettingUtils.outputPath, "!latest.log");

        if (!SettingUtils.outputPath.exists()) {
            SettingUtils.outputPath.mkdirs();
            LogUtils.log("Created output folder!");
        }

        if (!SettingUtils.logFile.exists()) {
            try {
                SettingUtils.logFile.createNewFile();
                LogUtils.log("Created new log file in output path!");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.log("Failed to create new log file: " + e.getMessage());
            }
        }

        // try to read input path from config file
        try {
            if (!SettingUtils.inputConfig.exists()) {
                SettingUtils.inputConfig.createNewFile();
            } else {
                try (BufferedReader read = new BufferedReader(new FileReader(inputConfig))) {
                    SettingUtils.inputDir = new File(read.readLine());
                    SettingUtils.outputPath = new File(SettingUtils.inputDir, "output");
                    SettingUtils.logFile = new File(SettingUtils.outputPath, "!latest.log");
                    read.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtils.log("Changed Input Path to: " + SettingUtils.inputDir.getAbsolutePath());
        LogUtils.log("Changed Output Path to: " + SettingUtils.outputPath.getAbsolutePath());

        // after change delete old inputConfig file and create new one with new input path
        try {
            try (BufferedWriter write = new BufferedWriter(new FileWriter(SettingUtils.inputConfig))) {
                write.write(SettingUtils.inputDir.getAbsolutePath());
                write.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author SleepyFish
     *
     * @version Version 1.0
     * @since Version 1.0
     * @implNote This class is used for the languages
     */
    public static final class SettingLanguage {
        public static Language currentLanguage = Language.German;

        public static String[] getLanguages() {
            final ArrayList<String> list = new ArrayList<>();

            for (final Language lang : Language.values())
                list.add(lang.name);

            return list.toArray(new String[0]);
        }

        public static void setCurrentLanguage(final String string) {
            for (final Language language : Language.values()) {
                if (language.toString().equalsIgnoreCase(string))
                    SettingLanguage.currentLanguage = language;
            }
        }
    }

}