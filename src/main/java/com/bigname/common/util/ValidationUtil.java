package com.bigname.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ValidationUtil {
    /**
     * Checks if a collection is empty.
     *
     * @param collection the collection
     * @return true, if the collection is empty
     */
    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if an object is null.
     *
     * @param object the object
     * @return true, if the object is null
     */
    public static boolean isNull(Object object) {
        return object == null;
    }


    /**
     * Checks if an object is not null.
     *
     * @param object the object
     * @return true, if the object is not null
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * Checks if an object is empty.
     *
     * @param object the object
     * @return true, if the object is empty
     */
    public static boolean isEmpty(Object object) {
        if(object == null) return true;
        else if(object instanceof String) {
            if (((String)object).trim().length() == 0) {
                return true;
            }
        } else if(object instanceof Collection) {
            return isCollectionEmpty((Collection<?>)object);
        } else if(object instanceof Map) {
            return ((Map)object).isEmpty();
        }
        return false;
    }

    /**
     * Checks if an object is not empty.
     *
     * @param object the object
     * @return true, if the object is not empty
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

}
