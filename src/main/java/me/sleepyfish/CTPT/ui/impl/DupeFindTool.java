package me.sleepyfish.CTPT.ui.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sleepyfish.CTPT.ui.Tool;
import me.sleepyfish.CTPT.ui.settings.impl.SliderSetting;
import me.sleepyfish.CTPT.utils.FileUtils;
import me.sleepyfish.CTPT.utils.LogUtils;
import me.sleepyfish.CTPT.utils.SettingUtils;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to copy all non-duplicate teleport positions from input to output
 */
public final class DupeFindTool extends Tool {

    public static final SliderSetting dupeRadiusXZ = new SliderSetting("Dupe Check Radius Horizontally (X, Z)", 1, 80, 22);
    public static final SliderSetting dupeRadiusY = new SliderSetting("Dupe Check Radius Vertically (Y)", 1, 80, 18);

    public DupeFindTool() {
        super("Dupe Find", "Dupe Find", "This tool is used to find duplicate teleport positions to remove them");

        this.addSetting(this.dupeRadiusXZ);
        this.addSetting(this.dupeRadiusY);
    }

    @Override
    public void run() {
        FileUtils.checkForFolders();

        final int dupeRadiusXZ = this.dupeRadiusXZ.value;
        final int dupeRadiusY = this.dupeRadiusY.value;

        final ArrayList<File> teleportFiles = this.getJsonFiles();
        if (teleportFiles.isEmpty()) {
            LogUtils.setNextError("No teleport files found");
            return;
        }

        final Set<File> alreadyLogged = new HashSet<>();
        final Set<JsonArray> loggedPositions = new HashSet<>();
        final JsonParser parser = new JsonParser();
        final Map<File, JsonObject> parsedFilesCache = new HashMap<>();
        int totalDuplicatePositions = 0;
        final List<File> uniqueFiles = new ArrayList<>();

        // Pre-parse all JSON files and cache them, improving file access and preventing repeated parsing
        for (File file : teleportFiles) {
            JsonObject json = getJsonObject(file, parser);
            if (json != null && json.has("position")) {
                parsedFilesCache.put(file, json);
            } else {
                LogUtils.setNextError("Error: File " + file.getName() + " does not have a 'position' field.");
            }
        }

        // Early exit if no valid files were parsed
        if (parsedFilesCache.isEmpty()) {
            LogUtils.setNextError("Error: No valid teleport files with positions found.");
            return;
        }

        // Iterate over cached parsed files and check for duplicates
        for (File teleportFileA : parsedFilesCache.keySet()) {
            if (alreadyLogged.contains(teleportFileA))
                continue;

            JsonObject jsonA = parsedFilesCache.get(teleportFileA);
            final JsonArray positionA = jsonA.getAsJsonArray("position");

            // Check if positionA has already been logged
            boolean alreadyLoggedPosition = false;
            for (JsonArray loggedPos : loggedPositions) {
                if (this.isPositionDuplicate(positionA, loggedPos, dupeRadiusXZ, dupeRadiusY)) {
                    alreadyLoggedPosition = true;
                    break;
                }
            }

            if (alreadyLoggedPosition)
                continue;

            // Log fileA and add its position to the logged positions set
            if (SettingUtils.logEveryFileEach)
                LogUtils.log("Info: Logging file: " + teleportFileA.getName());

            alreadyLogged.add(teleportFileA);
            loggedPositions.add(positionA);
            uniqueFiles.add(teleportFileA);

            // Now check for duplicates in the remaining files (optimized)
            for (File teleportFileB : parsedFilesCache.keySet()) {
                if (teleportFileA == teleportFileB || alreadyLogged.contains(teleportFileB))
                    continue;

                JsonObject jsonB = parsedFilesCache.get(teleportFileB);
                final JsonArray positionB = jsonB.getAsJsonArray("position");

                if (this.isPositionDuplicate(positionA, positionB, dupeRadiusXZ, dupeRadiusY)) {
                    alreadyLogged.add(teleportFileB);
                    loggedPositions.add(positionB);
                    totalDuplicatePositions++;
                }
            }
        }

        if (!SettingUtils.logEveryFileEach && !teleportFiles.isEmpty())
            LogUtils.log("Info: Found " + totalDuplicatePositions + " duplicate positions.");

        if (alreadyLogged.isEmpty()) {
            LogUtils.log("Info: No duplicate files found.");
        } else {
            LogUtils.log("Info: Total files analyzed: " + teleportFiles.size());

            for (final File file : uniqueFiles) {
                try {
                    if (!FileUtils.copyFile(file, new File(SettingUtils.outputPath, file.getName())))
                        LogUtils.setNextError("Error: Failed to copy file: " + file.getName());
                } catch (Exception e) {
                    LogUtils.setNextError("Error: copying file: " + file.getName());
                }
            }

            // log how many files are duplicates
            LogUtils.setNextError("Info: Duplicate files found: " + (alreadyLogged.size() - uniqueFiles.size()));
        }
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

    // Helper method to check if two position arrays are duplicates
    private boolean isPositionDuplicate(final JsonArray posA, final JsonArray posB, final float toleranceXZ, final float toleranceY) {
        final float posAX = posA.get(0).getAsFloat();
        final float posAY = posA.get(1).getAsFloat();
        final float posAZ = posA.get(2).getAsFloat();
        final float posBX = posB.get(0).getAsFloat();
        final float posBY = posB.get(1).getAsFloat();
        final float posBZ = posB.get(2).getAsFloat();

        final boolean xInRange = posAX >= (posBX - toleranceXZ) && posAX <= (posBX + toleranceXZ);
        final boolean yInRange = posAY >= (posBY - toleranceY) && posAY <= (posBY + toleranceY);
        final boolean zInRange = posAZ >= (posBZ - toleranceXZ) && posAZ <= (posBZ + toleranceXZ);

        return xInRange && yInRange && zInRange;
    }

}