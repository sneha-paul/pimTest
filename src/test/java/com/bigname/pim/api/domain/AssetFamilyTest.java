package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AssetFamilyDAO;
import com.bigname.pim.api.service.AssetFamilyService;
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

public class AssetFamilyTest {
    @Autowired
    AssetFamilyService assetFamilyService;
    @Autowired
    AssetFamilyDAO assetFamilyDAO;

    @Before
    public void setUp() throws Exception {
        assetFamilyDAO.getMongoTemplate().dropCollection(AssetFamily.class);
    }
    @Test
    public void accessorsTest(){
        AssetFamily assetFamilyDTO = new AssetFamily();
        assetFamilyDTO.setAssetFamilyId("test");
        assetFamilyDTO.setAssetFamilyName("test");
        assetFamilyDTO.setDescription("test");

        assetFamilyDTO.orchestrate();

        Assert.assertEquals(assetFamilyDTO.getAssetFamilyId(), "TEST");
        Assert.assertEquals(assetFamilyDTO.getAssetFamilyName(), "test");
        Assert.assertEquals(assetFamilyDTO.getDescription(), "test");
        Assert.assertEquals(assetFamilyDTO.getActive(), "N");

        assetFamilyService.create(assetFamilyDTO);
        AssetFamily newAssetFamily = assetFamilyService.get(assetFamilyDTO.getAssetFamilyId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAssetFamily));
        Assert.assertEquals(newAssetFamily.getAssetFamilyId(), assetFamilyDTO.getAssetFamilyId());
        Assert.assertEquals(newAssetFamily.getAssetFamilyName(), assetFamilyDTO.getAssetFamilyName());
        Assert.assertEquals(newAssetFamily.getDescription(), assetFamilyDTO.getDescription());
        Assert.assertEquals(newAssetFamily.getActive(), assetFamilyDTO.getActive());
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        assetFamilyDAO.getMongoTemplate().dropCollection(AssetFamily.class);
    }

}