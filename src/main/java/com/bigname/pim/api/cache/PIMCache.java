package com.bigname.pim.api.cache;

import com.m7.xcore.domain.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PIMCache {
    private static PIMCache cacheInstance = new PIMCache();

    public static PIMCache getInstance() {
        return cacheInstance;
    }

    private PIMCache() {
    }

    private Map<String, CacheRecord<? extends Entity>> entityCache = new ConcurrentHashMap<>();

    public CacheRecord<? extends Entity> getEntity(String key) {
        return entityCache.get(key);
    }

    public void clearEntityCache() {
        entityCache.clear();
    }

    public void evictEntity(String key) {
        entityCache.remove(key);
    }


}
