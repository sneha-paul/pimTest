package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.mongo.*;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.api.service.*;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.Criteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantServiceImplTest {

    @Autowired
    private UserService userService;

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

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(!userService.get(ID.EXTERNAL_ID("MANU@BLACWOOD.COM")).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COm");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) productDAO.getTemplate();
        }

        mongoTemplate.dropCollection(Product.class);

        mongoTemplate.dropCollection(Family.class);

        mongoTemplate.dropCollection(AttributeCollection.class);

        mongoTemplate.dropCollection(Channel.class);

        mongoTemplate.dropCollection(ProductVariant.class);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        Page<ProductVariant> productVariants =  productVariantService.findAll("productVariantName", "Test", ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), PageRequest.of(0, productVariantData.size()), false);
        Assert.assertEquals(productVariants.getContent().size(), productVariantData.size());
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);

        ProductVariant productVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        productVariant.setActive("N");
        productVariant.setGroup("DETAILS");

        //updating ProductVariant
        ProductVariant updatedProductVariant =  productVariantService.update(ID.INTERNAL_ID(productVariant.getId()), productVariant);
        ProductVariant productVariant1 =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(productVariant1));
        Assert.assertEquals(productVariant1.getActive(), "N");
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertEquals(productVariant.getProductVariantName(), productVariantDTO.getProductVariantName());
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        ProductVariant productVariant = productVariantService.get(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), ID.EXTERNAL_ID(productVariantData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertEquals(productVariant.getProductVariantName(), productVariantData.get(0).get("name"));
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant = productVariantDAO.findById(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId())).orElse(null);
        ProductVariant productVariant1 = productVariantService.get(ID.INTERNAL_ID(productVariant.getId()), false).orElse(null);
        Assert.assertEquals(productVariant1.getProductVariantName(), productVariantDTO.getProductVariantName());
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        //toggle
        productVariantService.toggle(ID.INTERNAL_ID(productVariant.getId()), Toggle.get(productVariant.getActive()));
        ProductVariant updatedProductVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant));
        Assert.assertEquals(updatedProductVariant.getActive(), "N");

        productVariantService.toggle(ID.INTERNAL_ID(updatedProductVariant.getId()), Toggle.get(updatedProductVariant.getActive()));
        ProductVariant updatedProductVariant1 =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant1));
        Assert.assertEquals(updatedProductVariant1.getActive(), "Y");
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
        productVariantDAO.insert(productVariantDTO);
        // Getting productVariant
        ProductVariant productVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        //toggle
        productVariantService.toggle(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), ID.INTERNAL_ID(productVariant.getId()), Toggle.get(productVariant.getActive()));
        ProductVariant updatedProductVariant =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant));
        Assert.assertEquals(updatedProductVariant.getActive(), "N");

        productVariantService.toggle(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), ID.INTERNAL_ID(updatedProductVariant.getId()), Toggle.get(updatedProductVariant.getActive()));
        ProductVariant updatedProductVariant1 =  productVariantService.get(ID.EXTERNAL_ID(productVariantDTO.getProductVariantId()), channel.getChannelId(), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedProductVariant1));
        Assert.assertEquals(updatedProductVariant1.getActive(), "Y");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstanceTest() throws Exception {

        //TODO check(method not used)
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstance1Test() throws Exception {
        //TODO check(method not used)
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants = productVariantService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), channel.getChannelId(), false);
        Assert.assertEquals(productVariants.size(), productVariantData.size());
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        Page<ProductVariant> productVariants = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), null, false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants1 = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        Page<ProductVariant> productVariants = productVariantService.getAll(Arrays.stream(ids).map(ID::INTERNAL_ID).collect(Collectors.toList()), 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants1 = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        Page<ProductVariant> productVariants = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), Arrays.stream(ids).map(ID::INTERNAL_ID).collect(Collectors.toList()), 0, productVariantData.size(), null, false);
        Assert.assertEquals(productVariants.getContent().size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants1 = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        List<ProductVariant> productVariants = productVariantService.getAll(Arrays.stream(ids).map(ID::INTERNAL_ID).collect(Collectors.toList()), Sort.by("productVariantName"),false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        Page<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.getContent().stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        Page<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, productVariantData.size(), null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.getContent().stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> paginatedResult = productVariantService.getAllWithExclusions(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, ProductVariant> productVariant = paginatedResult.stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariant.size() == (productVariantData.size() - ids.length) && !productVariant.containsKey(ids[0]) && !productVariant.containsKey(ids[1]));    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void validateTest() throws Exception {
        //TODO MANU
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getProductVariantPricingTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addAssetsTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void deleteAssetTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void reorderAssetsTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setAsDefaultAssetTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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
        List<ProductVariant> productVariants1 = productVariantService.getAll(ID.EXTERNAL_ID(product.getProductId()), channel.getChannelId(), null, false);
        String[] ids={productVariants1.get(0).getId(),productVariants1.get(1).getId()};
        List<ProductVariant> productVariants = productVariantService.getAll(ID.INTERNAL_ID(product.getId()), channel.getChannelId(), Arrays.stream(ids).map(ID::INTERNAL_ID).collect(Collectors.toList()), null, false);
        Assert.assertEquals(productVariants.size(), 2);
    }

    @WithUserDetails("manu@blacwood.com")
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
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

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setProductVariantsSequenceTest() throws Exception {
        //TODO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createEntitiesTest(){
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);

        //creating AttributeOptions
        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        //creating Families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findById(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Family family = familyDAO.findById(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
            Assert.assertTrue(family != null);

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

            family.addAttribute(familyAttributeDTO);
            familyDAO.save(family);

        });

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        List<Product> productDTOs = productsData.stream().map(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName(productData.get("name").toString());
            productDTO.setProductId(productData.get("externalId").toString());
            productDTO.setActive((String)productData.get("active"));
            productDTO.setDiscontinued((String)productData.get("discontinue"));
            return productDTO;
        }).collect(Collectors.toList());

        productService.create(productDTOs);
        Assert.assertEquals(productDAO.findAll(PageRequest.of(0, productDTOs.size()), false).getTotalElements(), productsData.size());

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //create productVariantInstance
        List<Map<String, Object>> productVariantData = new ArrayList<>();
        productVariantData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST1", "active", "Y"));
        productVariantData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST2", "active", "Y"));

        List<ProductVariant> productVariantDTOs = productVariantData.stream().map(productVariant -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName(productVariant.get("name").toString());
            productVariantDTO.setProductVariantId(productVariant.get("externalId").toString());
            productVariantDTO.setProductId(product.getId());
            productVariantDTO.setActive(productVariant.get("active").toString());
            productVariantDTO.setChannelId(channel.getChannelId());
            return productVariantDTO;
        }).collect(Collectors.toList());

        productVariantService.create(productVariantDTOs);
        Assert.assertEquals(productVariantService.findAll(PageRequest.of(0, productDTOs.size()), false).getTotalElements(), productVariantData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllAtSearchTest() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariant by searchField
        long size = productVariantData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<ProductVariant> paginatedResult = productVariantService.findAll("active", "Y", PageRequest.of(0, productVariantData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll2Test() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariant
        long size = productVariantData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<ProductVariant> paginatedResult = productVariantService.findAll(PageRequest.of(0, productVariantData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateEntitiesTest(){
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        String[] ids = {productVariantData.get(0).get("externalId").toString(), productVariantData.get(1).get("externalId").toString()};

        Page<ProductVariant> productVariantResult = productVariantService.findAll(PageRequest.of(0, productVariantData.size()), true);
        Map<String, ProductVariant> productVariantsMap = productVariantResult.getContent().stream().collect(Collectors.toMap(variant -> variant.getProductVariantId(), variant -> variant));
        Assert.assertTrue(productVariantsMap.size() == ids.length && productVariantsMap.containsKey(ids[0]) && productVariantsMap.containsKey(ids[1]));

        List<ProductVariant> productVariants = productVariantResult.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        //Updating productVariants
        productVariantService.update(productVariants);
        productVariantResult = productVariantService.findAll(PageRequest.of(0, productVariantData.size()), true);
        productVariantsMap = productVariantResult.stream().collect(Collectors.toMap(variant -> variant.getProductVariantName(), variant -> variant));
        Assert.assertTrue(productVariantsMap.size() == (productVariantData.size() - ids.length) && !productVariantsMap.containsKey(ids[0]) && !productVariantsMap.containsKey(ids[1]));
        Assert.assertFalse(productVariantsMap.size() == productVariantData.size() && productVariantsMap.containsKey(ids[0]) && productVariantsMap.containsKey(ids[1]) );
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll3Test() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariants
        long size = productVariantData.stream().filter(x -> x.get("active").equals("Y")).count();
        List<ProductVariant> result = productVariantService.findAll(Criteria.where("active").eq("Y"));
        Assert.assertTrue(result.size() == size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll4Test() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariants
        long size = productsData.stream().filter(x -> x.get("active").equals("Y")).count();
        List<Product> result = productService.findAll(Criteria.where("active").eq("Y"));
        Assert.assertTrue(result.size() == size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOneTest() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariant
        ProductVariant result = productVariantService.findOne(Criteria.where("productVariantName").eq(productVariantData.get(0).get("name"))).orElse(null);
        Assert.assertEquals(productVariantData.get(0).get("name"), result.getProductVariantName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOne1Test() {
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

        Channel channel = channelService.get(ID.EXTERNAL_ID(channelsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));
        //creating AttributeCollection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");

        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollection));
        Assert.assertTrue(attributeCollection.diff(attributeCollectionDTO).isEmpty());
        //creating Attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test_Attribute");
        attribute.setId("TEST_ATTRIBUTE");
        attributeCollection.addAttribute(attribute);
        List<AttributeCollection> attributeList= new ArrayList<>();
        attributeList.add(attributeCollection);
        attributeCollectionService.update(attributeList);

        //creating family
        Family familyDTO = new Family();
        familyDTO.setFamilyName("Test1");
        familyDTO.setFamilyId("TEST_1");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyService.create(familyDTO);
        Family family=familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
        Assert.assertEquals(family.getFamilyName(), familyDTO.getFamilyName());

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", family.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Getting productVariant
        Criteria criteria = Criteria.where("productVariantName").eq(productVariantData.get(0).get("name"));
        ProductVariant result = productVariantService.findOne(criteria).orElse(null);
        Assert.assertEquals(productVariantData.get(0).get("name"), result.getProductVariantName());
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Product.class);

        mongoTemplate.dropCollection(Family.class);

        mongoTemplate.dropCollection(AttributeCollection.class);

        mongoTemplate.dropCollection(Channel.class);

        mongoTemplate.dropCollection(Category.class);

        mongoTemplate.dropCollection(ProductVariant.class);
    }

}