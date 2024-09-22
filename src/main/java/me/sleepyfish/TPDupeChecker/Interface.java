package me.sleepyfish.TPDupeChecker;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;

public final class Interface extends JFrame {

    public static final Interface instance = new Interface();

    private int mouseX, mouseY; // Variables for dragging

    // GUI Components
    private final JTextField inputFolderPathField;
    private final JTextField outputFolderPathField;
    private final JCheckBox showEachFileCheckBox;
    private final JCheckBox openOutputFolderCheckBox;
    private final JCheckBox fileLoggingCheckBox;
    private final JCheckBox splitFilesCheckbox;
    private final JCheckBox splitFilesPerfectlyCheckbox;
    private final JLabel rangeSliderLabel;
    private final JLabel filesPerFolderLabel;
    private final JSlider rangeSlider;
    private final JSlider filesPerFolderSlider;
    private final JButton startButton;
    private JPanel titleBar;
    private final Color fontColor = new Color(200, 200, 200);

    public Interface() {
        setTitle(Variables.title);
        setUndecorated(true); // Remove the default title bar
        setSize(470, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // Custom title bar
        titleBar = new JPanel();
        titleBar.setBackground(new Color(25, 25, 25)); // Set custom background color
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, 30)); // Adjust height
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(x - mouseX, y - mouseY);
            }
        });

        JLabel titleLabel = new JLabel(Variables.title);
        titleLabel.setForeground(fontColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("x");
        closeButton.setBackground(new Color(80, 23, 23));
        closeButton.setForeground(fontColor);
        closeButton.setBorderPainted(false); // Remove button outline
        closeButton.setFocusPainted(false); // Remove focus outline
        closeButton.addActionListener(e -> System.exit(0)); // Close application
        titleBar.add(closeButton, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);

        // Set background color to #292929
        getContentPane().setBackground(new Color(41, 41, 41)); // #292929 in RGB

        // Main panel with BoxLayout
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(41, 41, 41)); // Set panel background

        // Font settings
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 12);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        // Set text color to #FFFFFF
        JLabel inputFolderPathLabel = new JLabel("Input Folder Path:");
        inputFolderPathLabel.setFont(labelFont);
        inputFolderPathLabel.setForeground(fontColor);
        inputFolderPathField = new JTextField(Functions.instance.inputFolderPath);
        inputFolderPathField.setEditable(false);
        inputFolderPathField.setFont(textFieldFont);
        inputFolderPathField.setBackground(new Color(52, 52, 52)); // Change background to new color
        inputFolderPathField.setForeground(fontColor);
        inputFolderPathField.setBorder(null); // Remove outline
        inputFolderPathField.addMouseListener(new FolderSelectInputListener(inputFolderPathField));
        panel.add(inputFolderPathLabel);
        panel.add(inputFolderPathField);

// Output Folder Path selection
        JLabel outputFolderPathLabel = new JLabel("Output Folder Path (empty for default):");
        outputFolderPathLabel.setFont(labelFont);
        outputFolderPathLabel.setForeground(fontColor);
        outputFolderPathField = new JTextField(Functions.instance.outputFolderPath);
        outputFolderPathField.setEditable(false);
        outputFolderPathField.setFont(textFieldFont);
        outputFolderPathField.setBackground(new Color(52, 52, 52)); // Change background to new color
        outputFolderPathField.setForeground(fontColor);
        outputFolderPathField.setBorder(null); // Remove outline
        outputFolderPathField.addMouseListener(new FolderSelectOutputListener(outputFolderPathField));
        panel.add(outputFolderPathLabel);
        panel.add(outputFolderPathField);


        // Enable File Logging
        addCheckbox(panel, "Enable File Logging:", fileLoggingCheckBox = new JCheckBox(), Functions.instance.fileLogging);

        // Show Each File
        addCheckbox(panel, "Show Each File in logger:", showEachFileCheckBox = new JCheckBox(), Functions.instance.showEachFile);

        // Open Output Folder When Finished
        addCheckbox(panel, "Open Output Folder When Finished:", openOutputFolderCheckBox = new JCheckBox(), Functions.instance.openOutputFolder);

        // Slider for range/tolerance
        rangeSliderLabel = new JLabel("Duplicate Position Tolerance (" + (int) Functions.instance.maxDifference + "m):");
        rangeSliderLabel.setFont(labelFont);
        rangeSliderLabel.setForeground(fontColor);
        rangeSlider = new JSlider(JSlider.HORIZONTAL, 1, 99, (int) Functions.instance.maxDifference);
        rangeSlider.setBackground(new Color(52, 52, 52)); // Match background
        rangeSlider.setUI(new CustomSliderUI(rangeSlider)); // Set custom UI
        rangeSlider.addChangeListener(e -> updateRange());
        panel.add(rangeSliderLabel);
        panel.add(rangeSlider);

        // Split Files boolean
        addCheckbox(panel, "Split Files Into Folders:", splitFilesCheckbox = new JCheckBox(), Functions.instance.splitFilesIntoFolders);

        // Split Files Evenly
        addCheckbox(panel, "Split Files Evenly:", splitFilesPerfectlyCheckbox = new JCheckBox(), Functions.instance.splitFilesPerfectly);

        // Files Per Folder
        filesPerFolderLabel = new JLabel("Files Per Folder: " + Functions.instance.filesPerFolder);
        filesPerFolderLabel.setFont(labelFont);
        filesPerFolderLabel.setForeground(fontColor);
        filesPerFolderSlider = new JSlider(JSlider.HORIZONTAL, 200, 1200, Functions.instance.filesPerFolder);
        filesPerFolderSlider.setBackground(new Color(52, 52, 52)); // Match background
        filesPerFolderSlider.setUI(new CustomSliderUI(filesPerFolderSlider)); // Set custom UI
        filesPerFolderSlider.addChangeListener(e -> {
            saveSettings();
            filesPerFolderLabel.setText("Files Per Folder: " + filesPerFolderSlider.getValue());
        });
        panel.add(filesPerFolderLabel);
        panel.add(filesPerFolderSlider);

        // Start Processing button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1, 10, 10));
        startButton = new JButton("Start Processing");
        startButton.setFont(buttonFont);
        startButton.setBackground(new Color(59, 119, 60));
        startButton.setForeground(fontColor);
        startButton.setFocusPainted(false); // Remove focus outline
        startButton.addActionListener(new StartButtonActionListener());
        buttonPanel.add(startButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addCheckbox(JPanel panel, String labelText, JCheckBox checkBox, boolean selected) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(fontColor); // Set label color
        checkBox.setSelected(selected);
        checkBox.setBackground(new Color(52, 52, 52)); // Match background
        checkBox.setForeground(fontColor); // Set checkbox color
        checkBox.addItemListener(new AutoSaveItemListener());
        panel.add(label);
        panel.add(checkBox);
    }

    private void updateRange() {
        int rangeValue = rangeSlider.getValue();
        Functions.instance.maxDifference = rangeValue;
        rangeSliderLabel.setText("Duplicate Position Tolerance (" + rangeValue + "m):");
        saveSettings();
    }

    private class CustomSliderUI extends BasicSliderUI {
        public CustomSliderUI(JSlider b) {
            super(b);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            // Set background color
            g.setColor(new Color(52, 52, 52));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
            super.paint(g, c);
        }

        @Override
        public void paintThumb(Graphics g) {
            g.setColor(new Color(87, 87, 87));
            g.fillRect(thumbRect.x, thumbRect.y, thumbRect.width / 2, thumbRect.height);
        }

        @Override
        public void paintTrack(Graphics g) {
            g.setColor(new Color(52, 52, 52)); // Optional: Change track color
            g.fillRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height);
        }
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
        Functions.instance.splitFilesIntoFolders = splitFilesCheckbox.isSelected();
        Functions.instance.filesPerFolder = filesPerFolderSlider.getValue();
        Functions.instance.splitFilesPerfectly = splitFilesPerfectlyCheckbox.isSelected();
    }

    private class StartButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startButton.setText("Processing...");
            JOptionPane.showMessageDialog(instance, "Starting processing...");
            Functions.instance.start();
            JOptionPane.showMessageDialog(instance, "Processing complete!");
            startButton.setText("Start Processing");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final Interface checkerUI = new Interface();
            checkerUI.setVisible(true);
        });
    }
}
