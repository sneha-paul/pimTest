package com.bigname.pim.util;

import com.bigname.common.util.StringUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.util.FindBy;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Manu on 8/6/2018.
 */
public class PimUtil {
    public static String[] getActiveOptions(boolean... activeOption) {
        boolean[] activeFlags = getActiveFlags(activeOption);
        List<String> options = new ArrayList<>();
        if(activeFlags[0]) {
            options.add("Y");
        }
        if(activeFlags[1]) {
            options.add("N");
        }

        return options.toArray(new String[0]);

    }

    public static boolean showDiscontinued(boolean... activeOption) {
        return getActiveFlags(activeOption)[2];
    }

    public static boolean[] getActiveFlags(boolean... activeFlags) {
        boolean flag1, flag2, flag3;
        if(getLength(activeFlags) < 2) {
            flag1 = true;
            flag2 = !getValueOrDefault(Boolean.TRUE, activeFlags);
            flag3 = false;
        } else {
            flag1 = getValue(0, activeFlags).orElse(false);
            flag2 = getValue(1, activeFlags).orElse(false);
            flag3 = getValue(2, activeFlags).orElse(false);
        }
        return new boolean[] {flag1, flag2, flag3};
    }

    public static int getLength(boolean... booleanVarArg) {
        return booleanVarArg == null ? 0 : booleanVarArg.length;
    }

    public static Optional<Boolean> getValue(boolean... booleanVarArg) {
        if(booleanVarArg == null || booleanVarArg.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(booleanVarArg[0]);
        }
    }

    public static Optional<Boolean> getValue(int idx, boolean... booleanVarArg) {
        if(idx == 0) {
            return getValue(booleanVarArg);
        }

        if(idx < 0 || booleanVarArg == null || booleanVarArg.length < idx + 1) {
            return Optional.empty();
        } else {
            return Optional.of(booleanVarArg[idx]);
        }
    }

    public static Optional<String> getValue(String... stringVarArg) {
        if(stringVarArg == null || stringVarArg.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(stringVarArg[0]);
        }
    }

    public static boolean getValueOrDefault(boolean defaultValue, boolean... booleanVarArg) {
        return getValue(booleanVarArg).orElse(defaultValue);
        /*if(booleanVarArg == null || booleanVarArg.length == 0) {
            return defaultValue;
        } else {
            return booleanVarArg[0];
        }*/
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
        Criteria _criteria = new Criteria();
        _criteria.andOperator(criteria.entrySet().stream().map(entry -> Criteria.where(entry.getKey()).is(entry.getValue())).collect(Collectors.toList()).toArray(new Criteria[0]));
        return _criteria;
    }

    public static boolean isDataTableRequest(HttpServletRequest request) {
        return request.getParameter("draw") != null;
    }

    public static boolean[] getStatusOptions(String statusOptions) {
        boolean[] _statusOptions = new boolean[3];
        statusOptions = StringUtil.trim(statusOptions, true);
        if(statusOptions.isEmpty()) {
            statusOptions = PIMConstants.DEFAULT_GRID_STATUS_OPTIONS;
        }
        char[] options =statusOptions.toCharArray();

        while(options.length < 3) {
            options[options.length] = '0';
        }

        for(int i = 0; i < 3; i ++) {
            _statusOptions[i] = options[i] == '1';
        }
        return _statusOptions;
    }

    public static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public static boolean isActive(String active, LocalDateTime from, LocalDateTime to){
        return isBetween(from,to) || "Y".equals(active);
    }

    public static boolean isBetween(LocalDateTime from, LocalDateTime to){

        LocalDateTime now = LocalDateTime.now();
        if( from != null && to != null ){
            return from.isBefore(now) && to.isAfter(now);
        } else if( from != null ) {
            return from.isBefore(now);
        } else if( to != null ) {
            return to.isAfter(now);
        }else {
            return false;
        }

    }

    public static boolean hasDiscontinued(String active, LocalDateTime from, LocalDateTime to){
        return isBetween(from,to) || "Y".equals(active);
    }
}
