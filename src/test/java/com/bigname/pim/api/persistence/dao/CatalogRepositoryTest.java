package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by dona on 19-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogRepositoryTest {
    @Autowired
    CatalogDAO catalogDAO;

    @Before
    public void setUp() {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void createCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        Catalog catalog = catalogDAO.insert(catalogDTO);
        Assert.assertTrue(catalog.diff(catalogDTO).isEmpty());

    }

    @Test
    public void retrieveCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        catalogDAO.insert(catalogDTO);
        Optional<Catalog> catalog = catalogDAO.findByExternalId(catalogDTO.getCatalogId());
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getCatalogId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
    }

    @Test
    public void updateCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        catalogDAO.insert(catalogDTO);

        Catalog catalogDetails = catalogDAO.findByExternalId(catalogDTO.getCatalogId()).orElse(null);
        Assert.assertTrue(catalogDetails != null);
        catalogDetails.setDescription("Test1 catalog description");
        catalogDetails.setGroup("DETAILS");
        catalogDAO.save(catalogDetails);
        
        Optional<Catalog> catalog = catalogDAO.findByExternalId(catalogDetails.getCatalogId());
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDetails.getCatalogId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
    }

    @Test
    public void retrieveCatalogsTest() {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_7description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_8description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_9description", "active", "Y"));

        List<Catalog> catalogDTOs = catalogsData.stream().map(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            return catalogDTO;
        }).collect(Collectors.toList());

        catalogDAO.insert(catalogDTOs);

        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false).getTotalElements(), catalogDTOs.size());
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size() - 1), false).getTotalElements(), catalogDTOs.size());
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size() - 1), false).getContent().size(), catalogDTOs.size() - 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(1, catalogDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size() - 1), false).getTotalPages(), 2);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);

        catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_7description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_8description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_9description", "active", "Y"));
        int[] activeCount = {0}, inactiveCount = {0};
        catalogDTOs = catalogsData.stream().map(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            if("Y".equals(catalogData.get("active"))) {
                activeCount[0] ++;
            } else {
                inactiveCount[0] ++;
            }
            return catalogDTO;
        }).collect(Collectors.toList());

        catalogDAO.insert(catalogDTOs);

        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false, false, true).getTotalElements(), 0);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "activeFrom", yesterday, "activeTo", todayEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "activeFrom", null, "activeTo", todayEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "activeFrom", tomorrowEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "N", "activeFrom", null, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_5description", "activeFrom", yesterday, "activeTo", tomorrowEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_6description", "activeFrom", yesterday, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_7description", "activeFrom", null, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_8description", "active", "Y", "activeFrom", null, "activeTo", null));
        int[] activeCount1 = {0}, inactiveCount1 = {0};
        catalogDTOs = catalogsData.stream().map(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setActiveFrom((LocalDateTime) catalogData.get("activeFrom"));
            catalogDTO.setActiveTo((LocalDateTime) catalogData.get("activeTo"));
            if(PimUtil.isActive(catalogDTO.getActive(), catalogDTO.getActiveFrom(), catalogDTO.getActiveTo())) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            return catalogDTO;
        }).collect(Collectors.toList());

        catalogDAO.insert(catalogDTOs);

        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false, false, true).getTotalElements(), 0);
    }

    @After
    public void tearDown() {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
}
