package me.sleepyfish.TPDupeChecker;

import me.sleepyfish.TPDupeChecker.libs.gson.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SleepyFish
 * @since 2022-06-01
 * @version 1.0
 * @implNote Takes the file name without the ".json" and puts it in the json "name" field
 * @see Gson for more information about the json stuff
 */
public class Functions {

    public static final Functions instance = new Functions();

    private final String desktopPath = System.getProperty("user.home") + "\\Desktop";

    // Custom teleport folder path (this folder is used to get the un-modified custom teleport files)
    String inputFolderPath = this.desktopPath + "\\korepi\\teleport";

    // Output folder path (this folder is used to save the modified custom teleport files)
    String outputFolderPath = this.inputFolderPath + "\\output";

    // Show each file in the logging process (used for debugging)
    boolean showEachFile = false;

    // File logging (used for debugging)
    boolean fileLogging = true;

    // Open output folder when finished
    boolean openOutputFolder = true;

    // Tolerance range for comparing duplicate positions
    float maxDifference = 50.0F;

    // Files
    File inputFolder = new File(inputFolderPath);
    File outputFolder = new File(outputFolderPath);
    File logFile = new File(outputFolder + "/!latest.log");

    public void start() {
        final int startTime = LocalTime.now().toSecondOfDay();

        this.log(Variables.title);
        this.log("");

        if (this.outputFolder == null || this.outputFolderPath.isEmpty()) {
            this.outputFolder = new File(this.inputFolderPath + "/output");
            this.outputFolderPath = this.outputFolder.getAbsolutePath();
            this.log("Info: Output Folder Path not set, using: " + this.outputFolderPath);
        }

        if (!this.inputFolder.exists()) {
            this.log("Error: Input Folder Path does not exist!");
            this.log("Info: Please make sure the path is correct!");
            return;
        }

        if (!this.outputFolder.exists()) {
            this.log("Info: Output Folder Path does not exist, creating output folder!");
            this.outputFolder.mkdirs();
        }

        if (this.inputFolderPath.equals(this.outputFolderPath)) {
            final String path = this.inputFolder.getAbsolutePath() + "/output";
            this.log("Error: Input Folder Path and Output Folder Path are the same!");
            this.log("Info: Setting Output Folder Path to: " + path);
            this.outputFolderPath = path;
            this.outputFolder = new File(this.outputFolderPath);
            this.outputFolder.mkdirs();
        }

        // Set up file for logging
        if (this.fileLogging) {
            this.logFile = new File(this.outputFolder + "/!latest.log");

            if (this.logFile.exists()) {
                this.logFile.delete();

                try {
                    this.logFile.createNewFile();
                } catch (Exception e) {
                    this.log("Error: Could not create this.log file!");
                }
            }
        }

        this.log("");
        this.log("Info: Input Folder Path: " + this.inputFolder.getAbsolutePath());
        this.log("Info: Output Folder Path: " + this.outputFolder.getAbsolutePath());
        this.log("");

        if (this.inputFolder.listFiles() == null) {
            this.log("Error: Input Folder is empty!");
            return;
        }

        File[] allFiles = inputFolder.listFiles();
        if (allFiles == null) {
            this.log("Error: No files found in input folder.");
            return;
        }

        List<File> jsonFiles = new ArrayList<>();
        for (File file : allFiles) {
            if (isJson(file) && !isEmpty(file)) {
                if (this.showEachFile)
                    this.log("Info: Added Valid file: " + file.getName());
                
                jsonFiles.add(file);
            }
        }
        
        if (!this.showEachFile && !jsonFiles.isEmpty()) {
            this.log("Info: Found " + jsonFiles.size() + " valid files.");
        }

        // Step 6: Loop through the .json files and analyze
        Set<File> alreadyLogged = new HashSet<>();  // Set to track files that are already logged as duplicates
        Set<JsonArray> loggedPositions = new HashSet<>();  // Track logged positions
        JsonParser parser = new JsonParser();
        int totalDuplicatePositions = 0;  // Counter to track total duplicate positions found
        List<File> uniqueFiles = new ArrayList<>();  // List to track unique files

        for (int i = 0; i < jsonFiles.size(); i++) {
            File fileA = jsonFiles.get(i);
            JsonObject jsonA = getJsonObject(fileA, parser);

            if (jsonA == null || !jsonA.has("position")) {
                this.log("Error: File " + fileA.getName() + " does not have a 'position' field.");
                continue;
            }

            JsonArray positionA = jsonA.getAsJsonArray("position");

            // If this position has already been logged, skip this file
            boolean alreadyLoggedPosition = false;
            for (JsonArray loggedPos : loggedPositions) {
                if (isPositionDuplicate(positionA, loggedPos, maxDifference)) {
                    alreadyLoggedPosition = true;
                    break;
                }
            }

            if (alreadyLoggedPosition) {
                continue;  // Skip logging this file if its position is already logged
            }

            // Log fileA and add its position to the logged positions set
            if (this.showEachFile)
                this.log("Info: Logging file: " + fileA.getName());

            alreadyLogged.add(fileA);
            loggedPositions.add(positionA);
            uniqueFiles.add(fileA);  // Add the unique file to the list

            // Now check for duplicates in the remaining files
            for (int j = i + 1; j < jsonFiles.size(); j++) {
                File fileB = jsonFiles.get(j);
                JsonObject jsonB = getJsonObject(fileB, parser);

                if (jsonB == null || !jsonB.has("position")) {
                    continue;
                }

                JsonArray positionB = jsonB.getAsJsonArray("position");

                // If positionB is a duplicate of positionA, mark it as already logged (but don't this.log it)
                if (isPositionDuplicate(positionA, positionB, maxDifference)) {
                    alreadyLogged.add(fileB);  // Mark as already logged
                    loggedPositions.add(positionB);  // Mark this position as logged
                    totalDuplicatePositions++;  // Increment duplicate position counter
                }
            }
        }

        if (!this.showEachFile && !jsonFiles.isEmpty()) {
            this.log("Info: Found " + totalDuplicatePositions + " duplicate positions.");
        }

        // Step 9: Final summary
        if (alreadyLogged.isEmpty()) {
            this.log("Info: No duplicate files found.");
        } else {
            this.log("");
            this.log("Info: Total unique positions logged: " + loggedPositions.size());
            this.log("Info: Total duplicate positions found: " + totalDuplicatePositions);  // Log total duplicates found
            this.log("Info: Total files analyzed: " + jsonFiles.size());

            for (final File file : uniqueFiles) {
                try {
                    Files.copy(file.toPath(), new File(outputFolder, file.getName()).toPath());
                } catch (Exception e) {
                    this.log("Error: copying file: " + file.getName());
                }
            }
        }

        if (this.fileLogging) {
            this.log("Info: Log file saved to: " + this.logFile.getAbsolutePath() + "!");
            this.log("Info: Logging started at: " + LocalTime.now());
        }

        if (this.openOutputFolder) {
            this.log("");

            try {
                Desktop.getDesktop().open(this.outputFolder);
                this.log("Info: Opened output folder: " + this.outputFolder.getAbsolutePath() + "!");
            } catch (Exception e) {
                this.log("Error: Failed to open output folder: " + e.getCause().getMessage());
            }
        }

        this.log("");
        this.log("Tool by " + Variables.author);

        if (this.fileLogging) {
            this.log("Info: Logging finished at: " + LocalTime.now());
        }

        this.log("Info: Finished in " + (LocalTime.now().toSecondOfDay() - startTime) + " seconds.");
    }

    // Helper method to parse JSON from a file
    private JsonObject getJsonObject(File file, JsonParser parser) {
        try (FileReader reader = new FileReader(file)) {
            JsonElement element = parser.parse(reader);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
            this.log("Error reading/parsing file: " + file.getName());
        }
        return null;
    }

    // Helper method to check if two position arrays are duplicates
    private boolean isPositionDuplicate(JsonArray posA, JsonArray posB, float tolerance) {
        try {
            // Extracting x, y, z coordinates from posA
            float xA = posA.get(0).getAsFloat();
            float yA = posA.get(1).getAsFloat();
            float zA = posA.get(2).getAsFloat();

            // Extracting x, y, z coordinates from posB
            float xB = posB.get(0).getAsFloat();
            float yB = posB.get(1).getAsFloat();
            float zB = posB.get(2).getAsFloat();

            // Check if all coordinates (x, y, z) are within the given tolerance range
            boolean xInRange = xA >= (xB - tolerance) && xA <= (xB + tolerance);
            boolean yInRange = yA >= (yB - tolerance) && yA <= (yB + tolerance);
            boolean zInRange = zA >= (zB - tolerance) && zA <= (zB + tolerance);

            // Return true if all three coordinates are within the tolerance range
            return xInRange && yInRange && zInRange;
        } catch (Exception e) {
            this.log("Error comparing positions.");
        }
        return false;
    }


    /**
     * @since 2022-06-01
     * @param file The file to check if it's a json file
     * @return True if the file is a json file
     */
    public boolean isJson(final File file) {
        return file.getName().endsWith(".json");
    }

    /**
     * @since 2022-06-01
     * @param file The file to check if it's empty
     * @return True if the file is empty
     */
    public boolean isEmpty(final File file) {
        return file.getTotalSpace() == 0;
    }

    /**
     * @since 2022-06-01
     * @return The selected folder path as string
     */
    public String chooseFolder() {
        final JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setVisible(false);

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Variables.title);
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

    /**
     * @since 2022-06-01
     * @param msg The message to print to the console
     */
    public void log(final String msg) {
        if (this.fileLogging) {
            if (this.logFile != null) {
                try (final FileWriter writer = new FileWriter(this.logFile, true)) {
                    writer.write(msg + "\n");
                } catch (Exception ignored) {
                }
            }
        }

        if (msg.isEmpty()) {
            System.out.println("===================================================================");
            return;
        }

        final LocalTime currentTime = LocalTime.now();
        try {
            final String currentTimeFormatted = currentTime.toString().substring(0, 8);
            System.out.println("[" + currentTimeFormatted + "]: " + msg);
        } catch (StringIndexOutOfBoundsException ignored) {
            final String currentTimeFormatted = "??:??:??";
            System.out.println("[" + currentTimeFormatted + "]: " + msg);
        }
    }
}
