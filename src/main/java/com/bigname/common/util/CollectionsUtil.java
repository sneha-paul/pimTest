package com.bigname.common.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CollectionsUtil {
    public static Map<String, Object> filterMap(Map<String, Object> map, List<String> keysToFilter) {
        keysToFilter.forEach(map::remove);
        return map;
    }

    public static <T, K, U> Collector<T, ?, Map<K,U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new
        );
    }

    public static <T, K, U> Collector<T, ?, Map<K,U>> toTreeMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                TreeMap::new
        );
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generifyMap(Map map) {
        Map<String, Object> genericMap = new LinkedHashMap<>();
        map.keySet().forEach(o -> {
            String key = (String) o;
            genericMap.put(key, map.get(o));
        });

        return genericMap;
    }
}
