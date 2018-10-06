package com.bigname.pim.api.domain;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeGroup extends ValidatableEntity {

    public static final String DETAILS_GROUP = "Details Group";
    public static final String FEATURES_GROUP = "Features Group";
    public static final String DEFAULT_GROUP = "Default Group";
    public static final String DETAILS_GROUP_ID = getDetailsMasterGroup(null).getId();
    public static final String FEATURES_GROUP_ID = getFeaturesMasterGroup(null).getId();
    public static final String DEFAULT_GROUP_ID = "DEFAULT_GROUP";
    /**
     * Name of the Group
     */
    @NotEmpty(message = "Group name cannot be empty")
    private String name;

    private String label;

    private String id;

    private String fullId;

    private String defaultGroup = "N";

    private String active = "Y";

    private long sequenceNum;

    private int subSequenceNum;

    private String masterGroup = "N";

    @Transient
    @JsonIgnore
    private AttributeGroup parentGroup;

    private Map<String, AttributeGroup> childGroups = new LinkedHashMap<>();

    private Map<String, Attribute> attributes = new LinkedHashMap<>();

    public AttributeGroup() {}

    public AttributeGroup(String name, String label, boolean masterGroup) {
        this.name = name;
        this.label = isNotEmpty(label) ? label : name;
        this.setMasterGroup(masterGroup ? "Y" : "N");
        if(DEFAULT_GROUP.equals(this.name)) {
            setDefaultGroup("Y");
        }
        orchestrate();
    }

    public AttributeGroup(String name, String label, AttributeGroup parentGroup) {
        this(name, label, false);
        this.parentGroup = parentGroup;
        this.parentGroup.getChildGroups().put(this.getId(), this);
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

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = toYesNo(defaultGroup, "Y");
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = toYesNo(active, "Y");
    }

    public String getMasterGroup() {
        return masterGroup;
    }

    public void setMasterGroup(String masterGroup) {
        this.masterGroup = toYesNo(masterGroup, "Y");
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
                this.getChildGroups().forEach((k, c) -> c.setFullId(c.getFullId(c)));
            }
        }
        return this;
    }

    public Map<String, AttributeGroup> getChildGroups() {
        return childGroups;
    }

    public void setChildGroups(Map<String, AttributeGroup> childGroups) {
        this.childGroups = childGroups;
    }

    public AttributeGroup addChildGroup(AttributeGroup childGroup) {
        if(isNotEmpty(childGroup)) {
            if(!childGroups.containsKey(childGroup.getId())) {
                childGroups.put(childGroup.getId(), childGroup);
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
        if(isNotEmpty(attributeGroup.getParentGroup()) && !attributeGroup.getParentGroup().isEmpty()) {
            fullId += getFullId(attributeGroup.getParentGroup()) + "|" + attributeGroup.getId();
        } else {
            fullId = attributeGroup.getId();
        }
        return fullId;
    }

    private static AttributeGroup getLeafGroup(List<String> groupIds, Map<String, AttributeGroup> groups) {
        if(isNotEmpty(groupIds, groupIds.get(0)) && groups.containsKey(groupIds.get(0))) {
            if(groupIds.size() > 1) {
                AttributeGroup child = getLeafGroup(groupIds.subList(1, groupIds.size()), groups.get(groupIds.get(0)).getChildGroups());
                AttributeGroup parent = groups.get(groupIds.get(0));
                setParent(child, parent);
                return child;
            } else {
                return groups.get(groupIds.get(0));
            }
        } else {
            return null;
        }
    }

    private static void setParent(AttributeGroup child, AttributeGroup parent) {
        if(!booleanValue(child.getMasterGroup())) {
            if (isEmpty(child.getParentGroup())) {
                child.setParentGroup(parent);
            } else {
                setParent(child.getParentGroup(), parent);
            }
        }
    }

    public static AttributeGroup getLeafGroup(String fullId, Map<String, AttributeGroup> familyMasterGroups) {
        return getLeafGroup(getPipedValues(fullId), familyMasterGroups);
    }

    public static AttributeGroup getMasterGroup(String fullId, Map<String, AttributeGroup> familyMasterGroups) {
        List<String> groupIds = getPipedValues(fullId);
        if(isNotEmpty(groupIds) && familyMasterGroups.containsKey(groupIds.get(0))) {
            return familyMasterGroups.get(groupIds.get(0));
        } else {
            return null;
        }
    }


    public static boolean addAttribute(Attribute attribute, Map<String, AttributeGroup> familyAttributeGroups) {
        boolean success = false;
//        attribute = attribute.getOrchestratedInstance(attribute);
        AttributeGroup attributeMasterGroup = attribute.getAttributeGroup().getParentGroup().getParentGroup();
        if(isNotEmpty(attributeMasterGroup)) {
            AttributeGroup familyMasterGroup = familyAttributeGroups.get(attributeMasterGroup.getId());
            if(isNotEmpty(familyMasterGroup)) {
                AttributeGroup attributeLevel2Group = attributeMasterGroup.getChildGroups().get(DEFAULT_GROUP_ID);
                AttributeGroup familyLevel2Group = familyMasterGroup.getChildGroups().get(DEFAULT_GROUP_ID);
                if(isNotEmpty(familyLevel2Group, attributeLevel2Group, attribute.getAttributeGroup())) {
                    AttributeGroup attributeLeafGroup = getLeafGroup(attribute.getAttributeGroup().getFullId(), familyAttributeGroups);
                    if(isNotEmpty(attributeLeafGroup)) {
                        AttributeGroup familyLeafGroup = familyLevel2Group.getChildGroups().get(attributeLeafGroup.getId());
                        if(isEmpty(familyLeafGroup)) {
                            familyLeafGroup = new AttributeGroup(attributeLeafGroup.getName(), attributeLeafGroup.getLabel(), familyLevel2Group);
                        }
                        attribute.setAttributeGroup(familyLeafGroup);
                        familyLeafGroup.getAttributes().put(attribute.getId(), attribute);
                        success = true;
                    }
                }
            } else {
                familyAttributeGroups.put(attributeMasterGroup.getId(), attributeMasterGroup);
                success = true;
            }
        }
        return success;
    }

    public static AttributeGroup getDetailsMasterGroup(String label) {
        return new AttributeGroup(DETAILS_GROUP, isEmpty(label) ? "Details" : label, true);
    }

    public static AttributeGroup getFeaturesMasterGroup(String label) {
        return new AttributeGroup(FEATURES_GROUP, isEmpty(label) ? "Features" : label, true);
    }

    public static AttributeGroup getDefaultGroup(String name, String label, AttributeGroup parentGroup) {
        return new AttributeGroup(name, label, parentGroup);
    }

    public static AttributeGroup createDefaultMasterGroup(boolean isSelectable) {
        if(isSelectable) {
            return createDefaultFeaturesGroup().getParentGroup().getParentGroup();
        } else {
            return createDefaultDetailsGroup().getParentGroup().getParentGroup();
        }
    }

    public static AttributeGroup createDefaultLeafGroup(boolean isSelectable) {
        if(isSelectable) {
            return createDefaultFeaturesGroup();
        } else {
            return createDefaultDetailsGroup();
        }
    }

    public static AttributeGroup createDefaultDetailsGroup() {
        return new AttributeGroup(AttributeGroup.DEFAULT_GROUP, "Details Group", new AttributeGroup(AttributeGroup.DEFAULT_GROUP, "Default Group(L2)", AttributeGroup.getDetailsMasterGroup("Details")));
    }

    public static AttributeGroup createDefaultFeaturesGroup() {
        return new AttributeGroup(AttributeGroup.DEFAULT_GROUP, "Features Group", new AttributeGroup(AttributeGroup.DEFAULT_GROUP, "Default Group(L2)", AttributeGroup.getFeaturesMasterGroup("Product Features")));
    }

    public static AttributeGroup createMasterGroup(String name, String label) {
        return createLeafGroup(name, label).getParentGroup().getParentGroup();
    }

    public static AttributeGroup createLeafGroup(String name, String label) {
        return new AttributeGroup(AttributeGroup.DEFAULT_GROUP, isEmpty(label) ? name : label, new AttributeGroup(AttributeGroup.DEFAULT_GROUP, "Default Group(L2)",new AttributeGroup(name, label, true)));
    }

    public static AttributeGroup createLeafGroup(String name, String label, AttributeGroup masterGroup) {
        if(isNotEmpty(masterGroup, masterGroup.getChildGroups(), masterGroup.getChildGroups().get(DEFAULT_GROUP_ID))) {
            return new AttributeGroup(name, label, masterGroup.getChildGroups().get(DEFAULT_GROUP_ID));
        } else {
            return null;
        }
    }

    public static List<AttributeGroup> getAllAttributeGroups(Map<String, AttributeGroup> groups, String mode, boolean firstRun, int... level) {
        final List<AttributeGroup> attributeGroups = new ArrayList<>();
        tune(groups, null);
        final List<AttributeGroup> groupsList = new ArrayList<>();
        // This will happen only when no attributes registered yet for an attribute group
        // We have two default master groups, details group and features group.
        if(firstRun ) { //This is the first run of the recursion

            if(!groups.containsKey(AttributeGroup.DETAILS_GROUP_ID)) {
                //Won't contain the default details master group, so add it
                groupsList.add(AttributeGroup.createDefaultMasterGroup(false));
            }

            if(!groups.containsKey(AttributeGroup.FEATURES_GROUP_ID)) {
                //Won't contain the default features master group, so add it
                groupsList.add(AttributeGroup.createDefaultMasterGroup(true));
            }
        }


        /*if(firstRun) {
            List<AttributeGroup> masterGroups = getAllAttributeGroups(groups, "MASTER_ONLY", true);
            List<AttributeGroup> defaultMasterGroups = masterGroups.stream().filter(g ->
                    g.getFullId().equals(AttributeGroup.getDetailsMasterGroup(null).getFullId()) ||
                            g.getFullId().equals(AttributeGroup.getFeaturesMasterGroup(null).getFullId())).collect(Collectors.toList());


        }*/

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
            case "MASTER_ONLY":
                attributeGroups.addAll(groupsList.stream().filter(g -> ValidationUtil.isEmpty(g.getParentGroup())).collect(Collectors.toList()));
                break;
            case "NON_MASTER_PARENT_ONLY":
                attributeGroups.addAll(groupsList.stream().filter(g -> ValidationUtil.isNotEmpty(g.getParentGroup()) && ValidationUtil.isNotEmpty(g.getChildGroups())).collect(Collectors.toList()));
                break;
            case "LEVEL":
                int _level = level == null || level.length != 1 || level[0] < 1 || level[0] > 3 ? 3 : level[0];
                groupsList.forEach(g -> {
                    if(g.getLevel() == _level) {
                        attributeGroups.add(g);
                    } else if(isNotEmpty(g.getChildGroups())){
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false, _level));
                    }
                });
                break;
            case "LEAF_ONLY":
                groupsList.forEach(g -> {
                    if(isEmpty(g.getChildGroups())) {
                        attributeGroups.add(g);
                    } else {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
                break;
            default:
                groupsList.forEach(g -> {
                    attributeGroups.add(g);
                    if(isEmpty(g.getChildGroups())) {
                        attributeGroups.addAll(getAllAttributeGroups(g.getChildGroups(), mode, false));
                    }
                });
        }
        return attributeGroups;
    }

    public static void tune(Map<String, AttributeGroup> attributeGroups, AttributeGroup parent) {

        attributeGroups.forEach((k, attributeGroup) -> {
            if(isNotEmpty(parent)) {
                attributeGroup.setParentGroup(parent);
            }
            if(isNotEmpty(attributeGroup.getChildGroups())) {
                tune(attributeGroup.getChildGroups(), attributeGroup);
            } else if(isNotEmpty(attributeGroup.getAttributes())) {
                attributeGroup.getAttributes().forEach((k1, attribute) -> attribute.setAttributeGroup(attributeGroup));
            }
        });
    }

}
