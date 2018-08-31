package com.bigname.pim.util;

import com.bigname.pim.api.domain.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
}
