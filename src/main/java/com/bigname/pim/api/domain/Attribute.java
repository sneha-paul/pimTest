package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import org.javatuples.Pair;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by manu on 9/4/18.
 */
public class Attribute extends ValidatableEntity {
    @NotEmpty(message = "Attribute type cannot be empty")
    private String type;

    @NotEmpty(message = "Attribute name cannot be empty")
    private String name;

    @NotEmpty(message = "Attribute label cannot be empty")
    private String label;

    private String required;

    public Attribute() {}

    public Attribute(String type, String name, String label, String required) {
        this.type = type;
        this.name = name;
        this.label = label;
        this.required = "Y".equalsIgnoreCase(required)  ? "Y" : "N";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = "Y".equalsIgnoreCase(required)  ? "Y" : "N";
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", getName());
        map.put("type", getType());
        map.put("label", getLabel());
        map.put("required", getRequired());
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return name.equals(attribute.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
