package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.RoleDAO;
import com.bigname.pim.api.service.RoleService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


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
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)roleDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Role.class);
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
        Role newRole = roleService.get(ID.EXTERNAL_ID(roleDTO.getRoleId()), false).orElse(null);
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
        mongoTemplate.dropCollection(Role.class);
    }

}