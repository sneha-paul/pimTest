package com.bigname.common.util;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CollectionsUtil {
    public static Map<String, Object> filterMap(Map<String, Object> map, List<String> keysToFilter) {
        keysToFilter.forEach(map::remove);
        return map;
    }
}
