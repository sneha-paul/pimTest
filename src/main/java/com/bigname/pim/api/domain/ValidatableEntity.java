package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.javatuples.Pair;
import org.springframework.data.annotation.Transient;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class ValidatableEntity implements Serializable {


    @Transient
    @JsonIgnore
    private String group = "";

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void orchestrate() {}

    public <T extends ValidatableEntity> T getOrchestratedInstance(T t) {
        t.orchestrate();
        return t;
    }

    public <E> Map<String, Pair<String, Object>> getValidationErrors(Set<ConstraintViolation<E>> violations) {
       Map<String, Pair<String, Object>> errors = new LinkedHashMap<>();
        if(violations.size() > 0) {
            violations.forEach(v -> {
                if(!errors.containsKey(v.getPropertyPath().toString())) {
                    errors.put(v.getPropertyPath().toString(), Pair.with(v.getMessage(), v.getInvalidValue()));
                }
            });
        }
        return errors;
    }

    static boolean isEmpty(Object... objects) {
        if(objects == null) {
            return true;
        } else if(objects.length == 1) {
            return ValidationUtil.isEmpty(objects[0]);
        }
        for(Object obj : ConversionUtil.toList(objects)) {
            if(ValidationUtil.isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /*static boolean isEmpty(Object object) {
        return ValidationUtil.isEmpty(object);
    }*/

    static boolean isNotEmpty(Object... objects) {
        return !isEmpty(objects);
    }

    static boolean isNull(Object... objects) {
        if(objects == null) {
            return true;
        } else if(objects.length == 1) {
            return ValidationUtil.isNull(objects[0]);
        }
        for(Object obj : ConversionUtil.toList(objects)) {
            if(ValidationUtil.isNull(obj)) {
                return true;
            }
        }
        return false;
    }

    static boolean isNotNull(Object... objects) {
        return !isNull(objects);
    }

    static String toId(String value) {
        return ConversionUtil.toId(value);
    }

    static String toYesNo(String value, String checkFor) {
        switch(checkFor) {
            case "Y":
                return "Y".equalsIgnoreCase(value)  ? "Y" : "N";
            case "N":
                return "N".equalsIgnoreCase(value)  ? "N" : "Y";
            /*case "TRUE":
                return "TRUE".equalsIgnoreCase(value)  ? "TRUE" : "FALSE";
            case "FALSE":
                return "FALSE".equalsIgnoreCase(value)  ? "FALSE" : "TRUE";*/
            default:
                return value;
        }
    }

    static List<String> getPipedValues(String value) {
        return StringUtil.splitPipeDelimitedAsList(value);
    }

    static boolean booleanValue(String value) {
        return "Y".equals(value);
    }

    public interface CreateGroup {}
    public interface DetailsGroup {}
}
