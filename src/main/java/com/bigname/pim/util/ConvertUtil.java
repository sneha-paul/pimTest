package com.bigname.pim.util;

import com.m7.xtreme.common.util.PlatformUtil;

/**
 * Created by Manu on 8/7/2018.
 */
public class ConvertUtil {
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String Y = "y";
    public static final String N = "n";
    public static final boolean BOOLEAN_DEFAULT = false;

    public static boolean toBoolean(String value, boolean... defaultValue) {
        if(value == null) {
            return PlatformUtil.getValueOrDefault(BOOLEAN_DEFAULT, defaultValue);
        }
        return TRUE.equalsIgnoreCase(value) || YES.equalsIgnoreCase(value) || Y.equalsIgnoreCase(value);
    }
}
