package com.bigname.pim.client.web.controller;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        familyDAO.getMongoTemplate().dropCollection(Family.class);
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

    @Test
    public void detailsTest() throws Exception {
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

    @Test
    public void allAttributes() throws Exception {
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
    public void allAttributeOptions() throws Exception {
    }

    @Test
    public void availableAttributeOptions() throws Exception {
    }

    @Test
    public void availableAttributeOptions1() throws Exception {
    }

    @Test
    public void attributeOptionDetails() throws Exception {
    }

    @Test
    public void availableAxisAttributes() throws Exception {
    }

    @Test
    public void variantGroupDetails() throws Exception {
    }

    @Test
    public void updateVariantAttribute() throws Exception {
    }

    @Test
    public void updateAxisAttribute() throws Exception {
    }

    @Test
    public void saveVariantGroup() throws Exception {
    }

    @Test
    public void createVariantGroup() throws Exception {
    }

    @Test
    public void toggleVariantGroup() throws Exception {
    }

    @Test
    public void attributeOptions() throws Exception {
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

    @Test
    public void saveAttributeOptions() throws Exception {
    }

    @Test
    public void getVariantGroups() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}