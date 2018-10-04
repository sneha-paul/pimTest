package com.bigname.common.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> toMap(String[] array) {
        Map<String, String> map = new LinkedHashMap<>();
        for(int i = 0; i < (array.length % 2 == 0 ? array.length : array.length - 1); i = i + 2) {
            map.put(array[i], array[i + 1]);
        }
        return map;
    }

    public static final String[] splitPipeDelimited(String value) {
        return split(value, "\\|");
    }
    public static final List<String> splitPipeDelimitedAsList(String value) {
        return Arrays.asList(split(value, "\\|"));
    }
}
