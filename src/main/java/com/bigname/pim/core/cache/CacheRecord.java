package com.bigname.pim.core.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CacheRecord<T> implements Serializable {
    private String key;
    private T object;
    private Class<T> objectType;
    private LocalDateTime createdTime = LocalDateTime.now();
    private LocalDateTime lastAccessedTime = LocalDateTime.now();
    private BigDecimal hits = BigDecimal.ZERO;
    private double estimatedSize = 0.0;

    public CacheRecord(String key, T object, Class<T> objectType) {
        this.key = key;
        this.object = object;
        this.objectType = objectType;
    }

    public String getKey() {
        return key;
    }

    public T getObject() {
        return object;
    }

    public Class<T> getObjectType() {
        return objectType;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getLastAccessedTime() {
        return lastAccessedTime;
    }

    public BigDecimal getHits() {
        return hits;
    }

    public double getEstimatedSize() {
        return estimatedSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheRecord<?> that = (CacheRecord<?>) o;

        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode() + object.hashCode();
    }
}
