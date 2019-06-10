package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
import com.m7.common.util.CollectionsUtil;
import com.m7.common.util.ValidationUtil;
import com.m7.xcore.domain.ValidatableEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sanoop on 13/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FamilyAttributeTest {
    @Autowired
    FamilyDAO familyDAO;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    FamilyService familyService;
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

            familyDAO.save(family);

            FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attribute.getId());

            Assert.assertEquals(familyAttributeDTO.getActive(), "Y");
            Assert.assertEquals(familyAttributeDTO.getScopable(), "Y");
            Assert.assertEquals(familyAttributeDTO.getName(), "test");
            Assert.assertEquals(familyAttributeDTO.getCollectionId(), "TEST");

            //Equals Checking Family Attribute
            Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttribute));
            Assert.assertEquals(familyAttribute.getCollectionId(), familyAttributeDTO.getCollectionId());
            Assert.assertEquals(familyAttribute.getAttribute(), familyAttributeDTO.getAttribute());
            Assert.assertEquals(familyAttribute.getScope(), familyAttributeDTO.getScope());
            Assert.assertEquals(familyAttribute.getAttributeId(), familyAttributeDTO.getAttributeId());
            Assert.assertEquals(familyAttribute.getUiType(), familyAttributeDTO.getUiType());
            Assert.assertEquals(familyAttribute.getName(), familyAttributeDTO.getName());
            Assert.assertEquals(familyAttribute.getActive(), familyAttributeDTO.getActive());
            Assert.assertEquals(familyAttribute.getScopable(), familyAttributeDTO.getScopable());

        });
    }
      @Test
    public void getAttributeId() throws Exception {
    }

    @Test
    public void setAttributeId() throws Exception {
    }

    @Test
    public void getCollectionId() throws Exception {
    }

    @Test
    public void setCollectionId() throws Exception {
    }

    @Test
    public void getLevel() throws Exception {
    }

    @Test
    public void setLevel() throws Exception {
    }

    @Test
    public void getScope() throws Exception {
    }

    @Test
    public void setScope() throws Exception {
    }

    @Test
    public void getFamily() throws Exception {
    }

    @Test
    public void setFamily() throws Exception {
    }

    @Test
    public void getAttribute() throws Exception {
    }

    @Test
    public void setAttribute() throws Exception {
    }

    @Test
    public void getAttributeGroup() throws Exception {
    }

    @Test
    public void setAttributeGroup() throws Exception {
    }

    @Test
    public void getOptions() throws Exception {
    }

    @Test
    public void setOptions() throws Exception {
    }

    @Test
    public void getParentAttributeId() throws Exception {
    }

    @Test
    public void setParentAttributeId() throws Exception {
    }

    @Test
    public void getParentAttribute() throws Exception {
    }

    @Test
    public void getParentBasedOptions() throws Exception {
    }

    @Test
    public void setParentBasedOptions() throws Exception {
    }

    @Test
    public void validate() throws Exception {
    }

    @Test
    public void getType() throws Exception {
    }

    @Test
    public void isRequired() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
        //Create New Instance
        FamilyAttribute familyAttributeDTO = new FamilyAttribute();
        familyAttributeDTO.setId("test");
        familyAttributeDTO.setName("Test");
        familyAttributeDTO.orchestrate();

        Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttributeDTO.getId()));
        Assert.assertEquals(familyAttributeDTO.getId(), "test");
    }

    @Test
    public void merge() throws Exception {
        //Create Original Instance
        FamilyAttribute original = new FamilyAttribute();
        original.setName("Test");
        original.setLabel("test");

        //create Modified Instance
        FamilyAttribute modified = new FamilyAttribute();
        modified.setName("Test-A");
        modified.setLabel("test-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getName(), "Test-A");
        Assert.assertEquals(original.getLabel(), "test-A");
    }

    @Test
    public void toMap() throws Exception {

    }

    @Test
    public void findAttribute() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }


}