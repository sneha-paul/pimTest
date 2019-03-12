package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
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
import static com.bigname.core.util.FindBy.findBy;
import static org.junit.Assert.*;

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
    @Before
    public void setUp() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
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

            List<Pair<String, String>> result = attributeCollectionService.getAttributeGroupsIdNamePair(attributeCollectionDTO.getCollectionId(),EXTERNAL_ID,null);
            Assert.assertEquals(result.get(0).getValue0(),"DEFAULT_GROUP");
        });
    }

    @Test
    public void getAttributeOptions() throws Exception {
      /*  List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO= new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String)AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String)AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

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
            attributeCollectionDAO.save(attributeCollectionDetails);
          //  AttributeCollection attributeCollection = attributeCollectionDAO.findById(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID).orElse(null);
            Page<AttributeOption> result = attributeCollectionService.getAttributeOptions(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, "STYLE",0,1,null);
            System.out.println(result);
        });*/
    }

    @Test
    public void findAttribute() throws Exception {
      /*  List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

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
           // String attributeFullId=attributeCollectionDetails.getAttributes().get("name").getFullId();
            Optional<Attribute> result = attributeCollectionService.findAttribute(attributeCollectionDTO.getCollectionId(),FindBy.EXTERNAL_ID, null);
        });*/
    }

    @Test
    public void findAttributeOption() throws Exception {
        /*List<Map<String, Object>> attributesCollectionData = new ArrayList<>();
        attributesCollectionData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        attributesCollectionData.forEach(AttributeCollection -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) AttributeCollection.get("name"));
            attributeCollectionDTO.setCollectionId((String) AttributeCollection.get("externalId"));
            attributeCollectionDTO.setActive((String) AttributeCollection.get("active"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", "DROPDOWN"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attribute.setId((String) attributeData.get("id"));
                attribute.setUiType(Attribute.UIType.DROPDOWN);
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData -> {
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String) attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String) attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });
            attributeCollectionDAO.save(attributeCollectionDetails);
           // String attributeOptionId=;
        });*/
    }

    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }

}