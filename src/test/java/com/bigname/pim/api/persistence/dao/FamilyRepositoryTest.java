package com.bigname.pim.api.persistence.dao;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by dona on 22-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FamilyRepositoryTest {
    @Autowired
    FamilyDAO familyDAO;

    @Before
    public void setUp() {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void createFamilyTest() {
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        Family family = familyDAO.insert(familyDTO);
        Assert.assertTrue(family.diff(familyDTO).isEmpty());
    }

    @Test
    public void retrieveFamilyTest() {
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyDAO.insert(familyDTO);
        Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
        Assert.assertTrue(family.isPresent());
        family = familyDAO.findById(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(family.isPresent());
        family = familyDAO.findById(familyDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(family.isPresent());
    }

    @Test
    public void updateFamilyTest() {
        Attribute attribute = new Attribute();
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setDataType("string");
        attribute.setLabel("Style");
        attribute.setName("Style");
        attribute.setActive("Y");
        attribute.setId("STYLE");

        FamilyAttribute familyAttribute = new FamilyAttribute();
        familyAttribute.setAttribute(attribute);

        Map<String, FamilyAttribute> attributes = new LinkedHashMap<>();
        attributes.put("STYLE",familyAttribute);

        FamilyAttributeGroup familyAttributeGroup =  new FamilyAttributeGroup();
        familyAttributeGroup.setAttributes(attributes);
        familyAttributeGroup.setName("Default Group");
        familyAttributeGroup.setLabel("Default Group");
        familyAttributeGroup.setFullId("Default Group");
        familyAttributeGroup.setActive("Y");
        familyAttributeGroup.setId("DEFAULT_GROUP");

        Map<String, FamilyAttributeGroup> attributeGroupMap = new HashMap<>();
        attributeGroupMap.put("DEFAULT_GROUP", familyAttributeGroup);

        FamilyAttributeGroup familyAttributeGroup1 = new FamilyAttributeGroup();
        familyAttributeGroup1.setChildGroups(attributeGroupMap);

       /* Map<String, FamilyAttributeGroup> stringFamilyAttributeGroupMap = new HashMap<>();
        stringFamilyAttributeGroupMap.put("childGroups",familyAttributeGroup1);*/

        Map<String, FamilyAttributeGroup> attributeGroupMap1 = new HashMap<>();
        attributeGroupMap1.put("DETAILS_GROUP", familyAttributeGroup1);


       Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyDTO.setAttributes(attributeGroupMap1);
        familyDAO.insert(familyDTO);
        Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
        Assert.assertTrue(family.isPresent());
    }


    @After
    public void tearDown() {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }
}
