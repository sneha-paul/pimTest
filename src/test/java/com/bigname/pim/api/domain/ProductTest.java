package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.*;
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

import java.util.*;

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
    @Autowired
    FamilyService familyService;
    @Autowired
    ChannelDAO channelDAO;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    AttributeCollectionService attributeCollectionService;
    @Autowired
    FamilyDAO familyDAO;

    @Before
    public void setUp() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }
    @Test
    public void accessorsTest() {
        /*//Create Family
        Family familyDTO = new Family();
        familyDTO.setFamilyId("tes11");
        familyDTO.setFamilyName("test");
        familyService.create(familyDTO);

       Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);

        //Create new instance
        Product productDTO = new Product();
        productDTO.setProductId("test");
        productDTO.setExternalId("test");
        productDTO.setProductName("Test.com");
        productDTO.setProductFamilyId(family.getFamilyId());

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
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "TEST", "externalId", "TEST", "active", "Y", "discontinue", "N"));

        familiesData.forEach((Map<String, Object> familyData) -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId",familyDetails.getFamilyId(), "productFamilyId", "TEST", "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });
        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

      /*  //Testing equals unique id
        Assert.assertEquals(product.getProductId(), "TEST");
        Assert.assertEquals(product.getExternalId(), "TEST");
        Assert.assertEquals(product.getProductName(), "Product Test 1");

        //create
        productService.create(product);
        Product newProduct = productService.get(product.getProductId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newProduct));
        Assert.assertEquals(newProduct.getProductId(), product.getProductId());
        Assert.assertEquals(newProduct.getProductName(), product.getProductName());
        Assert.assertEquals(newProduct.getExternalId(), product.getExternalId());*/
    }


    @Test
    public void orchestrate() throws Exception {
        //Create id
        Product productDTO = new Product();
        productDTO.setExternalId("test");
        productDTO.orchestrate();

        //Check websiteId
        Assert.assertTrue(ValidationUtil.isNotEmpty(productDTO.getProductId()));
        Assert.assertEquals(productDTO.getProductId(), "TEST");
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void cloneInstance() throws Exception {
        /*//Adding website
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "discontinued", "N", "active", "Y"));

        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String) productData.get("name"));
            productDTO.setProductFamilyId((String) productData.get("externalId"));
            productDTO.setActive((String) productData.get("active"));
            productDTO.setDiscontinued((String) productData.get("discontinued"));
            productDAO.insert(productDTO);

            //Clone website
            Product newProduct = productService.get(productDTO.getProductFamilyId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newProduct != null);
            Assert.assertTrue(newProduct.diff(productDTO).isEmpty());

            Product productClone = productService.cloneInstance(newProduct.getProductFamilyId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(productClone.getProductFamilyId() .equals(newProduct.getProductFamilyId() + "_COPY") && productClone.getProductName().equals(newProduct.getProductName() + "_COPY") && productClone.getDiscontinued().equals(newProduct.getDiscontinued() + "_COPY") && productClone.getActive() != newProduct.getActive());
        });*/
    }

    @Test
    public void toMap() throws Exception {
        //Create New Instance
        Product productDTO = new Product();
        productDTO.setProductName("test1");
        productDTO.setExternalId("test");

        Map<String, String> map = new HashMap<>();
        map.put("productName", "test1");
        map.put("externalId", "TEST");

        Map<String, String> map1 = productDTO.toMap();
        Assert.assertEquals(map1.get("productName"), map.get("productName"));
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));

    }

    @Test
    public void setAttributeValues() throws Exception {
    }

    @Test
    public void diff() throws Exception {
        //Create New Instance
        Product product1 = new Product();
        product1.setProductFamilyId("test");
        product1.setProductName("test11");
        product1.setActive("Y");
        product1.setExternalId("test");

        //Create second instance
        Product product2 = new Product();
        product2.setProductFamilyId("test");
        product2.setProductName("test11.com");
        product2.setActive("Y");
        product2.setExternalId("test");

        //Checking First instance and Second instance
        Map<String, Object> diff = product1.diff(product2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("productName"), "test11.com");

        //Checking First instance and Second instance
        Map<String, Object> diff1 = product1.diff(product2,true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("productName"), "test11.com");

    }
    @After
    public void tearDown() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}