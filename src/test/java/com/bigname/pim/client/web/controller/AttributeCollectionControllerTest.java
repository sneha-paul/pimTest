package com.bigname.pim.client.web.controller;

import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }

    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(attributeCollectionController);
        Assert.assertNotNull(attributeCollectionDAO);
    }

    @Test
    public void details() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void create() throws Exception {
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
    public void update() throws Exception {
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

    @Test
    public void all() throws Exception {
    }

    @Test
    public void all1() throws Exception {
    }

    @Test
    public void attributeDetails() throws Exception {
    }

    @Test
    public void createAttribute() throws Exception {
    }

    @Test
    public void updateAttribute() throws Exception {
    }

    @Test
    public void getAttributes() throws Exception {
    }

    @Test
    public void getAttributeOptions() throws Exception {
    }

    @Test
    public void attributeOptionDetails() throws Exception {
    }

    @Test
    public void createAttributeOption() throws Exception {
    }

    @Test
    public void updateAttributeOption() throws Exception {
    }

}