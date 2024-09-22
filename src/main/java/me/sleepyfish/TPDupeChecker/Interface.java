package me.sleepyfish.TPDupeChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public final class Interface extends JFrame {

    public static final Interface instance = new Interface();

    // GUI Components
    private final JTextField inputFolderPathField;
    private final JTextField outputFolderPathField;
    private final JCheckBox showEachFileCheckBox;
    private final JCheckBox openOutputFolderCheckBox;
    private final JCheckBox fileLoggingCheckBox;
    private final JCheckBox splitFilesCheckbox;
    private final JLabel rangeSliderLabel;
    private final JLabel filesPerFolderLabel;
    private final JSlider rangeSlider;
    private final JSlider filesPerFolderSlider;
    private final JButton startButton;

    public Interface() {
        setTitle(Variables.title);
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        // Input Folder Path selection
        JLabel inputFolderPathLabel = new JLabel("Input Folder Path:");
        inputFolderPathField = new JTextField(Functions.instance.inputFolderPath);
        inputFolderPathField.setEditable(false);
        inputFolderPathField.addMouseListener(new FolderSelectInputListener(inputFolderPathField));
        panel.add(inputFolderPathLabel);
        panel.add(inputFolderPathField);

        // Output Folder Path selection
        JLabel outputFolderPathLabel = new JLabel("Output Folder Path:");
        outputFolderPathField = new JTextField(Functions.instance.outputFolderPath);
        outputFolderPathField.setEditable(false);
        outputFolderPathField.addMouseListener(new FolderSelectOutputListener(outputFolderPathField));
        panel.add(outputFolderPathLabel);
        panel.add(outputFolderPathField);

        // Enable File Logging
        JLabel fileLoggingLabel = new JLabel("Enable File Logging:");
        fileLoggingCheckBox = new JCheckBox();
        fileLoggingCheckBox.setSelected(Functions.instance.fileLogging);
        fileLoggingCheckBox.addItemListener(new AutoSaveItemListener());
        panel.add(fileLoggingLabel);
        panel.add(fileLoggingCheckBox);

        // Show Each File
        JLabel showEachFileLabel = new JLabel("Show Each File in logger:");
        showEachFileCheckBox = new JCheckBox();
        showEachFileCheckBox.setSelected(Functions.instance.showEachFile);
        showEachFileCheckBox.addItemListener(new AutoSaveItemListener());
        panel.add(showEachFileLabel);
        panel.add(showEachFileCheckBox);

        // Open Output Folder When Finished
        JLabel openOutputFolderLabel = new JLabel("Open Output Folder When Finished:");
        openOutputFolderCheckBox = new JCheckBox();
        openOutputFolderCheckBox.setSelected(Functions.instance.openOutputFolder);
        openOutputFolderCheckBox.addItemListener(new AutoSaveItemListener());
        panel.add(openOutputFolderLabel);
        panel.add(openOutputFolderCheckBox);

        // Slider for range/tolerance
        rangeSliderLabel = new JLabel("Duplicate Position Tolerance (" + (int) Functions.instance.maxDifference + "m):");
        rangeSlider = new JSlider(JSlider.HORIZONTAL, 1, 99, (int) Functions.instance.maxDifference);
        rangeSlider.setMajorTickSpacing(10);
        rangeSlider.setMinorTickSpacing(1);
        rangeSlider.setPaintTicks(false);
        rangeSlider.addChangeListener(e -> updateRange());

        if (rangeSlider.getValue() == 0) {
            rangeSlider.setValue(1);
        }

        // Label to show current slider value
        panel.add(rangeSliderLabel);
        panel.add(rangeSlider);

        add(panel, BorderLayout.CENTER);

        // Split Files boolean
        JLabel splitFilesLabel = new JLabel("Split Files Into Folders:");
        splitFilesCheckbox = new JCheckBox();
        splitFilesCheckbox.setSelected(Functions.instance.splitFilesIntoFolders);
        splitFilesCheckbox.addItemListener(new AutoSaveItemListener());
        panel.add(splitFilesLabel);
        panel.add(splitFilesCheckbox);

        // Files Per Folder
        filesPerFolderLabel = new JLabel("Files Per Folder: 1000");
        filesPerFolderSlider = new JSlider(JSlider.HORIZONTAL, 200, 1000, Functions.instance.filesPerFolder);
        filesPerFolderSlider.setMajorTickSpacing(10);
        filesPerFolderSlider.setMinorTickSpacing(1);
        filesPerFolderSlider.setPaintTicks(false);
        filesPerFolderSlider.addChangeListener(e -> saveSettings());
        filesPerFolderSlider.addChangeListener(e -> updateRange());
        panel.add(filesPerFolderLabel);
        panel.add(filesPerFolderSlider);

        // Start Processing button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1, 10, 10));

        startButton = new JButton("Start Processing");
        startButton.addActionListener(new StartButtonActionListener());
        buttonPanel.add(startButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to update the range/tolerance based on the slider value
    private void updateRange() {
        int rangeValue = rangeSlider.getValue();
        Functions.instance.maxDifference = rangeValue;
        rangeSliderLabel.setText("Duplicate Position Tolerance (" + rangeValue + "m):");

        int filesPerFolderValue = filesPerFolderSlider.getValue();
        Functions.instance.filesPerFolder = filesPerFolderValue;
        filesPerFolderLabel.setText("Files Per Folder: " + filesPerFolderValue);

        saveSettings();
    }

    private class FolderSelectInputListener extends MouseAdapter {
        private final JTextField textField;

        public FolderSelectInputListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            String folderPath = Functions.instance.chooseFolder();
            if (folderPath != null) {
                Functions.instance.inputFolderPath = folderPath;
                Functions.instance.inputFolder = new File(folderPath);
                textField.setText(folderPath);
                saveSettings();
            }
        }
    }

    private class FolderSelectOutputListener extends MouseAdapter {
        private final JTextField textField;

        public FolderSelectOutputListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            String folderPath = Functions.instance.chooseFolder();
            if (folderPath != null) {
                Functions.instance.outputFolderPath = folderPath;
                Functions.instance.outputFolder = new File(folderPath);
                textField.setText(folderPath);
                saveSettings();
            }
        }
    }

    private class AutoSaveItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            saveSettings();
        }
    }

    private void saveSettings() {
        Functions.instance.inputFolderPath = inputFolderPathField.getText();
        Functions.instance.outputFolderPath = outputFolderPathField.getText();
        Functions.instance.fileLogging = fileLoggingCheckBox.isSelected();
        Functions.instance.showEachFile = showEachFileCheckBox.isSelected();
        Functions.instance.openOutputFolder = openOutputFolderCheckBox.isSelected();
        Functions.instance.maxDifference = rangeSlider.getValue();
    }

    private class StartButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            startButton.setText("Processing...");
            JOptionPane.showMessageDialog(instance, "Starting processing...");
            Functions.instance.start();
            JOptionPane.showMessageDialog(instance, "Processing complete!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final Interface checkerUI = new Interface();
            checkerUI.setVisible(true);
        });
    }

}
