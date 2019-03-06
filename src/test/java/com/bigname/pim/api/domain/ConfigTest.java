package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.ConfigDAO;
import com.bigname.pim.api.service.ConfigService;
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
public class ConfigTest {
    @Autowired
    ConfigService configService;
    @Autowired
    ConfigDAO configDAO;

    @Before
    public void setUp() throws Exception {
        configDAO.getMongoTemplate().dropCollection(Config.class);
    }
    @Test
    public void accessorsTest(){
        Config configDTO = new Config();
        configDTO.setConfigId("test");
        configDTO.setConfigName("test");

        configDTO.orchestrate();

        Assert.assertEquals(configDTO.getConfigId(), "TEST");
        Assert.assertEquals(configDTO.getConfigName(), "test");
        Assert.assertEquals(configDTO.getActive(), "N");

        configService.create(configDTO);
        Config newConfig = configService.get(configDTO.getConfigId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newConfig));
        Assert.assertEquals(newConfig.getConfigName(), configDTO.getConfigName());
        Assert.assertEquals(newConfig.getActive(), configDTO.getActive());
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
        configDAO.getMongoTemplate().dropCollection(Config.class);
    }


}