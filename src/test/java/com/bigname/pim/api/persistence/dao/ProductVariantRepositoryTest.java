package com.bigname.pim.api.persistence.dao;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.ProductVariant;
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

import java.util.Optional;

/**
 * Created by dona on 19-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantRepositoryTest {

    @Autowired
    ProductVariantDAO productVariantDAO;

    @Before
    public void setUp() {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }

    @Test
    public void createProductVariantTest() {
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId("11a8377c-6a97-49b8-b72a-5f98217e9b88");
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        ProductVariant productVariant = productVariantDAO.insert(productVariantDTO);
        Assert.assertTrue(productVariant.diff(productVariantDTO).isEmpty());

    }

    @Test
    public void retrieveProductVariantTest() {
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId("11a8377c-6a97-49b8-b72a-5f98217e9b88");
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        productVariantDAO.insert(productVariantDTO);
        Optional<ProductVariant> productVariant = productVariantDAO.findByExternalId(productVariantDTO.getProductVariantId());
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
    }

    @Test
    public void updateProductVariantTest() {
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId("11a8377c-6a97-49b8-b72a-5f98217e9b88");
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        productVariantDAO.insert(productVariantDTO);

        productVariantDTO.setProductVariantName("Test1Name");
        productVariantDTO.setProductVariantId("TEST1_ID");
        productVariantDTO.setActive("N");
        productVariantDAO.save(productVariantDTO);

        Optional<ProductVariant> productVariant = productVariantDAO.findByExternalId(productVariantDTO.getProductVariantId());
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
    }


    @After
    public void tearDown() {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }
}
