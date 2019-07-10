package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.mongo.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.mongo.ChannelDAO;
import com.bigname.pim.api.persistence.dao.mongo.FamilyDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.FamilyService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasProperty;
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
public class FamilyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyController familyController;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private FamilyService familyService;

    @Autowired
    ChannelService channelService;

    @Autowired
    AttributeCollectionService attributeCollectionService;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    private ChannelDAO channelDAO;

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
            mongoTemplate = (MongoTemplate) channelDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Channel.class);
        mongoTemplate.dropCollection(AttributeCollection.class);
        mongoTemplate.dropCollection(Family.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(familyController);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/families"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/families"))
                .andExpect(forwardedUrl("/settings/families.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1Test() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test 1", "externalId", "TEST_1", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 2", "externalId", "TEST_2", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 3", "externalId", "TEST_3", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 4", "externalId", "TEST_4", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 5", "externalId", "TEST_5", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 6", "externalId", "TEST_6", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 7", "externalId", "TEST_7", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 8", "externalId", "TEST_8", "active", "Y"));
        familiesData.add(CollectionsUtil.toMap("name", "Test 9", "externalId", "TEST_9", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("5"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/families/data")
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
                get("/pim/families/data")
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
    public void detailsTest() throws Exception {
        //Create mode
        mockMvc.perform(
                get("/pim/families/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/family"))
                .andExpect(forwardedUrl("/settings/family.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("FAMILIES")));

        //Details mode, with non=existing familyID - TODO

        //Add a family instance
        List<Family> createdFamilyInstances = addFamilyInstances();
        Assert.assertFalse(createdFamilyInstances.isEmpty());

        //Details mode with valid familyID
        String familyId = createdFamilyInstances.get(0).getFamilyId();

        String channel = ConversionUtil.toJSONString(channelService.getAll(0, 100, null).getContent().stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName)));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("channels", ConversionUtil.toList(channel));

        mockMvc.perform(
                get("/pim/families/" + familyId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/family"))
                .andExpect(forwardedUrl("/settings/family.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("FAMILIES")))
                .andExpect(model().attribute("channels", is(channel)))
                .andExpect(model().attribute("family", hasProperty("externalId", is(familyId))));

        //Details mode with reload true
        params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/families/" + familyId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/family_body"))
                .andExpect(forwardedUrl("/settings/family_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("FAMILIES")))
                .andExpect(model().attribute("channels", is(channel)))
                .andExpect(model().attribute("family", hasProperty("externalId", is(familyId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("familyName", ConversionUtil.toList("TestFamily"));
        params.put("familyId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/families")
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("familyName", ConversionUtil.toList("TestFamily"));
        params.put("familyId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/families")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result.andExpect(status().isOk());

        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("familyName", ConversionUtil.toList("TestFamilyNew"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/families/TEST")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(3));
        result1.andExpect(jsonPath("$.success").value(true));
        result1.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allAttributesTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            });
            familyService.update(ConversionUtil.toList(family));
        });

        //getting attribute
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("10"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/families/TEST_1/attributes/data")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(10));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(10));
        result.andExpect(jsonPath("$.recordsTotal").value(10));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void attributeDetailsTest() throws Exception {
        List<Family> createdFamilyAttributeInstances = addFamilyAttributeInstances();
        Assert.assertFalse(createdFamilyAttributeInstances.isEmpty());

        String familyId = createdFamilyAttributeInstances.get(0).getFamilyId();
        //Create mode
        mockMvc.perform(
                get("/pim/families/" + familyId + "/attributes/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/familyAttribute"))
                .andExpect(forwardedUrl("/settings/familyAttribute.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")));

        //Details mode
        List<FamilyAttribute> familyAttributeList = familyService.getFamilyAttributes(ID.EXTERNAL_ID(familyId), 0, 10, null).getContent();
        String attributeId = familyAttributeList.get(0).getId();

        mockMvc.perform(
                get("/pim/families/" + familyId + "/attributes/" + attributeId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/familyAttribute"))
                .andExpect(forwardedUrl("/settings/familyAttribute.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("attribute", hasProperty("id", is(attributeId))))
                .andExpect(model().attribute("uiTypes", is(Attribute.UIType.getAll())));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createAttributeTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        //creating attribute
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", ConversionUtil.toList("Color"));
        params.put("collectionId", ConversionUtil.toList("TEST_ATTRIBUTE_COLLECTION"));
        params.put("attributeId", ConversionUtil.toList(attributeDetails.getFullId()));
        params.put("attributeGroup.fullId", ConversionUtil.toList(FamilyAttributeGroup.DETAILS_LEAF_GROUP_FULL_ID));
        params.put("attributeGroup.parentGroup.masterGroup", ConversionUtil.toList("N"));

        ResultActions result = mockMvc.perform(
                post("/pim/families/TEST_1/attributes")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateAttributeTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

            });
            familyService.update(ConversionUtil.toList(family));
        });


        List<FamilyAttribute> familyAttributes = familyService.getFamilyAttributes(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), 0, 3, null).getContent();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", ConversionUtil.toList("New Color"));
        params.put("fullId", ConversionUtil.toList(familyAttributes.get(0).getAttributeGroup().getFullId() +"|"+ familyAttributes.get(0).getId()));
        params.put("group", ConversionUtil.toList("ATTRIBUTES"));
        ResultActions result = mockMvc.perform(
                put("/pim/families/TEST_1/attributes/COLOR")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(0));


    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allAttributeOptionsTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

                //TODO add option
                if(attributeData.getName().equals("Color")) {
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
            familyService.update(ConversionUtil.toList(family));
        });

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("3"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/families/TEST_1/attributes/DEFAULT_GROUP|DEFAULT_GROUP|DEFAULT_GROUP|COLOR/options/data")
                        .params(detailsParams)
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
    public void availableAttributeOptionsTest() throws Exception {
        //Add a product instance
        List<Family> createdFamilyAttributeInstances = new ArrayList<>();
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            });
            familyService.update(ConversionUtil.toList(family));
        });

        List<FamilyAttribute> familyAttributes = familyService.getFamilyAttributes(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), 0, 3, null).getContent();

        //AvailableCategories with valid productID
        String attributeId = familyAttributes.get(0).getId();
        String familyId = familiesData.get(0).get("externalId").toString();
        mockMvc.perform(
                get("/pim/families/" + familyId + "/attributes/" + attributeId + "/options/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/availableFamilyAttributeOptions"))
                .andExpect(forwardedUrl("/settings/availableFamilyAttributeOptions.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableAttributeOptions1Test() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

            });
            familyService.update(ConversionUtil.toList(family));
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("3"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/families/TEST_1/attributes/COLOR/options/available/data")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(3));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(3));
        result1.andExpect(jsonPath("$.recordsTotal").value(3));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void attributeOptionDetailsTest() throws Exception {
        List<Family> createdFamilyAttributeInstances = addFamilyAttributeInstances();
        Assert.assertFalse(createdFamilyAttributeInstances.isEmpty());

        String familyId = createdFamilyAttributeInstances.get(0).getFamilyId();
        List<FamilyAttribute> familyAttributeList = familyService.getFamilyAttributes(ID.EXTERNAL_ID(familyId), 0, 10, null).getContent();
        String attributeId = familyAttributeList.get(0).getId();
        List<FamilyAttributeOption> familyAttributeOptionsList = familyService.getFamilyAttributeOptions(ID.EXTERNAL_ID(familyId), attributeId, 0, 10, null).getContent();
        String attributeOptionId = familyAttributeOptionsList.get(0).getId();

        mockMvc.perform(
                get("/pim/families/" + familyId + "/attributes/" + attributeId + "/options/" + attributeOptionId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/familyAttributeOption"))
                .andExpect(forwardedUrl("/settings/familyAttributeOption.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("attribute", hasProperty("id", is(attributeId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableAxisAttributesTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

                //TODO add option
                if(attributeData.getName().equals("Color")) {
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

            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            /*family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put("ECOMMERCE", variantGroup.getId());*/
            familyService.update(ConversionUtil.toList(family));
        });

        mockMvc.perform(
                get("/pim/families/TEST_1/variantGroups/TEST_VARIANT_1/axisAttributes/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/availableVariantAxisAttributes"))
                .andExpect(forwardedUrl("/settings/availableVariantAxisAttributes.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void variantGroupDetailsTest() throws Exception {
        List<Family> createdFamilyAttributeInstances = addFamilyAttributeInstances();
        Assert.assertFalse(createdFamilyAttributeInstances.isEmpty());
        String familyId = createdFamilyAttributeInstances.get(0).getFamilyId();

        //Create mode
        mockMvc.perform(
                get("/pim/families/" + familyId + "/variantGroups/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/variantGroup"))
                .andExpect(forwardedUrl("/settings/variantGroup.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("FAMILIES")));


        List<VariantGroup> variantGroups = familyService.getVariantGroups(ID.EXTERNAL_ID(familyId), 0, 1, null).getContent();
        String variantGroupId = variantGroups.get(0).getId();
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        Map<String, List<FamilyAttribute>> variantGroupAttribute = family.get().getVariantGroupAttributes(variantGroupId);
        Map<String, List<FamilyAttribute>> variantGroupAxisAttribute = family.get().getVariantGroupAxisAttributes(variantGroupId);

        //Details mode with valid familyID
        mockMvc.perform(
                get("/pim/families/" + familyId + "/variantGroups/" + variantGroupId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/variantGroup"))
                .andExpect(forwardedUrl("/settings/variantGroup.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("FAMILIES")))
                .andExpect(model().attribute("familyId", is(familyId)))
                .andExpect(model().attribute("variantGroup", hasProperty("id", is(variantGroupId))))
                .andExpect(model().attribute("variantGroupAttributes", is(variantGroupAttribute)))
                .andExpect(model().attribute("variantGroupAxisAttributes", is(variantGroupAxisAttribute)));

        //Details mode with reload true
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/families/" + familyId + "/variantGroups/" + variantGroupId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/variantGroup_body"))
                .andExpect(forwardedUrl("/settings/variantGroup_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("FAMILIES")))
                .andExpect(model().attribute("familyId", is(familyId)))
                .andExpect(model().attribute("variantGroup", hasProperty("id", is(variantGroupId))))
                .andExpect(model().attribute("variantGroupAttributes", is(variantGroupAttribute)))
                .andExpect(model().attribute("variantGroupAxisAttributes", is(variantGroupAxisAttribute)));
    }

    @Test
    public void updateVariantAttribute() throws Exception {
    }

    @Test
    public void updateAxisAttribute() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void saveVariantGroupTest() throws Exception {

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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

                //TODO add option
                if(attributeData.getName().equals("Color")) {
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

            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put("ECOMMERCE", variantGroup.getId());
            familyService.update(ConversionUtil.toList(family));
        });

        //Update
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("name", ConversionUtil.toList("New Test Variant"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/families/TEST_1/variantGroups/TEST_VARIANT_1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(4));
        result1.andExpect(jsonPath("$.success").value(true));
        result1.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createVariantGroupTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

                //TODO add option
                if(attributeData.getName().equals("Color")) {
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
            familyService.update(ConversionUtil.toList(family));
        });

        Family family = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        //Creating variant group
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", ConversionUtil.toList("Test Variant 1"));
        params.put("familyId", ConversionUtil.toList(family.getFamilyId()));
        params.put("level", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                post("/pim/families/TEST_1/variantGroups")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(0));
    }

    @Test
    public void toggleVariantGroup() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void attributeOptionsTest() throws Exception {
        List<Family> createdFamilyAttributeInstances = addFamilyAttributeInstances();
        Assert.assertFalse(createdFamilyAttributeInstances.isEmpty());

        //attributeOptions with valid familyID and attributeID
        String familyId = createdFamilyAttributeInstances.get(0).getFamilyId();
        List<FamilyAttribute> family = familyService.getFamilyAttributes(ID.EXTERNAL_ID(familyId), 0, 1, null).getContent();
        String attributeId = family.get(0).getId();
        mockMvc.perform(
                get("/pim/families/" + familyId + "/attributes/" + attributeId + "/options"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/familyAttributeOptions"))
                .andExpect(forwardedUrl("/settings/familyAttributeOptions.jsp"));
    }

    @Test
    public void saveAttributeScope() throws Exception {
    }

    @Test
    public void saveVariantGroupScope() throws Exception {
    }

    @Test
    public void saveAttributeAsScopable() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void saveAttributeOptionsTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test Attribute Collection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTE_COLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
        familiesData.forEach(familyData -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

            });
            familyService.update(ConversionUtil.toList(family));
        });

        ResultActions result = mockMvc.perform(
                post("/pim/families/TEST_1/attributes/COLOR/options/BLUE")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(0));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getVariantGroupsTest() throws Exception {
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String) familyData.get("name"));
            familyDTO.setFamilyId((String) familyData.get("externalId"));
            familyDTO.setActive((String) familyData.get("active"));
            familyDTO.setDiscontinued((String) familyData.get("discontinue"));
            familyService.create(familyDTO);

            Family family = familyService.get(ID.EXTERNAL_ID(familyDTO.getFamilyId()), false).orElse(null);
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

                //TODO add option
                if (attributeData.getName().equals("Color")) {
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
            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put("ECOMMERCE", variantGroup.getId());
            familyService.update(ConversionUtil.toList(family));
        });

        //Getting variant group
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("1"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/families/TEST_1/variantGroups/list")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(1));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(1));
        result.andExpect(jsonPath("$.recordsTotal").value(1));


        detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("1"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/families/TEST_1/variantGroups")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(1));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(1));
        result1.andExpect(jsonPath("$.recordsTotal").value(1));
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Channel.class);
        mongoTemplate.dropCollection(AttributeCollection.class);
    }

    private List<Family> addFamilyInstances() {
        List<Family> createdFamilyInstances = new ArrayList<>();
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            createdFamilyInstances.add(familyService.create(familyDTO));
        });
        return createdFamilyInstances;
    }

    private List<Family> addFamilyAttributeInstances() {
        List<Family> createdFamilyAttributeInstances = new ArrayList<>();
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

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionService.create(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
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

        AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDetails.getCollectionId()), false).orElse(null);

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

                //TODO add option
                if(attributeData.getName().equals("Color")) {
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

            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName("Test Variant1");
            variantGroup.setId("TEST_VARIANT_1");
            variantGroup.setLevel(1);
            variantGroup.setActive("N");
            family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put("ECOMMERCE", variantGroup.getId());
            familyService.update(ConversionUtil.toList(family));
            Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);
            createdFamilyAttributeInstances.add(family1);
        });
        return createdFamilyAttributeInstances;
    }

}