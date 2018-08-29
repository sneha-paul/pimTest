package com.bigname.pim.util;

public enum Toggle {
    ENABLE("Y"), DISABLE("N"), ACTIVATE("Y"), DEACTIVATE("N");
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
}
