package com.lunagameserve.get_shaded.util;

/**
 * Created by sixstring982 on 4/16/15.
 */
public class MathUtil {

    /**
     * Modulus like it works in most languages.
     * @param a The first double to mod in the expression a % b
     * @param b The second double to mod in the expression a % b
     * @return a % b as it is in most languages.
     */
    public static double mod(double a, double b) {
        return (a % b + b) % b;
    }
}
