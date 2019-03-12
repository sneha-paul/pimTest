package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.WebsiteService;
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
 * Created by sanoop on 12/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductTest {
    @Autowired
    ProductService productService;
    @Autowired
    ProductDAO productDAO;

    @Before
    public void setUp() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }
    @Test
    public void accessorsTest() {
        //Create new instance
      /*  Product productDTO = new Product();
        productDTO.setProductId("test");
        productDTO.setExternalId("test");
        productDTO.setProductName("Test.com");

        productDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(productDTO.getProductId(), "TEST");
        Assert.assertEquals(productDTO.getExternalId(), "TEST");
        Assert.assertEquals(productDTO.getProductName(), "Test.com");

        //create
        productService.create(productDTO);
        Product newProduct = productService.get(productDTO.getProductId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newProduct));
        Assert.assertEquals(newProduct.getProductId(), productDTO.getProductId());
        Assert.assertEquals(newProduct.getProductName(), productDTO.getProductName());
        Assert.assertEquals(newProduct.getExternalId(), productDTO.getExternalId());*/
    }
    @Test
    public void getProductFamilyId() throws Exception {
    }

    @Test
    public void setProductFamilyId() throws Exception {
    }

    @Test
    public void getChannelId() throws Exception {
    }

    @Test
    public void setChannelId() throws Exception {
    }

    @Test
    public void getProductFamily() throws Exception {
    }

    @Test
    public void setProductFamily() throws Exception {
    }

    @Test
    public void getScopedFamilyAttributes() throws Exception {
    }

    @Test
    public void setScopedFamilyAttributes() throws Exception {
    }

    @Test
    public void getChannelFamilyAttributes() throws Exception {
    }

    @Test
    public void setChannelFamilyAttributes() throws Exception {
    }

    @Test
    public void getScopedAssets() throws Exception {
    }

    @Test
    public void setScopedAssets() throws Exception {
    }

    @Test
    public void getChannelAssets() throws Exception {
    }

    @Test
    public void setChannelAssets() throws Exception {
    }

    @Test
    public void getDefaultAsset() throws Exception {
    }

    @Test
    public void setExternalId() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void cloneInstance() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void setAttributeValues() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

}