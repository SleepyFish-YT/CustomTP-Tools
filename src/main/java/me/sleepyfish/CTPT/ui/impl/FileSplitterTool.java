package me.sleepyfish.CTPT.ui.impl;

import me.sleepyfish.CTPT.ui.Tool;
import me.sleepyfish.CTPT.ui.settings.impl.CheckBoxSetting;
import me.sleepyfish.CTPT.ui.settings.impl.SliderSetting;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.LogUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;

import java.io.File;
import java.util.ArrayList;

public final class FileSplitterTool extends Tool {

    public static final CheckBoxSetting splitFilesIntoFolders = new CheckBoxSetting("Split Files Into Folders", true);
    public static final CheckBoxSetting splitFilesEvenly = new CheckBoxSetting("Split Files Evenly", true);
    public static final SliderSetting filesPerFolder = new SliderSetting("Files Per Folder", 200, 3000, 1200);

    public FileSplitterTool() {
        super("File Splitter", "File Splitter", "This tool is used to split your files into multiple folders");

        this.addSetting(this.splitFilesIntoFolders);
        this.addSetting(this.filesPerFolder);
        this.addSetting(this.splitFilesEvenly);
    }

    @Override
    public void run() {
        FileUtils.checkForFolders();

        final boolean splitFiles = this.splitFilesIntoFolders.state;
        final int filesPerFolder = this.filesPerFolder.value;
        final boolean splitFilesEven = this.splitFilesEvenly.state;

        final ArrayList<File> teleportFiles = this.getJsonFiles();
        if (teleportFiles.isEmpty()) {
            LogUtils.log("No teleport files found");
            return;
        }

        // Split files into folders if the option is enabled
        if (splitFiles && teleportFiles.size() > filesPerFolder) {
            if (splitFilesEven) {
                int totalFiles = teleportFiles.size();
                int folderCount = (int) Math.ceil((double) totalFiles / filesPerFolder);
                int filesPerFolderPerfect = (int) Math.ceil((double) totalFiles / folderCount);

                LogUtils.log("Info: Splitting files into " + folderCount + " folders with approx " + filesPerFolderPerfect + " files each.");

                int fileCounter = 0;
                int currentFolderIndex = 1;
                File currentSplitFolder = new File(SettingUtils.outputPath, "split" + currentFolderIndex);
                currentSplitFolder.mkdirs();
                LogUtils.log("Info: Created folder: " + currentSplitFolder.getAbsolutePath());

                for (final File file : teleportFiles) {
                    if (fileCounter == filesPerFolderPerfect && currentFolderIndex < folderCount) {
                        currentFolderIndex++;
                        fileCounter = 0;
                        currentSplitFolder = new File(SettingUtils.outputPath, "split" + currentFolderIndex);
                        currentSplitFolder.mkdirs();
                        LogUtils.log("Info: Created folder: " + currentSplitFolder.getAbsolutePath());
                    }

                    if (!FileUtils.copyFileToOutput(file, new File(currentSplitFolder, file.getName()))) {
                        LogUtils.setNextError("Error: Failed to copy file: " + file.getName());
                    } else {
                        fileCounter++;
                    }
                }
            } else {
                int folderCount = 1;
                int fileCounter = 0;
                File currentSplitFolder = new File(SettingUtils.outputPath, "split" + folderCount);
                currentSplitFolder.mkdirs();
                LogUtils.log("Info: Created folder: " + currentSplitFolder.getAbsolutePath());

                for (final File file : teleportFiles) {
                    if (fileCounter == filesPerFolder) {
                        folderCount++;
                        fileCounter = 0;
                        currentSplitFolder = new File(SettingUtils.outputPath, "split" + folderCount);
                        currentSplitFolder.mkdirs();
                        LogUtils.log("Info: Created folder: " + currentSplitFolder.getAbsolutePath());
                    }

                    if (!FileUtils.copyFile(file, new File(currentSplitFolder, file.getName())))
                        LogUtils.setNextError("Error: Failed to copy file: " + file.getName());

                    fileCounter++;
                }
            }
        }
    }


}