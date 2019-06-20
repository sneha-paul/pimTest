package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Event;
import com.m7.xtreme.xcore.persistence.mongo.dao.EventDAO;
import com.m7.xtreme.xcore.service.EventService;
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

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class EventTest {
    @Autowired
    EventService eventService;
    @Autowired
    EventDAO eventDAO;
    @Before
    public void setUp() throws Exception {
        eventDAO.getMongoTemplate().dropCollection(Event.class);
    }
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
        Event newEvent = eventService.get(eventDTO.getExternalId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newEvent));
        Assert.assertEquals(newEvent.getExternalId(), eventDTO.getExternalId());
        Assert.assertEquals(newEvent.getUser(), eventDTO.getUser());
        Assert.assertEquals(newEvent.getDetails(), eventDTO.getDetails());
        Assert.assertEquals(newEvent.getActive(), eventDTO.getActive());

    }
    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        eventDAO.getMongoTemplate().dropCollection(Event.class);
    }

}