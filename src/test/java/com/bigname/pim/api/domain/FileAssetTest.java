package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.FamilyService;
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
 * Created by sanoop on 20/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class FileAssetTest {

    @Autowired
    AssetCollectionService assetCollectionService;
    @Autowired
    AssetCollectionDAO assetCollectionDAO;
    @Autowired
    FamilyService familyService;
    @Before
    public void setUp() throws Exception {
        assetCollectionDAO.getMongoTemplate().dropCollection(FileAsset.class);
    }
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
    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        assetCollectionDAO.getMongoTemplate().dropCollection(FileAsset.class);
    }
}