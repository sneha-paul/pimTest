package com.bigname.common.util;

import org.javatuples.Pair;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ValidationUtil2 {
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
     * Checks if any of the object is empty.
     *
     * @param objects the objects
     * @return true, if any of the object is empty
     */
    public static boolean isAllEmpty(Object... objects) {
        return _isEmpty(true, objects);
    }

    public static boolean isAnyEmpty(Object... objects) {
        return _isEmpty(false, objects);
    }

    public static boolean isEmpty(Object object) {
        return _isEmpty(true, object);
    }

    /**
     * Checks if all the objects are not empty.
     *
     * @param objects the objects
     * @return true, if all the object are not empty
     */
    public static boolean isNotEmpty(Object... objects) {
        return !isAnyEmpty(objects);
    }


    private static boolean _isEmpty(boolean all, Object... objects) {
        if(objects == null) {
            return true;
        } else if(objects.length == 1) {
            return _isEmpty(objects[0]);
        }

        for(Object obj : ConversionUtil.toList(objects)) {
            if(all) {
                if (!_isEmpty(obj)) {
                    return false;
                }
            } else {
                if (_isEmpty(obj)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean _isEmpty(Object object) {
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



}
