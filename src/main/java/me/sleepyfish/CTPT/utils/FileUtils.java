package me.sleepyfish.CTPT.utils;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for file handling
 */
public final class FileUtils {

    public static void createDirsAndLogFile() {
        if (!SettingUtils.inputDir.exists()) {
            SettingUtils.inputDir.mkdirs();
            LogUtils.log("Created input directory: " + SettingUtils.inputDir.getAbsolutePath());
        } else {
            LogUtils.log("Input directory already exists: " + SettingUtils.inputDir.getAbsolutePath());
        }

        if (!SettingUtils.outputPath.exists()) {
            SettingUtils.outputPath.mkdirs();
            LogUtils.log("Created output directory: " + SettingUtils.outputPath.getAbsolutePath());
        } else {
            LogUtils.log("Output directory already exists: " + SettingUtils.outputPath.getAbsolutePath());
        }

        try {
            // final String logFileName = "Date-"+TimeUtils.getCurrentDate().replace("/", "-") + "-Time-" + TimeUtils.getCurrentTime().replace(":", "-");
            // SettingUtils.logFile = new File(SettingUtils.outputPath, logFileName + ".log");
            SettingUtils.logFile = new File(SettingUtils.outputPath, "!latest.log");

            if (SettingUtils.logFile.exists())
                SettingUtils.logFile.delete();

            SettingUtils.logFile.createNewFile();
            LogUtils.log("Created log file: " + SettingUtils.logFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<File> getJsonFilesInDir(final File dir) {
        if (dir == null)
            return null;

        if (!dir.isDirectory())
            return null;

        final ArrayList<File> files = new ArrayList<>();

        for (final File file : Objects.requireNonNull(dir.listFiles())) {
            if (!FileUtils.isFileEmpty(file))
                if (FileUtils.isFileJson(file))
                    files.add(file);
        }

        return files;
    }

    /**
     * @return The selected folder path as string
     * @since 2022-06-01
     */
    public static String chooseFolder() {
        final JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setVisible(false);

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setSize(900, 500);
        final int result = fileChooser.showOpenDialog(frame);
        frame.dispose();

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static boolean isFileEmpty(final File file) {
        return file.getTotalSpace() == 0;
    }

    public static boolean isFileJson(final File file) {
        return file.getName().endsWith(".json");
    }

    public static void logToFile(final String msg) {
        if (SettingUtils.logFile.exists()) {
            try {
                final FileWriter fw = new FileWriter(SettingUtils.logFile, true);
                fw.write(msg + "\n");
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDefaultPath() {
        final String windowsDesktop = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop";
        return windowsDesktop + "\\CustomTP-Tools";
    }

    public static boolean copyFile(final File file, final File file2) {
        try {
            Files.copy(file.toPath(), file2.toPath());
            LogUtils.log("File copied: " + file.getName());
            return true;
        } catch (Exception e) {
            LogUtils.setNextError("Error: Failed to copy file: " + e.getMessage());
            return false;
        }
    }

    public static boolean copyFileToOutput(final File file, final File outputPath) {
        return copyFile(file, new File(outputPath, file.getName()));
    }

    public static void checkForFolders() {
        if (!SettingUtils.inputDir.exists()) {
            SettingUtils.inputDir.mkdirs();
            LogUtils.log("Created input folder: " + SettingUtils.inputDir.getAbsolutePath());
        }

        if (!SettingUtils.outputPath.exists()) {
            SettingUtils.outputPath.mkdirs();
            LogUtils.log("Created output folder: " + SettingUtils.outputPath.getAbsolutePath());
        }
    }

}