package com.bigname.pim.api.cache;

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

}
