package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import com.m7.xtreme.common.util.ValidationUtil;
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

/**
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantTest {
    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ProductVariantDAO productVariantDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) productVariantDAO.getTemplate();
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

        mongoTemplate.dropCollection(ProductVariant.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantId("test");
        productVariantDTO.setProductVariantName("Test");
        productVariantDTO.setExternalId("test");
        productVariantDTO.setActive("Y");
        productVariantDTO.setDiscontinued("N");
        productVariantDTO.setChannelId("test11");

        productVariantDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(productVariantDTO.getProductVariantId(), "TEST");
        Assert.assertEquals(productVariantDTO.getProductVariantName(), "Test");
        Assert.assertEquals(productVariantDTO.getExternalId(), "TEST");
        Assert.assertEquals(productVariantDTO.getActive(), "Y");
        Assert.assertEquals(productVariantDTO.getDiscontinued(), "N");
        Assert.assertEquals(productVariantDTO.getChannelId(), "test11");

        //TODO

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
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
    public void diff() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(ProductVariant.class);
    }


}