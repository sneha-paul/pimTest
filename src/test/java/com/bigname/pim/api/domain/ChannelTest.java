package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
import com.bigname.pim.api.service.ChannelService;
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
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})

public class ChannelTest {
    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelDAO channelDAO;

    @Before
    public void setUp() throws Exception {
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }
    @Test
    public void accessorsTest(){
        Channel channelDTO = new Channel();
        channelDTO.setChannelId("test");
        channelDTO.setChannelName("test");

        channelDTO.orchestrate();

        Assert.assertEquals(channelDTO.getChannelId(), "TEST");
        Assert.assertEquals(channelDTO.getChannelName(), "test");
        Assert.assertEquals(channelDTO.getActive(), "N");

        channelService.create(channelDTO);
        Channel newChannel = channelService.get(channelDTO.getChannelId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newChannel));
        Assert.assertEquals(newChannel.getChannelId(), channelDTO.getChannelId());
        Assert.assertEquals(newChannel.getChannelName(), channelDTO.getChannelName());
    }
    
    @Test
    public void orchestrate() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }

}