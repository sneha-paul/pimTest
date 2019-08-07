package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.AssetCollectionDAO;
import com.bigname.pim.api.service.AssetCollectionService;
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

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AssetCollectionTest {

    @Autowired
    private AssetCollectionService assetCollectionService;
    @Autowired
    private AssetCollectionDAO assetCollectionDAO;
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
        mongoTemplate.dropCollection(AssetCollection.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest(){
        //Create New Instance
        AssetCollection assetCollectionDTO = new AssetCollection();
        assetCollectionDTO.setCollectionId("test");
        assetCollectionDTO.setCollectionName("test");
        assetCollectionDTO.setRootId("test");

        assetCollectionDTO.orchestrate();

        //Equals Checking With Id
        Assert.assertEquals(assetCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(assetCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(assetCollectionDTO.getRootId(), "test");
        Assert.assertEquals(assetCollectionDTO.getActive(), "N");

        assetCollectionService.create(assetCollectionDTO);
        AssetCollection newAssetCollection = assetCollectionService.get(ID.EXTERNAL_ID(assetCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAssetCollection));
        Assert.assertEquals(newAssetCollection.getCollectionId(), assetCollectionDTO.getCollectionId());
        Assert.assertEquals(newAssetCollection.getCollectionName(), assetCollectionDTO.getCollectionName());
        Assert.assertEquals(newAssetCollection.getActive(), assetCollectionDTO.getActive());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create New Original Instance
        AssetCollection original = new AssetCollection();
        original.setExternalId("test");
        original.setCollectionName("Test");
        original.setActive("Y");

        //Create New Modified Instance
        AssetCollection modified = new AssetCollection();
        modified.setGroup("DETAILS");
        modified.setExternalId("TEST-A");
        modified.setCollectionName("Test-A");
        modified.setActive("N");

        original = original.merge(modified);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getCollectionName(), "Test-A");
        Assert.assertEquals(original.getActive(), "N");

        //Create New Modified Instance Without Details
        AssetCollection modified1 = new AssetCollection();
        modified1.setExternalId("TEST");
        modified1.setCollectionName("Test");
        modified1.setActive("Y");

        original = original.merge(modified1);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getCollectionName(), "Test-A");
        Assert.assertEquals(original.getActive(), "N");

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        AssetCollection assetCollectionDTO = new AssetCollection();
        assetCollectionDTO.setExternalId("test");
        assetCollectionDTO.setCollectionName("Test");
        assetCollectionDTO.setRootId("test");
        assetCollectionDTO.setActive("Y");

        //Create New Map For Equals Checking
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("collectionName", "Test");
        map.put("rootId", "test");
        map.put("active", "Y");

        Map<String, String> map1 = assetCollectionDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("collectionName"), map.get("collectionName"));
        Assert.assertEquals(map1.get("rootId"), map.get("rootId"));
        Assert.assertEquals(map1.get("active"), map.get("active"));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap1() throws Exception {
        //create New Instance
        AssetCollection assetCollectionDTO = new AssetCollection();
        assetCollectionDTO.setExternalId("test");
        assetCollectionDTO.setCollectionName("Test");
        assetCollectionDTO.setRootId("test");
        assetCollectionDTO.setActive("Y");

        //Create New Map For Equals Checking
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("collectionName", "Test");
        map.put("rootId", "test");
        map.put("active", "Y");

        Map<String, String> map1 = assetCollectionDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("collectionName"), map.get("collectionName"));
        Assert.assertEquals(map1.get("rootId"), map.get("rootId"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void diff() throws Exception {
        //Create First New Instance
        AssetCollection assetCollection1 = new AssetCollection();
        assetCollection1.setCollectionName("Test");
        assetCollection1.setRootId("test");

        //Create Second New Instance
        AssetCollection assetCollection2 = new AssetCollection();
        assetCollection2.setCollectionName("Test.com");
        assetCollection2.setRootId("test");

        //Checking First instance and Second instance
        Map<String, Object> diff = assetCollection1.diff(assetCollection2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("collectionName"), "Test.com");

        //Create First New Instance For Ignore internal Id
        AssetCollection assetCollection3 = new AssetCollection();
        assetCollection3.setCollectionName("Test");
        assetCollection3.setRootId("test");

        //Create Second New Instance For Ignore internal Id
        AssetCollection assetCollection4 = new AssetCollection();
        assetCollection4.setCollectionName("Test.com");
        assetCollection4.setRootId("test");

        //Checking First instance and Second instance
        Map<String, Object> diff1 = assetCollection3.diff(assetCollection4, true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("collectionName"), "Test.com");
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AssetCollection.class);
    }
}