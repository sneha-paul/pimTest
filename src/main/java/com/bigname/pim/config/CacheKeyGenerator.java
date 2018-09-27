package com.bigname.pim.config;

import com.bigname.pim.api.domain.Entity;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... objects) {
        if(target.getClass().isAssignableFrom(Entity.class)) {
            Entity entity = (Entity) target;
            return target.getClass().getSimpleName() + "_" + entity.getId();
        } else {
            return target.getClass().getSimpleName() + "_"
                    + StringUtils.arrayToDelimitedString(objects, "_");
        }
    }
}
