package com.bigname.pim.api.domain;

import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VariantGroup extends ValidatableEntity {

    private String id;
    private String name;
    private String active;
    private int level = 1;
    private long sequenceNum;
    private int subSequenceNum;
    private String familyId;
    private Map<Integer, List<FamilyAttributeGroup>> variantAxis = new LinkedHashMap<>();

    private Map<Integer, List<FamilyAttributeGroup>> variantAttributes = new LinkedHashMap<>();

    public VariantGroup() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = toYesNo(active, "Y");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public Map<Integer, List<FamilyAttributeGroup>> getVariantAxis() {
        return variantAxis;
    }

    public void setVariantAxis(Map<Integer, List<FamilyAttributeGroup>> variantAxis) {
        this.variantAxis = variantAxis;
    }

    public Map<Integer, List<FamilyAttributeGroup>> getVariantAttributes() {
        return variantAttributes;
    }

    public void setVariantAttributes(Map<Integer, List<FamilyAttributeGroup>> variantAttributes) {
        this.variantAttributes = variantAttributes;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("name", getName());
        map.put("variantAxis1", getVariantAxis().containsKey(1) ? "TODO1, TODO2" : "");
        map.put("variantAxis2", getVariantAxis().containsKey(2) ? "TODO1, TODO2" : "");
        return map;
    }

    
}
