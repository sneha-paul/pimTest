package com.bigname.pim.client.web.controller;


import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.persistence.dao.mongo.AssetCollectionDAO;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class AssetCollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AssetCollectionController assetCollectionController;

    @Autowired
    private AssetCollectionDAO assetCollectionDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(!userService.get(ID.EXTERNAL_ID("MANU@BLACWOOD.COM")).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COM");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) assetCollectionDAO.getTemplate();
        }
        mongoTemplate.dropCollection(AssetCollection.class);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AssetCollection.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(assetCollectionController);
        Assert.assertNotNull(assetCollectionDAO);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void create() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("collectionName", ConversionUtil.toList("TestCollection"));
        params.put("collectionId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/assetCollections")
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
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void details() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void assetDetails() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void assetBrowser() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAssetsData() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createAsset() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void uploadFile() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsHierarchy() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void downloadImage() throws Exception {
    }

}