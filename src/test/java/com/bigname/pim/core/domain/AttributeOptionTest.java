package com.bigname.pim.core.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.persistence.dao.mongo.AttributeCollectionDAO;
import com.bigname.pim.core.service.AttributeCollectionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Created by sanoop on 14/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeOptionTest {
    @Autowired
    private AttributeCollectionService attributeCollectionService;
    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) attributeCollectionDAO.getTemplate();
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


        mongoTemplate.dropCollection(AttributeCollection.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create Attribute collection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test");
        attributeCollectionDTO.setCollectionId("TEST");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);

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

        Page<AttributeOption> result = attributeCollectionService.getAttributeOptions(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), attributeCollectionDTO.getAttributeFullId(attributeOption),0,1,null);

        Assert.assertEquals(attributeOption.getActive(), "Y");
        Assert.assertEquals(attributeOption.getValue(), "TestOption");

        Assert.assertTrue(ValidationUtil.isNotEmpty(result));
        Assert.assertEquals(result.getContent().get(0).getActive(), attributeOption.getActive());
        Assert.assertEquals(result.getContent().get(0).getValue(), attributeOption.getValue());
        Assert.assertEquals(result.getContent().get(0).getCollectionId(), attributeOption.getCollectionId());


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
    public void getAttributeId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setAttributeId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getCollectionId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setCollectionId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getValue() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setValue() throws Exception {
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
    public void getIndependent() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setIndependent() throws Exception {
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
    public void getParentOptionFullId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setParentOptionFullId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getParentOptionValue() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setParentOptionValue() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getReferenceMap() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setReferenceMap() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create Original Instance
        AttributeOption original = new AttributeOption();
        original.setValue("test");
        original.setParentOptionFullId("test");

        //Create Modified Instance
        AttributeOption modified = new AttributeOption();
        modified.setValue("TEST-A");
        modified.setParentOptionFullId("TEST-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getValue(), "TEST-A");
        Assert.assertEquals(original.getParentOptionFullId(), "TEST-A");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
        //Create New Instance
        AttributeOption attributeOptionDTO = new AttributeOption();
        attributeOptionDTO.setActive("Y");
        attributeOptionDTO.setIndependent("test");
        attributeOptionDTO.setId("test");
        attributeOptionDTO.orchestrate();

        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeOptionDTO.getId()));
        Assert.assertEquals(attributeOptionDTO.getId(), "TEST");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        AttributeOption attributeOptionDTO = new AttributeOption();
        attributeOptionDTO.setId("test");
        attributeOptionDTO.setValue("Test");
        attributeOptionDTO.setActive("Y");

        //Checking For Map
        Map<String, String> map = new HashMap<>();
        map.put("id", "TEST");
        map.put("value", "Test");
        map.put("active", "Y");

        Map<String, String> map1 = attributeOptionDTO.toMap();
        Assert.assertEquals(map1.get("id"), map.get("id"));
        Assert.assertEquals(map1.get("value"), map.get("value"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AttributeCollection.class);
    }
}