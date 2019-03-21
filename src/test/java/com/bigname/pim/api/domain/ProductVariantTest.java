package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
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

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

/**
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantTest {
    @Autowired
    ProductVariantService productVariantService;
    @Autowired
    ProductVariantDAO productVariantDAO;
    @Before
    public void setUp() throws Exception {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }
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
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }


}