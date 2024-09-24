package me.sleepyfish.CTPT.ui.settings.impl;

import me.sleepyfish.CTPT.ui.settings.Setting;

import javax.swing.JTextField;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for text settings
 */
public final class TextSetting extends Setting {

    public String value;
    public final JTextField element;

    public TextSetting(final String name, final String value) {
        super(name, 2);
        this.value = value;
        this.element = new JTextField(value);
    }

}