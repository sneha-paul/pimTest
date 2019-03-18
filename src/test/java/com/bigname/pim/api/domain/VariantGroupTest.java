package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
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
 * Created by sanoop on 14/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class VariantGroupTest {
    @Autowired
    FamilyDAO familyDAO;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    FamilyService familyService;
    @Autowired
    ChannelDAO channelDAO;
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

            //Create familyAttribute instance
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

                //Create Variant Group
                family.setGroup("VARIANT_GROUPS");
                VariantGroup variantGroup = new VariantGroup();
                variantGroup.setName(attribute.getName());
                variantGroup.setId(attribute.getId());
                variantGroup.setActive("Y");
                variantGroup.setLevel(1);
                variantGroup.setFamilyId(family.getFamilyId());
                variantGroup.getVariantAxis().put(1, Arrays.asList(attribute.getName()));
                variantGroup.getVariantAttributes().put(1, Arrays.asList(attribute.getName(), attribute.getName()));
                family.addVariantGroup(variantGroup);
               // family.getChannelVariantGroups().put(channel.getChannelId(), variantGroup.getId());

                familyDAO.save(family);

                //Getting Variant group using family service  and equals checking
                Page<VariantGroup> result=familyService.getVariantGroups(family.getFamilyId(), FindBy.EXTERNAL_ID, 0, 1, null);

                Assert.assertEquals(variantGroup.getActive(), "Y");

                Assert.assertTrue(ValidationUtil.isNotEmpty(result));
                Assert.assertEquals(result.getContent().get(0).getName(), variantGroup.getName());
                Assert.assertEquals(result.getContent().get(0).getFamilyId(), variantGroup.getFamilyId());
                Assert.assertEquals(result.getContent().get(0).getId(), variantGroup.getId());
                Assert.assertEquals(result.getContent().get(0).getActive(), variantGroup.getActive());
                Assert.assertEquals(result.getContent().get(0).getVariantAttributes(), variantGroup.getVariantAttributes());
                Assert.assertEquals(result.getContent().get(0).getVariantAxis(), variantGroup.getVariantAxis());

            });
        });}
   @Test
    public void getId() throws Exception {
    }

    @Test
    public void setId() throws Exception {
    }

    @Test
    public void getName() throws Exception {
    }

    @Test
    public void setName() throws Exception {
    }

    @Test
    public void getActive() throws Exception {
    }

    @Test
    public void setActive() throws Exception {
    }

    @Test
    public void getLevel() throws Exception {
    }

    @Test
    public void setLevel() throws Exception {
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
    public void getVariantAxis() throws Exception {
    }

    @Test
    public void setVariantAxis() throws Exception {
    }

    @Test
    public void getVariantAttributes() throws Exception {
    }

    @Test
    public void setVariantAttributes() throws Exception {
    }

    @Test
    public void getFamilyId() throws Exception {
    }

    @Test
    public void setFamilyId() throws Exception {
    }

    @Test
    public void getFamily() throws Exception {
    }

    @Test
    public void setFamily() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
        //Create id
        VariantGroup variantGroupDTO = new VariantGroup();
        variantGroupDTO.setName("test");
        variantGroupDTO.orchestrate();

        //Check Variant Group Id
        Assert.assertTrue(ValidationUtil.isNotEmpty(variantGroupDTO.getId()));
        Assert.assertEquals(variantGroupDTO.getId(), "TEST");
    }

    @Test
    public void toMap() throws Exception {

    }

    @Test
    public void merge() throws Exception {
        //Create Original Instance
        VariantGroup original = new VariantGroup();
        original.setName("Test");
        original.setId("test");
        original.setActive("Y");

        //Create Modified Instance
        VariantGroup modified = new VariantGroup();
        modified.setGroup("DETAILS");
        modified.setName("Test-A");
        modified.setId("test");
        modified.setActive("Y");

        original = original.merge(modified);
        Assert.assertEquals(original.getName(), "Test-A");
        Assert.assertEquals(original.getId(), "test");
        Assert.assertEquals(original.getActive(), "Y");

        // Without DETAILS
        VariantGroup modified1 = new VariantGroup();
        modified1.setName("Test");
        modified1.setId("test");
        modified1.setActive("Y");

        original = original.merge(modified1);
        Assert.assertEquals(original.getName(), "Test-A");
        Assert.assertEquals(original.getId(), "test");
        Assert.assertEquals(original.getActive(), "Y");

    }
    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
}