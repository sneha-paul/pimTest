package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.AttributeOption;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "active", "N", "discontinued", "Y"));

        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));

            AttributeCollection attributeCollection = attributeCollectionDAO.insert(attributeCollectionDTO);
            Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        });
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
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST3", "active", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            AttributeCollection attributeCollection = attributeCollectionDAO.insert(attributeCollectionDTO);
            Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        });

        List<Map<String, Object>> collectionsUpdateData = new ArrayList<>();
        collectionsUpdateData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "N"));
        collectionsUpdateData.forEach(collectionUpdateData -> {
            AttributeCollection attributeCollectionDTO1 = new AttributeCollection();
            attributeCollectionDTO1.setCollectionName((String)collectionUpdateData.get("name"));
            attributeCollectionDTO1.setCollectionId((String)collectionUpdateData.get("externalId"));
            attributeCollectionDTO1.setActive((String)collectionUpdateData.get("active"));
            attributeCollectionDTO1.setGroup("DETAILS");
            attributeCollectionDAO.save(attributeCollectionDTO1);
        });
        Optional<AttributeCollection> attributeCollection1 = attributeCollectionDAO.findById(collectionsUpdateData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(attributeCollection1.isPresent());
    }

    @Test
    public void retrieveAttributeCollectionsTest() {

        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "active", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "active", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "active", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "active", "N"));

        List<AttributeCollection> attributeCollectionDTOs = collectionsData.stream().map(attributeCollectionData -> {
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

        collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "N", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "active", "N", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "active", "N", "discontinued", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "active", "N", "discontinued", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "active", "Y", "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        attributeCollectionDTOs = collectionsData.stream().map(attributeCollectionData -> {
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

        collectionsData = new ArrayList<>();

        collectionsData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "activeFrom", yesterday, "activeTo", todayEOD, "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "activeFrom", null, "activeTo", todayEOD, "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "activeFrom", tomorrow, "discontinued", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "active", "N", "activeFrom", null, "activeTo", null, "discontinued", "Y"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "activeFrom", yesterday, "activeTo", tomorrowEOD, "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "activeFrom", yesterday, "activeTo", null, "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        collectionsData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "active", "Y", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        int[] activeCount1 = {0}, inactiveCount1 = {0}, discontinued1 = {0};

        attributeCollectionDTOs = collectionsData.stream().map(attributeCollectionData -> {
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

    @Test
    public void createAttributeTest() {

        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
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
        });
        AttributeCollection attributeCollection1 = attributeCollectionDAO.findById(collectionsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(attributeCollection1.getAttributes() != null);
    }

    @Test
    public void retrieveAttributeTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
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
        });
        AttributeCollection attributeCollection = attributeCollectionDAO.findByExternalId(collectionsData.get(0).get("externalId").toString()).orElse(null);
        Assert.assertTrue(attributeCollection.getAttributes() != null);
        attributeCollection = attributeCollectionDAO.findById(collectionsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(attributeCollection.getAttributes() != null);
    }

    @Test
    public void updateAttributeTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE"));
            attributesData.add(CollectionsUtil.toMap("name", "color", "active", "Y", "id", "COLOR"));
            attributesData.add(CollectionsUtil.toMap("name", "size", "active", "Y", "id", "SIZE"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionDAO.save(attributeCollectionDetails);

            AttributeCollection attributeCollectionUpdate = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);


            List<Map<String, Object>> attributesUpdateData = new ArrayList<>();
            attributesUpdateData.add(CollectionsUtil.toMap("name", "styleNew", "active", "Y", "id", "STYLE"));
            attributesUpdateData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attributeCollectionUpdate.updateAttribute(attribute);
            });
            attributeCollectionDAO.save(attributeCollectionUpdate);
        });

        AttributeCollection attributeCollection = attributeCollectionDAO.findByExternalId(collectionsData.get(0).get("externalId").toString()).orElse(null);
        Assert.assertTrue(attributeCollection.getAttributes() != null);
        attributeCollection = attributeCollectionDAO.findById(collectionsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(attributeCollection.getAttributes() != null);
    }

    @Test
    public void createAttributeOptionTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
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
        });

        AttributeCollection attributeCollection = attributeCollectionDAO.findByExternalId(collectionsData.get(0).get("externalId").toString()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection.getAttributes()));
        attributeCollection = attributeCollectionDAO.findById(collectionsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection.getAttributes()));
    }

    @Test
    public void updateAttributeOptionsTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
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

           /* AttributeCollection attributeCollectionUpdate = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

                List<Map<String, Object>> attributesOptionsUpdateData = new ArrayList<>();
                attributesOptionsUpdateData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsUpdateData.add(CollectionsUtil.toMap("value", "OPEN_END1", "active", "Y"));
                attributesOptionsUpdateData.add(CollectionsUtil.toMap("value", "PAPERS", "active", "Y"));
                attributesOptionsUpdateData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionUpdate.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attributeUpdate.getFullId());
                    attributeOption.orchestrate();
                    //attribute.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                    attributeCollectionUpdate.addAttributeOption(attributeOption);
                });

            attributeCollectionDAO.save(attributeCollectionUpdate);*/
        });
    }

    @Test
    public void retrieveAttributeOptionTest() {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        List<Map<String, Object>> attributesData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Envelopes Attributes Collection", "externalId", "ENVELOPE", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionDAO.insert(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

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
        });
        AttributeCollection attributeCollection = attributeCollectionDAO.findByExternalId(collectionsData.get(0).get("externalId").toString()).orElse(null);
        Attribute attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attributesData.get(0).get("id").toString()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(attribute));
        Assert.assertEquals(attribute.getOptions().get("FOLDERS").getValue(), "FOLDERS");
    }

    @After
    public void tearDown() {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
}
