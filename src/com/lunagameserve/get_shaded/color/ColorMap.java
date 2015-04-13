package com.lunagameserve.get_shaded.color;

/**
 * @author Sixstring982
 * @since 4/13/2015
 */
public class ColorMap {
    public static int redBlueGradient(double min, double val, double max) {
        double percentage = (val - min) / (max - min);
        int cval = (int)(percentage * 512);
        if (cval < 256) {
            return 0x3f0000ff | ((cval & 0xff) << 16);
        } else {
            return 0x3fff0000 | ((512 - cval) & 0xff);
        }
    }
}
