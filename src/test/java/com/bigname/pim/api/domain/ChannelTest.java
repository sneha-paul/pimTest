package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.ChannelDAO;
import com.bigname.pim.api.service.ChannelService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.mongo.UserDAO;
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

public class ChannelTest {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelDAO channelDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) channelDAO.getTemplate();
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
        mongoTemplate.dropCollection(Channel.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest(){
        //Create New Instance
        Channel channelDTO = new Channel();
        channelDTO.setChannelId("test");
        channelDTO.setChannelName("test");

        channelDTO.orchestrate();

        //Equals checking With Id
        Assert.assertEquals(channelDTO.getChannelId(), "TEST");
        Assert.assertEquals(channelDTO.getChannelName(), "test");
        Assert.assertEquals(channelDTO.getActive(), "N");

        channelService.create(channelDTO);
        Channel newChannel = channelService.get(ID.EXTERNAL_ID(channelDTO.getChannelId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newChannel));
        Assert.assertEquals(newChannel.getChannelId(), channelDTO.getChannelId());
        Assert.assertEquals(newChannel.getChannelName(), channelDTO.getChannelName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
        //Create New Instance
        Channel channelDTO = new Channel();
        channelDTO.setActive("N");
        channelDTO.orchestrate();

        //Equals Checking
        Assert.assertTrue(ValidationUtil.isNotEmpty(channelDTO.getActive()));
        Assert.assertEquals(channelDTO.getActive(), "N");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create Original Instance
        Channel original = new Channel();
        original.setChannelName("Test");
        original.setActive("Y");
        original.setDiscontinued("Y");

        //Modified Instance Add Details
        Channel modified = new Channel();
        modified.setGroup("DETAILS");
        modified.setChannelName("Test-A");
        modified.setActive("N");
        modified.setDiscontinued("N");

        original = original.merge(modified);
        Assert.assertEquals(original.getChannelName(), "Test-A");
        Assert.assertEquals(original.getActive(), "N");
        Assert.assertEquals(original.getDiscontinued(), "N");

        //Modified Instance Without Details
        Channel modified1 = new Channel();
        modified1.setChannelName("test");
        modified1.setActive("Y");
        modified1.setDiscontinued("Y");

        original = original.merge(modified1);
        Assert.assertEquals(original.getChannelName(), "Test-A");
        Assert.assertEquals(original.getActive(), "N");
        Assert.assertEquals(original.getDiscontinued(), "N");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create new instance
        Channel channelDTO = new Channel();
        channelDTO.setChannelName("TEST");
        channelDTO.setExternalId("test");
        channelDTO.setActive("Y");
        channelDTO.setDiscontinued("Y");

        Map<String, String> map = new HashMap<>();
        map.put("channelName", "TEST");
        map.put("externalId", "TEST");
        map.put("active", "Y");
        map.put("discontinued", "Y");

        //Checking Equals for Convert map
        Map<String, String> map1 = channelDTO.toMap();
        Assert.assertEquals(map1.get("channelName"), map.get("channelName"));
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
        Assert.assertEquals(map1.get("discontinued"), map.get("discontinued"));

    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Channel.class);
    }

}