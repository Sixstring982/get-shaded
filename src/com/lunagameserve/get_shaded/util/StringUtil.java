package com.lunagameserve.get_shaded.util;

/**
 * @author Sixstring982
 * @since 4/17/2015
 */
public class StringUtil {
    public static String pluralize(String str, int count) {
        return count + " " + str + (count != 1 ? "s" : "");
    }
}
