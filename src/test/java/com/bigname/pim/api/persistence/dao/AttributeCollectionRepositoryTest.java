package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dona on 22-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeCollectionRepositoryTest {
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;

    @Before
    public void setUp() {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }

    @Test
    public void createAttributeCollectionTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test1");
        attributeCollectionDTO.setCollectionId("TEST1");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        AttributeCollection attributeCollection = attributeCollectionDAO.insert(attributeCollectionDTO);
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
    }

    @Test
    public void retrieveAttributeCollectionTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test1");
        attributeCollectionDTO.setCollectionId("TEST1");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        Optional<AttributeCollection> attributeCollection = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId());
        Assert.assertTrue(attributeCollection.isPresent());
        attributeCollection = attributeCollectionDAO.findById(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(attributeCollection.isPresent());
        attributeCollection = attributeCollectionDAO.findById(attributeCollectionDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(attributeCollection.isPresent());
    }

    @Test
    public void updateAttributeCollectionTest() {

        Attribute attribute = new Attribute();
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setDataType("string");
        attribute.setLabel("Style");
        attribute.setName("Style");
        attribute.setActive("Y");
        attribute.setId("STYLE");

        Attribute attribute1 = new Attribute();
        attribute1.setUiType(Attribute.UIType.DROPDOWN);
        attribute1.setDataType("string");
        attribute1.setLabel("Color");
        attribute1.setName("Color");
        attribute1.setActive("Y");
        attribute1.setId("COLOR");

        Map<String, Attribute> attributes = new LinkedHashMap<>();
        attributes.put("STYLE",attribute);
        attributes.put("COLOR",attribute1);

        AttributeGroup attributeGroupData = new AttributeGroup();
        attributeGroupData.setAttributes(attributes);
        attributeGroupData.setName("Default Group");
        attributeGroupData.setLabel("Default Group");
        attributeGroupData.setFullId("Default Group");
        attributeGroupData.setActive("Y");
        attributeGroupData.setId("DEFAULT_GROUP");

        Map<String, AttributeGroup> attributeGroupMap = new HashMap<>();
        attributeGroupMap.put("DEFAULT_GROUP", attributeGroupData);

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Envelopes Attributes Collection");
        attributeCollectionDTO.setCollectionId("ENVELOPES");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDTO.setAttributes(attributeGroupMap);
        attributeCollectionDAO.insert(attributeCollectionDTO);

       AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
       Assert.assertTrue(attributeCollectionDetails != null);
       attributeCollectionDetails.setCollectionName("New Envelopes Attributes Collection");
       attributeCollectionDetails.setGroup("DETAILS");
       attributeCollectionDAO.save(attributeCollectionDetails);

        Optional<AttributeCollection> attributeCollection = attributeCollectionDAO.findByExternalId(attributeCollectionDetails.getCollectionId());
        Assert.assertTrue(attributeCollection.isPresent());
        attributeCollection = attributeCollectionDAO.findById(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(attributeCollection.isPresent());
        attributeCollection = attributeCollectionDAO.findById(attributeCollectionDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(attributeCollection.isPresent());


    }

    @Test
    public void retrieveAttributeCollectionsTest() {

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "active", "N"));

        List<AttributeCollection> attributeCollectionDTOs = categoriesData.stream().map(attributeCollectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)attributeCollectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)attributeCollectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)attributeCollectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)attributeCollectionData.get("discontinued"));
            return attributeCollectionDTO;
        }).collect(Collectors.toList());

        attributeCollectionDAO.insert(attributeCollectionDTOs);

        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false).getTotalElements(), attributeCollectionDTOs.size());
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size() - 1), false).getTotalElements(), attributeCollectionDTOs.size());
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size() - 1), false).getContent().size(), attributeCollectionDTOs.size() - 1);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(1, attributeCollectionDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size() - 1), false).getTotalPages(), 2);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "active", "Y", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        attributeCollectionDTOs = categoriesData.stream().map(attributeCollectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)attributeCollectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)attributeCollectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)attributeCollectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)attributeCollectionData.get("discontinued"));
            if("Y".equals(attributeCollectionData.get("discontinued"))){
                discontinued[0] ++;
            } else {
                if("Y".equals(attributeCollectionData.get("active"))) {
                    activeCount[0] ++;
                } else {
                    inactiveCount[0] ++;
                }
            }
            return attributeCollectionDTO;
        }).collect(Collectors.toList());

        attributeCollectionDAO.insert(attributeCollectionDTOs);

        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, false, true).getTotalElements(), discontinued[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, true, true).getTotalElements(), inactiveCount[0] + discontinued[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true, true, true).getTotalElements(), activeCount[0] + inactiveCount[0] + discontinued[0]);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        categoriesData = new ArrayList<>();

        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "activeFrom", yesterday, "activeTo", todayEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "activeFrom", null, "activeTo", todayEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "activeFrom", tomorrow, "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "active", "N", "activeFrom", null, "activeTo", null, "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "activeFrom", yesterday, "activeTo", tomorrowEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "activeFrom", yesterday, "activeTo", null, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "active", "Y", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        int[] activeCount1 = {0}, inactiveCount1 = {0}, discontinued1 = {0};

        attributeCollectionDTOs = categoriesData.stream().map(attributeCollectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)attributeCollectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)attributeCollectionData.get("externalId"));
            attributeCollectionDTO.setDiscontinued((String)attributeCollectionData.get("discontinued"));
            attributeCollectionDTO.setActive((String)attributeCollectionData.get("active"));
            attributeCollectionDTO.setActiveFrom((LocalDateTime) attributeCollectionData.get("activeFrom"));
            attributeCollectionDTO.setActiveTo((LocalDateTime) attributeCollectionData.get("activeTo"));

            if(PimUtil.hasDiscontinued(attributeCollectionDTO.getDiscontinued(), attributeCollectionDTO.getDiscontinuedFrom(), attributeCollectionDTO.getDiscontinuedTo())) {
                discontinued1[0]++;
            } else if(PimUtil.isActive(attributeCollectionDTO.getActive(), attributeCollectionDTO.getActiveFrom(), attributeCollectionDTO.getActiveTo())) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            return attributeCollectionDTO;
        }).collect(Collectors.toList());

        attributeCollectionDAO.insert(attributeCollectionDTOs);

        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true, true, true).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true, true, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), true, false, true).getTotalElements(), activeCount1[0] + discontinued1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, true, true).getTotalElements(), inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        /*Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);*/
        Assert.assertEquals(attributeCollectionDAO.findAll(PageRequest.of(0, attributeCollectionDTOs.size()), false, false, true).getTotalElements(), discontinued1[0]);
    }

    @After
    public void tearDown() {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
}
