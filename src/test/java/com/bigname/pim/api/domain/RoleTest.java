package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.RoleDAO;
import com.bigname.pim.api.service.RoleService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

/**
 * Created by sanoop on 20/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RoleTest {

    @Autowired
    RoleDAO roleDAO;
    @Autowired
    RoleService roleService;
    @Before
    public void setUp() throws Exception {
        roleDAO.getMongoTemplate().dropCollection(Role.class);
    }
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
        Role newRole = roleService.get(roleDTO.getRoleId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newRole));
        Assert.assertEquals(newRole.getRoleId(), roleDTO.getRoleId());
        Assert.assertEquals(newRole.getUserRole(), roleDTO.getUserRole());
        Assert.assertEquals(newRole.getExternalId(), roleDTO.getExternalId());
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        roleDAO.getMongoTemplate().dropCollection(Role.class);
    }

}