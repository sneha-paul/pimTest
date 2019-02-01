package com.bigname.common.util;

import java.util.*;
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

    public static Map<String, Object> toMap(Object... objects) {
        Map<String, Object> map = new LinkedHashMap<>();
        for(int i = 0;  i < (objects.length % 2 == 0 ? objects.length : objects.length - 1); i++) {
            map.put(objects[i].toString(), objects[++i]);
        }
        return map;

    }

    public static <K, V> Map<K, V> compareMaps(Map<K, V> map1, Map<K, V> map2){
        Map<K, V> difference = new HashMap<>();
        Set<K> keySet1 = map1.keySet();
        for(K key : keySet1) {
            Set<K> keySet2 = map2.keySet();
            if(keySet2.contains(key)) {
                if(map1.get(key) instanceof Map) {
                    Map<K, V> value1 = (Map<K, V>) map1.get(key);
                    Map<K, V> value2 = (Map<K, V>) map2.get(key);
                    difference = compareMaps(value1, value2);
                }else {
                    V value1 = map1.get(key);
                    V value2 = map2.get(key);
                    if(!value1.equals(value2)) {
                        difference.put(key, value1);
                    }
                }
            }else {
                difference.put(key, map1.get(key));
            }
        }
        return difference;
    }
}
