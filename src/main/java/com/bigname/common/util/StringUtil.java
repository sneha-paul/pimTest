package com.bigname.common.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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

    public static String getUniqueName(String name, List<String> names) {
        int idx = getIndex(name, names, 0);
        return idx > 0 ? name + "_" + idx : name;
    }

    private static int getIndex(String name, List<String> names, int idx1) {
        java.util.regex.Pattern p1 = java.util.regex.Pattern.compile("(" + name +"_)(\\d+)$");
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile("("+ name + ")");
        int idx = 0, i = 0;
        String paramName = name;
        for (i = 0; i < names.size(); i ++) {
            String value = names.get(i);
            Matcher m = p1.matcher(value);
            if(m.find()) {
                if(m.groupCount() == 2) {
                    idx = Integer.parseInt(m.group(2)) + 1;
                    break;
                }
            } else if(!m.find()) {
                m = p2.matcher(value);
                if(m.find()) {
                    if(m.groupCount() == 1) {
                        idx = 1;
                        break;
                    }
                }
            }
        }
        if(i + 1 < names.size()) {
            idx = getIndex(paramName, names.subList(i + 1, names.size()), idx > idx1 ? idx : idx1);
        }
        return idx > idx1 ? idx : idx1;
    }
}
