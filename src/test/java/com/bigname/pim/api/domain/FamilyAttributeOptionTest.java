package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
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

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by sanoop on 13/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FamilyAttributeOptionTest {
    @Autowired
    FamilyDAO familyDAO;
    @Autowired
    FamilyService familyService;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Before
    public void setUp() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
    @Test
    public void accessorsTest() {
        //Create Attribute collection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test");
        attributeCollectionDTO.setCollectionId("TEST");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        //Create attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test");
        attribute.setId("TEST");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        //Create Attribute Option
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

            //Create Family
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String) familyData.get("name"));
            familyDTO.setFamilyId((String) familyData.get("externalId"));
            familyDTO.setActive((String) familyData.get("active"));
            familyDTO.setDiscontinued((String) familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

            //Create Attribute Group
            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
            familyAttributeGroup.setId(familyAttributeGroup.getFullId());

            //Create the new familyAttribute instance
            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attribute.getName(), null);
            familyAttributeDTO.setActive("Y");
            familyAttributeDTO.setName("test");
            familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
            familyAttributeDTO.setUiType(attribute.getUiType());
            familyAttributeDTO.setScopable("Y");
            familyAttributeDTO.setAttributeId(attribute.getFullId());
            familyAttributeDTO.getScope().put("ECOMMERCE", FamilyAttribute.Scope.OPTIONAL);
            familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
            familyAttributeDTO.setAttribute(attribute);

            family.addAttribute(familyAttributeDTO);
            FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attribute.getId());

            //Create Family Attribute Option
            List<AttributeOption> attributeOptionList = new ArrayList(attribute.getOptions().values());
            attributeOptionList.forEach(attributeOption1 -> {
                FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                familyAttributeOption.setActive("Y");
                familyAttributeOption.setValue(attributeOption.getValue());
                familyAttributeOption.setId(attributeOption.getId());
                familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                familyAttributeDTO.getOptions().put(attributeOption.getId(), familyAttributeOption);
                family.addAttributeOption(familyAttributeOption, attributeOption);

                familyDAO.save(family);

                //Getting Attribute using family service Option and equals checking

               // Page<FamilyAttributeOption> result = familyService.getFamilyAttributeOptions(family.getFamilyId(), FindBy.EXTERNAL_ID, familyAttribute.getId(), 0, 2, null);
                FamilyAttribute result = family.getAllAttributesMap(false).get(attribute.getId());
                Assert.assertEquals(familyAttributeOption.getActive(), "Y");

                Assert.assertTrue(ValidationUtil.isNotEmpty(result));
                Assert.assertEquals(result.getId(), familyAttributeOption.getId());
                Assert.assertEquals(result.getActive(), familyAttributeOption.getActive());
               /* Assert.assertEquals(result.getContent().get(0).getActive(), familyAttributeOption.getActive());
                Assert.assertEquals(result.getContent().get(0).getId(), familyAttributeOption.getId());
                Assert.assertEquals(result.getContent().get(0).getId(), familyAttributeOption.getValue());*/
            });
        });}
    @Test
    public void getId() throws Exception {
    }

    @Test
    public void setId() throws Exception {
    }

    @Test
    public void getFullId() throws Exception {
    }

    @Test
    public void setFullId() throws Exception {
    }

    @Test
    public void getFamilyAttributeId() throws Exception {
    }

    @Test
    public void setFamilyAttributeId() throws Exception {
    }

    @Test
    public void getAttributeOptionId() throws Exception {
    }

    @Test
    public void setAttributeOptionId() throws Exception {
    }

    @Test
    public void getValue() throws Exception {
    }

    @Test
    public void setValue() throws Exception {
    }

    @Test
    public void getActive() throws Exception {
    }

    @Test
    public void setActive() throws Exception {
    }

    @Test
    public void getSequenceNum() throws Exception {
    }

    @Test
    public void setSequenceNum() throws Exception {
    }

    @Test
    public void getSubSequenceNum() throws Exception {
    }

    @Test
    public void setSubSequenceNum() throws Exception {
    }

    @Test
    public void getParentOptionFullId() throws Exception {
    }

    @Test
    public void setParentOptionFullId() throws Exception {
    }

    @Test
    public void getReferenceMap() throws Exception {
    }

    @Test
    public void setReferenceMap() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
        FamilyAttributeOption familyAttributeOptionDTO = new FamilyAttributeOption();
        familyAttributeOptionDTO.setValue("test");
        familyAttributeOptionDTO.orchestrate();

        Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttributeOptionDTO.getId()));
        Assert.assertEquals(familyAttributeOptionDTO.getId(), "TEST");
    }

    @Test
    public void toMap() throws Exception {
        //Create new Instance
        FamilyAttributeOption familyAttributeOptionDTO = new FamilyAttributeOption();
        familyAttributeOptionDTO.setId("test");
        familyAttributeOptionDTO.setValue("Test");
        familyAttributeOptionDTO.setActive("Y");

        //Checking for map
        Map<String, String> map = new HashMap<>();
        map.put("id", "test");
        map.put("value", "Test");
        map.put("active", "Y");

        Map<String, String> map1 = familyAttributeOptionDTO.toMap();
        Assert.assertEquals(map1.get("id"), map.get("id"));
        Assert.assertEquals(map1.get("value"), map.get("value"));
        Assert.assertEquals(map1.get("active"), map.get("active"));

    }
    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }

}