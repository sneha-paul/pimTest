package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantServiceImplTest {
    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private ProductVariantDAO productVariantDAO;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ProductVariantService productVariantService;

    @Before
    public void setUp() throws Exception {

        productDAO.getMongoTemplate().dropCollection(Product.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);

        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }

    @Test
    public void findAllTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        //Getting productVariants
        Page<ProductVariant> productVariants =  productVariantService.findAll("productVariantName", "Test", product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), PageRequest.of(0, productVariantData.size()), false);
        Assert.assertEquals(productVariants.getContent().size(), productVariantData.size());
    }

    @Test
    public void updateTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);

        ProductVariant productVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        productVariant.setActive("N");
        productVariant.setGroup("DETAILS");

        //updating ProductVariant
        ProductVariant updatedProductVariant =  productVariantService.update(productVariant.getId(), FindBy.INTERNAL_ID, productVariant);
        ProductVariant productVariant1 =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(productVariant1));
        Assert.assertEquals(productVariant1.getActive(), "N");
    }

    @Test
    public void getTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertEquals(productVariant.getProductVariantName(), productVariantDTO.getProductVariantName());
    }

    @Test
    public void get1Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        //Getting productVariant
        ProductVariant productVariant = productVariantService.get(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(),productVariantData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertEquals(productVariant.getProductVariantName(), productVariantData.get(0).get("name"));
    }

    @Test
    public void get2Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant = productVariantDAO.findById(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID).orElse(null);
        ProductVariant productVariant1 = productVariantService.get(productVariant.getId(), FindBy.INTERNAL_ID, false).orElse(null);
        Assert.assertEquals(productVariant1.getProductVariantName(), productVariantDTO.getProductVariantName());
    }

    @Test
    public void toggleTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        //toggle
        productVariantService.toggle(productVariant.getId(), FindBy.INTERNAL_ID, Toggle.get(productVariant.getActive()));
        ProductVariant updatedProductVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant));
        Assert.assertEquals(updatedProductVariant.getActive(), "N");

        productVariantService.toggle(updatedProductVariant.getId(), FindBy.INTERNAL_ID, Toggle.get(updatedProductVariant.getActive()));
        ProductVariant updatedProductVariant1 =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant1));
        Assert.assertEquals(updatedProductVariant1.getActive(), "Y");
    }

    @Test
    public void toggle1Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        //toggle
        productVariantService.toggle(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), productVariant.getId(), FindBy.INTERNAL_ID, Toggle.get(productVariant.getActive()));
        ProductVariant updatedProductVariant =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant));
        Assert.assertEquals(updatedProductVariant.getActive(), "N");

        productVariantService.toggle(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), updatedProductVariant.getId(), FindBy.INTERNAL_ID, Toggle.get(updatedProductVariant.getActive()));
        ProductVariant updatedProductVariant1 =  productVariantService.get(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID, channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant1));
        Assert.assertEquals(updatedProductVariant1.getActive(), "Y");
    }

    @Test
    public void cloneInstanceTest() throws Exception {

        //TODO check(method not used)
    }

    @Test
    public void cloneInstance1Test() throws Exception {
        //TODO check(method not used)
    }

    @Test
    public void getAllTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        List<Map<String,Object>> productVariants = productVariantService.getAll();
        Assert.assertEquals(productVariants.size(), productVariantData.size());
    }

    @Test
    public void getAll1Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        String[] ids = {productsData.get(0).get("externalId").toString()};
        List<ProductVariant> productVariants = productVariantService.getAll(ids, FindBy.EXTERNAL_ID, channel.getChannelId(), false);
        Assert.assertEquals(productVariants.size(), productVariantData.size());
    }

    @Test
    public void getAll2Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        Page<ProductVariant> productVariants = productVariantService.getAll(0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 0);
    }

    @Test
    public void getAll3Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        Page<ProductVariant> productVariants = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @Test
    public void getAll4Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        List<ProductVariant> productVariants = productVariantService.getAll( null, false);
        Assert.assertEquals(productVariants.size(), 0);
    }

    @Test
    public void getAll5Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        List<ProductVariant> productVariants = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), null, false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @Test
    public void getAll6Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariant
        List<ProductVariant> productVariants1 = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        Page<ProductVariant> productVariants = productVariantService.getAll(ids, FindBy.INTERNAL_ID, 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @Test
    public void getAll7Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariants
        List<ProductVariant> productVariants1 = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        Page<ProductVariant> productVariants = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), ids, FindBy.INTERNAL_ID, 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @Test
    public void getAll8Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariant
        List<ProductVariant> productVariants1 = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        List<ProductVariant> productVariants = productVariantService.getAll(ids,FindBy.INTERNAL_ID, Sort.by("productVariantName"),false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @Test
    public void getAllWithExclusionsTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariant
        String[] ids = {productVariantData.get(0).get("externalId").toString(), productVariantData.get(1).get("externalId").toString()};
        //Getting productVariants with exclude Ids
        Page<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.getContent().stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @Test
    public void getAllWithExclusions1Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariant
        String[] ids = {productVariantData.get(0).get("externalId").toString(), productVariantData.get(1).get("externalId").toString()};
        //Getting productVariants with exclude Ids
        Page<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), ids, FindBy.EXTERNAL_ID, 0, productVariantData.size(), null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.getContent().stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @Test
    public void getAllWithExclusions2Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        String[] ids = {productVariantData.get(0).get("externalId").toString(), productVariantData.get(1).get("externalId").toString()};
        //Getting productVariants with exclude Ids
        List<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(ids, FindBy.EXTERNAL_ID, null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @Test
    public void getAllWithExclusions3Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });

        //Getting productVariants with exclude Ids
        String[] ids = {productVariantData.get(0).get("externalId").toString(), productVariantData.get(1).get("externalId").toString()};
        List<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), ids, FindBy.EXTERNAL_ID, null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));    }

    @Test
    public void validateTest() throws Exception {
        //TODO MANU
    }

    @Test
    public void getProductVariantPricingTest() throws Exception {
        //TODO
    }

    @Test
    public void addAssetsTest() throws Exception {
        //TODO
    }

    @Test
    public void deleteAssetTest() throws Exception {
        //TODO
    }

    @Test
    public void reorderAssetsTest() throws Exception {
        //TODO
    }

    @Test
    public void setAsDefaultAssetTest() throws Exception {
        //TODO
    }

    @Test
    public void getAll9Test() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantDAO.insert(productVariantDTO);
        });
        // Getting productVariant
        List<ProductVariant> productVariants1 = productVariantService.getAll(product.getProductId(), FindBy.EXTERNAL_ID, channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        List<ProductVariant> productVariants = productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, channel.getChannelId(), ids, FindBy.INTERNAL_ID, null, false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @Test
    public void createTest() throws Exception {
        //creating channel
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelService.create(channel);
        });

        Channel channel = channelService.get(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
        //creating Attributes
        List<AttributeCollection> attributeCollectionList = new ArrayList<>();
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
        attributeCollectionList.add(attributeCollectionDetails);
        attributeCollectionService.update(attributeCollectionList);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);
        //Creating AttributeOption
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

        attributeCollectionService.update(attributeCollectionList);

        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(familyDTO.getFamilyId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

            List<Family> familyList = new ArrayList<>();
            familyList.add(family);
            familyService.update(familyList);

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));
        productVariantData.forEach(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            productVariantService.create(productVariantDTO);
        });
        List<Map<String,Object>> productVariant = productVariantService.getAll();
        Assert.assertEquals(productVariant.size(), productVariantData.size());
    }

    @Test
    public void setProductVariantsSequenceTest() throws Exception {
        //TODO
    }

    @After
    public void tearDown() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);

        categoryDAO.getMongoTemplate().dropCollection(Category.class);

        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }

}