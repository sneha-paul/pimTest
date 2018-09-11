package com.bigname.pim.api.domain;

/**
 * Created by manu on 9/4/18.
 */
public class Attribute {
    String type;
    String name;
    boolean required;

    public Attribute(String type, String name, boolean required) {
        this.type = type;
        this.name = name;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
