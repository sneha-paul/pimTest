package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.javatuples.Pair;
import org.springframework.data.annotation.Transient;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
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

    public <E> Map<String, Pair<String, Object>> getValidationErrors(Set<ConstraintViolation<E>> violations) {
        return ValidationUtil.getValidationErrors(violations);
    }
}
