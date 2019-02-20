package com.bigname.pim.api.domain;
import com.bigname.core.domain.ValidatableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AttributeOption extends ValidatableEntity {
    @NotEmpty(message = "Option value cannot be empty")
    private String value;

    @Transient
//    @NotEmpty(message = "Option id cannot be empty") - TODO - verify if this is required
    private String id;

    @NotEmpty(message = "Option fullId cannot be empty")
    private String fullId;

    private String parentOptionFullId;
    private String independent = "Y";

    @Transient
    private String parentOptionValue;

    private String active = "Y";

    private long sequenceNum;
    private int subSequenceNum;

    //TODO - this needs to be handled in the future, since this lookup is somewhat expensive
    //Reference map of usage. Key will be familyId and values will be familyAttributeOptionFullIds.
    private Map<String, List<String>> referenceMap = new HashMap<>();

    @NotEmpty(message = "Attribute id cannot be empty")
    @Transient
    @JsonIgnore
    private String attributeId;

    @Transient
    @JsonIgnore
    private String collectionId;

    public AttributeOption() {
    }

    public String getId() {
        if(isEmpty(id) && isNotEmpty(fullId)) {
            List<String> idChain = getPipedValues(fullId);
            id = idChain.get(idChain.size() - 1);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.toUpperCase() : null;
        if(isNotEmpty(fullId)) {
            fullId = null;
            orchestrate();
        }
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
        orchestrate();
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
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

    public String getIndependent() {
        return independent;
    }

    public void setIndependent(String independent) {
        this.independent = toYesNo(independent, "Y");
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

    public String getParentOptionValue() {
        return parentOptionValue;
    }

    public void setParentOptionValue(String parentOptionValue) {
        this.parentOptionValue = parentOptionValue;
    }

    public Map<String, List<String>> getReferenceMap() {
        return referenceMap;
    }

    public void setReferenceMap(Map<String, List<String>> referenceMap) {
        this.referenceMap = referenceMap;
    }

    public AttributeOption merge(AttributeOption attributeOption) {
        this.setValue(attributeOption.getValue());
        this.setParentOptionFullId(attributeOption.getParentOptionFullId());
        //TODO - update attributeOption id - If the attributeOption id is used in familyAttributes, then we should update all those references

        return this;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        setIndependent(getIndependent());
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
        map.put("fullId", getCollectionId() + "|" + getFullId());
        map.put("parent", getParentOptionValue());
        return map;
    }
}
