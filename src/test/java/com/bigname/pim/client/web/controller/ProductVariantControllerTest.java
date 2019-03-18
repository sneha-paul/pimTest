package com.bigname.pim.client.web.controller;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes={PimApplication.class})
public class ProductVariantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductVariantDAO productVariantDAO;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductService productService;

    @Autowired
    FamilyDAO familyDAO;

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;


    @Before
    public void setUp() throws Exception {
        if(!userService.get("MANU@BLACWOOD.COM", FindBy.EXTERNAL_ID).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COm");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);

    }

    @Test
    public void getAvailableVariants() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void create() throws Exception {
        /*List<Map<String, Object>> channelsData = new ArrayList<>();
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));
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

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

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
        //create Product instance
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setChannelId(channel.getChannelId());
        productDTO.setProductFamilyId(familyDetails.getFamilyId());
        productDTO.setActive("Y");
        Product product = productService.create(productDTO);
        Assert.assertTrue(product.diff(productDTO).isEmpty());

        //create productVariantInstance
        Product newProduct = productService.get(product.getProductId(), FindBy.EXTERNAL_ID).orElse(null);

        Page<FamilyAttributeOption> familyAttributeOptions = familyService.getFamilyAttributeOptions(familyDetails.getFamilyId(),FindBy.EXTERNAL_ID, attributeDetails.getId(), 0, 2, null);

        List<FamilyAttribute> variantAxisAttributes = familyService.getVariantAxisAttributes(familyDetails.getFamilyId(),attributeDetails.getId().toUpperCase(), FindBy.EXTERNAL_ID, null);
        variantAxisAttributes.get(0).getOptions().forEach((k, v) -> {
            ProductVariant productVariant = new ProductVariant(newProduct);
            productVariant.setProductVariantId(familyAttributeOptions.getContent().get(0).getId());
            productVariant.setChannelId(channel.getChannelId());
            productVariant.setActive("Y");
            Map<String, String> axisAttributes = new HashMap<>();
            axisAttributes.put(variantAxisAttributes.get(0).getId(), k);
            productVariant.setAxisAttributes(axisAttributes);
            productVariant.setLevel(1);
            productVariant.setProductVariantName(newProduct.getProductName() + " - " + k);

            Page<VariantGroup> variantGroups = familyService.getVariantGroups(familyDetails.getFamilyId(), FindBy.EXTERNAL_ID, 0, 1, null);

            Map<Integer, List<String>> variantAttributesMap = variantGroups.getContent().get(0).getVariantAttributes();
            //String variantGroupId = newProduct.getProductFamily().getChannelVariantGroups().get(channel.getChannelId());
            // VariantGroup variantGroup = newProduct.getProductFamily().getVariantGroups().get();
            List<String> variantAttributeIds = variantGroups.getContent().get(0).getVariantAttributes().get(productVariant.getLevel());
            variantAttributeIds.forEach(attributeId -> {
                if(variantAttributesMap.containsKey(attributeId)) {
                    productVariant.getVariantAttributes().put(attributeId, variantAttributesMap.get(attributeId));
                }
            });
            ProductVariant productVariantDTO = productVariantDAO.insert(productVariant);
        });*/
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void addAssets() throws Exception {
    }

    @Test
    public void deleteAsset() throws Exception {
    }

    @Test
    public void setAsDefaultAsset() throws Exception {
    }

    @Test
    public void reorderAsset() throws Exception {
    }

    @Test
    public void allChannelVariants() throws Exception {
    }

    @Test
    public void setProductVariantsSequence() throws Exception {
    }

    @Test
    public void variantPricingDetails() throws Exception {
    }

    @Test
    public void savePricingDetails() throws Exception {
    }

    @Test
    public void variantDetails() throws Exception {
    }

    @Test
    public void customToggle() throws Exception {
    }

    @Test
    public void validate() throws Exception {
    }

    @Test
    public void getProductVariantPricing() throws Exception {
    }

    @Test
    public void downloadVariantAssetsImage() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

}