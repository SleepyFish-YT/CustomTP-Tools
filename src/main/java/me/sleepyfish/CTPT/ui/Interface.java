package me.sleepyfish.CTPT.ui;

import me.sleepyfish.CTPT.Main;
import me.sleepyfish.CTPT.ui.settings.Setting;
import me.sleepyfish.CTPT.ui.settings.impl.CheckBoxSetting;
import me.sleepyfish.CTPT.ui.settings.impl.ModeSetting;
import me.sleepyfish.CTPT.ui.settings.impl.SliderSetting;
import me.sleepyfish.CTPT.ui.settings.impl.TextSetting;
import me.sleepyfish.CTPT.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Improved user interface of CTP-T with global settings in a separate tab
 */
public final class Interface extends JFrame {

    private static ArrayList<JComponent> componentsJ;

    public Interface() {
        super("KorePI " + ProjectUtils.title);

        componentsJ = new ArrayList<>();

        // Use modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        this.setSize(540, 430);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setFocusable(false);

        // Create and add the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        this.addToolsTab(tabbedPane);
        this.addGlobalSettingsTab(tabbedPane);
        this.addAuthorTab(tabbedPane);
        this.add(tabbedPane, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                LogUtils.log("Opened User Interface of " + ProjectUtils.nameShort);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                LogUtils.log("Exiting " + ProjectUtils.nameShort + " by User Request");
                System.exit(0);
            }
        });

        // Make the frame visible
        this.setVisible(true);
    }

    private void addAuthorTab(JTabbedPane tabbedPane) {
        JPanel authorPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Add padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("<html><b>Title: " + ProjectUtils.title + "</b></html>");
        componentsJ.add(titleLabel);
        authorPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel spaceLabel = new JLabel("<html><h1></h1></html>");
        componentsJ.add(spaceLabel);
        authorPanel.add(spaceLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel authorLabel = new JLabel("<html><b>Author: " + ProjectUtils.author + "</b></html>");
        componentsJ.add(authorLabel);
        authorPanel.add(authorLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel versionLabel = new JLabel("<html><b>Version: " + ProjectUtils.version + "</b></html>");
        componentsJ.add(versionLabel);
        authorPanel.add(versionLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel githubLabel = new JLabel("<html><b>Github: " + ProjectUtils.github + "</b></html>");
        githubLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        githubLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(ProjectUtils.github));
                } catch (Exception ignored) {
                }
            }
        });
        componentsJ.add(githubLabel);
        authorPanel.add(githubLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel discordLabel = new JLabel("<html><b>Discord: " + ProjectUtils.discord + "</b></html>");
        discordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        discordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(ProjectUtils.discord));
                } catch (Exception ignored) {
                }
            }
        });
        componentsJ.add(discordLabel);
        authorPanel.add(discordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel payPalLabel = new JLabel("<html><b>PayPal: " + ProjectUtils.payPal + "</b></html>");
        payPalLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        payPalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(ProjectUtils.payPal));
                } catch (Exception ignored) {
                }
            }
        });
        componentsJ.add(payPalLabel);
        authorPanel.add(payPalLabel, gbc);

        componentsJ.add(authorPanel);
        tabbedPane.add("Author", authorPanel);
    }

    private void addToolsTab(final JTabbedPane tabbedPane) {
        for (final Tool tool : Main.tools.tools) {
            JPanel toolPanel = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);  // Add padding
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;

            int row = 0;

            // Add tool description
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 2;
            JLabel descriptionLabel = new JLabel("<html><b>" + tool.description + "</b></html>");
            componentsJ.add(descriptionLabel);
            toolPanel.add(descriptionLabel, gbc);

            // Separator
            gbc.gridy = row++;
            JSeparator separator = new JSeparator();
            componentsJ.add(separator);
            toolPanel.add(separator, gbc);

            // Render all settings for this tool
            for (final Setting setting : tool.settings) {
                gbc.gridy = row++;

                switch (setting.type) {
                    case 0:
                        if (setting instanceof CheckBoxSetting) {
                            JCheckBox checkBox = createCheckBox(setting.name, ((CheckBoxSetting) setting).state);
                            checkBox.addActionListener(e -> {
                                ((CheckBoxSetting) setting).state = checkBox.isSelected();
                                tool.onCheckBoxUpdate();
                            });
                            componentsJ.add(checkBox);
                            toolPanel.add(checkBox, gbc);
                        }
                    break;

                    case 1:
                        if (setting instanceof SliderSetting) {

                            JLabel nameLabel = new JLabel(setting.name + ": " + ((SliderSetting) setting).value);
                            toolPanel.add(nameLabel, gbc);

                            gbc.gridy = row++;

                            JSlider slider = createSlider((SliderSetting) setting);

                            slider.addChangeListener(e -> {
                                nameLabel.setText(setting.name + ": " + slider.getValue());
                                ((SliderSetting) setting).value = slider.getValue();
                                tool.onSliderUpdate();
                            });

                            componentsJ.add(slider);
                            toolPanel.add(slider, gbc);
                        }
                    break;

                    case 2:
                        if (setting instanceof TextSetting) {
                            JTextField textField = createTextField((TextSetting) setting);
                            textField.addActionListener(e -> {
                                ((TextSetting) setting).value = textField.getText();
                                LogUtils.log("Set " + setting.name + " to " + ((TextSetting) setting).value);
                                tool.onTextFieldUpdate();
                            });

                            componentsJ.add(textField);
                            toolPanel.add(textField, gbc);
                        }
                    break;

                    case 3:
                        if (setting instanceof ModeSetting) {
                            JLabel nameLabel = new JLabel(setting.name);
                            toolPanel.add(nameLabel, gbc);

                            gbc.gridy = row++;

                            JComboBox<String> comboBox = createComboBox((ModeSetting) setting);
                            comboBox.addActionListener(e -> {
                                ((ModeSetting) setting).currentMode = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                                tool.onModeSettingUpdate();
                            });

                            componentsJ.add(comboBox);
                            toolPanel.add(comboBox, gbc);
                        }
                    break;
                }
            }

            // Run button
            gbc.gridy = row++;
            gbc.gridwidth = 2;

            JButton runButton = new JButton("Run " + tool.titleName);
            runButton.addActionListener(e -> {
                LogUtils.log("Started " + tool.titleName);
                LogUtils.showPopup("Starting " + tool.titleName);
                tool.run();

                if (LogUtils.currentError.isEmpty()) {
                    LogUtils.showPopup("Finished " + tool.titleName);
                } else {
                    LogUtils.showPopup(LogUtils.currentError);
                    LogUtils.currentError = "";
                }

                if (SettingUtils.openOutputPathWhenDone) {
                    try {
                        Desktop.getDesktop().open(SettingUtils.outputPath);
                        LogUtils.log("Opened output path: " + SettingUtils.outputPath);
                    } catch (Exception ignored) {
                        LogUtils.showPopup("Failed to open output path: " + SettingUtils.outputPath);
                    }
                }

                LogUtils.log("Finished " + tool.titleName);
            });
            runButton.setFocusable(false);
            runButton.setFocusPainted(false);
            componentsJ.add(runButton);
            toolPanel.add(runButton, gbc);

            // Add the panel as a tab
            tabbedPane.addTab(tool.titleName, toolPanel);
        }
    }

    /**
     * Adds the global settings tab to the provided tabbed pane.
     */
    private void addGlobalSettingsTab(JTabbedPane tabbedPane) {
        JPanel globalSettingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Add padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;

        JLabel nameLabel = new JLabel("Language (This does not work)");
        componentsJ.add(nameLabel);
        globalSettingsPanel.add(nameLabel, gbc);

        gbc.gridy = row++;

        JComboBox<String> languageComboBox = createComboBox(new ModeSetting("Language (This does not work)", "English", SettingUtils.SettingLanguage.getLanguages()));
        languageComboBox.addActionListener(e -> {
            SettingUtils.SettingLanguage.setCurrentLanguage(Objects.requireNonNull(languageComboBox.getSelectedItem()).toString());
            LogUtils.log("Language: " + SettingUtils.SettingLanguage.currentLanguage);
        });
        gbc.gridy = row++;
        componentsJ.add(languageComboBox);
        globalSettingsPanel.add(languageComboBox, gbc);

        // "Open Output Folder When Done" checkbox
        JCheckBox openOutputFolderCheckBox = createCheckBox("Open Output Folder When Done", SettingUtils.openOutputPathWhenDone);
        openOutputFolderCheckBox.addActionListener(e -> {
            SettingUtils.openOutputPathWhenDone = openOutputFolderCheckBox.isSelected();
            LogUtils.log("Open Output Folder When Done: " + SettingUtils.openOutputPathWhenDone);
        });
        gbc.gridy = row++;
        componentsJ.add(openOutputFolderCheckBox);
        globalSettingsPanel.add(openOutputFolderCheckBox, gbc);

        // "Log Each File" checkbox
        JCheckBox logEachFileCheckBox = createCheckBox("Log Each File", SettingUtils.logEveryFileEach);
        logEachFileCheckBox.addActionListener(e -> {
            SettingUtils.logEveryFileEach = logEachFileCheckBox.isSelected();
            LogUtils.log("Log Each File: " + SettingUtils.logEveryFileEach);
        });
        gbc.gridy = row++;
        componentsJ.add(logEachFileCheckBox);
        globalSettingsPanel.add(logEachFileCheckBox, gbc);

        // "File Logging" checkbox
        JCheckBox fileLoggingCheckBox = createCheckBox("File Logging", SettingUtils.logConsoleToFile);
        fileLoggingCheckBox.addActionListener(e -> {
            SettingUtils.logConsoleToFile = fileLoggingCheckBox.isSelected();
            LogUtils.log("File Logging: " + SettingUtils.logConsoleToFile);
        });
        gbc.gridy = row++;
        componentsJ.add(fileLoggingCheckBox);
        globalSettingsPanel.add(fileLoggingCheckBox, gbc);

        // Input Path Label
        JLabel inputPathLabel = new JLabel("Input Path: " + SettingUtils.inputDir.getAbsolutePath());
        gbc.gridy = row++;
        componentsJ.add(inputPathLabel);
        globalSettingsPanel.add(inputPathLabel, gbc);

        // "Change Input Path" button
        JButton changeInputPathButton = new JButton("Change Input Path");
        changeInputPathButton.addActionListener(e -> {
            String inputPath = FileUtils.chooseFolder();
            if (inputPath != null) {
                SettingUtils.changeInputPath(inputPath, true);
                inputPathLabel.setText("Input Path: " + inputPath);
            }
        });
        changeInputPathButton.setFocusable(false);
        changeInputPathButton.setFocusPainted(false);
        gbc.gridy = row++;
        componentsJ.add(changeInputPathButton);
        globalSettingsPanel.add(changeInputPathButton, gbc);

        JLabel outputPathLabel = new JLabel("Output Path: Input Path + \"\\output\"");
        gbc.gridy = row++;
        componentsJ.add(outputPathLabel);
        globalSettingsPanel.add(outputPathLabel, gbc);

        // Add the global settings panel as a new tab
        componentsJ.add(globalSettingsPanel);
        tabbedPane.addTab("Global Settings", globalSettingsPanel);
    }

    private JCheckBox createCheckBox(String name, boolean state) {
        JCheckBox checkBox = new JCheckBox(name, state);
        checkBox.setFocusPainted(false);
        checkBox.setFocusable(false);
        checkBox.setToolTipText("Enable or disable " + name);
        return checkBox;
    }

    private JSlider createSlider(SliderSetting setting) {
        JSlider slider = new JSlider(setting.min, setting.max, setting.value);
        slider.setFocusable(false);
        slider.setToolTipText("Adjust the value of " + setting.name);
        return slider;
    }

    private JTextField createTextField(TextSetting setting) {
        JTextField textField = new JTextField(setting.value);
        textField.setToolTipText("Set the text for " + setting.name);
        return textField;
    }

    private JComboBox<String> createComboBox(ModeSetting setting) {
        JComboBox<String> comboBox = new JComboBox<>(setting.modes);
        comboBox.setFocusable(false);
        comboBox.setToolTipText("Set the mode for " + setting.name);
        return comboBox;
    }

    public static ArrayList<JComponent> getJComponents() {
        return componentsJ;
    }

}