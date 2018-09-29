package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;

import javax.validation.constraints.NotEmpty;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FeatureOption extends ValidatableEntity {
    private String name;
    @NotEmpty(message = "Feature option value cannot be empty")
    private String value;
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    public FeatureOption() {
    }

    public FeatureOption(String name, String value, String active) {
        if(ValidationUtil.isEmpty(name)) {

        }
        this.name = name;
        this.value = value;
        this.active = "Y".equalsIgnoreCase(active)  ? "Y" : "N";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public int getSubSequenceNum() {
        return subSequenceNum;
    }

    public void setSubSequenceNum(int subSequenceNum) {
        this.subSequenceNum = subSequenceNum;
    }
}
