package com.bigname.core.util;

import com.bigname.common.util.ValidationUtil;

public enum Toggle {
    ENABLE("Y"), DISABLE("N");
    private String active = "";
    Toggle(String active) {
        this.active = active;
    }

    public String state() {
        return this.active;
    }

    public Boolean booleanValue() {
        return active.isEmpty() ? null : active.equals("Y") ? Boolean.TRUE : Boolean.FALSE;
    }

    public static Toggle get(String active) {
        return ValidationUtil.isNotEmpty(active) && active.equalsIgnoreCase("Y") ? DISABLE : ENABLE;
    }


}
