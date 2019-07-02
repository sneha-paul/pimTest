package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
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
public class AttributeCollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AttributeCollectionController attributeCollectionController;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    AttributeCollectionService attributeCollectionService;

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
            mongoTemplate = ((GenericRepositoryImpl)attributeCollectionDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(AttributeCollection.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(attributeCollectionController);
        Assert.assertNotNull(attributeCollectionDAO);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void detailsTest() throws Exception {
        //Create mode
        mockMvc.perform(
                get("/pim/attributeCollections/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeCollection"))
                .andExpect(forwardedUrl("/settings/attributeCollection.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("ATTRIBUTE_COLLECTIONS")));

        //Details mode, with non=existing collectionID - TODO

        //Add a attribute collection instance
        List<AttributeCollection> createdAttributeCollectionInstances = addAttributeCollectionInstances();
        Assert.assertFalse(createdAttributeCollectionInstances.isEmpty());

        //Details mode with valid collectionID
        String collectionId = createdAttributeCollectionInstances.get(0).getCollectionId();
        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeCollection"))
                .andExpect(forwardedUrl("/settings/attributeCollection.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("ATTRIBUTE_COLLECTIONS")))
                .andExpect(model().attribute("attributeCollection", hasProperty("externalId", is(collectionId))));

        //Details mode with reload true
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeCollection_body"))
                .andExpect(forwardedUrl("/settings/attributeCollection_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("ATTRIBUTE_COLLECTIONS")))
                .andExpect(model().attribute("attributeCollection", hasProperty("externalId", is(collectionId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("collectionName", ConversionUtil.toList("TestAttributesCollection"));
        params.put("collectionId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/attributeCollections")
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
        params.put("collectionName", ConversionUtil.toList("TestAttributesCollection"));
        params.put("collectionId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/attributeCollections")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result.andExpect(status().isOk());

        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("collectionName", ConversionUtil.toList("TestAttributesCollection1"));
        params.put("collectionId", ConversionUtil.toList("TEST"));
        result = mockMvc.perform(
                put("/pim/attributeCollections/TEST")
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
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/attributeCollections"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeCollections"))
                .andExpect(forwardedUrl("/settings/attributeCollections.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1Test() throws Exception {
        //creating attribute collections
        List<Map<String, Object>> attributeCollectionsData = new ArrayList<>();
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 2", "externalId", "TEST_COLLECTION_2", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 3", "externalId", "TEST_COLLECTION_3", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 4", "externalId", "TEST_COLLECTION_4", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 5", "externalId", "TEST_COLLECTION_5", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 6", "externalId", "TEST_COLLECTION_6", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 7", "externalId", "TEST_COLLECTION_7", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 8", "externalId", "TEST_COLLECTION_8", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.add(CollectionsUtil.toMap("name", "Test Collection 9", "externalId", "TEST_COLLECTION_9", "active", "Y", "discontinued", "N"));
        attributeCollectionsData.forEach(attributeCollectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)attributeCollectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)attributeCollectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)attributeCollectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)attributeCollectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);
        });

        //Getting entries as page
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("5"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/attributeCollections/data")
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
                get("/pim/attributeCollections/data")
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
    public void attributeDetailsTest() throws Exception {
        List<AttributeCollection> createdAttributeCollectionInstances = addAttributeCollectionInstances();
        Assert.assertFalse(createdAttributeCollectionInstances.isEmpty());

        String collectionId = createdAttributeCollectionInstances.get(0).getCollectionId();

        //Create mode
        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId + "/attributes/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attribute"))
                .andExpect(forwardedUrl("/settings/attribute.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")));

        //Details mode
        List<Attribute> attributeList = attributeCollectionService.getAttributes(ID.EXTERNAL_ID(collectionId), 0, 10, null).getContent();
        String attributeId = attributeList.get(0).getId();

        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId + "/attributes/" + attributeId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attribute"))
                .andExpect(forwardedUrl("/settings/attribute.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("attribute", hasProperty("id", is(attributeId))))
                .andExpect(model().attribute("uiTypes", is(Attribute.UIType.getAll())));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createAttributeTest() throws Exception {
        //creating attribute collection
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionDAO.insert(attributeCollectionDTO);
        });

        //creating attribute
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", ConversionUtil.toList("style"));
        params.put("active", ConversionUtil.toList("Y"));
        params.put("id", ConversionUtil.toList("STYLE"));
        ResultActions result = mockMvc.perform(
                post("/pim/attributeCollections/TEST_COLLECTION_1/attributes")
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
        //creating attribute collection
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            //creating attribute
            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("id", "STYLE", "name", "style", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("id", "COLOR", "name", "color", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("id", "SIZE", "name", "size", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
        });

        //updating attribute
        List<Attribute> attributes = attributeCollectionService.getAttributes(ID.EXTERNAL_ID(collectionsData.get(0).get("externalId").toString()), 0, 3, null).getContent();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", ConversionUtil.toList("New Style"));
        params.put("fullId", ConversionUtil.toList(attributes.get(2).getFullId()));
        params.put("group", ConversionUtil.toList("ATTRIBUTES"));
        ResultActions result = mockMvc.perform(
                put("/pim/attributeCollections/TEST_COLLECTION_1/attributes/STYLE")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(0));// TODO check

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAttributesTest() throws Exception {
        //creating attribute collection
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            //Adding attribute
            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("id", "STYLE", "name", "style", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("id", "COLOR", "name", "color", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.add(CollectionsUtil.toMap("id", "SIZE", "name", "size", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
        });

        //getting attribute
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("3"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/attributeCollections/TEST_COLLECTION_1/attributes/data")
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
    public void getAttributeOptionsTest() throws Exception {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", Attribute.UIType.DROPDOWN));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
        });

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("3"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/attributeCollections/TEST_COLLECTION_1/attributes/DEFAULT_GROUP|STYLE/options/data")
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
    public void attributeOptionDetailsTest() throws Exception {
        List<AttributeCollection> createdAttributeInstances = addAttributeCollectionInstances();
        Assert.assertFalse(createdAttributeInstances.isEmpty());

        String collectionId = createdAttributeInstances.get(0).getCollectionId();
        List<Attribute> attributeList = attributeCollectionService.getAttributes(ID.EXTERNAL_ID(collectionId), 0, 1, null).getContent();
        String attributeId = attributeList.get(0).getId();
        String attributeId1 = attributeList.get(0).getFullId();

        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId + "/attributes/" + attributeId + "//options/create/"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeOption"))
                .andExpect(forwardedUrl("/settings/attributeOption.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")));

        List<AttributeOption> attributeOptionsList = attributeCollectionService.getAttributeOptions(ID.EXTERNAL_ID(collectionId), attributeId1, 0, 1, null).getContent();
        String attributeOptionId = attributeOptionsList.get(0).getId();

        //Details mode
        mockMvc.perform(
                get("/pim/attributeCollections/" + collectionId + "/attributes/" + attributeId + "/options/" + attributeOptionId))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/attributeOption"))
                .andExpect(forwardedUrl("/settings/attributeOption.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("attribute", hasProperty("id", is(attributeId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createAttributeOptionTest() throws Exception {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String) collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String) collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String) collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String) collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("id", "STYLE", "name", "style", "uiType", Attribute.UIType.DROPDOWN, "active", "Y"));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String) attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String) attributeData.get("name"));
                attributeCollectionDetails.addAttribute(attribute);
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
        });

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("value", ConversionUtil.toList("FOLDERS"));
        params.put("active", ConversionUtil.toList("Y"));
        ResultActions result = mockMvc.perform(
                post("/pim/attributeCollections/TEST_COLLECTION_1/attributes/STYLE/attributeOptions")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateAttributeOptionTest() throws Exception {
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", Attribute.UIType.DROPDOWN));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
        });
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AttributeCollection.class);
    }

    private List<AttributeCollection> addAttributeCollectionInstances() {
        List<AttributeCollection> createdAttributeCollectionInstances = new ArrayList<>();
        List<Map<String, Object>> collectionsData = new ArrayList<>();
        collectionsData.add(CollectionsUtil.toMap("name", "Test Collection 1", "externalId", "TEST_COLLECTION_1", "active", "Y", "discontinued", "N"));
        collectionsData.forEach(collectionData -> {
            AttributeCollection attributeCollectionDTO = new AttributeCollection();
            attributeCollectionDTO.setCollectionName((String)collectionData.get("name"));
            attributeCollectionDTO.setCollectionId((String)collectionData.get("externalId"));
            attributeCollectionDTO.setActive((String)collectionData.get("active"));
            attributeCollectionDTO.setDiscontinued((String)collectionData.get("discontinued"));
            attributeCollectionService.create(attributeCollectionDTO);

            AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

            List<Map<String, Object>> attributesData = new ArrayList<>();
            attributesData.add(CollectionsUtil.toMap("name", "style", "active", "Y", "id", "STYLE", "uiType", Attribute.UIType.DROPDOWN));
            attributesData.forEach(attributeData -> {
                Attribute attribute = new Attribute();
                attribute.setActive((String)attributeData.get("active"));
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setName((String)attributeData.get("name"));
                attribute.setId((String)attributeData.get("id"));
                attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
                attributeCollectionDetails.addAttribute(attribute);

                Attribute attribute1 = attributeCollectionDetails.getAttribute(attribute.getFullId()).orElse(null);

                List<Map<String, Object>> attributesOptionsData = new ArrayList<>();
                attributesOptionsData.add(CollectionsUtil.toMap("value", "FOLDERS", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "OPEN_END", "active", "Y"));
                attributesOptionsData.add(CollectionsUtil.toMap("value", "PAPER", "active", "Y"));
                attributesOptionsData.forEach(attributeOptionData ->{
                    AttributeOption attributeOption = new AttributeOption();
                    attributeOption.setValue((String)attributeOptionData.get("value"));
                    attributeOption.setCollectionId(attributeCollectionDetails.getCollectionId());
                    attributeOption.setActive((String)attributeOptionData.get("active"));
                    attributeOption.setAttributeId(attribute.getFullId());
                    attributeOption.orchestrate();
                    attribute1.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
                });
            });
            attributeCollectionService.update(ConversionUtil.toList(attributeCollectionDetails));
            AttributeCollection attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(collectionsData.get(0).get("externalId").toString()), false).orElse(null);
            createdAttributeCollectionInstances.add(attributeCollection);
        });
        return createdAttributeCollectionInstances;
    }

}