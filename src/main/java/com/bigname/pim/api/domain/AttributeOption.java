package com.bigname.pim.api.domain;
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

    private String id;
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    public AttributeOption() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("value", getValue());
        map.put("active", getActive());

        return map;
    }

}
