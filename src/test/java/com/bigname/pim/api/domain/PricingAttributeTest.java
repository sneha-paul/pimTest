package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class PricingAttributeTest {
    @Autowired
    private PricingAttributeService pricingAttributeService;
    @Autowired
    private PricingAttributeDAO pricingAttributeDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) pricingAttributeDAO.getTemplate();
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
        mongoTemplate.dropCollection(PricingAttribute.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        PricingAttribute pricingAttributeDTO = new PricingAttribute();
        pricingAttributeDTO.setPricingAttributeId("test");
        pricingAttributeDTO.setPricingAttributeName("test");

        pricingAttributeDTO.orchestrate();

        //Testing equals with id
        Assert.assertEquals(pricingAttributeDTO.getPricingAttributeId(), "TEST");
        Assert.assertEquals(pricingAttributeDTO.getPricingAttributeName(), "test");
        Assert.assertEquals(pricingAttributeDTO.getActive(), "N");

        pricingAttributeService.create(pricingAttributeDTO);
        PricingAttribute newPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newPricingAttribute));
        Assert.assertEquals(newPricingAttribute.getPricingAttributeId(), pricingAttributeDTO.getPricingAttributeId());
        Assert.assertEquals(newPricingAttribute.getPricingAttributeName(), pricingAttributeDTO.getPricingAttributeName());
        Assert.assertEquals(newPricingAttribute.getActive(), pricingAttributeDTO.getActive());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstance() throws Exception {
        //Create New Map
        List<Map<String,Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO = new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String) pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String) pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String) pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);

            //Clone PricingAttribute
            PricingAttribute newPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId()), false).orElse(null);
           Assert.assertTrue(newPricingAttribute != null);
             Assert.assertTrue(newPricingAttribute.diff(pricingAttributeDTO).isEmpty());

            PricingAttribute pricingAttributeClone = pricingAttributeService.cloneInstance(ID.EXTERNAL_ID(newPricingAttribute.getPricingAttributeId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(pricingAttributeClone.getPricingAttributeId() .equals(newPricingAttribute.getPricingAttributeId() + "_COPY") && pricingAttributeClone.getPricingAttributeName().equals(newPricingAttribute.getPricingAttributeName() + "_COPY") &&  pricingAttributeClone.getActive() != newPricingAttribute.getActive());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create Original instance
        PricingAttribute original = new PricingAttribute();
        original.setPricingAttributeId("test");
        original.setExternalId("test");
        original.setPricingAttributeName("test");
        original.setActive("N");

        //Modified instance or ADD details
        PricingAttribute modified = new PricingAttribute();
        modified.setGroup("DETAILS");
        modified.setPricingAttributeName("TEST");
        modified.setPricingAttributeId("TEST");
        modified.setExternalId("TEST");
        modified.setActive("N");

        //Merge Original to Modified
        original = original.merge(modified);
        Assert.assertEquals(original.getPricingAttributeName(), "TEST");
        Assert.assertEquals(original.getPricingAttributeId(), "TEST");
        Assert.assertEquals(original.getExternalId(), "TEST");
        Assert.assertEquals(original.getActive(), "N");

        //Without Details
        PricingAttribute modified1 = new PricingAttribute();
        modified1.setPricingAttributeName("test");
        modified1.setPricingAttributeId("test");
        modified1.setExternalId("test");
        modified1.setActive("N");

        original = original.merge(modified1);
        Assert.assertEquals(original.getPricingAttributeName(), "TEST");
        Assert.assertEquals(original.getPricingAttributeId(), "TEST");
        Assert.assertEquals(original.getExternalId(), "TEST");
        Assert.assertEquals(original.getActive(), "N");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        PricingAttribute pricingAttributeDTO = new PricingAttribute();
        pricingAttributeDTO.setExternalId("test");
        pricingAttributeDTO.setPricingAttributeName("Test");
        pricingAttributeDTO.setActive("Y");

        //Create New Instance For Checking Map
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("pricingAttributeName", "Test");
        map.put("active", "Y");

        Map<String, String> map1 = pricingAttributeDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("pricingAttributeName"), map.get("pricingAttributeName"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void diff() throws Exception {
        //Create first instance
        PricingAttribute pricingAttribute1 = new PricingAttribute();
        pricingAttribute1.setExternalId("test");
        pricingAttribute1.setPricingAttributeName("test");

        //Create second instance
        PricingAttribute pricingAttribute2 = new PricingAttribute();
        pricingAttribute2.setExternalId("test");
        pricingAttribute2.setPricingAttributeName("test.com");

        Map<String, Object> diff = pricingAttribute1.diff(pricingAttribute2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("pricingAttributeName"), "test.com");

        //Create Second Instance For Ignore Internal Id
        //Create first instance
        PricingAttribute pricingAttribute3 = new PricingAttribute();
        pricingAttribute3.setExternalId("test");
        pricingAttribute3.setPricingAttributeName("test");

        //Create second instance
        PricingAttribute pricingAttribute4 = new PricingAttribute();
        pricingAttribute4.setExternalId("test");
        pricingAttribute4.setPricingAttributeName("test.com2");

        Map<String, Object> diff1 = pricingAttribute3.diff(pricingAttribute4, true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("pricingAttributeName"), "test.com2");
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(PricingAttribute.class);
    }

}