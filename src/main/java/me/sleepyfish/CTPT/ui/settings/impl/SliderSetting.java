package me.sleepyfish.CTPT.ui.settings.impl;

import me.sleepyfish.CTPT.ui.settings.Setting;

import javax.swing.JSlider;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for slider settings
 */
public final class SliderSetting extends Setting {

    public int min;
    public int max;
    public int value;
    public JSlider element;

    public SliderSetting(final String name, final int min, final int max, final int value) {
        super(name, 1);

        this.min = min;
        this.max = max;
        this.value = value;

        this.element = new JSlider(min, max, value);

        // this.element.setPaintTicks(false);
        // this.element.setPaintLabels(false);
        // this.element.setMinorTickSpacing((max - min) / 10);
        // this.element.setMajorTickSpacing((max - min) / 10);
        // this.element.setPaintTrack(false);
        // this.element.setSnapToTicks(true);
    }

}