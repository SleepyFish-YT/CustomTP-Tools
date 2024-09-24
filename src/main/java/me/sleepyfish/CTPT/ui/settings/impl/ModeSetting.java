package me.sleepyfish.CTPT.ui.settings.impl;

import me.sleepyfish.CTPT.ui.settings.Setting;

import javax.swing.JComboBox;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for mode settings
 */
public final class ModeSetting extends Setting {

    public String[] modes;
    public String currentMode;
    public JComboBox<String> element;

    public ModeSetting(final String name, final String value, final String[] modes) {
        super(name, 3);

        this.modes = modes;
        this.currentMode = value;
        this.element = new JComboBox<>(modes);
    }

}