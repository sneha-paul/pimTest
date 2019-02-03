package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeGroup extends ValidatableEntity {

    public static final String DEFAULT_GROUP = "Default Group";
    public static final String DEFAULT_GROUP_ID = "DEFAULT_GROUP";

    private String name;
    private String label;
    private String id;
    private String fullId;

    private String active = "Y";

    private long sequenceNum;

    private int subSequenceNum;

    @Transient
    @JsonIgnore
    private AttributeGroup parentGroup;

    private Map<String, AttributeGroup> childGroups = new LinkedHashMap<>();

    private Map<String, Attribute> attributes = new LinkedHashMap<>();

    public AttributeGroup() {}

    public AttributeGroup(String name, String label) {
        this.name = name;
        this.label = isNotEmpty(label) ? label : name;
        orchestrate();
    }

    public AttributeGroup(String name, String label, AttributeGroup parentGroup) {
        this(name, label);
        this.parentGroup = parentGroup;
        if(isNotEmpty(parentGroup)) {
            this.parentGroup.getChildGroups().put(this.getId(), this);
        }
        orchestrate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        if(isEmpty(label)) {
            label = getName();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEmpty() {
        return isEmpty(id) && isEmpty(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullId() {
        if(isEmpty(fullId)) {
            fullId = getFullId(this);
        }
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
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

    public AttributeGroup setParentGroup(AttributeGroup parentGroup) {
        this.parentGroup = parentGroup;
        if(!parentGroup.isEmpty()) {
            setFullId(getFullId(this));
            if(isNotEmpty(this.getChildGroups())) {
                this.getChildGroups().forEach((k, child) -> child.setFullId(child.getFullId(child)));
            }
        }
        return this;
    }

    public Map<String, AttributeGroup> getChildGroups() {
        return childGroups;
    }

    private void setChildGroups(Map<String, AttributeGroup> childGroups) {
        this.childGroups = childGroups;
    }

    public AttributeGroup addChildGroup(AttributeGroup childGroup) {
        if(isNotEmpty(childGroup)) {
            if(!this.childGroups.containsKey(childGroup.getId())) {
                childGroup.setParentGroup(this);
                this.childGroups.put(childGroup.getId(), childGroup);
            }
        }
        return this;
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void orchestrate() {
        setActive(getActive());
        if(isEmpty(getId())) {
            setId(toId(getName()));
        }
        setFullId(getFullId(this));
    }

    public int getLevel() {
        return getPipedValues(getFullId()).size();
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("name", getName());
        map.put("active", getActive());
        map.put("parentGroup", isNotEmpty(parentGroup) ? parentGroup.getName() : "");
        map.put("numOfAttributes", Integer.toString(getAttributes().size()));
        map.put("numOfChildGroups", Integer.toString(getChildGroups().size()));
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeGroup that = (AttributeGroup) o;

        return this.getFullId(this).equals(that.getFullId(that));
    }

    @Override
    public int hashCode() {
        return getFullId(this).hashCode();
    }

    private String getFullId(AttributeGroup attributeGroup) {
        String fullId = "";
        if(isNotEmpty(attributeGroup.getId())) {
            if (isNotEmpty(attributeGroup.getParentGroup()) && !attributeGroup.getParentGroup().isEmpty()) {
                fullId += getFullId(attributeGroup.getParentGroup());
            }

            if (!attributeGroup.isEmpty()) {
                fullId += (isNotEmpty(fullId) ? "|" : "") + attributeGroup.getId();
            }
        }
        return fullId;
    }


    //##################################################################################################################//
    //                                              STATIC HELPER METHODS                                               //
    //##################################################################################################################//

    public static Map<String, AttributeGroup> map(Map<String, AttributeGroup> attributeGroups, AttributeGroup... parent) {

        attributeGroups.forEach((k, attributeGroup) -> {
            if(isNotEmpty(ConversionUtil.toList(parent))) {
                attributeGroup.setParentGroup(parent[0]);
            }
            if(isNotEmpty(attributeGroup.getChildGroups())) {
                map(attributeGroup.getChildGroups(), attributeGroup);
            }

            if(isNotEmpty(attributeGroup.getAttributes())) {
                attributeGroup.getAttributes().forEach((k1, attribute) -> attribute.setAttributeGroup(attributeGroup));
            }
        });
        return attributeGroups;
    }

    public static AttributeGroup getAttributeGroup(String fullId, Map<String, AttributeGroup> mappedGroups) {
        List<String> groupIds = getPipedValues(fullId);
        Map<String, AttributeGroup> groups = mappedGroups;
        AttributeGroup attributeGroup = null;
        for (String groupId : groupIds) {
            if (groups.containsKey(groupId)) {
                attributeGroup = groups.get(groupId);
                groups = attributeGroup.getChildGroups();
            } else {
                return null;
            }
        }
        return attributeGroup;
    }

    public static List<String> getAllAttributeIds(Map<String, AttributeGroup> groups) {
        return getAllAttributes(groups).stream().map(Attribute::getId).collect(Collectors.toList());
    }

    public static List<Attribute> getAllAttributes(Map<String, AttributeGroup> groups) {
        List<Attribute> attributes = new ArrayList<>();
        getAllAttributeGroups(groups, GetMode.ALL, true).forEach(attributeGroup -> attributeGroup.getAttributes().forEach((s, attribute) -> attributes.add(attribute)));
        return attributes;
    }

    public static List<AttributeGroup> getAllAttributeGroups(Map<String, AttributeGroup> groups, GetMode mode, boolean firstRun, int... level) {
        final List<AttributeGroup> attributeGroups = new ArrayList<>();
        AttributeGroup.map(groups);
        final List<AttributeGroup> groupsList = new ArrayList<>();
        // This will happen only when no attributes registered yet for an attribute group

        if(firstRun ) { //This is the first run of the recursion
            if(!groups.containsKey(AttributeGroup.DEFAULT_GROUP_ID)) {
                //Won't contain the default group, so add it
                groupsList.add(new AttributeGroup(DEFAULT_GROUP, null));
            }
        }

        //Extract all the groups from the map to a list for easy processing
        groupsList.addAll(groups.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));

        //Sort all the groups - sequenceNum ASC and subSequenceNum DESC
        groupsList.sort((o1, o2) -> {
            if(o1.getSequenceNum() == o2.getSequenceNum()) {
                return o2.getSequenceNum() < o1.getSequenceNum() ? -1 : o2.getSequenceNum() > o1.getSequenceNum() ? 1 : 0;
            } else {
                return o1.getSequenceNum() < o2.getSequenceNum() ? -1 : o1.getSequenceNum() > o2.getSequenceNum() ? 1 : 0;
            }
        });

        switch (mode) {
            case TOP_LEVEL:
                attributeGroups.addAll(groupsList.stream().filter(g -> ValidationUtil.isEmpty(g.getParentGroup())).collect(Collectors.toList()));
                break;
            case LEAF_ONLY:
                groupsList.forEach(g -> {
                    if(isEmpty(g.getChildGroups())) {
                        attributeGroups.add(g);
                    } else {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
                break;
            case ALL:
                groupsList.forEach(g -> {
                    attributeGroups.add(g);
                    if(isNotEmpty(g.getChildGroups())) {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
        }
        return attributeGroups;
    }

    public static String getFullGroupLabel(AttributeGroup mappedGroup, String separator) {
        String fullLabel = "";
        if(isNotEmpty(mappedGroup)) {
            fullLabel = mappedGroup.getLabel();
            AttributeGroup group = mappedGroup;
            while(isNotEmpty(group.getParentGroup())) {
                group = group.getParentGroup();
                fullLabel = group.getLabel() + separator + fullLabel;
            }
        }
        return fullLabel;
    }

    public static AttributeGroup getDefaultGroup() {
        return new AttributeGroup(DEFAULT_GROUP, null);
    }

    public enum GetMode {
        TOP_LEVEL,
        LEVEL,
        LEAF_ONLY,
        ALL

    }
}
