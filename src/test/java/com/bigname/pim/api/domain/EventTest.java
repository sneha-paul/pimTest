package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.Event;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.EventDAO;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import com.m7.xtreme.xplatform.service.EventService;
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
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class EventTest {
    @Autowired
    private EventService eventService;
    @Autowired
    private EventDAO eventDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) eventDAO.getTemplate();
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
        mongoTemplate.dropCollection(Event.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        Event eventDTO = new Event();
        eventDTO.setExternalId("test");
        eventDTO.setUser("test");
        eventDTO.setDetails("test.com");
        eventDTO.setActive("Y");

        eventDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(eventDTO.getExternalId(), "TEST");
        Assert.assertEquals(eventDTO.getUser(), "test");
        Assert.assertEquals(eventDTO.getDetails(), "test.com");
        Assert.assertEquals(eventDTO.getActive(), "Y");

        //create
        eventService.create(eventDTO);
        Event newEvent = eventService.get(ID.EXTERNAL_ID(eventDTO.getExternalId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newEvent));
        Assert.assertEquals(newEvent.getExternalId(), eventDTO.getExternalId());
        Assert.assertEquals(newEvent.getUser(), eventDTO.getUser());
        Assert.assertEquals(newEvent.getDetails(), eventDTO.getDetails());
        Assert.assertEquals(newEvent.getActive(), eventDTO.getActive());

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
        mongoTemplate.dropCollection(Event.class);
    }

}