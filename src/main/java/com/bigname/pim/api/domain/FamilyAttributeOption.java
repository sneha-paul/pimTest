package com.bigname.pim.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class FamilyAttributeOption extends ValidatableEntity {
    @NotEmpty(message = "Option value cannot be empty")
    private String value;

    @NotEmpty(message = "Option id cannot be empty")
    private String id;

    @Transient
    private String fullId;

    private String parentOptionFullId;

    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    //Reference map of usage. Key will be channelId and values will be productIds.
    private Map<String, List<String>> referenceMap = new HashMap<>();

    @NotEmpty(message = "Attribute option id cannot be empty", groups = {AddOptionGroup.class})
    @Transient @JsonIgnore
    private String attributeOptionId;

    @NotEmpty(message = "Attribute id cannot be empty", groups = {AddOptionGroup.class})
    @Transient @JsonIgnore
    private String familyAttributeId;

    public FamilyAttributeOption() {}

    public FamilyAttributeOption(FamilyAttributeOption familyAttributeOption, AttributeOption attributeOption) {
        this.value = attributeOption.getValue();
        this.id = attributeOption.getId();
        this.familyAttributeId = familyAttributeOption.getFamilyAttributeId();
//        this.attributeOptionId = attributeOption.getId();
        orchestrate();
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

    public String getFamilyAttributeId() {
        return familyAttributeId;
    }

    public void setFamilyAttributeId(String familyAttributeId) {
        this.familyAttributeId = familyAttributeId;
    }

    public String getAttributeOptionId() {
        return attributeOptionId;
    }

    public void setAttributeOptionId(String attributeOptionId) {
        this.attributeOptionId = attributeOptionId;
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

    public String getParentOptionFullId() {
        return parentOptionFullId;
    }

    public void setParentOptionFullId(String parentOptionFullId) {
        this.parentOptionFullId = parentOptionFullId;
    }

    public Map<String, List<String>> getReferenceMap() {
        return referenceMap;
    }

    public void setReferenceMap(Map<String, List<String>> referenceMap) {
        this.referenceMap = referenceMap;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId()) && isNotEmpty(getValue())) {
            setId(toId(getValue()));
        }
        if(isNotEmpty(getId())) {
            fullId = familyAttributeId + "|" + getId();
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("value", getValue());
        map.put("active", getActive());

        return map;
    }

    public interface AddOptionGroup {}
}
