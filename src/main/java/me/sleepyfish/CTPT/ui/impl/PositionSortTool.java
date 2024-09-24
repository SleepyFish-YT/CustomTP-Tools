package me.sleepyfish.CTPT.ui.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sleepyfish.CTPT.ui.Tool;
import me.sleepyfish.CTPT.ui.settings.impl.CheckBoxSetting;
import me.sleepyfish.CTPT.ui.settings.impl.ModeSetting;
import me.sleepyfish.CTPT.ui.settings.impl.TextSetting;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.LogUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;
import me.sleepyfish.CTPT.utils.Vec3F;

import java.io.*;
import java.util.ArrayList;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to sort the positions of your teleport files
 */
public final class PositionSortTool extends Tool {

    public static final ModeSetting sortMode = new ModeSetting("Sort Mode", "Normal Positions", new String[]{"Normal Positions", "Rounded Positions"});
    public static final CheckBoxSetting customName = new CheckBoxSetting("Custom Name (Save with Enter) (% is replaced with number) ($ is replaced with file name)", true);
    public static final TextSetting customNameValue = new TextSetting("Custom Name", "%-sort-$");

    public PositionSortTool() {
        super("Position Sorter", "Position Sort", "This tool is used to sort the positions of your teleports (low pos to high pos)");

        this.addSetting(this.sortMode);
        this.addSetting(this.customName);
        this.addSetting(this.customNameValue);
    }

    @Override
    public void run() {
        FileUtils.checkForFolders();

        final boolean shouldRoundPositions = this.sortMode.currentMode.equals("Rounded Positions");

        final ArrayList<File> teleportFiles = this.getJsonFiles();
        if (teleportFiles.isEmpty()) {
            LogUtils.log("No teleport files found");
            return;
        }

        final JsonParser parser = new JsonParser();
        ArrayList<FilePosition> filePositions = new ArrayList<>();

        // Read each file and extract positions
        for (final File teleportFile : teleportFiles) {
            JsonObject jsonA = getJsonObject(teleportFile, parser);

            if (jsonA == null || !jsonA.has("position")) {
                LogUtils.setNextError("Error: File " + teleportFile.getName() + " does not have a 'position' field.");
                continue;
            }

            final JsonArray positionField = jsonA.getAsJsonArray("position");
            Vec3F posVec = getPositionToVec(positionField);
            filePositions.add(new FilePosition(teleportFile, posVec));

            if (SettingUtils.logEveryFileEach)
                LogUtils.log("Info: Added position from file: " + teleportFile.getName());
        }

        if (shouldRoundPositions) {
            filePositions.sort((a, b) -> {
                int roundXComparison = Integer.compare(Math.round(a.position.x), Math.round(b.position.x));
                if (roundXComparison != 0) return roundXComparison;

                int roundYComparison = Integer.compare(Math.round(a.position.y), Math.round(b.position.y));
                if (roundYComparison != 0) return roundYComparison;

                return Integer.compare(Math.round(a.position.z), Math.round(b.position.z));
            });
        } else {
            filePositions.sort((a, b) -> {
                int xComparison = Float.compare(a.position.x, b.position.x);
                if (xComparison != 0) return xComparison;

                int yComparison = Float.compare(a.position.y, b.position.y);
                if (yComparison != 0) return yComparison;

                return Float.compare(a.position.z, b.position.z);
            });
        }

        // Save sorted files with new names in the output folder
        this.saveSortedFiles(filePositions);
    }

    // Helper method to parse JSON from a file
    private JsonObject getJsonObject(final File file, final JsonParser parser) {
        try (final FileReader reader = new FileReader(file)) {
            final JsonElement element = parser.parse(reader);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
            LogUtils.setNextError("Error: reading/parsing file: " + file.getName());
        }
        return null;
    }

    // Helper method to convert position JsonArray to Vec3F
    private Vec3F getPositionToVec(final JsonArray pos) {
        final float posX = pos.get(0).getAsFloat();
        final float posY = pos.get(1).getAsFloat();
        final float posZ = pos.get(2).getAsFloat();
        return new Vec3F(posX, posY, posZ);
    }

    // Method to save sorted files with new names
    private void saveSortedFiles(final ArrayList<FilePosition> filePositions) {
        for (int i = 0; i < filePositions.size(); i++) {
            final FilePosition filePosition = filePositions.get(i);
            final File originalFile = filePosition.file;
            final String newFileName = customNameValue.value.replace("%", String.valueOf(i)).replace("$", originalFile.getName().replace(".json", ""));
            final File newFile = new File(SettingUtils.outputPath, newFileName + ".json");
            FileUtils.copyFile(originalFile, newFile);
        }
    }

    private static class FilePosition {
        final File file;
        final Vec3F position;

        public FilePosition(final File file, final Vec3F position) {
            this.file = file;
            this.position = position;
        }
    }

}