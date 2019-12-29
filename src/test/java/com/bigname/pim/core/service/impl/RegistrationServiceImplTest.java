package com.bigname.pim.core.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.service.RegistrationService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RegistrationServiceImplTest {
    @Autowired
    private UserDAO userDAO;

    @Qualifier("customUserService")
    @Autowired
    private UserService userService;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationService registrationService;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) userDAO.getTemplate();
        }
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void sendVerificationEmailTest() throws Exception {
        /*List<Map<String, Object>> usersData = new ArrayList<>();
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
            userDAO.insert(userDTO);

            User newUser = userService.get(userDTO.getEmail(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newUser != null);
            Assert.assertTrue(newUser.diff(userDTO).isEmpty());
        });

        User user = userService.get(usersData.get(0).get("email").toString().toUpperCase(), EXTERNAL_ID, false).orElse(null);
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String message = "Hi "+  user.getUserName() + ",  Please login to the following link to confirm your registration : ";

        registrationService.sendVerificationEmail(subject, recipientAddress, message); //TODO : returnType void, so didn;t write anyAssert
*/
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(User.class);
    }


}