package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.bigname.pim.api.service.VirtualFileService;
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
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class VirtualFileTest {
    @Autowired
    VirtualFileService virtualFileService;
    @Autowired
    VirtualFileDAO virtualFileDAO;
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)virtualFileDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(VirtualFile.class);
    }
    @Test
    public void accessorsTest() {
        //Create new instance
        VirtualFile virtualFileDTO = new VirtualFile();
        virtualFileDTO.setFileId("test");
        virtualFileDTO.setFileName("Test");
        virtualFileDTO.setIsDirectory("N");
        virtualFileDTO.setType("test11");
        virtualFileDTO.setExtension("test22");
        virtualFileDTO.setRootDirectoryId("test33");
        virtualFileDTO.setSize(12);

        virtualFileDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(virtualFileDTO.getFileId(), "TEST");
        Assert.assertEquals(virtualFileDTO.getFileName(), "Test");
        Assert.assertEquals(virtualFileDTO.getIsDirectory(), "N");
        Assert.assertEquals(virtualFileDTO.getType(), "test11");
        Assert.assertEquals(virtualFileDTO.getExtension(), "test22");
        Assert.assertEquals(virtualFileDTO.getRootDirectoryId(), "test33");
        Assert.assertEquals(virtualFileDTO.getSize(), 12);

        //create
        virtualFileService.create(virtualFileDTO);
        VirtualFile newVirtualFile = virtualFileService.get(ID.EXTERNAL_ID(virtualFileDTO.getFileId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newVirtualFile));
        Assert.assertEquals(newVirtualFile.getFileId(), virtualFileDTO.getFileId());
        Assert.assertEquals(newVirtualFile.getFileName(), virtualFileDTO.getFileName());
        Assert.assertEquals(newVirtualFile.getIsDirectory(), virtualFileDTO.getIsDirectory());
        Assert.assertEquals(newVirtualFile.getType(), virtualFileDTO.getType());
        Assert.assertEquals(newVirtualFile.getExtension(), virtualFileDTO.getExtension());
        Assert.assertEquals(newVirtualFile.getRootDirectoryId(), virtualFileDTO.getRootDirectoryId());
        Assert.assertEquals(newVirtualFile.getSize(), virtualFileDTO.getSize());
    }
    @Test
    public void merge() throws Exception {
        //Create Original Instance
        VirtualFile original = new VirtualFile();
        original.setExternalId("test");
        original.setFileName("Test");
        original.setActive("Y");
        original.setIsDirectory("N");
        original.setType("test11");
        original.setExtension("test22");

        //Create Modified Instance
        VirtualFile modified = new VirtualFile();
        modified.setGroup("DETAILS");
        modified.setExternalId("TEST-A");
        modified.setFileName("Test-A");
        modified.setActive("Y");
        modified.setIsDirectory("N");
        modified.setType("test11-A");
        modified.setExtension("test22-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getFileName(), "Test-A");
        Assert.assertEquals(original.getActive(), "Y");
        Assert.assertEquals(original.getIsDirectory(), "N");
        Assert.assertEquals(original.getType(), "test11-A");
        Assert.assertEquals(original.getExtension(), "test22-A");

        //Without Details
        VirtualFile modified1 = new VirtualFile();
        modified1.setExternalId("test");
        modified1.setFileName("Test");
        modified1.setActive("Y");
        modified1.setIsDirectory("N");
        modified1.setType("test11");
        modified1.setExtension("test22");

        original = original.merge(modified1);
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getFileName(), "Test-A");
        Assert.assertEquals(original.getActive(), "Y");
        Assert.assertEquals(original.getIsDirectory(), "N");
        Assert.assertEquals(original.getType(), "test11-A");
        Assert.assertEquals(original.getExtension(), "test22-A");
    }
    @Test
    public void toMap() throws Exception {
        //Create New Instance
        VirtualFile virtualFileDTO = new VirtualFile();
        virtualFileDTO.setExternalId("test");
        virtualFileDTO.setFileName("Test");
        virtualFileDTO.setIsDirectory("N");
        virtualFileDTO.setType("test11");
        virtualFileDTO.setExtension("test22");
        virtualFileDTO.setActive("Y");

        //Checking For Map
        Map<String, String> map = new HashMap<>();
        map.put("externalId", "TEST");
        map.put("fileName", "Test");
        map.put("isDirectory", "N");
        map.put("type", "test11");
        map.put("extension", "test22");
        map.put("active", "Y");

        //Equals Checking
        Map<String, String> map1 = virtualFileDTO.toMap();
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("name"), map.get("fileName"));
        Assert.assertEquals(map1.get("isDirectory"), map.get("isDirectory"));
        Assert.assertEquals(map1.get("type"), map.get("type"));
        Assert.assertEquals(map1.get("extension"), map.get("extension"));
        Assert.assertEquals(map1.get("active"), map.get("active"));
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(VirtualFile.class);
    }
}