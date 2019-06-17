package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.FindBy;
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
    private ProductDAO productDAO;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Before
    public void setUp() {
        productDAO.getMongoTemplate().dropCollection(Product.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }

    @Test
    public void createProductTest() {
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelDAO.insert(channel);
        });

        Channel channel = channelDAO.findById(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));

        List<Map<String, Object>> attributesData = new ArrayList<>();
        attributesData.add(CollectionsUtil.toMap("name", "Color", "externalId", "COLOR", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute2", "externalId", "TEST_ATTRIBUTE_2", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute3", "externalId", "TEST_ATTRIBUTE_3", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute4", "externalId", "TEST_ATTRIBUTE_4", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute5", "externalId", "TEST_ATTRIBUTE_5", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute6", "externalId", "TEST_ATTRIBUTE_6", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute7", "externalId", "TEST_ATTRIBUTE_7", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute8", "externalId", "TEST_ATTRIBUTE_8", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute9", "externalId", "TEST_ATTRIBUTE_9", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute10", "externalId", "TEST_ATTRIBUTE_10", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));

        attributesData.forEach(attributeData -> {
            Attribute attribute = new Attribute();
            attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
            attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
            attribute.setName((String) attributeData.get("name"));
            attribute.setId((String) attributeData.get("externalId"));
            attribute.setActive((String) attributeData.get("active"));
            attributeCollectionDetails.addAttribute(attribute);
        });

        attributeCollectionDAO.save(attributeCollectionDetails);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);

        List<Map<String, Object>> attributeOptionsData = new ArrayList<>();
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Blue", "active", "Y"));
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Green", "active", "Y"));
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Red", "active", "Y"));

        attributeOptionsData.forEach(attributeOptionData -> {
            AttributeOption attributeOption = new AttributeOption();
            attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
            attributeOption.setValue((String) attributeOptionData.get("value"));
            attributeOption.setAttributeId(attributeDetails.getFullId());
            attributeOption.setActive((String) attributeOptionData.get("active"));
            attributeOption.orchestrate();
            attributeDetails.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
        });

        attributeCollectionDAO.save(attributeCollectionDetails);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
            familyAttributeGroup.setId(familyAttributeGroup.getFullId());

            //Create the new familyAttribute instance
            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeDetails.getName(), null);
            familyAttributeDTO.setActive("Y");
            familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
            familyAttributeDTO.setUiType(attributeDetails.getUiType());
            familyAttributeDTO.setScopable("Y");
            familyAttributeDTO.setAttributeId(attributeDetails.getFullId());
            familyAttributeDTO.getScope().put(channel.getChannelId(), FamilyAttribute.Scope.OPTIONAL);
            familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
            familyAttributeDTO.setAttribute(attributeDetails);

            family.addAttribute(familyAttributeDTO);

            FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attributeDetails.getId());

            List<AttributeOption> attributeOptionList = new ArrayList(attributeDetails.getOptions().values());
            attributeOptionList.forEach(attributeOption -> {
                FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                familyAttributeOption.setActive("Y");
                familyAttributeOption.setValue(attributeOption.getValue());
                familyAttributeOption.setId(attributeOption.getId());
                familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                familyAttributeDTO.getOptions().put(attributeOption.getId(), familyAttributeOption);
                family.addAttributeOption(familyAttributeOption, attributeOption);
            });

            //set parentAttribute //TODO

            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName(attributeDetails.getName());
            variantGroup.setId(attributeDetails.getId());
            variantGroup.setActive("Y");
            variantGroup.setLevel(1);
            variantGroup.setFamilyId(family.getFamilyId());
            variantGroup.getVariantAxis().put(1, Arrays.asList(attributeDetails.getName()));
            variantGroup.getVariantAttributes().put(1, Arrays.asList(attributes.get(2).getName(), attributes.get(9).getName()));
            family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put(channel.getChannelId(), variantGroup.getId());

            familyDAO.save(family);

        });

        Family familyDetails = familyDAO.findByExternalId(familiesData.get(0).get("externalId").toString()).orElse(null);

        //create Product instance
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setChannelId(channel.getChannelId());
        productDTO.setProductFamilyId(familyDetails.getId());
        productDTO.setActive("Y");
        Product product = productDAO.insert(productDTO);
        Assert.assertTrue(product.diff(productDTO).isEmpty());

        /*List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 2", "externalId", "PRODUCT_TEST_2", "productFamilyId", familyDetails.getId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 3", "externalId", "PRODUCT_TEST_3", "productFamilyId", familyDetails.getId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 4", "externalId", "PRODUCT_TEST_4", "productFamilyId", familyDetails.getId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productDAO.insert(productDTO);
        });*/

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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Optional<Family> family = familyDAO.findByExternalId(familyDTO.getFamilyId());
            Assert.assertTrue(family.isPresent());
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
            familyAttributeGroup.setId(familyAttributeGroup.getFullId());

            //Create the new familyAttribute instance
            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attribute.getName(), null);
            familyAttributeDTO.setActive("Y");
            familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
            familyAttributeDTO.setUiType(attribute.getUiType());
            familyAttributeDTO.setScopable("Y");
            familyAttributeDTO.setAttributeId(attribute.getFullId());
            familyAttributeDTO.getScope().put("ECOMMERCE", FamilyAttribute.Scope.OPTIONAL);
            familyAttributeDTO.setAttributeGroup(familyAttributeGroup); //TODO : check whether right or wrong
            familyAttributeDTO.setAttribute(attribute);

            family.get().addAttribute(familyAttributeDTO);

            FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
            familyAttributeOption.setActive("Y");
            familyAttributeOption.setValue(attributeOption.getValue());
            familyAttributeOption.setId(attributeOption.getId());
            familyAttributeOption.setFamilyAttributeId(familyAttributeDTO.getId());
            familyAttributeDTO.getOptions().put(attributeOption.getId(), familyAttributeOption);


            //set parentAttribute //TODO

            //create variantGroup
            family.get().setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            family.get().addVariantGroup(variantGroup);
            family.get().getChannelVariantGroups().put("ECOMMERCE", variantGroup.getId());

            familyDAO.save(family.get());

        });

        Family familyDetails = familyDAO.findByExternalId(familiesData.get(0).get("externalId").toString()).orElse(null);


        //create Product instance
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setChannelId("AMAZON");
        productDTO.setProductFamilyId(familyDetails.getId());
        // productDTO.setProductFamily();
        productDTO.setActive("Y");
        Product product = productDAO.insert(productDTO);
        Assert.assertTrue(product.diff(productDTO).isEmpty());


        Product productDetails = productDAO.findByExternalId(productDTO.getProductId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(productDetails));

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

        Optional<Product> product1 = productDAO.findByExternalId(productDetails.getProductId());
        Assert.assertTrue(product1.isPresent());
        product1 = productDAO.findById(productDetails.getProductId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(product1.isPresent());
        product1 = productDAO.findById(productDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(product1.isPresent());

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

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }

}
