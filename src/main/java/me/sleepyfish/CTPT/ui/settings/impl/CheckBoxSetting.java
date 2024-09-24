package me.sleepyfish.CTPT.ui.settings.impl;

import me.sleepyfish.CTPT.ui.settings.Setting;

import javax.swing.JCheckBox;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for checkbox settings
 */
public final class CheckBoxSetting extends Setting {

    public boolean state;
    public JCheckBox element;

    public CheckBoxSetting(final String name, final boolean state) {
        super(name, 0);

        this.state = state;
        this.element = new JCheckBox(name, state);
    }

}