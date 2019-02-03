package com.bigname.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class BeanUtil {
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        if(clazz.getSuperclass() != null) {
            fields = getAllFields(clazz.getSuperclass());
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    public static List<String> getAllFieldNames(Class<?> clazz) {
        return getAllFields(clazz).stream().map(Field::getName).collect(Collectors.toList());
    }
}
