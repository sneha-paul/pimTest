package com.bigname.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ConversionUtil {

    public static final String ID_REG_EX_PATTERN = "[^a-zA-Z0-9]+";
    @SuppressWarnings("unchecked")
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

    public static String toJSONString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //TODO - log exception
            return "";
        }
    }

    public static String toId(String value) {
        return value.replaceAll(ID_REG_EX_PATTERN, "_").toUpperCase();
    }

    public static String getFileSize(long sizeInBytes) {
        String size = "0";
        String unit = "KB";
        if(sizeInBytes > 0) {
            BigDecimal bytes = new BigDecimal(sizeInBytes);
            BigDecimal kb = bytes.divide(new BigDecimal(1024), BigDecimal.ROUND_UP);
            unit = "KB";
            if(kb.intValue() < 4096) {
                size = kb.toString();
            } else {
                BigDecimal mb = kb.divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_EVEN);
                unit = "MB";
                if(mb.intValue() < 1024) {
                    size = mb.toString();
                } else {
                    BigDecimal gb = mb.divide(new BigDecimal(1024), BigDecimal.ROUND_HALF_EVEN);
                    unit = "GB";
                    size = gb.toString();
                }
            }
            if(size.length() == 4) {
                size = size.substring(0, 1) + "," + size.substring(1);
            }
        }
        return size + " " + unit;
    }
}
