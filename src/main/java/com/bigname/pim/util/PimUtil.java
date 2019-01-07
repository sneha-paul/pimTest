package com.bigname.pim.util;

import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Entity;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;

/**
 * Created by Manu on 8/6/2018.
 */
public class PimUtil {
    public static String[] getActiveOptions(boolean... activeOnly) {
        if(getValue(Boolean.TRUE, activeOnly)) {
            return new String[] {"Y"};
        } else {
            return new String[] {"Y", "N"};
        }
    }

    public static Optional<Boolean> getValue(boolean... booleanVarArg) {
        if(booleanVarArg == null || booleanVarArg.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(booleanVarArg[0]);
        }
    }

    public static Optional<String> getValue(String... stringVarArg) {
        if(stringVarArg == null || stringVarArg.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(stringVarArg[0]);
        }
    }

    public static boolean getValue(boolean defaultValue, boolean... booleanVarArg) {
        if(booleanVarArg == null || booleanVarArg.length == 0) {
            return defaultValue;
        } else {
            return booleanVarArg[0];
        }
    }

    public static Collection<? extends Entity> sort(List<? extends Entity> source, List<String> sortedIds) {
        source.sort((e1, e2) -> sortedIds.indexOf(e1.getId()) > sortedIds.indexOf(e2.getId()) ? 1 : -1);
        return source;
    }

    public static <T extends Entity> Map<String, T> getIdedMap(List<T> entityList, FindBy findBy) {
        Map<String, T> idedMap = new HashMap<>();
        entityList.forEach(e -> idedMap.put(findBy == FindBy.INTERNAL_ID ? e.getId() : e.getExternalId(), e));
        return idedMap;
    }

    public static Map<String, String> getTokenizedParameter(String value) {
        return StringUtil.toMap(StringUtil.split(value, "\\|"));
    }

    public static Criteria buildCriteria(Map<String, Object> criteria) {
        Criteria _criteria = null;
        int idx = 0;
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            if(idx ++ == 0) {
                _criteria = Criteria.where(entry.getKey()).is(entry.getValue());
            } else {
                _criteria.andOperator(Criteria.where(entry.getKey()).is(entry.getValue()));
            }
        }
        return _criteria;
    }
}
