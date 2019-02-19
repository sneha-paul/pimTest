package com.bigname.pim.api.persistence.dao;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Product;
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
public class ProductRepositoryTest {
    @Autowired
    ProductDAO productDAO;

    @Before
    public void setUp() {
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

    @Test
    public void createProductTest() {
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setProductFamilyId("7abf9064-aba5-4573-9557-e1d83547e771");
        productDTO.setChannelId("AMAZON");
        productDTO.setActive("Y");
        Product product = productDAO.insert(productDTO);
        Assert.assertTrue(product.diff(productDTO).isEmpty());

    }

    @Test
    public void retrieveProductTest() {
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setProductFamilyId("PARENT_TEST");
        productDTO.setChannelId("AMAZON");
        productDTO.setActive("Y");
        productDAO.insert(productDTO);
        Optional<Product> product = productDAO.findByExternalId(productDTO.getProductId());
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDTO.getProductId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(product.isPresent());
    }

    @Test
    public void updateProductTest() {
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setProductFamilyId("PARENT_TEST");
        productDTO.setChannelId("AMAZON");
        productDTO.setActive("Y");
        productDAO.insert(productDTO);

        productDTO.setProductName("Test1Name");
        productDTO.setActive("Y");
        productDAO.save(productDTO);
        Optional<Product> product = productDAO.findByExternalId(productDTO.getProductId());
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDTO.getProductId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(product.isPresent());

    }

    @After
    public void tearDown() {
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

}
