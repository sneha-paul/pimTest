package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeGroup extends ValidatableEntity {

    public static final String DEFAULT_GROUP_NAME = "Default Group";
    public static final String DEFAULT_GROUP_ID = "DEFAULT_GROUP";

    @NotEmpty(message = "Group name cannot be empty")
    private String name;

    private String id;
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

    @Transient
    private AttributeGroup parentGroup;

    private Set<AttributeGroup> subGroups = new TreeSet<>();

    private Map<String, Attribute> attributes = new LinkedHashMap<>();

    public AttributeGroup() { }

    public AttributeGroup(String name) {
        this.name = name;
        orchestrate();
    }

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

    public AttributeGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(AttributeGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public Set<AttributeGroup> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Set<AttributeGroup> subGroups) {
        this.subGroups = subGroups;
    }

    public Set<AttributeGroup> addSubGroups(AttributeGroup... groups) {
        ConversionUtil.toList(groups).forEach(group -> getSubGroups().add(group));
        return getSubGroups();
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Attribute> addAttributes(Attribute... attributes) {
        ConversionUtil.toList(attributes).forEach(attribute -> getAttributes().put(attribute.getId(), attribute));
        return getAttributes();
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
        map.put("active", getActive());
        map.put("parentGroup", isNotEmpty(parentGroup) ? parentGroup.getName() : "");
        map.put("attributes", Integer.toString(getAttributes().size()));
        map.put("subGroups", Integer.toString(getSubGroups().size()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeGroup that = (AttributeGroup) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static AttributeGroup getDefaultGroup() {
        return new AttributeGroup(DEFAULT_GROUP_NAME);
    }
}
