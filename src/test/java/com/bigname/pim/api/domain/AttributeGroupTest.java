package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AttributeGroupTest {
    Map<String, AttributeGroup> groups = new LinkedHashMap<>();
    @Before
    public void setUp() throws Exception {
        AttributeGroup D = new AttributeGroup("D", "Default Group");
        AttributeGroup A1 = new AttributeGroup("A1", "Level 1 A");
        AttributeGroup B1 = new AttributeGroup("B1", "Level 1 B");
        AttributeGroup C1 = new AttributeGroup("C1", "Level 1 C");
        AttributeGroup D1 = new AttributeGroup("D1", "Level 1 D");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAttributeGroup() throws Exception {
        assertEquals(new AttributeGroup(AttributeGroup.DEFAULT_GROUP, null).getId(), AttributeGroup.DEFAULT_GROUP_ID);
        assertEquals(new AttributeGroup("Child Group", null, new AttributeGroup("Parent Group", null)).getId(), "CHILD_GROUP");
        assertEquals(new AttributeGroup("Child Group", null, new AttributeGroup("Parent Group", null)).getFullId(), "PARENT_GROUP|CHILD_GROUP");
        Map<String, AttributeGroup> groups = new LinkedHashMap<>();
        Attribute attributeDTO1 = new Attribute();
        attributeDTO1.setName("Name");
        Attribute attribute1 = Attribute.buildInstance(attributeDTO1, groups);
        assertEquals(attribute1.getAttributeGroup().getId(), AttributeGroup.DEFAULT_GROUP_ID);
        assertEquals(attribute1.getAttributeGroup().getFullId(), AttributeGroup.DEFAULT_GROUP_ID);
        assertEquals(attribute1.getAttributeGroup().getChildGroups().size(), 0);
        groups.put(attribute1.getAttributeGroup().getId(), attribute1.getAttributeGroup());
        assertEquals(groups.size(), 1);

        Attribute attributeDTO2 = new Attribute();
        AttributeGroup attributeGroupDTO2 = new AttributeGroup();
        attributeDTO2.setName("Height");
        attributeGroupDTO2.setName("Measurements");
        attributeDTO2.setAttributeGroup(attributeGroupDTO2);
        Attribute attribute2 = Attribute.buildInstance(attributeDTO2, groups);
        assertEquals(attribute2.getAttributeGroup().getId(), "MEASUREMENTS");
        assertEquals(attribute2.getAttributeGroup().getFullId(), "MEASUREMENTS");
        assertEquals(attribute2.getAttributeGroup().getChildGroups().size(), 0);
        assertEquals(attribute2.getAttributeGroup().getAttributes().size(), 1);
        assertTrue(ValidationUtil.isEmpty(attribute2.getAttributeGroup().getParentGroup()));
        groups.put(attribute2.getAttributeGroup().getId(), attribute2.getAttributeGroup());
        assertEquals(groups.size(), 2);

        Attribute attributeDTO3 = new Attribute();
        AttributeGroup attributeGroupDTO3 = new AttributeGroup();
        attributeGroupDTO3.setFullId(attribute2.getAttributeGroup().getFullId());
        attributeDTO3.setName("Weight");
        attributeDTO3.setAttributeGroup(attributeGroupDTO3);
        Attribute attribute3 = Attribute.buildInstance(attributeDTO3, groups);
        assertEquals(attribute3.getAttributeGroup().getId(), "MEASUREMENTS");
        assertEquals(attribute3.getAttributeGroup().getFullId(), "MEASUREMENTS");
        assertEquals(attribute3.getAttributeGroup().getChildGroups().size(), 0);
        assertEquals(attribute3.getAttributeGroup().getAttributes().size(), 2);
        assertTrue(ValidationUtil.isEmpty(attribute3.getAttributeGroup().getParentGroup()));
        assertEquals(groups.size(), 2);

        Attribute attributeDTO4 = new Attribute();
        AttributeGroup attributeGroupDTO4 = new AttributeGroup();
        attributeDTO4.setName("Color");
        attributeGroupDTO4.setName("Appearance");
        attributeDTO4.setAttributeGroup(attributeGroupDTO4);
        Attribute attribute4 = Attribute.buildInstance(attributeDTO4, groups);
        assertEquals(attribute4.getAttributeGroup().getId(), "APPEARANCE");
        assertEquals(attribute4.getAttributeGroup().getFullId(), "APPEARANCE");
        assertEquals(attribute4.getAttributeGroup().getChildGroups().size(), 0);
        assertEquals(attribute4.getAttributeGroup().getAttributes().size(), 1);
        assertTrue(ValidationUtil.isEmpty(attribute4.getAttributeGroup().getParentGroup()));
        groups.put(attribute4.getAttributeGroup().getId(), attribute4.getAttributeGroup());
        assertEquals(groups.size(), 3);

        Attribute attributeDTO5 = new Attribute();
        attributeDTO5.setName("Title");
        Attribute attribute5 = Attribute.buildInstance(attributeDTO5, groups);
        assertEquals(attribute5.getAttributeGroup().getId(), AttributeGroup.DEFAULT_GROUP_ID);
        assertEquals(attribute5.getAttributeGroup().getFullId(), AttributeGroup.DEFAULT_GROUP_ID);
        assertEquals(attribute5.getAttributeGroup().getChildGroups().size(), 0);
        assertEquals(groups.size(), 3);

        Attribute attributeDTO6 = new Attribute();
        AttributeGroup attributeGroupDTO6 = new AttributeGroup();
        attributeDTO6.setName("Size Code");
        attributeGroupDTO6.setName("Size");
        attributeGroupDTO6.setParentGroup(new AttributeGroup());
        attributeGroupDTO6.getParentGroup().setFullId(attribute2.getAttributeGroup().getFullId());
        attributeDTO6.setAttributeGroup(attributeGroupDTO6);
        Attribute attribute6 = Attribute.buildInstance(attributeDTO6, groups);
        assertEquals(attribute6.getAttributeGroup().getId(), "SIZE");
        assertEquals(attribute6.getAttributeGroup().getFullId(), "MEASUREMENTS|SIZE");
        assertEquals(attribute6.getAttributeGroup().getChildGroups().size(), 0);
        assertEquals(attribute6.getAttributeGroup().getParentGroup().getChildGroups().size(), 1);
        assertEquals(attribute6.getAttributeGroup().getAttributes().size(), 1);
        assertTrue(ValidationUtil.isNotEmpty(attribute6.getAttributeGroup().getParentGroup()));
        assertEquals(groups.size(), 3);
        System.out.println(groups);


    }

}