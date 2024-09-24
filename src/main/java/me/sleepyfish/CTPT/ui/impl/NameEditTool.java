package me.sleepyfish.CTPT.ui.impl;

import com.google.gson.*;
import me.sleepyfish.CTPT.ui.Tool;
import me.sleepyfish.CTPT.ui.settings.impl.CheckBoxSetting;
import me.sleepyfish.CTPT.ui.settings.impl.SliderSetting;
import me.sleepyfish.CTPT.ui.settings.impl.TextSetting;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.LogUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * @author SleepyFish
 *
 * @version Version 1.2
 * @since Version 1.0
 * @implNote This class is used to edit the name json field inside your teleport files to that teleportFile name
 */
public final class NameEditTool extends Tool {

    public static final CheckBoxSetting formatNamesCorrectly = new CheckBoxSetting("Format Names Correctly", true);
    public static final CheckBoxSetting renameNames = new CheckBoxSetting("Rename Names (Save with Enter)", false);
    public static final TextSetting newNameOfNames = new TextSetting("New Name", "");
    public static final CheckBoxSetting renameDescriptions = new CheckBoxSetting("Rename Descriptions (Save with Enter)", true);
    public static final TextSetting newDescriptionName = new TextSetting("New Description", "");
    public static final CheckBoxSetting roundPositions = new CheckBoxSetting("Round Positions", false);
    public static final SliderSetting roundPositionsDecimalPlaces = new SliderSetting("Round Positions Decimal Places (0.01)", 0, 4, 2);

    public NameEditTool() {
        super("Name Editor", "Name Edit", "This tool is used to edit the json fields of your teleport files");

        this.addSetting(this.formatNamesCorrectly);
        this.addSetting(this.renameNames);
        this.addSetting(this.newNameOfNames);
        this.addSetting(this.renameDescriptions);
        this.addSetting(this.newDescriptionName);
        this.addSetting(this.roundPositions);
        this.addSetting(this.roundPositionsDecimalPlaces);
    }

    @Override
    public void run() {
        FileUtils.checkForFolders();

        final boolean formatNames = this.formatNamesCorrectly.state;
        final boolean renameName = this.renameNames.state;
        final boolean renameDesc = this.renameDescriptions.state;
        final boolean roundPositions = this.roundPositions.state;

        final ArrayList<File> teleportFiles = this.getJsonFiles();
        if (teleportFiles.isEmpty()) {
            LogUtils.setNextError("Error: No teleport files found");
            return;
        }

        for (final File teleportFile : teleportFiles) {
            String originalName = this.getFileName(teleportFile);
            String name = originalName;

            // Format names only once
            if (formatNames) {
                name = formatFileName(name);
            }

            if (!FileUtils.copyFileToOutput(teleportFile, SettingUtils.outputPath)) {
                LogUtils.setNextError("Error: Failed to save teleportFile to output folder: " + originalName);
                continue;
            }

            final File copiedFile = new File(SettingUtils.outputPath, teleportFile.getName());

            // Read and process the JSON file
            try (FileReader reader = new FileReader(copiedFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                boolean modified = false;

                // Rename the 'name' field
                if (renameName && jsonObject.has("name")) {
                    jsonObject.addProperty("name", this.newNameOfNames.value);
                    modified = true;
                    logFileModification("Cleared name field", name);
                } else if (jsonObject.has("name")) {
                    jsonObject.addProperty("name", name);
                    modified = true;
                    logFileModification("Updated name field", name);
                }

                // Rename the 'description' field
                if (renameDesc && jsonObject.has("description")) {
                    jsonObject.addProperty("description", this.newDescriptionName.value);
                    modified = true;
                    logFileModification("Cleared description field", name);
                }

                // Round the 'position' field
                if (roundPositions && jsonObject.has("position") && jsonObject.get("position").isJsonArray()) {
                    JsonArray roundedPosition = roundPosition(jsonObject.getAsJsonArray("position"), roundPositionsDecimalPlaces.value);
                    if (roundedPosition != null) {
                        jsonObject.add("position", roundedPosition);
                        modified = true;
                        logFileModification("Rounded position field", name);
                    }
                }

                // Write back the modified JSON if any changes occurred
                if (modified) {
                    try (FileWriter writer = new FileWriter(copiedFile)) {
                        new Gson().toJson(jsonObject, writer);
                    }
                    this.totalModifiedFiles++;
                }
            } catch (Exception e) {
                LogUtils.setNextError("Error: processing teleportFile " + originalName + ": " + e.getMessage());
            }
        }

        LogUtils.setNextError("Processed: " + this.totalModifiedFiles + " teleportFiles");
    }

    /**
     * Helper method to format file names by replacing unwanted characters.
     */
    private String formatFileName(String name) {
        return name.replaceAll("[ ()/&%]", "_");
    }

    /**
     * Helper method to log file modifications.
     */
    private void logFileModification(String message, String fileName) {
        if (SettingUtils.logEveryFileEach) {
            LogUtils.log("Info: " + message + " from teleportFile: " + fileName);
        }
    }

    /**
     * Helper method to round position coordinates to 2 decimal places.
     */
    private JsonArray roundPosition(JsonArray position, final int precision) {
        if (position.size() < 3) return null;

        try {
            // Calculate the rounding factor based on the precision
            float factor = (float) Math.pow(10, precision);

            // Extract the coordinates
            float x = position.get(0).getAsFloat();
            float y = position.get(1).getAsFloat();
            float z = position.get(2).getAsFloat();

            // Create a new JsonArray to store the rounded values
            JsonArray roundedPosition = new JsonArray();

            // Round each coordinate to the given precision
            roundedPosition.add(Math.round(x * factor) / factor);
            roundedPosition.add(Math.round(y * factor) / factor);
            roundedPosition.add(Math.round(z * factor) / factor);

            return roundedPosition;
        } catch (Exception e) {
            LogUtils.setNextError("Error: rounding position data: " + e.getMessage());
            return null;
        }
    }

    public String getFileName(final File file) {
        return file.getName().replace(".json", "");
    }

}