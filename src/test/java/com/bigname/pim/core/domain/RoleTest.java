package com.bigname.pim.core.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.persistence.dao.mongo.RoleDAO;
import com.bigname.pim.core.service.RoleService;
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


/**
 * Created by sanoop on 20/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RoleTest {

    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) roleDAO.getTemplate();
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
        mongoTemplate.dropCollection(Role.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        Role roleDTO = new Role();
        roleDTO.setRoleId("test");
        roleDTO.setUserRole("Test");
        roleDTO.setExternalId("Test");

        roleDTO.orchestrate();

        //Equals checking with id
        Assert.assertEquals(roleDTO.getRoleId(), "TEST");
        Assert.assertEquals(roleDTO.getUserRole(), "Test");
        Assert.assertEquals(roleDTO.getExternalId(), "TEST");

        roleService.create(roleDTO);
        Role newRole = roleService.get(ID.EXTERNAL_ID(roleDTO.getRoleId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newRole));
        Assert.assertEquals(newRole.getRoleId(), roleDTO.getRoleId());
        Assert.assertEquals(newRole.getUserRole(), roleDTO.getUserRole());
        Assert.assertEquals(newRole.getExternalId(), roleDTO.getExternalId());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Role.class);
    }

}