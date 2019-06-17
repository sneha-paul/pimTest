package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.*;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.User;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.service.UserService;
import com.m7.xtreme.xcore.util.FindBy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes={PimApplication.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductController productController;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    FamilyDAO familyDAO;

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDAO categoryDAO;

    @Test
    public void downloadDigitalAssetsImage() throws Exception {
    }

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
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(productController);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
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
        attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));

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

        attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));

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

            familyService.update(ConversionUtil.toList(family));

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("productName", ConversionUtil.toList("Test1"));
        params.put("productId", ConversionUtil.toList("TEST1"));
        params.put("productFamilyId", ConversionUtil.toList(familyDetails.getFamilyId()));
        params.put("channelId", ConversionUtil.toList(channel.getChannelId()));

        ResultActions result = mockMvc.perform(
                post("/pim/products")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);
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

        attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));

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

        attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));

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
            attributes.forEach(attributeData -> {
                        FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeData.getName(), null);
                        familyAttributeDTO.setActive("Y");
                        familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
                        familyAttributeDTO.setUiType(attributeData.getUiType());
                        familyAttributeDTO.setScopable("Y");
                        familyAttributeDTO.setAttributeId(attributeData.getFullId());
                        familyAttributeDTO.getScope().put(channel.getChannelId(), FamilyAttribute.Scope.OPTIONAL);
                        familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
                        familyAttributeDTO.setAttribute(attributeData);
                        family.addAttribute(familyAttributeDTO);

            FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attributeData.getId());

            if(attributeData.getName() == "Color") {
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
            }
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

            familyService.update(ConversionUtil.toList(family));

        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("productName", ConversionUtil.toList("Test1"));
        params.put("productId", ConversionUtil.toList("TEST1"));
        params.put("productFamilyId", ConversionUtil.toList(familyDetails.getFamilyId()));
        params.put("channelId", ConversionUtil.toList(channel.getChannelId()));
        ResultActions result = mockMvc.perform(
                post("/pim/products")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());

        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("productName", ConversionUtil.toList("New Test1"));
        params.put("TESTATTRIBUTE2", ConversionUtil.toList("Test2Value"));
        params.put("TESTATTRIBUTE4", ConversionUtil.toList("Test4Value"));
        params.put("TESTATTRIBUTE5", ConversionUtil.toList("Test5Value"));
        params.put("TESTATTRIBUTE6", ConversionUtil.toList("Test6Value"));
        params.put("TESTATTRIBUTE7", ConversionUtil.toList("Test7Value"));
        params.put("TESTATTRIBUTE8", ConversionUtil.toList("Test8Value"));
        params.put("TESTATTRIBUTE9", ConversionUtil.toList("Test9Value"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/products/TEST1/channels/ECOMMERCE")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(3));
        result1.andExpect(jsonPath("$.success").value(true));
        result1.andExpect(jsonPath("$.group.length()").value(1));
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

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void detailsTest() throws Exception {
        //Create mode
        List<Product> createdProductInstances = addProductInstance();
        Assert.assertFalse(createdProductInstances.isEmpty());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String channel = channelService.getAll(0, 100, null).stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName)).toString();
        params.put("channelId", ConversionUtil.toList(channel));

        //create mode
        mockMvc.perform(
                get("/pim/products/create").params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product"))
                .andExpect(forwardedUrl("/product/product.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("PRODUCTS")));

        //Deatils mode
        String productId = createdProductInstances.get(0).getProductId();
        mockMvc.perform(
                get("/pim/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product"))
                .andExpect(forwardedUrl("/product/product.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")));

        //Details mode with reload true
        params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/products/" + productId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product_body"))
                .andExpect(forwardedUrl("/product/product_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/products"))
                .andExpect(forwardedUrl("/product/products.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1Test() throws Exception {
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

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 2", "externalId", "PRODUCT_TEST_2", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 3", "externalId", "PRODUCT_TEST_3", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 4", "externalId", "PRODUCT_TEST_4", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 5", "externalId", "PRODUCT_TEST_5", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 6", "externalId", "PRODUCT_TEST_6", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 7", "externalId", "PRODUCT_TEST_7", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 8", "externalId", "PRODUCT_TEST_8", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product Test 9", "externalId", "PRODUCT_TEST_9", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        //Getting entries as page
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("5"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/products/data")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(5));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(9));
        result.andExpect(jsonPath("$.recordsTotal").value(9));

        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("5"));
        detailsParams1.put("length", ConversionUtil.toList("5"));
        detailsParams1.put("draw", ConversionUtil.toList("2"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/products/data")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(4));
        result1.andExpect(jsonPath("$.draw").value(2));
        result1.andExpect(jsonPath("$.recordsFiltered").value(9));
        result1.andExpect(jsonPath("$.recordsTotal").value(9));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getProductCategoriesTest() throws Exception {
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

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "CATEGORY_TEST_1", "description", "Test Category 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "CATEGORY_TEST_2", "description", "Test Category 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "CATEGORY_TEST_3", "description", "Test Category 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "CATEGORY_TEST_4", "description", "Test Category 4", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            productService.addCategory(product.getProductId(), FindBy.EXTERNAL_ID, categoryData.get("externalId").toString(), FindBy.EXTERNAL_ID);
        });
        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("4"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/products/PRODUCT_TEST_1/categories/data")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(4));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(4));
        result1.andExpect(jsonPath("$.recordsTotal").value(4));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableCategoriesTest() throws Exception {
        //Add a product instance
        List<Product> createdProductInstances = addProductInstance();
        Assert.assertFalse(createdProductInstances.isEmpty());

        //AvailableCategories with valid productID
        String productId = createdProductInstances.get(0).getProductId();
        mockMvc.perform(
                get("/pim/products/" + productId + "/categories/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/availableCategories"))
                .andExpect(forwardedUrl("/category/availableCategories.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableCategoriesTest() throws Exception {
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

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "CATEGORY_TEST_1", "description", "Test Category 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "CATEGORY_TEST_2", "description", "Test Category 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "CATEGORY_TEST_3", "description", "Test Category 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "CATEGORY_TEST_4", "description", "Test Category 4", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding productCategory
        productService.addCategory(product.getProductId(), FindBy.EXTERNAL_ID, categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);

        //Getting Categories
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("5"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/products/PRODUCT_TEST_1/categories/available/list")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(3));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(3));
        result.andExpect(jsonPath("$.recordsTotal").value(3));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addCategoryTest() throws Exception {
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

        //creating family
        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
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

        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "CATEGORY_TEST_1", "description", "Test Category 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "CATEGORY_TEST_2", "description", "Test Category 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "CATEGORY_TEST_3", "description", "Test Category 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "CATEGORY_TEST_4", "description", "Test Category 4", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding productCategories
        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Page<Category> productCategory = productService.getAvailableCategoriesForProduct(product.getProductId(), FindBy.EXTERNAL_ID, 1, 1, null, false);

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("id", ConversionUtil.toList("PRODUCT_TEST_1"));
        detailsParams.put("categoryId", ConversionUtil.toList("CATEGORY_TEST_1"));
        ResultActions result = mockMvc.perform(
                post("/pim/products/PRODUCT_TEST_1/categories/CATEGORY_TEST_1")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(1));
        result.andExpect(jsonPath("$.success").value(true));

        Page<Category> productCategory1 = productService.getAvailableCategoriesForProduct(product.getProductId(), FindBy.EXTERNAL_ID, 1, 1, null, false);
        Assert.assertEquals(productCategory1.getTotalElements(), productCategory.getTotalElements() - 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleProductCategoryTest() throws Exception {
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

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "CATEGORY_TEST_1", "description", "Test Category 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "CATEGORY_TEST_2", "description", "Test Category 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "CATEGORY_TEST_3", "description", "Test Category 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "CATEGORY_TEST_4", "description", "Test Category 4", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            productService.addCategory(product.getProductId(), FindBy.EXTERNAL_ID, categoryData.get("externalId").toString(), FindBy.EXTERNAL_ID);
        });

        //Toggle
        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("id", ConversionUtil.toList("PRODUCT_TEST_1"));
        detailParams.put("categoryId", ConversionUtil.toList("TEST_CATEGORY_1"));
        detailParams.put("active", ConversionUtil.toList("Y"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/products/PRODUCT_TEST_1/categories/CATEGORY_TEST_1/active/Y")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(1));
        result1.andExpect(jsonPath("$.success").value(true));
        List<ProductCategory> productCategoryList = productService.getProductCategories(product.getProductId(), FindBy.EXTERNAL_ID, 0, 4, null, false).getContent();
        Assert.assertEquals(productCategoryList.get(3).getActive(), "N");

    }

    @After
    public void tearDown() throws Exception {
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    private List<Product> addProductInstance() {
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

        List<Product> createdProductInstances = new ArrayList<>();
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData ->{
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            createdProductInstances.add(productService.create(productDTO));
        });
        return createdProductInstances;
    }
}