package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Autowired
    FamilyDAO familyDAO;

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

         /*AttributeCollection attributeCollection1 = new AttributeCollection();
        attributeCollection1.setGroup("ATTRIBUTES");
        attributeCollection1.addAttribute(attributeDetailsMap);*/

        /*Map<String , Attribute> attributeMap = null;

        attributeMap.put("STYLE", attributeDetailsMap);

        AttributeGroup attributeGroup = new AttributeGroup();
        attributeGroup.setAttributes(attributeMap);

        Map<String, AttributeGroup> attributesMap = null;
        attributesMap.put("DEFAULT_GROUP", attributeGroup);*/
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        /*Attribute attributeDetails1 = new Attribute();
        attributeDetails1.setName("Style");
        attributeDetails1.setLabel("Style");
        attributeDetails1.setDataType("string");

        Map<String, Attribute> attributeMap = new HashMap<>();
        attributeMap.put("Attributes", attributeDetails1);

        AttributeGroup attributeGroup = new AttributeGroup();
        attributeGroup.setAttributes(attributeMap);

        Map<String, AttributeGroup> attributeGroupHashMap = new HashMap<>();
        attributeGroupHashMap.put("DEFAULT_GROUP", attributeGroup);

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Envelopes Attributes Collection");
        attributeCollectionDTO.setCollectionId("ENVELOPES");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        //attributeCollectionDTO.setAttributes(attributesMap);
        attributeCollectionDAO.insert(attributeCollectionDTO);
        AttributeCollection attributeCollection = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
        Assert.assertTrue(attributeCollection != null);
        attributeCollection.setGroup("ATTRIBUTES");
        //attributeCollection.addAttribute(attributeDetailsMap);
        attributeCollection.setAttributes(attributeGroupHashMap);
        attributeCollectionDAO.save(attributeCollection);*/

        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);

        Family familyDTO = new Family();
        familyDTO.setFamilyId("PAPER");
        familyDTO.setFamilyName("PAPER");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyDAO.insert(familyDTO);
        Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
        Assert.assertTrue(family != null);


        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setProductFamilyId(family.getFamilyId());
        productDTO.setChannelId("AMAZON");
        productDTO.setActive("Y");
        productDAO.insert(productDTO);

        Product productDetails = productDAO.findByExternalId(productDTO.getProductId()).orElse(null);
        Assert.assertTrue(productDetails != null);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("PLAIN_LEAD_TIME", "5");
        attributes.put("STANDARD_LEAD_TIME", "3");

        Map<String, Object> assets = new HashMap<>();
        Map<String, Object> assetsDetails = new HashMap<>();
        assetsDetails.put("defaultFlag", "Y");
        assetsDetails.put("name", "LUX-KWBM-0.png");
        assetsDetails.put("type", "IMAGE");
        assets.put("ASSETS", assetsDetails);

        productDetails.setProductName("Test1Name");
        productDetails.setScopedFamilyAttributes("ECOMMERCE", attributes);
        productDetails.setScopedAssets("ECOMMERCE", assets);
        productDetails.setGroup("DETAILS");
        productDAO.save(productDetails);

        Optional<Product> product = productDAO.findByExternalId(productDetails.getProductId());
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDetails.getProductId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(product.isPresent());
        product = productDAO.findById(productDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(product.isPresent());

    }

    @Test
    public void retrieveProductsTest() {

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y"));

        List<Product> productDTOs = productsData.stream().map(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productDTO.setDiscontinued((String)productData.get("discontinued"));
            return productDTO;
        }).collect(Collectors.toList());

        productDAO.insert(productDTOs);

        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false).getTotalElements(), productDTOs.size());
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size() - 1), false).getTotalElements(), productDTOs.size());
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size() - 1), false).getContent().size(), productDTOs.size() - 1);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(1, productDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size() - 1), false).getTotalPages(), 2);

        productDAO.getMongoTemplate().dropCollection(Product.class);

        productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "N", "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "N", "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "N", "discontinued", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "N", "discontinued", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "active", "Y", "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        productDTOs = productsData.stream().map(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productDTO.setDiscontinued((String)productData.get("discontinued"));
            if("Y".equals(productData.get("discontinued"))){
                discontinued[0] ++;
            } else {
                if("Y".equals(productData.get("active"))) {
                    activeCount[0] ++;
                } else {
                    inactiveCount[0] ++;
                }
            }
            return productDTO;
        }).collect(Collectors.toList());

        productDAO.insert(productDTOs);

        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, false, true).getTotalElements(), discontinued[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, true, true).getTotalElements(), inactiveCount[0] + discontinued[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true, true, true).getTotalElements(), activeCount[0] + inactiveCount[0] + discontinued[0]);

        productDAO.getMongoTemplate().dropCollection(Product.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        productsData = new ArrayList<>();

        productsData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "activeFrom", yesterday, "activeTo", todayEOD, "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "activeFrom", null, "activeTo", todayEOD, "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "activeFrom", tomorrow, "discontinued", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "N", "activeFrom", null, "activeTo", null, "discontinued", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "activeFrom", yesterday, "activeTo", tomorrowEOD, "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "activeFrom", yesterday, "activeTo", null, "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "productFamilyId", "7abf9064-aba5-4573-9557-e1d83547e771", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        productsData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "productFamilyId", "008b2edc-2dea-47a9-8965-554b46080368", "active", "Y", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        int[] activeCount1 = {0}, inactiveCount1 = {0}, discontinued1 = {0};

        productDTOs = productsData.stream().map(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setDiscontinued((String)productData.get("discontinued"));
            productDTO.setActive((String)productData.get("active"));
            productDTO.setActiveFrom((LocalDateTime) productData.get("activeFrom"));
            productDTO.setActiveTo((LocalDateTime) productData.get("activeTo"));

            if(PimUtil.hasDiscontinued(productDTO.getDiscontinued(), productDTO.getDiscontinuedFrom(), productDTO.getDiscontinuedTo())) {
                discontinued1[0]++;
            } else if(PimUtil.isActive(productDTO.getActive(), productDTO.getActiveFrom(), productDTO.getActiveTo())) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            return productDTO;
        }).collect(Collectors.toList());

        productDAO.insert(productDTOs);

        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true, true, true).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true, true, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), true, false, true).getTotalElements(), activeCount1[0] + discontinued1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, true, true).getTotalElements(), inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        /*Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);*/
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false, false, true).getTotalElements(), discontinued1[0]);
    }

    @After
    public void tearDown() {
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

}
