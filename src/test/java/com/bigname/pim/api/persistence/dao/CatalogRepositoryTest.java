package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    RootCategoryDAO rootCategoryDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)catalogDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }

    @Test
    public void createCatalogTest() {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));

            Catalog catalog = catalogDAO.insert(catalogDTO);
            Assert.assertTrue(catalog.diff(catalogDTO).isEmpty());
        });
    }

    @Test
    public void retrieveCatalogTest() {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            Optional<Catalog> catalog = catalogDAO.findByExternalId(catalogsData.get(0).get("externalId").toString());
            Assert.assertTrue(catalog.isPresent());
            catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()));
            Assert.assertTrue(catalog.isPresent());
            catalog = catalogDAO.findById(ID.INTERNAL_ID(catalogDTO.getId()));
            Assert.assertTrue(catalog.isPresent());
        });
    }

    @Test
    public void updateCatalogTest() {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            Catalog catalogDetails = catalogDAO.findByExternalId(catalogsData.get(0).get("externalId").toString()).orElse(null);
            Assert.assertTrue(catalogDetails != null);
            catalogDetails.setDescription("Test1 catalog description");
            catalogDetails.setGroup("DETAILS");
            catalogDAO.save(catalogDetails);

            Catalog catalog = catalogDAO.findByExternalId(catalogDetails.getCatalogId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(catalog));
            Map<String, Object> diff = catalogDTO.diff(catalog);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("description"), "Test1 catalog description");
        });
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

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            Optional<Catalog> catalog = catalogDAO.findById(catalogDTO.getId());
            Assert.assertTrue(catalog.isPresent());
        });


        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false).getTotalElements(), catalogsData.size());
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size() - 1), false).getTotalElements(), catalogsData.size());
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size() - 1), false).getContent().size(), catalogsData.size() - 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(1, catalogsData.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size() - 1), false).getTotalPages(), 2);

        mongoTemplate.dropCollection(Catalog.class);

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

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            if("Y".equals(catalogData.get("active"))) {
                activeCount[0] ++;
            } else {
                inactiveCount[0] ++;
            }
            catalogDAO.insert(catalogDTO);

            Optional<Catalog> catalog = catalogDAO.findById(ID.INTERNAL_ID(catalogDTO.getId()));
            Assert.assertTrue(catalog.isPresent());
        });

        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false, false, true).getTotalElements(), 0);

        mongoTemplate.dropCollection(Catalog.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "activeFrom", yesterday, "activeTo", todayEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "activeFrom", null, "activeTo", todayEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "activeFrom", tomorrow));
        catalogsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "N", "activeFrom", null, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_5description", "activeFrom", yesterday, "activeTo", tomorrowEOD));
        catalogsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_6description", "activeFrom", yesterday, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_7description", "activeFrom", null, "activeTo", null));
        catalogsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_8description", "active", "Y", "activeFrom", null, "activeTo", null));

        int[] activeCount1 = {0}, inactiveCount1 = {0};

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            if("Y".equals(catalogData.get("active"))) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            catalogDAO.insert(catalogDTO);

            Optional<Catalog> catalog = catalogDAO.findById(catalogDTO.getId());
            Assert.assertTrue(catalog.isPresent());
        });

        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogsData.size()), false, false, true).getTotalElements(), 0);
    }

    @Test
    public void getRootCategoriesTest() throws Exception {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        Catalog catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString())).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoryData.get("externalId").toString())).orElse(null);


            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        Page<Map<String, Object>> rootCategoriesMap = catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(0, categoriesData.size(), null));
        Assert.assertEquals(rootCategoriesMap.getSize(), categoriesData.size());
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(0, categoriesData.size(), null)).getTotalElements(), categoriesData.size());
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(0, categoriesData.size()-1, null)).getTotalElements(), categoriesData.size());
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(0, categoriesData.size()-1, null)).getContent().size(), categoriesData.size() - 1);
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(1, 1, null)).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(1, categoriesData.size()-1, null)).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.getRootCategories(catalog.getId(), PageRequest.of(0, categoriesData.size()-1, null)).getTotalPages(), 2);
    }

    @Test
    public void findAllRootCategoriesTest() throws Exception {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        Catalog catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString())).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            Category category = categoryDAO.findById(ID.EXTERNAL_ID((String) categoryData.get("externalId"))).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        boolean[] activeRequired = {false};

        Page<Map<String, Object>> rootCategories =  catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size(),null), activeRequired);
        Assert.assertEquals(rootCategories.getSize(),categoriesData.size());
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size(),null), activeRequired).getTotalElements(), categoriesData.size());
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1,null), activeRequired).getTotalElements(), categoriesData.size());
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1,null), activeRequired).getContent().size(), categoriesData.size() - 1);
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(1, 1, null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(1,categoriesData.size()-1,null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAllRootCategories(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1,null), activeRequired).getTotalPages(), 2);
    }

    @Test
    public void getAllRootCategoriesTest() throws Exception {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        Catalog catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString())).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            Category category = categoryDAO.findById(ID.EXTERNAL_ID((String) categoryData.get("externalId"))).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        List<RootCategory> rootCategoryList = catalogDAO.getAllRootCategories(catalog.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(rootCategoryList));

    }

    @Test
    public void findAvailableRootCategoriesForCatalogTest() throws Exception {

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        Catalog catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString())).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        Page<Category> availableCategoriesPage = catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(0, categoriesData.size() - 1), false);
        Assert.assertEquals(availableCategoriesPage.getContent().size(), 1);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1), false).getTotalElements(), categoriesData.size()-1);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1), false).getTotalElements(), categoriesData.size()-1);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1), false).getContent().size(), categoriesData.size() - 1);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(1, 1), false).getContent().size(), 0);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(1,categoriesData.size()-1), false).getContent().size(), 0);
        Assert.assertEquals(catalogDAO.findAvailableRootCategoriesForCatalog(catalog.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-1), false).getTotalPages(), 1);
    }

    @After
    public void tearDown() {
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }
}
