package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class UserServiceImplTest {

    @Autowired
    private UserDAO userDAO;

    @Qualifier("customUserService")
    @Autowired
    private UserService userService;

    @Qualifier("customUserService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public PasswordEncoder passwordEncoder;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(!userService.get(ID.EXTERNAL_ID("MANU@BLACWOOD.COM")).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COM");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userDAO.save(user);
        }
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) userDAO.getTemplate();
        }
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void loadUserByUsernameTest() throws Exception {
        List<Map<String, Object>> usersData = new ArrayList<>();
        usersData.add(CollectionsUtil.toMap("name", "TestUser", "password", "test123", "externalId", "testUser@gmail.com", "email", "testUser@gmail.com", "active", "Y"));

        usersData.forEach(userData -> {

            User userDTO = new User();
            userDTO.setUserName((String) userData.get("name"));
            userDTO.setPassword(passwordEncoder.encode((String) userData.get("password")));
            userDTO.setEmail((String) userData.get("email"));
            userDTO.setActive((String) userData.get("active"));
            userDTO.setStatus("Active");
            userDTO.setAvatar(userDTO.getAvatar());
            userDTO.setGroup("CREATE");
            userService.create(userDTO);

            User newUser = userService.get(ID.EXTERNAL_ID(userDTO.getEmail()), false).orElse(null);
            Assert.assertTrue(newUser != null);
            Assert.assertTrue(newUser.diff(userDTO).isEmpty());

            UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getEmail());
            Assert.assertTrue(userDetails.getUsername() .equals(newUser.getUserName()) && userDetails.getPassword().equals(newUser.getPassword()));
        });

    }

    @Test
    public void validate() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        //mongoTemplate.dropCollection(User.class);
    }

}