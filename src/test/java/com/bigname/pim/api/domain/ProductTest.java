package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.FamilyDAO;
import com.bigname.pim.api.persistence.dao.mongo.ProductDAO;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    FamilyDAO familyDAO;
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) productDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
    }
    @Test
    public void accessorsTest() {
       //Create Family
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "TEST", "externalId", "TEST", "active", "Y", "discontinue", "N"));

        familiesData.forEach((Map<String, Object> familyData) -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

        });

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        //Creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId","TEST", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });
        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //Testing equals unique id
        Assert.assertEquals(product.getProductId(), "TEST");
        Assert.assertEquals(product.getExternalId(), "TEST");
        Assert.assertEquals(product.getProductName(), "Product Test 1");

        Assert.assertTrue(ValidationUtil.isNotEmpty(product));
        Assert.assertEquals(productsData.get(0).get("externalId"),product.getExternalId());
        Assert.assertEquals(product.getProductFamilyId(), familyDetails.getId());
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
        //create Original instance
        Product original = new Product();
        original.setExternalId("test");
        original.setProductName("Test");
        original.setActive("Y");

        //Create modified instance
        Product modified = new Product();
        modified.setGroup("DETAILS");
        modified.setExternalId("test-A");
        modified.setProductName("Test-A");
        modified.setActive("Y");

        original = original.merge(modified);
        Assert.assertEquals(original.getProductName(), "Test-A");
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getActive(), "Y");

        //Without Details
        Product modified1 = new Product();
        modified1.setExternalId("test");
        modified1.setProductName("Test");
        modified1.setActive("Y");

        original = original.merge(modified1);
        Assert.assertEquals(original.getProductName(), "Test-A");
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getActive(), "Y");
    }

    @Test
    public void cloneInstance() throws Exception {

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
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
    }

}