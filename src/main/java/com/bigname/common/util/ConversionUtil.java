package com.bigname.common.util;

import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ConversionUtil {

    public static final String ID_REG_EX_PATTERN = "[^a-zA-Z0-9]+";
    public static <T> List<T> toList(T... varArgs) {

        return toObjectList(true, varArgs).stream().map(e -> (T)e).collect(Collectors.toList());
    }

    /**
     * Convert the given varArgs into a list.
     * All null elements in the varArgs will be ignored
     *
     * @param varArgs The variable argument array
     * @return A list of non empty varArg elements
     */
    public static List<Object> toObjectList(Object... varArgs) {
        return toObjectList(true, varArgs);
    }

    /**
     * Convert the given varArgs into a list.
     * All null elements in the varArgs will be ignored, if the ignoreNull is true
     *
     * @param ignoreNull The boolean flag for ignoring null varArg elements from the returning list
     * @param varArgs The variable argument array
     * @return A list of non empty varArg elements
     */

    public static List<Object> toObjectList(boolean ignoreNull, Object... varArgs) {
        List<Object> list = new ArrayList<>();
        if(varArgs != null) {
            for (Object varArg : varArgs) {
                if (!ignoreNull || isNotNull(varArg)) {
                    list.add(varArg);
                }
            }
        }
        return list;
    }

    public static String toId(String value) {
        return value.replaceAll(ID_REG_EX_PATTERN, "_").toUpperCase();
    }
}
