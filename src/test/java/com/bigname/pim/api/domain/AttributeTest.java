package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.AttributeCollectionDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.mongo.UserDAO;
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


/**
 * Created by sanoop on 14/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeTest {
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
        //Create New Instance attributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionId("test");
        attributeCollectionDTO.setCollectionName("test");

        attributeCollectionDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(attributeCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(attributeCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(attributeCollectionDTO.getActive(), "N");

        //Create
        attributeCollectionService.create(attributeCollectionDTO);
        AttributeCollection newAttributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection));
        Assert.assertEquals(newAttributeCollection.getCollectionId(), attributeCollectionDTO.getCollectionId());
        Assert.assertEquals(newAttributeCollection.getCollectionName(), attributeCollectionDTO.getCollectionName());
        Assert.assertEquals(newAttributeCollection.getActive(), attributeCollectionDTO.getActive());

        //Create new instance attribute
        Attribute attributeDTO = new Attribute();
        attributeDTO.setActive("Y");
        attributeDTO.setName("test.com");

        Assert.assertEquals(attributeDTO.getActive(), "Y");
        Assert.assertEquals(attributeDTO.getName(), "test.com");

        newAttributeCollection.addAttribute(attributeDTO);
        attributeCollectionDAO.save(newAttributeCollection);

        AttributeCollection newAttributeCollection2 = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection2));
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getName(), attributeDTO.getName());
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getActive(), attributeDTO.getActive());

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
        //Create id
        Attribute attributeDTO = new Attribute();
        attributeDTO.setId("test");
        attributeDTO.setName("test");
        attributeDTO.orchestrate();

        //Check websiteId
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeDTO.getId()));
        Assert.assertEquals(attributeDTO.getId(), "test");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void compare() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AttributeCollection.class);
    }


}