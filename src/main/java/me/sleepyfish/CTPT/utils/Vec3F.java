package me.sleepyfish.CTPT.utils;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This class is used to store 3D coordinates
 */
public final class Vec3F {

    public float x, y, z;

    public Vec3F(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3F() {
        this(0, 0, 0);
    }

}