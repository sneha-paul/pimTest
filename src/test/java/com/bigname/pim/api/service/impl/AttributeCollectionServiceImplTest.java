package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import org.javatuples.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeCollectionServiceImplTest {
    @Autowired
    AttributeCollectionService attributeCollectionService;

    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    FamilyDAO familyDAO;

    @Before
    public void setUp() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void getAll() throws Exception {
        List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO= new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String)AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String)AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);
        });

        Page<AttributeCollection> paginatedResult = attributeCollectionService.getAll(0, 5, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), attributesCollectionData.size());
    }

    @Test
    public void get() throws Exception {
        List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO= new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String)AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String)AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
            Map<String, Object> diff = attributeCollectionDTO.diff(attributeCollectionDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @Test
    public void getAttributes() throws Exception {
        List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO= new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String)AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String)AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("name", "color", "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("name", "size", "active", "Y"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionDAO.save(attributeCollectionDetails);

            Page<Attribute> result = attributeCollectionService.getAttributes(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID,0,3,null);
            String[] actual = result.get().map(attribute -> attribute.getName()).collect(Collectors.toList()).toArray(new String[]{});
            String[] expected = attributesData.stream().map(attributes -> (String)attributes.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
            Assert.assertArrayEquals(expected, actual);
        });
    }

    @Test
    public void getAttributeGroupsIdNamePair() throws Exception {
        List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO= new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String)AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String)AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("name", "color", "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("name", "size", "active", "Y"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionDAO.save(attributeCollectionDetails);

            List<Pair<String, String>> result = attributeCollectionService.getAttributeGroupsIdNamePair(attributeCollectionDTO.getCollectionId(), EXTERNAL_ID, null);
            Assert.assertEquals(result.get(0).getValue0(),"DEFAULT_GROUP");
        });
    }

    @Test
    public void getAttributeOptions() throws Exception {
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

        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        Page<AttributeOption> result = attributeCollectionService.getAttributeOptions(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, attributeCollectionDTO.getAttributeFullId(attributeOption),0,1,null);
        Assert.assertEquals(result.getContent().size(), 1);
        Assert.assertEquals(result.getContent().get(0).getValue(), "TestOption");

    }

    @Test
    public void findAttribute() throws Exception {
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

        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        Optional<Attribute> result = attributeCollectionService.findAttribute(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, attributeCollectionDTO.getAttributeFullId(attributeOption));
        Assert.assertEquals(result.get().getName(), "Test_Attribute");
    }

    @Test
    public void findAttributeOption() throws Exception {
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

            Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
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

            family.addAttribute(familyAttributeDTO);
            familyDAO.save(family);

            Optional<AttributeOption> result= attributeCollectionService.findAttributeOption(familyAttributeDTO,"TESTOPTION");
            Assert.assertEquals(result.get().getValue(), "TestOption");
        });
    }

    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}