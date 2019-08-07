package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.AssetCollectionDAO;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.FamilyService;
import com.m7.xtreme.common.util.ValidationUtil;
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

/**
 * Created by sanoop on 20/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FileAssetTest {

    @Autowired
    private AssetCollectionService assetCollectionService;
    @Autowired
    private AssetCollectionDAO assetCollectionDAO;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) assetCollectionDAO.getTemplate();
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

        mongoTemplate.dropCollection(FileAsset.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        FileAsset fileAssetDTO = new FileAsset();
        fileAssetDTO.setId("test");
        fileAssetDTO.setName("Test");
        fileAssetDTO.setInternalName("Test11");
        fileAssetDTO.setDefaultFlag("N");

        //Testing equals unique id
        Assert.assertEquals(fileAssetDTO.getId(), "test");
        Assert.assertEquals(fileAssetDTO.getName(), "Test");
        Assert.assertEquals(fileAssetDTO.getInternalName(), "Test11");
        Assert.assertEquals(fileAssetDTO.getDefaultFlag(), "N");

        //create
        //assetCollectionService.create(fileAssetDTO);
        //ToDO
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(FileAsset.class);
    }
}