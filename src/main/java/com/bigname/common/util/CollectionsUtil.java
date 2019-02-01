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
    public static <K, V> Map<K, V> flattenMap(Map<K, V> map){
        Map<K, V> flatten = new HashMap<>();
        Set<K> keySet = map.keySet();
        for(K key : keySet) {
        Map<K, V> value = (Map<K, V>) map.get(key);
            Set<K> nestedKeyset = value.keySet();
            for(K nestedkey : nestedKeyset) {
                flatten.put(nestedkey,value.get(nestedkey));
            }
        }
        return  flatten ;
        }

/*    public static void main(String[] args) {
        Map<String, Map<String, Object>> input = new HashMap<>();
        Map<String, Map<String, Object>> plan = new HashMap<>();
        Map<String, Object> objInput=new HashMap<>();
        Map<String, Object> objInput1=new HashMap<>();
        Map<String, Object> objInput2=new HashMap<>();
        Map<String, Object> objInput3=new HashMap<>();

        objInput.put("50",9);
        objInput.put("25",6);
        objInput.put("10",3);
        input.put("plan",objInput);

        objInput1.put("50",4);
        objInput1.put("25",45);
        objInput1.put("10",66);
        input.put("map1",objInput1);

        objInput2.put("50",3);
        objInput2.put("25",77);
        objInput2.put("10",6);
        input.put("map2",objInput2);

        objInput3.put("50",1);
        objInput3.put("25",2);
        objInput3.put("10",3);
        input.put("map3",objInput3);

        System.out.println("Input : "+input);
        System.out.println(buildMapString(input,0).toString());
    }*/
//buildMapString(input,0) -- pass 0 index at first time
    private static <K, V> StringBuilder buildMapString(Map<K, V> map, int index) {
        StringBuilder builder = new StringBuilder();
        Set<K> keySet = map.keySet();
        for (K key : keySet) {
            if (map.get(key) instanceof Map) {
                Map<K, V> value = (Map<K, V>) map.get(key);
                builder.append(buildMapString(value,index));
                index++;
            } else {
                V value = map.get(key);
                builder.append(key + ",");
                builder.append(index + ",");
                builder.append(value + ";");
            }
        }
        return builder;
    }
}
