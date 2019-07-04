package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.api.service.VirtualFileService;
import com.m7.xtreme.common.util.CollectionsUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class VirtualFileServiceImplTest {
    @Autowired
    private AssetCollectionDAO assetCollectionDAO;

    @Autowired
    private AssetCollectionService assetCollectionService;

    @Autowired
    private VirtualFileService assetService;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) assetCollectionDAO.getTemplate();
        }
        mongoTemplate.dropCollection(AssetCollection.class);
    }

    @Test
    public void createTest() throws Exception {
    }

    @Test
    public void updateTest() throws Exception {
    }

    @Test
    public void getFilesTest() throws Exception {
        List<Map<String, Object>> assetCollectionData = new ArrayList<>();
        assetCollectionData.add(CollectionsUtil.toMap("name", "TestAssetCollection"));

        assetCollectionData.forEach(assetData -> {

            AssetCollection assetCollectionDTO = new AssetCollection();
            assetCollectionDTO.setCollectionId((String) assetData.get("name"));
            assetCollectionDTO.setCollectionName(((String) assetData.get("name")));
            assetCollectionDTO.setActive("Y");
            assetCollectionDTO.setRootId(assetService.create(VirtualFile.getRootInstance()).getId());
            assetCollectionDAO.insert(assetCollectionDTO);

            AssetCollection newAssetCollection = assetCollectionService.get(ID.EXTERNAL_ID(assetCollectionDTO.getCollectionId()), false).orElse(null);
            Assert.assertTrue(newAssetCollection != null);
            Assert.assertTrue(newAssetCollection.diff(assetCollectionDTO).isEmpty());

            List<VirtualFile> assets = assetService.getFiles(newAssetCollection.getRootId());
            Assert.assertTrue(assets != null);
        });
    }

    @Test
    public void getFileTest() throws Exception {
    }

    @Test
    public void validateTest() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AssetCollection.class);
    }

}