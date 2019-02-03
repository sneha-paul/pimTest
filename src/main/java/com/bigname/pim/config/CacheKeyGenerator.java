package com.bigname.pim.config;

import com.bigname.core.service.BaseService;
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
        if(target instanceof BaseService) {
            BaseService service = (BaseService) target;

            return service.getEntityName() + "_" + StringUtils.arrayToDelimitedString(objects, "_");
        } else {
            return target.getClass().getSimpleName() + "_"
                    + StringUtils.arrayToDelimitedString(objects, "_");
        }
    }
}
