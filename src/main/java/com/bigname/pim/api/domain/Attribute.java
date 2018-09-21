package com.bigname.pim.api.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manu on 9/4/18.
 */
public class Attribute {
    private String type;
    private String name;
    private String label;
    private String required;

    public Attribute() {}

    public Attribute(String type, String name, String label, String required) {
        this.type = type;
        this.name = name;
        this.label = label;
        this.required = required;
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
        this.required = required;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", getName());
        map.put("type", getType());
        map.put("label", getLabel());
        map.put("required", getRequired());
        return map;
    }
}
