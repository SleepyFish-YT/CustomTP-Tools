package me.sleepyfish.CTPT.ui;

import me.sleepyfish.CTPT.ui.settings.Setting;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to create new tools
 */
public class Tool {

    public String name;
    public String titleName;
    public String description;
    public final FlowLayout layout;
    public final ArrayList<Setting> settings;
    public int totalModifiedFiles;

    public Tool(final String name, final String titleName, final String description, final FlowLayout layout) {
        this.name = name;
        this.titleName = titleName;
        this.description = description;
        this.layout = layout;

        this.totalModifiedFiles = 0;
        this.settings = new ArrayList<>();
    }

    public Tool(final String name, final String titleName, final String description) {
        this(name, titleName, description, new FlowLayout(FlowLayout.LEFT));
    }

    public void addSetting(final Setting setting) {
        this.settings.add(setting);
    }

    public ArrayList<File> getJsonFiles() {
        return FileUtils.getJsonFilesInDir(SettingUtils.inputDir);
    }

    public void onCheckBoxUpdate() {
    }

    public void onSliderUpdate() {
    }

    public void onTextFieldUpdate() {
    }

    public void onModeSettingUpdate() {
    }

    public void run() {
    }

}