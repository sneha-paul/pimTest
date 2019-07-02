package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AssetFamilyDAO;
import com.bigname.pim.api.service.AssetFamilyService;
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

import java.util.HashMap;
import java.util.Map;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})

public class AssetFamilyTest {
    @Autowired
    AssetFamilyService assetFamilyService;
    @Autowired
    AssetFamilyDAO assetFamilyDAO;
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)assetFamilyDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(AssetFamily.class);
    }
    @Test
    public void accessorsTest(){
        //Create New Instance
        AssetFamily assetFamilyDTO = new AssetFamily();
        assetFamilyDTO.setAssetFamilyId("test");
        assetFamilyDTO.setAssetFamilyName("test");
        assetFamilyDTO.setDescription("test");

        assetFamilyDTO.orchestrate();

        //Equals Checking with Id
        Assert.assertEquals(assetFamilyDTO.getAssetFamilyId(), "TEST");
        Assert.assertEquals(assetFamilyDTO.getAssetFamilyName(), "test");
        Assert.assertEquals(assetFamilyDTO.getDescription(), "test");
        Assert.assertEquals(assetFamilyDTO.getActive(), "N");

        assetFamilyService.create(assetFamilyDTO);
        AssetFamily newAssetFamily = assetFamilyService.get(ID.EXTERNAL_ID(assetFamilyDTO.getAssetFamilyId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAssetFamily));
        Assert.assertEquals(newAssetFamily.getAssetFamilyId(), assetFamilyDTO.getAssetFamilyId());
        Assert.assertEquals(newAssetFamily.getAssetFamilyName(), assetFamilyDTO.getAssetFamilyName());
        Assert.assertEquals(newAssetFamily.getDescription(), assetFamilyDTO.getDescription());
        Assert.assertEquals(newAssetFamily.getActive(), assetFamilyDTO.getActive());
    }

    @Test
    public void merge() throws Exception {
        //Create New Instance For Original
        AssetFamily original = new AssetFamily();
        original.setExternalId("test");
        original.setAssetFamilyName("Test");
        original.setDescription("test");

        //Create Modified Instance
        AssetFamily modified = new AssetFamily();
        modified.setGroup("DETAILS");
        modified.setExternalId("TEST-A");
        modified.setAssetFamilyName("Test-A");
        modified.setDescription("test-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getAssetFamilyName(), "Test-A");
        Assert.assertEquals(original.getDescription(), "test-A");

        //Create New Instance for Without Details
        AssetFamily modified1 = new AssetFamily();
        modified1.setExternalId("TEST");
        modified1.setAssetFamilyName("Test");
        modified1.setDescription("test");

        original = original.merge(modified1);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getAssetFamilyName(), "Test-A");
        Assert.assertEquals(original.getDescription(), "test-A");
    }
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        AssetFamily assetFamilyDTO = new AssetFamily();
        assetFamilyDTO.setExternalId("test");
        assetFamilyDTO.setAssetFamilyName("Test");
        assetFamilyDTO.setDescription("test");

        //Create New Instance For Checking map
        Map<String, String> map = new HashMap<>();
        map.put("externalId","TEST");
        map.put("assetFamilyName","Test");
        map.put("description","test");

        //Equals Checking For Getting Map
        Map<String, String> map1 = assetFamilyDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("assetFamilyName"), map.get("assetFamilyName"));
        Assert.assertEquals(map1.get("description"), map.get("description"));
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AssetFamily.class);
    }
}