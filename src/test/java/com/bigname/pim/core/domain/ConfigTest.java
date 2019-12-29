package com.bigname.pim.core.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.persistence.dao.mongo.ConfigDAO;
import com.bigname.pim.core.service.ConfigService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ConfigTest {
    @Autowired
    private ConfigService configService;
    @Autowired
    private ConfigDAO configDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) configDAO.getTemplate();
        }

        User user1 = userDAO.findByEmail("MANU@BLACWOOD.COM");
        if(ValidationUtil.isEmpty(user1)){
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COM");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Blacwood");
            userDAO.save(user);
        }
        User user2 = userDAO.findByEmail("MANU@E-XPOSURE.COM");
        if(ValidationUtil.isEmpty(user2)) {
            User user = new User();
            user.setUserName("MANU@E-XPOSURE.COM");
            user.setPassword("temppass1");
            user.setEmail("manu@e-xposure.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Exposure");
            userDAO.save(user);
        }
        mongoTemplate.dropCollection(Config.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest(){
        //Create Instance
        Config configDTO = new Config();
        configDTO.setConfigId("test");
        configDTO.setConfigName("test");

        configDTO.orchestrate();

        //Equals Checking With Id
        Assert.assertEquals(configDTO.getConfigId(), "TEST");
        Assert.assertEquals(configDTO.getConfigName(), "test");
        Assert.assertEquals(configDTO.getActive(), "N");

        configService.create(configDTO);
        Config newConfig = configService.get(ID.EXTERNAL_ID(configDTO.getConfigId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newConfig));
        Assert.assertEquals(newConfig.getConfigName(), configDTO.getConfigName());
        Assert.assertEquals(newConfig.getActive(), configDTO.getActive());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create Original Instance
        Config original = new Config();
        original.setExternalId("test");
        original.setConfigId("test");
        original.setConfigName("Test");

        //ADD DETAILS
        Config modified = new Config();
        modified.setGroup("DETAILS");
        modified.setExternalId("TEST-A");
        modified.setConfigId("test-A");
        modified.setConfigName("Test-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getConfigId(), "TEST-A");
        Assert.assertEquals(original.getConfigName(), "Test-A");

        //Without DETAILS
        Config modified1 = new Config();
        modified1.setExternalId("test");
        modified1.setConfigId("test");
        modified1.setConfigName("test");

        original = original.merge(modified1);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getConfigId(), "TEST-A");
        Assert.assertEquals(original.getConfigName(), "Test-A");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        Config configDTO = new Config();
        configDTO.setExternalId("test");
        configDTO.setConfigName("Test");
        configDTO.setActive("Y");
        configDTO.setDiscontinued("Y");

        //Equals Checking For Map
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("configName", "Test");
        map.put("active", "Y");
        map.put("discontinued", "Y");

        Map<String, String> map1 = configDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("configName"), map.get("configName"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
        Assert.assertEquals(map1.get("discontinued"), map.get("discontinued"));
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Config.class);
    }


}