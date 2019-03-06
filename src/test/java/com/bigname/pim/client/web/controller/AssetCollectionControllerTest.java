package com.bigname.pim.client.web.controller;

import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
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
        assetCollectionDAO.getMongoTemplate().dropCollection(AssetCollection.class);
    }

    @After
    public void tearDown() throws Exception {
        assetCollectionDAO.getMongoTemplate().dropCollection(AssetCollection.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(assetCollectionController);
        Assert.assertNotNull(assetCollectionDAO);
    }

    @Test
    public void all() throws Exception {
    }

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

    @Test
    public void update() throws Exception {
    }

    @Test
    public void details() throws Exception {
    }

    @Test
    public void assetDetails() throws Exception {
    }

    @Test
    public void assetBrowser() throws Exception {
    }

    @Test
    public void getAssetsData() throws Exception {
    }

    @Test
    public void createAsset() throws Exception {
    }

    @Test
    public void uploadFile() throws Exception {
    }

    @Test
    public void getAllAsHierarchy() throws Exception {
    }

    @Test
    public void downloadImage() throws Exception {
    }

}