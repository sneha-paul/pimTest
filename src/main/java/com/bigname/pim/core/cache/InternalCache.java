package com.bigname.pim.core.cache;

import com.bigname.pim.core.domain.Attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class InternalCache {

    private static InternalCache cacheInstance = new InternalCache();

    public static InternalCache getInstance() {
        return cacheInstance;
    }

    private InternalCache() {
    }

    private Map<String, Object> levle1 = new ConcurrentHashMap<>();

    public class AtttributeCollectionCache {
        private Map<String, Attribute> cache = new ConcurrentHashMap<>();
        public AtttributeCollectionCache() {

        }

        public void setAll(Map<String, Attribute> attributes) {
            cache.putAll(attributes);
        }

        public Attribute get(String attributeId) {
            return cache.get(attributeId);
        }

        public void resetCache() {
            cache.clear();
        }


    }

}
