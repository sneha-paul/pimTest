package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.util.PimUtil;
import org.javatuples.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
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

    @Test
    public void createEntityTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());

        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
         List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);

        attributeCollectionService.update(attributeList);

        Page<Attribute> attribute1= attributeCollectionService.getAttributes(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, 0, 1, null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attribute1));
        Assert.assertEquals(attribute1.getContent().get(0).getName(),attribute.getName());

        Optional<Attribute> attributeDetails = attributeCollection.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);
        attributeCollectionService.update(attributeList);

        Page<AttributeOption> attributeOption1= attributeCollectionService.getAttributeOptions(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, attribute.getFullId(), 0, 1, null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeOption1));
        Assert.assertEquals(attributeOption1.getContent().get(0).getValue(),attributeOption.getValue());
    }

    @Test
    public void createEntitiesTest(){
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

          List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });
        List<AttributeCollection>  collection = attributeCollectionService.getAll(null, false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(collection));
        Assert.assertEquals(collection.size(),collectionsData.size());
        List<Attribute> attributes=collection.get(0).getAllAttributes();
        Assert.assertEquals(attributes.size(),1);
        Assert.assertEquals(attributes.get(0).getOptions().size(),3);
    }

    @Test
    public void toggleTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        //toggle
        attributeCollectionService.toggle(attributeCollection.getCollectionId(), EXTERNAL_ID, Toggle.get(attributeCollection.getActive()));
        AttributeCollection updatedAttributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedAttributeCollection));
        Assert.assertEquals(updatedAttributeCollection.getActive(), "N");

        attributeCollectionService.toggle(attributeCollection.getCollectionId(), EXTERNAL_ID, Toggle.get(updatedAttributeCollection.getActive()));
        AttributeCollection updatedAttributeCollection1 = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedAttributeCollection1));
        Assert.assertEquals(updatedAttributeCollection1.getActive(), "Y");
    }

    @Test
    public void getTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);

        Optional<Attribute> attributeDetails = attributeCollection.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);
        attributeCollectionService.update(attributeList);

        AttributeCollection collection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(collection));
        Assert.assertEquals(collection.getCollectionName(),"Test_AttributeCollection");
        Assert.assertEquals(collection.getAllAttributes().size(),1);
        Assert.assertEquals(collection.getAllAttributes().get(0).getOptions().size(),1);
    }

    @Test
    public void getAllAsPageTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);


            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });

        Page<AttributeCollection> paginatedResult = attributeCollectionService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), collectionsData.size());
        Assert.assertEquals(paginatedResult.getContent().get(0).getAllAttributes().size(),1);
        Assert.assertEquals(paginatedResult.getContent().get(0).getAllAttributes().get(0).getOptions().size(),3);
    }

    @Test
    public void getAllAsListTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection4", "externalId", "ENVELOPE4", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);
        });
        // sorting : ascending
        List<AttributeCollection> result = attributeCollectionService.getAll(Sort.by("collectionName").ascending(), false);
        String[] actual = result.stream().map(collection -> collection.getCollectionName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = collectionsData.stream().map(categoryData -> (String)categoryData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);


        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection4", "externalId", "ENVELOPE4", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionDAO.insert(attributeCollectionDTO);
        });
        // sorting : Descending
        result = attributeCollectionService.getAll(Sort.by("collectionName").descending(), false);
        actual = result.stream().map(collection -> collection.getCollectionName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = collectionsData.stream().map(collectionData -> (String)collectionData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @Test
    public void getAllWithIdsAsPageTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });

        String[] ids = {collectionsData.get(0).get("externalId").toString(), collectionsData.get(1).get("externalId").toString(), collectionsData.get(2).get("externalId").toString()};

        Page<AttributeCollection> paginatedResult = attributeCollectionService.getAll(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, AttributeCollection> collectionsMap = paginatedResult.getContent().stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == ids.length && collectionsMap.containsKey(ids[0]) && collectionsMap.containsKey(ids[1]) && collectionsMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithIdsAsListTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });
        String[] ids = {collectionsData.get(0).get("externalId").toString(), collectionsData.get(1).get("externalId").toString(), collectionsData.get(2).get("externalId").toString()};
        List<AttributeCollection> listedResult = attributeCollectionService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, AttributeCollection> collectionsMap = listedResult.stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == ids.length && collectionsMap.containsKey(ids[0]) && collectionsMap.containsKey(ids[1]) && collectionsMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsPageTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });

        String[] ids = {collectionsData.get(0).get("externalId").toString(), collectionsData.get(1).get("externalId").toString(), collectionsData.get(2).get("externalId").toString()};
        Page<AttributeCollection> paginatedResult = attributeCollectionService.getAllWithExclusions(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, AttributeCollection> collectionsMap = paginatedResult.getContent().stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == (collectionsData.size() - ids.length) && !collectionsMap.containsKey(ids[0]) && !collectionsMap.containsKey(ids[1]) && !collectionsMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsListTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });
        String[] ids = {collectionsData.get(0).get("externalId").toString(), collectionsData.get(1).get("externalId").toString(), collectionsData.get(2).get("externalId").toString()};
        List<AttributeCollection> listedResult = attributeCollectionService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);
        Map<String, AttributeCollection> collectionsMap = listedResult.stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == (collectionsData.size() - ids.length) && !collectionsMap.containsKey(ids[0]) && !collectionsMap.containsKey(ids[1]) && !collectionsMap.containsKey(ids[2]));
    }

    @Test
    public void findAllAtSearchTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });
        Page<AttributeCollection> paginatedResult = attributeCollectionService.findAll("active", "Y", PageRequest.of(0, collectionsData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), collectionsData.size());
    }

    @Test
    public void findAllTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

        });
        Page<AttributeCollection> paginatedResult = attributeCollectionService.findAll(PageRequest.of(0, collectionsData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), collectionsData.size());//size
    }

   @Test
    public void updateEntityTest() {
       AttributeCollection attributeCollectionDTO = new AttributeCollection();
       attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
       attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
       attributeCollectionDTO.setActive("Y");
       attributeCollectionDTO.setDiscontinued("N");

       attributeCollectionService.create(attributeCollectionDTO);

       AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
       Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
       Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
       attributeCollection.setActive("N");
       attributeCollection.setGroup("DETAILS");

       attributeCollectionService.update(attributeCollection.getCollectionId(), EXTERNAL_ID, attributeCollection);
       AttributeCollection updatedCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
       Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCollection));
       Assert.assertEquals(updatedCollection.getActive(), "N");

   }

     @Test
    public void updateEntitiesTest(){
         List<Map<String, Object>> collectionsData = new ArrayList<>();
         collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
         collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
         collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

         collectionsData.forEach(collectionData -> {
             AttributeCollection attributeCollectionDTO = new AttributeCollection();
             attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
             attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
             attributeCollectionDTO.setActive((String)collectionData.get("active"));
             attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
             attributeCollectionService.create(attributeCollectionDTO);
         });

         String[] ids = {collectionsData.get(0).get("externalId").toString(), collectionsData.get(1).get("externalId").toString(), collectionsData.get(2).get("externalId").toString()};

        List<AttributeCollection> result = attributeCollectionService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, AttributeCollection> collectionsMap = result.stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == ids.length && collectionsMap.containsKey(ids[0]) && collectionsMap.containsKey(ids[1]) && collectionsMap.containsKey(ids[2]));

        List<AttributeCollection> categories = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        attributeCollectionService.update(categories);
        result = attributeCollectionService.getAll(Sort.by("collectionName").descending(), true);
        collectionsMap = result.stream().collect(Collectors.toMap(collection -> collection.getCollectionId(), collection -> collection));
        Assert.assertTrue(collectionsMap.size() == (collectionsData.size() - ids.length) && !collectionsMap.containsKey(ids[0]) && !collectionsMap.containsKey(ids[1]) && !collectionsMap.containsKey(ids[2]));
        Assert.assertFalse(collectionsMap.size() == collectionsData.size() && collectionsMap.containsKey(ids[0]) && collectionsMap.containsKey(ids[1]) && collectionsMap.containsKey(ids[2]));
    }

    @Test
    public void cloneInstanceTest() {
       /* List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection newAttributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection));
            Assert.assertTrue(newAttributeCollection.diff(attributeCollectionDTO).isEmpty());

           AttributeCollection collectionClone = attributeCollectionService.cloneInstance(newAttributeCollection.getCollectionId(), FindBy.EXTERNAL_ID, Entity.CloneType.LIGHT);
         //   Assert.assertTrue(collectionClone.getCollectionId() .equals(newAttributeCollection.getCollectionId() + "_COPY") && collectionClone.getCollectionName().equals(collectionClone.getCollectionName() + "_COPY")  && collectionClone.getActive() != collectionClone.getActive());
        });*/
    }

   @Test
    public void findAll1Test() {
       List<Map<String, Object>> collectionsData = new ArrayList<>();
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

       collectionsData.forEach(collectionData -> {
           AttributeCollection attributeCollectionDTO = new AttributeCollection();
           attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
           attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
           attributeCollectionDTO.setActive((String)collectionData.get("active"));
           attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
           attributeCollectionService.create(attributeCollectionDTO);

           AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

           List<Map<String, Object>> attributesData = new ArrayList<>();
           attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
           attributesData.forEach(attributeData -> {
               Attribute attribute = new Attribute();
               attribute.setActive((String)attributeData.get("active"));
               attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
               attribute.setName((String)attributeData.get("name"));
               attribute.setId((String)attributeData.get("id"));
               attribute.setUiType(Attribute.UIType.DROPDOWN);
               attributeCollectionDetails.addAttribute(attribute);

               Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

               List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
               attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
               attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
               attributesOptionsData.forEach(attributeOptionData ->{
                   AttributeOption attributeOption = new AttributeOption();
                   attributeOption.setValue((String)attributeOptionData.get("value"));
                   attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                   attributeOption.setActive((String)attributeOptionData.get("active"));
                   attributeOption.setAttributeId(attribute.getFullId());
                   attributeOption.orchestrate();
                   attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
               });
           });

           List<AttributeCollection> attributeList= new ArrayList<>();
           attributeList.add(attributeCollectionDetails);
           attributeCollectionService.update(attributeList);
       });

        List<AttributeCollection> result = attributeCollectionService.findAll(CollectionsUtil.toMap("active", "N"));
        long size = collectionsData.stream().filter(x -> x.get("active").equals("N")).count();
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findAll2Test() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });

            List<AttributeCollection> attributeList= new ArrayList<>();
            attributeList.add(attributeCollectionDetails);
            attributeCollectionService.update(attributeList);
        });
        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<AttributeCollection> result = attributeCollectionService.findAll(criteria);
        long size = collectionsData.stream().filter(x -> x.get("active").equals("N")).count();
        Assert.assertTrue(result.size() == size);
    }

   @Test
    public void findOneTest() {
       List<Map<String, Object>> collectionsData = new ArrayList<>();
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
       collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

       collectionsData.forEach(collectionData -> {
           AttributeCollection attributeCollectionDTO = new AttributeCollection();
           attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
           attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
           attributeCollectionDTO.setActive((String)collectionData.get("active"));
           attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
           attributeCollectionService.create(attributeCollectionDTO);
       });

       Optional<AttributeCollection> result = attributeCollectionService.findOne(CollectionsUtil.toMap("collectionName", collectionsData.get(0).get("name")));
       Assert.assertEquals(collectionsData.get(0).get("name"), result.get().getCollectionName());

    }

    @Test
    public void findOne1Test() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection1", "externalId", "ENVELOPE1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection2", "externalId", "ENVELOPE2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection3", "externalId", "ENVELOPE3", "active", "Y", "discontinued", "N"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);
        });
        //Getting category
        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("collectionName", collectionsData.get(0).get("name")));
        Optional<AttributeCollection> result = attributeCollectionService.findOne(criteria);
        Assert.assertEquals(collectionsData.get(0).get("name"), result.get().getCollectionName());
    }

    @Test
    public void validateTest() throws Exception {
        /* Create a valid new instance with id TEST_ATTRIBUTECOLLECTION */
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        Map<String, Object> context = new HashMap<>();

        Class groups = ValidatableEntity.CreateGroup.class;
//        validate
        Assert.assertTrue(attributeCollectionService.validate(attributeCollectionDTO, context, groups).isEmpty());
//        insert the valid instance
        attributeCollectionService.create(attributeCollectionDTO);

        /*Create a second instance with the same id TEST_ATTRIBUTECOLLECTION to check the unique constraint violation of collectionId*/

        AttributeCollection attributeCollectionDTO1 = new AttributeCollection();
        attributeCollectionDTO1.setCollectionName("Test_AttributeCollection1");
        attributeCollectionDTO1.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO1.setActive("Y");
        attributeCollectionDTO1.setDiscontinued("N");

        Assert.assertEquals(attributeCollectionService.validate(attributeCollectionDTO1, context, groups).size(), 1);

        /*Testing forceUniqueId*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(attributeCollectionService.validate(attributeCollectionDTO1, context, groups).isEmpty());
        Assert.assertEquals(attributeCollectionDTO1.getExternalId(), "TEST_ATTRIBUTECOLLECTION_1");
        attributeCollectionDAO.insert(attributeCollectionDTO1);

        context.clear();

        /*Testing uniqueConstraint violation of collectionId with update operation*/
        AttributeCollection attributeCollection = attributeCollectionDAO.findById(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID).orElse(null);
        attributeCollection.setCollectionName("Test_AttributeCollection1");
        attributeCollection.setCollectionId("TEST_ATTRIBUTECOLLECTION_1");
        attributeCollection.setActive("Y");
        attributeCollection.setDiscontinued("N");
        context.put("id", attributeCollectionDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(attributeCollectionService.validate(attributeCollection, context, groups).size(), 1);

        /*Testing forceUniqueId with update operation*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(attributeCollectionService.validate(attributeCollection, context, groups).isEmpty());
        Assert.assertEquals(attributeCollection.getExternalId(), "TEST_ATTRIBUTECOLLECTION_1_1");
    }

    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}