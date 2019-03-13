package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

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

            Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
            Assert.assertTrue(family.isPresent());
            Assert.assertTrue(family != null);

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

           family.get().addAttribute(familyAttributeDTO);
           // familyDAO.save(family.get());

            Assert.assertEquals(familyAttributeDTO.getActive(), "Y");
            Assert.assertEquals(familyAttributeDTO.getScopable(), "Y");
            Assert.assertEquals(familyAttributeDTO.getName(), "test");
            Assert.assertEquals(familyAttributeDTO.getCollectionId(), "TEST");

           // Family newFamilyAttribute = familyService.get(familyDTO.getId(), EXTERNAL_ID, false).orElse(null);

          // Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttributeDTO));

            Assert.assertEquals(familyAttributeDTO.getFullId(), familyAttributeDTO.getFullId());
            Assert.assertEquals(familyAttributeDTO.getCollectionId(), familyAttributeDTO.getCollectionId());
            Assert.assertEquals(familyAttributeDTO.getAttributeGroup(), familyAttributeDTO.getAttributeGroup());
            Assert.assertEquals(familyAttributeDTO.getAttribute(), familyAttributeDTO.getAttribute());
            Assert.assertEquals(familyAttributeDTO.getScope(), familyAttributeDTO.getScope());
            Assert.assertEquals(familyAttributeDTO.getAttributeId(), familyAttributeDTO.getAttributeId());
            Assert.assertEquals(familyAttributeDTO.getUiType(), familyAttributeDTO.getUiType());

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
    }

    @Test
    public void merge() throws Exception {
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