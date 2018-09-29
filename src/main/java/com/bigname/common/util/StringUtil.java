package com.bigname.common.util;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class StringUtil {

    public static String[] split(String value, String delim) {
        if(ValidationUtil.isNull(value)) {
            return new String[0];
        } else if(ValidationUtil.isEmpty(delim)) {
            return new String[]{value};
        } else {
            return value.split(delim);
        }
    }

    public static final String[] splitPipeDelimited(String value) {
        return split(value, "\\|");
    }
}
