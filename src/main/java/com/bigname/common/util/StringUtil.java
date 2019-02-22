package com.bigname.common.util;

import java.util.*;
import java.util.regex.*;

import static com.bigname.common.util.ConversionUtil.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class StringUtil {

    public static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String[] split(String value, String delim) {
        if("|".equals(delim)) {
            delim = "\\|";
        }
        if(ValidationUtil.isNull(value)) {
            return new String[0];
        } else if(ValidationUtil.isEmpty(delim)) {
            return new String[]{value};
        } else {
            return value.split(delim);
        }
    }

    public static List<String> split(String value, String[] delimiters) {
        return delimiters.length == 0 ? toList(value) : split(toList(value), toList(delimiters));
    }

    private static List<String> split(List<String> fragments, List<String> delimiters) {
        List<String> tokens = new ArrayList<>();
        for(String fragment : fragments) {
            if(delimiters.size() == 1) {
                tokens.addAll(Arrays.asList(split(fragment, delimiters.get(0))));
            } else {
                tokens.addAll(split(Arrays.asList(split(fragment, delimiters.remove(0))), delimiters));
            }
        }
        return tokens;
    }

    public static String concatinate(List<String> values, String delim) {
        StringBuilder builder = new StringBuilder();
        if(ValidationUtil.isNotEmpty(values)) {
            values.forEach(value -> builder.append(builder.length() == 0 ? "" : delim).append(value));
        }
        return builder.toString();
    }

    public static Map<String, String> toMap(String[] array) {
        Map<String, String> map = new LinkedHashMap<>();
        for(int i = 0; i < (array.length % 2 == 0 ? array.length : array.length - 1); i = i + 2) {
            map.put(array[i], array[i + 1]);
        }
        return map;
    }

    public static List<String> getPipedValues(String value) {
        return splitPipeDelimitedAsList(value);
    }

    public static String getPipedValue(String... values) {
        return concatinate(Arrays.asList(values), "|");
    }

    public static String getSimpleId(String pipedValues) {
        return ValidationUtil.isEmpty(pipedValues) || !pipedValues.contains("|") ? pipedValues : pipedValues.substring(pipedValues.lastIndexOf("|") + 1);
    }

    public static final String[] splitPipeDelimited(String value) {
        return ValidationUtil.isEmpty(value) ? new String[0] : split(value, "\\|");
    }
    public static final List<String> splitPipeDelimitedAsList(String value) {
        return Arrays.asList(split(value, "\\|"));
    }

    public static String getUniqueName(String name, List<String> names) {
        int idx = getIndex(name, names, 0);
        return idx > 0 ? name + "_" + idx : name;
    }

    private static int getIndex(String name, List<String> names, int idx1) {
        java.util.regex.Pattern p1 = java.util.regex.Pattern.compile("^(" + name +"_)(\\d+)$");
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile("^("+ name + ")$");
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

    public static String trim(String s) {
        return s == null ? s : s.trim();
    }

    public static String trim(String s, boolean replaceNull) {
        return s == null ? replaceNull ? "" : s : s.trim();
    }

    public static final java.util.regex.Pattern camelCasePattern = java.util.regex.Pattern.compile("(.+?)([A-Z])");
    public static final java.util.regex.Pattern snakeCasePattern = java.util.regex.Pattern.compile("(.*?)_([a-zA-Z])");

    public static String toSnakeCase(String camelCaseValue, boolean... lowerCase) {
        Matcher matcher = camelCasePattern.matcher(camelCaseValue);
        StringBuffer sb = new StringBuffer();
        String suffix = camelCasePattern.matcher(camelCaseValue).replaceAll("");
        while(matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + "_" + matcher.group(2));
        }
        return getValue(lowerCase) ? sb.append(suffix).toString().toLowerCase() : sb.append(suffix).toString().toUpperCase();
    }

    public static String toCamelCase(String snakeCaseValue, boolean... lowerCase) {
        Matcher matcher = snakeCasePattern.matcher(snakeCaseValue);
        StringBuffer sb = new StringBuffer();
        String suffix = snakeCasePattern.matcher(snakeCaseValue).replaceAll("");
        while(matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toLowerCase() + matcher.group(2).toUpperCase());
        }
        sb.append(suffix.toLowerCase());
        return getValue(lowerCase) ? sb.replace(0, 1, sb.substring(0, 1).toLowerCase()).toString() : sb.replace(0, 1, sb.substring(0, 1).toUpperCase()).toString();
    }


}
