package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
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
public class ProductVariantRepositoryTest {

    @Autowired
    ProductVariantDAO productVariantDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    FamilyDAO familyDAO;

    @Before
    public void setUp() {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }

    @Test
    public void createProductVariantTest() {
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId("11a8377c-6a97-49b8-b72a-5f98217e9b88");
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        ProductVariant productVariant = productVariantDAO.insert(productVariantDTO);
        Assert.assertTrue(productVariant.diff(productVariantDTO).isEmpty());

    }

    @Test
    public void retrieveProductVariantTest() {
        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId("11a8377c-6a97-49b8-b72a-5f98217e9b88");
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        productVariantDAO.insert(productVariantDTO);
        Optional<ProductVariant> productVariant = productVariantDAO.findByExternalId(productVariantDTO.getProductVariantId());
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getProductVariantId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
    }

    @Test
    public void updateProductVariantTest() {
        Family familyDTO = new Family();
        familyDTO.setFamilyName("TestFamily");
        familyDTO.setFamilyId("TESTFAMILY");
        familyDTO.setActive("Y");
        familyDTO.setDiscontinued("N");
        familyDAO.insert(familyDTO);
        Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
        Assert.assertTrue(family != null);

        Product productDTO = new Product();
        productDTO.setProductName("TestProduct");
        productDTO.setProductId("TESTPRODUCT");
        productDTO.setProductFamilyId(family.getId());
        productDAO.insert(productDTO);
        Product product = productDAO.findByExternalId(productDTO.getProductId()).orElse(null);
        Assert.assertTrue(family != null);

        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(product.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId("ECOMMERCE");
        productVariantDAO.insert(productVariantDTO);
        ProductVariant productVariantDetails = productVariantDAO.findByExternalId(productVariantDTO.getProductVariantId()).orElse(null);
        Assert.assertTrue(productVariantDetails != null);

        productVariantDetails.setProductVariantName("Test1Name");
        productVariantDetails.setGroup("DETAILS");
        productVariantDAO.save(productVariantDetails);

        Optional<ProductVariant> productVariant = productVariantDAO.findByExternalId(productVariantDetails.getProductVariantId());
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDetails.getProductVariantId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());
        productVariant = productVariantDAO.findById(productVariantDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(productVariant.isPresent());

        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

    @Test
    public void retrieveProductVariantsTest() {

        List<Map<String, Object>> productVariantsData = new ArrayList<>();
        productVariantsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "active", "Y"));

        List<ProductVariant> productVariantDTOs = productVariantsData.stream().map(productVariantData -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName((String)productVariantData.get("name"));
            productVariantDTO.setProductVariantId((String)productVariantData.get("externalId"));
            productVariantDTO.setChannelId((String)productVariantData.get("channelId"));
            productVariantDTO.setProductId((String)productVariantData.get("productId"));
            productVariantDTO.setActive((String)productVariantData.get("active"));
            productVariantDTO.setDiscontinued((String)productVariantData.get("discontinued"));
            return productVariantDTO;
        }).collect(Collectors.toList());

        productVariantDAO.insert(productVariantDTOs);

        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false).getTotalElements(), productVariantDTOs.size());
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size() - 1), false).getTotalElements(), productVariantDTOs.size());
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size() - 1), false).getContent().size(), productVariantDTOs.size() - 1);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(1, productVariantDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size() - 1), false).getTotalPages(), 2);

        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);

        productVariantsData = new ArrayList<>();
        productVariantsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "N", "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "N", "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "active", "N", "discontinued", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "N", "discontinued", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "active", "Y", "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        productVariantDTOs = productVariantsData.stream().map(productVariantData -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName((String)productVariantData.get("name"));
            productVariantDTO.setProductVariantId((String)productVariantData.get("externalId"));
            productVariantDTO.setChannelId((String)productVariantData.get("channelId"));
            productVariantDTO.setProductId((String)productVariantData.get("productId"));
            productVariantDTO.setActive((String)productVariantData.get("active"));
            productVariantDTO.setDiscontinued((String)productVariantData.get("discontinued"));
            if("Y".equals(productVariantData.get("discontinued"))){
                discontinued[0] ++;
            } else {
                if("Y".equals(productVariantData.get("active"))) {
                    activeCount[0] ++;
                } else {
                    inactiveCount[0] ++;
                }
            }
            return productVariantDTO;
        }).collect(Collectors.toList());

        productVariantDAO.insert(productVariantDTOs);

        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, false, true).getTotalElements(), discontinued[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, true, true).getTotalElements(), inactiveCount[0] + discontinued[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true, true, true).getTotalElements(), activeCount[0] + inactiveCount[0] + discontinued[0]);

        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        productVariantsData = new ArrayList<>();

        productVariantsData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "activeFrom", yesterday, "activeTo", todayEOD, "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "activeFrom", null, "activeTo", todayEOD, "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "activeFrom", tomorrow, "discontinued", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "active", "N", "activeFrom", null, "activeTo", null, "discontinued", "Y"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "channelId", "ECOMMERCE", "productId", "fff79289-0edd-4b5a-84dd-700a6f5142c3", "activeFrom", yesterday, "activeTo", tomorrowEOD, "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "activeFrom", yesterday, "activeTo", null, "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "channelId", "ECOMMERCE", "productId", "11a8377c-6a97-49b8-b72a-5f98217e9b88", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        productVariantsData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "channelId", "ECOMMERCE", "productId", "2933f331-4d5a-4958-92d9-d25129510c14", "active", "Y", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        int[] activeCount1 = {0}, inactiveCount1 = {0}, discontinued1 = {0};

        productVariantDTOs = productVariantsData.stream().map(productVariantData -> {
            ProductVariant productVariantDTO = new ProductVariant();
            productVariantDTO.setProductVariantName((String)productVariantData.get("name"));
            productVariantDTO.setProductVariantId((String)productVariantData.get("externalId"));
            productVariantDTO.setChannelId((String)productVariantData.get("channelId"));
            productVariantDTO.setProductId((String)productVariantData.get("productId"));
            productVariantDTO.setDiscontinued((String)productVariantData.get("discontinued"));
            productVariantDTO.setActive((String)productVariantData.get("active"));
            productVariantDTO.setActiveFrom((LocalDateTime) productVariantData.get("activeFrom"));
            productVariantDTO.setActiveTo((LocalDateTime) productVariantData.get("activeTo"));

            if(PimUtil.hasDiscontinued(productVariantDTO.getDiscontinued(), productVariantDTO.getDiscontinuedFrom(), productVariantDTO.getDiscontinuedTo())) {
                discontinued1[0]++;
            } else if(PimUtil.isActive(productVariantDTO.getActive(), productVariantDTO.getActiveFrom(), productVariantDTO.getActiveTo())) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            return productVariantDTO;
        }).collect(Collectors.toList());

        productVariantDAO.insert(productVariantDTOs);

        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true, true, true).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true, true, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), true, false, true).getTotalElements(), activeCount1[0] + discontinued1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, true, true).getTotalElements(), inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        /*Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);*/
        Assert.assertEquals(productVariantDAO.findAll(PageRequest.of(0, productVariantDTOs.size()), false, false, true).getTotalElements(), discontinued1[0]);
    }

    @After
    public void tearDown() {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);
    }
}
