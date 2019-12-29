package com.bigname.pim.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VariantGroup extends ValidatableEntity {


    @NotEmpty(message = "Group name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    @NotBlank(message = "Group name cannot be blank", groups = {CreateGroup.class, DetailsGroup.class})
    private String name;
    private String id;
    private String active;
    private int level = 1;
    private long sequenceNum;
    private int subSequenceNum;
    private String familyId;

    @Transient @JsonIgnore
    private Family family;

    private Map<Integer, List<String>> variantAxis = new LinkedHashMap<>();

    private Map<Integer, List<String>> variantAttributes = new LinkedHashMap<>();

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

    public Map<Integer, List<String>> getVariantAxis() {
        return variantAxis;
    }

    public void setVariantAxis(Map<Integer, List<String>> variantAxis) {
        this.variantAxis = variantAxis;
    }

    public Map<Integer, List<String>> getVariantAttributes() {
        return variantAttributes;
    }

    public void setVariantAttributes(Map<Integer, List<String>> variantAttributes) {
        this.variantAttributes = variantAttributes;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("externalId", getId());
        map.put("name", getName());
        map.put("active", getActive());
        map.put("channelVariantGroup", getFamily().getChannelVariantGroups());
        map.put("variantAxis", getVariantAxis().containsKey(1) ? "TODO1, TODO2" : "");
//        map.put("variantAxis2", getVariantAxis().containsKey(2) ? "TODO1, TODO2" : "");
        return map;
    }

    public VariantGroup merge(VariantGroup variantGroup) {
        this.setGroup(variantGroup.getGroup());
        for (String group : variantGroup.getGroup()) {
            switch(group) {
                case "DETAILS":
                    this.setName(variantGroup.getName());
                    this.setActive(variantGroup.getActive());
                    break;
            }
        }
        return this;
    }
}
