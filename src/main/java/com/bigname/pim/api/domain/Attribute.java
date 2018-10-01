package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * Created by manu on 9/4/18.
 */
public class Attribute extends ValidatableEntity {
    @NotEmpty(message = "Attribute name cannot be empty")
    private String name;

    @NotEmpty(message = "Attribute type cannot be empty")
    private String type;

    private Type uiType = Type.INPUT_BOX;
    private String dataType = "string";  //Initial version only supports String type
    private String id;
    private String fullId;
    private String required = "N";
    private String selectable = "N";
    private String active = "Y";
    private long sequenceNum;
    private int subSequenceNum;

   /* @Transient @JsonIgnore
    private String attributeGroupId;

    @Transient
    private String attributeGroupName;*/

    @Transient @JsonIgnore
    private AttributeGroup attributeGroup;

    private Set<AttributeOption> options = new TreeSet<>();

    public Attribute() {}

    public Attribute(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullId() {
        if(isEmpty(fullId) && isNotEmpty(getAttributeGroup())) {
            fullId = getAttributeGroup().getFullId() + "|" + getId();
        }
        return fullId;
    }

    public void setFullId() {
        this.fullId = this.attributeGroup.getFullId() + "|" + this.getId();
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Type getUiType() {
        return uiType;
    }

    public void setUiType(Type uiType) {
        this.uiType = uiType;
        setSelectable(uiType.isSelectable());
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = toYesNo(required, "Y");
    }

    public String getSelectable() {
        return selectable;
    }

    public void setSelectable(String selectable) {
        this.selectable = toYesNo(selectable, "Y");
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

    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    public Attribute setAttributeGroup(AttributeGroup attributeGroup) {
        if(isEmpty(attributeGroup)) {
            attributeGroup = AttributeGroup.getDefaultGroup();
        }
        attributeGroup.addAttributes(this);
        this.attributeGroup = attributeGroup;
        this.setFullId();
        return this;
    }

    public Set<AttributeOption> getOptions() {
        return options;
    }

    public void setOptions(Set<AttributeOption> options) {
        this.options = options;
    }

    public Set<AttributeOption> addOptions(AttributeOption... options) {
        ConversionUtil.toList(options).forEach(option -> getOptions().add(option));
        return getOptions();
    }

    @Override
    public void orchestrate() {
        setRequired(getRequired());
        setSelectable(getSelectable());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("fullId", getFullId());
        map.put("uiType", getUiType().name());
        map.put("dataType", getDataType());
        map.put("name", getName());
        map.put("group", getAttributeGroup().getName());
        map.put("required", getRequired());
        map.put("selectable", getSelectable());
        map.put("options", Integer.toString(options.size()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return id.equals(attribute.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public enum Type {
        INPUT_BOX("Input Box", "N"), DROPDOWN("Dropdown", "Y"), CHECKBOX("Checkbox", "Y"), TEXTAREA("Textarea", "N");
        String label = "";
        String selectable = "N";
        Type(String label, String selectable) {
            this.label = label;
            this.selectable = selectable;
        }

        public String getLabel() {
            return label;
        }

        public String isSelectable() {
            return selectable;
        }

        public static Type get(String value) {
            for (Type type : values()) {
                if(type.name().equals(value)) {
                    return type;
                }
            }
            return Type.INPUT_BOX;

        }
    }
}
