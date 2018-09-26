package com.bigname.pim.api.domain;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Feature extends ValidatableEntity {
    @NotEmpty(message = "Feature name cannot be empty")
    private String name;

    @NotEmpty(message = "Feature label cannot be empty")
    private String label;

    private String required = "N";
    private String selectable = "Y";
    private Map<String, String> values = new HashMap<>();

    public Feature() {}

    public Feature(String name, String label, String required) {
        this.name = name;
        this.label = label;
        this.required = "Y".equalsIgnoreCase(required)  ? "Y" : "N";
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

    public String getSelectable() {
        return selectable;
    }

    public void setSelectable(String selectable) {
        this.selectable = "Y".equalsIgnoreCase(selectable)  ? "Y" : "N";;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> addValue(String key, String value) {
        getValues().put(key, value);
        return values;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", getName());
        map.put("label", getLabel());
        map.put("required", getRequired());
        map.put("selectable", getSelectable());
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        return name.equals(feature.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
