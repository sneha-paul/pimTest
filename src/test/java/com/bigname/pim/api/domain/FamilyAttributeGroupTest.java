package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.mongo.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by sanoop on 13/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FamilyAttributeGroupTest {
    @Autowired
    private FamilyDAO familyDAO;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) familyDAO.getTemplate();
        }

        User user1 = userDAO.findByEmail("MANU@BLACWOOD.COM");
        if(ValidationUtil.isEmpty(user1)){
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COM");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Blacwood");
            userDAO.save(user);
        }
        User user2 = userDAO.findByEmail("MANU@E-XPOSURE.COM");
        if(ValidationUtil.isEmpty(user2)) {
            User user = new User();
            user.setUserName("MANU@E-XPOSURE.COM");
            user.setPassword("temppass1");
            user.setEmail("manu@e-xposure.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Exposure");
            userDAO.save(user);
        }
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(AttributeCollection.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest(){
        //Create Attribute collection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test");
        attributeCollectionDTO.setCollectionId("TEST");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()),false).orElse(null);

        //Create attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test");
        attribute.setId("TEST");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);

            //Create Family
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String) familyData.get("name"));
            familyDTO.setFamilyId((String) familyData.get("externalId"));
            familyDTO.setActive((String) familyData.get("active"));
            familyDTO.setDiscontinued((String) familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Family family = familyDAO.findById(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

            //Create Attribute Group
            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setFullId("DEFAULT_GROUP");
            // familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
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

            //Getting Attribute group and equals checking
            FamilyAttributeGroup familyAttributeGroup1 = family.getAllAttributesMap(false).get(attribute.getId()).getAttributeGroup();

            Assert.assertEquals(familyAttributeGroup.getActive(), "Y");
            Assert.assertEquals(familyAttributeGroup.getMasterGroup(), "Y");

            Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttributeGroup1));
            Assert.assertEquals(familyAttributeGroup1.getFullId(), familyAttributeGroup.getFullId());

        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getName() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setName() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getLabel() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setLabel() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void isEmpty() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getFullId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setFullId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getDefaultGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setDefaultGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getActive() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setActive() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getMasterGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setMasterGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getSequenceNum() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setSequenceNum() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getSubSequenceNum() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setSubSequenceNum() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getParentGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setParentGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getChildGroups() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setChildGroups() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addChildGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAttributes() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setAttributes() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
        //Create New Instance
        FamilyAttributeGroup familyAttributeGroupDTO = new FamilyAttributeGroup();
        familyAttributeGroupDTO.setActive("Y");
        familyAttributeGroupDTO.setId("test");
        familyAttributeGroupDTO.setName("Test");
        familyAttributeGroupDTO.setFullId("test");
        familyAttributeGroupDTO.orchestrate();

        Assert.assertTrue(ValidationUtil.isNotEmpty(familyAttributeGroupDTO.getId()));
        Assert.assertEquals(familyAttributeGroupDTO.getId(), "test");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void isAvailable() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        FamilyAttributeGroup familyAttributeGroupDTO = new FamilyAttributeGroup();
        familyAttributeGroupDTO.setId("TEST");
        familyAttributeGroupDTO.setName("Test");
        familyAttributeGroupDTO.setActive("Y");

        //Checking for map
        Map<String, String> map = new HashMap<>();
        map.put("id", "TEST");
        map.put("name", "Test");
        map.put("active", "Y");

        Map<String, String> map1 = familyAttributeGroupDTO.toMap();
        Assert.assertEquals(map1.get("id"), map.get("id"));
        Assert.assertEquals(map1.get("name"), map.get("name"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void equals() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(AttributeCollection.class);
    }

}