package com.malcolmcrum.spacetrader;

import java.util.Random;

/**
 * Created by Malcolm on 8/28/2015.
 */
public class Utils {
    private static Random rand = new Random();

    public static int GetRandom(int upper) {
        return GetRandom(0, upper);
    }

    /**
     * Return random value between lower and upper, including lower and excluding upper
     * @param lower Lower bound
     * @param upper Upper bound
     * @return Random value in range
     */
    public static int GetRandom(int lower, int upper) {
        return rand.nextInt(upper - lower) + lower;
    }

    // thanks http://stackoverflow.com/a/14257525/281657
    public static <T extends Enum<?>> T RandomEnum(Class<T> c) {
        return RandomEnum(c, 0);
    }

    public static <T extends Enum<?>> T RandomEnum(Class<T> c, int offset) {
        int x = offset + rand.nextInt(c.getEnumConstants().length - offset);
        return c.getEnumConstants()[x];
    }
}

class Vector2i {
    int x;
    int y;

    public static double Distance(Vector2i p1, Vector2i p2) {
        return Math.sqrt((p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y));
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}