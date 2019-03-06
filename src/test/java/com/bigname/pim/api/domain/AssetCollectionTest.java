package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.service.AssetCollectionService;
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
public class AssetCollectionTest {

    @Autowired
    AssetCollectionService assetCollectionService;
    @Autowired
    AssetCollectionDAO assetCollectionDAO;

    @Before
    public void setUp() throws Exception {
        assetCollectionDAO.getMongoTemplate().dropCollection(AssetCollection.class);
    }
    @Test
    public void accessorsTest(){
        AssetCollection assetCollectionDTO = new AssetCollection();
        assetCollectionDTO.setCollectionId("test");
        assetCollectionDTO.setCollectionName("test");
        assetCollectionDTO.setRootId("test");

        assetCollectionDTO.orchestrate();

        Assert.assertEquals(assetCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(assetCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(assetCollectionDTO.getRootId(), "test");
        Assert.assertEquals(assetCollectionDTO.getActive(), "N");

        assetCollectionService.create(assetCollectionDTO);
        AssetCollection newAssetCollection = assetCollectionService.get(assetCollectionDTO.getCollectionId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAssetCollection));
        Assert.assertEquals(newAssetCollection.getCollectionId(), assetCollectionDTO.getCollectionId());
        Assert.assertEquals(newAssetCollection.getCollectionName(), assetCollectionDTO.getCollectionName());
        Assert.assertEquals(newAssetCollection.getActive(), assetCollectionDTO.getActive());
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void toMap1() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        assetCollectionDAO.getMongoTemplate().dropCollection(AssetCollection.class);
    }
}