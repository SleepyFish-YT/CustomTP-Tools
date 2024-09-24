package me.sleepyfish.CTPT.ui.settings;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used for settings
 */
public class Setting {

    /*
     * Setting types
     * 0 = checkbox
     * 1 = slider
     * 2 = text
     * 3 = mode
     */

    public String name;
    public final int type;

    public Setting(final String name, final int type) {
        this.name = name;
        this.type = type;
    }

}