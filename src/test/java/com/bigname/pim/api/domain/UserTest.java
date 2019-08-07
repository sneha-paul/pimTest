package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.mongo.UserDAO;
import com.m7.xtreme.xplatform.service.UserService;
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
 * Created by sanoop on 20/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class UserTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) userDAO.getTemplate();
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
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        User userDTO = new User();
        userDTO.setEmail("test");
        userDTO.setUserName("test1");
        userDTO.setPassword("test2");
        userDTO.setStatus("test3");
        userDTO.setExternalId("test");

        userDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(userDTO.getExternalId(), "TEST");
        Assert.assertEquals(userDTO.getEmail(), "TEST");
        Assert.assertEquals(userDTO.getUserName(), "test1");
        Assert.assertEquals(userDTO.getPassword(), "test2");
        Assert.assertEquals(userDTO.getStatus(), "test3");

        //Create
        userService.create(userDTO);
        User newUser = userService.get(ID.EXTERNAL_ID(userDTO.getExternalId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newUser));
        Assert.assertEquals(newUser.getExternalId(), userDTO.getExternalId());
        Assert.assertEquals(newUser.getEmail(), userDTO.getEmail());
        Assert.assertEquals(newUser.getUserName(), userDTO.getUserName());
        Assert.assertEquals(newUser.getPassword(), userDTO.getPassword());
        Assert.assertEquals(newUser.getStatus(), userDTO.getStatus());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create new Instance
        User userDTO = new User();
        userDTO.setExternalId("test");
        userDTO.setUserName("Test");
        userDTO.setPassword("Test1");
        userDTO.setStatus("pending");
        userDTO.setActive("Y");

        //Checking for Map
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("userName", "Test");
        map.put("password", "Test1");
        map.put("status", "pending");
        map.put("active", "Y");

        Map<String, String> map1 = userDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("userName"), map.get("userName"));
        Assert.assertEquals(map1.get("password"), map.get("password"));
        Assert.assertEquals(map1.get("status"), map.get("status"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void diff() throws Exception {
        //Create first instance
        User user1 = new User();
        user1.setExternalId("test");
        user1.setUserName("Test");
        user1.setEmail("test.com");
        user1.setPassword("test1");
        user1.setStatus("pending");

        //Create Second Instance
        User user2 = new User();
        user2.setExternalId("test");
        user2.setUserName("Test");
        user2.setEmail("test.com");
        user2.setPassword("test2");
        user2.setStatus("pending");

        //Checking First instance and Second instance
        Map<String, Object> diff = user1.diff(user2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("password"), "test2");

        //Checking First instance and Second instance for Ignore Internal Id
        Map<String, Object> diff1 = user1.diff(user2,true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("password"), "test2");
    }

    @After
    public void tearDown() throws Exception {
        //mongoTemplate.dropCollection(User.class);
    }


}