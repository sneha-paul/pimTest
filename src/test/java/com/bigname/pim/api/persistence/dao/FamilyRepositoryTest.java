package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.domain.ValidatableEntity;
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

import java.util.*;

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

    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;

    @Before
    public void setUp() {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void createFamilyTest() {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "Y", "discontinue", "N"));

        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            Family family = familyDAO.insert(familyDTO);
            Assert.assertTrue(family.diff(familyDTO).isEmpty());
        });

        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void retrieveFamilyTest() {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "Y", "discontinue", "N"));

        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
            Assert.assertTrue(family.isPresent());
            Assert.assertTrue(family != null);
        });

        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void updateFamilyTest() {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
            Assert.assertTrue(family.isPresent());
            Assert.assertTrue(family != null);

            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
            familyAttributeGroup.setId(familyAttributeGroup.getFullId());

            //Create the new familyAttribute instance
            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attribute.getName(), null);
            familyAttributeDTO.setActive("Y");
            familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
            familyAttributeDTO.setUiType(attribute.getUiType());
            familyAttributeDTO.setScopable("Y");
            familyAttributeDTO.setAttributeId(attribute.getFullId());
            familyAttributeDTO.getScope().put("ECOMMERCE", FamilyAttribute.Scope.OPTIONAL);
            familyAttributeDTO.setAttributeGroup(familyAttributeGroup); //TODO : check whether right or wrong
            familyAttributeDTO.setAttribute(attribute);

            family.get().addAttribute(familyAttributeDTO);

            //create variantGroup
            family.get().setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            family.get().addVariantGroup(variantGroup);

            familyDAO.save(family.get());

            //TODO : can't set FamilyAttributeOption : set
            /*Family family1 = familyDAO.findByExternalId(familiesData.get(0).get("externalId").toString()).orElse(null);
            Optional<FamilyAttribute> familyAttributeDetails = family1.getAttribute(attribute.getFullId()); // null pointer exception occurs

            FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
            familyAttributeOption.setActive("Y");
            familyAttributeOption.setValue(attributeOption.getValue());
            familyAttributeOption.setId(attributeOption.getId());
            familyAttributeOption.setFamilyAttributeId(familyAttributeDTO.getId());
            familyAttributeDetails.get().getOptions().put(attributeOption.getId(), familyAttributeOption);
            familyDAO.save(family.get());*/

        });

        Optional<Family> familyDetails = familyDAO.findByExternalId(familiesData.get(0).get("externalId").toString());

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

    }

    @Test
    public void updateFamilyTest1() {
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
