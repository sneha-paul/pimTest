package com.bigname.pim.api.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeOption extends ValidatableEntity {
    @NotEmpty(message = "Option value cannot be empty")
    private String value;

    @NotEmpty(message = "Option id cannot be empty")
    private String id;

    @NotEmpty(message = "Option fullId cannot be empty")
    private String fullId;

    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    @NotEmpty(message = "Attribute id cannot be empty")
    @Transient
    @JsonIgnore
    private String attributeId;

    public AttributeOption() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullId() {
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
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
        this.active = toYesNo(active, "Y");
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
    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId())) {
            setId(toId(getValue()));
        }
        fullId = attributeId + "|" + getId();
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("value", getValue());
        map.put("active", getActive());

        return map;
    }
}
