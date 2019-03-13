package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.AttributeCollectionService;
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
import java.util.*;
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
    private ProductVariantDAO productVariantDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    private ChannelDAO channelDAO;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Before
    public void setUp() {
        productVariantDAO.getMongoTemplate().dropCollection(ProductVariant.class);

        productDAO.getMongoTemplate().dropCollection(Product.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }

    @Test
    public void createProductVariantTest() {
        List<Map<String, Object>> channelsData = new ArrayList<>();
        channelsData.add(CollectionsUtil.toMap("name", "Ecommerce", "externalId", "ECOMMERCE", "active", "Y"));

        channelsData.forEach(channelData -> {
            Channel channel = new Channel();
            channel.setChannelName((String)channelData.get("name"));
            channel.setChannelId((String)channelData.get("externalId"));
            channel.setActive((String)channelData.get("active"));
            channelDAO.insert(channel);
        });

        Channel channel = channelDAO.findById(channelsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(channel));

        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test_AttributeCollection");
        attributeCollectionDTO.setCollectionId("TEST_ATTRIBUTECOLLECTION");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDetails));

        List<Map<String, Object>> attributesData = new ArrayList<>();
        attributesData.add(CollectionsUtil.toMap("name", "Color", "externalId", "COLOR", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute2", "externalId", "TEST_ATTRIBUTE_2", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute3", "externalId", "TEST_ATTRIBUTE_3", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute4", "externalId", "TEST_ATTRIBUTE_4", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute5", "externalId", "TEST_ATTRIBUTE_5", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute6", "externalId", "TEST_ATTRIBUTE_6", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute7", "externalId", "TEST_ATTRIBUTE_7", "active", "Y", "uiType", Attribute.UIType.TEXTAREA));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute8", "externalId", "TEST_ATTRIBUTE_8", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute9", "externalId", "TEST_ATTRIBUTE_9", "active", "Y", "uiType", Attribute.UIType.INPUT_BOX));
        attributesData.add(CollectionsUtil.toMap("name", "TestAttribute10", "externalId", "TEST_ATTRIBUTE_10", "active", "Y", "uiType", Attribute.UIType.DROPDOWN));

        attributesData.forEach(attributeData -> {
            Attribute attribute = new Attribute();
            attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
            attribute.setUiType((Attribute.UIType) attributeData.get("uiType"));
            attribute.setName((String) attributeData.get("name"));
            attribute.setId((String) attributeData.get("externalId"));
            attribute.setActive((String) attributeData.get("active"));
            attributeCollectionDetails.addAttribute(attribute);
        });

        attributeCollectionDAO.save(attributeCollectionDetails);

        AttributeCollection attributeCollection = attributeCollectionService.get(attributeCollectionDetails.getCollectionId(), FindBy.EXTERNAL_ID, false).orElse(null);

        List<Attribute> attributes = attributeCollection.getAllAttributes();
        Attribute attributeDetails = attributeCollectionDetails.getAttribute(attributes.get(0).getFullId()).orElse(null);

        List<Map<String, Object>> attributeOptionsData = new ArrayList<>();
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Blue", "active", "Y"));
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Green", "active", "Y"));
        attributeOptionsData.add(CollectionsUtil.toMap("value", "Red", "active", "Y"));

        attributeOptionsData.forEach(attributeOptionData -> {
            AttributeOption attributeOption = new AttributeOption();
            attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
            attributeOption.setValue((String) attributeOptionData.get("value"));
            attributeOption.setAttributeId(attributeDetails.getFullId());
            attributeOption.setActive((String) attributeOptionData.get("active"));
            attributeOption.orchestrate();
            attributeDetails.getOptions().put(ValidatableEntity.toId(attributeOption.getValue()), attributeOption);
        });

        attributeCollectionDAO.save(attributeCollectionDetails);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach((Map<String, Object> familyData) -> {
            AttributeCollection finalAttributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDTO.setDiscontinued((String)familyData.get("discontinue"));
            familyDAO.insert(familyDTO);

            Family family = familyDAO.findByExternalId(familyDTO.getFamilyId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(family));

            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
            familyAttributeGroup.setActive("Y");
            familyAttributeGroup.setMasterGroup("Y");
            familyAttributeGroup.setName(FamilyAttributeGroup.DEFAULT_GROUP);
            familyAttributeGroup.setId(familyAttributeGroup.getFullId());

            //Create the new familyAttribute instance
            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeDetails.getName(), null);
            familyAttributeDTO.setActive("Y");
            familyAttributeDTO.setCollectionId(finalAttributeCollectionDetails.getCollectionId());
            familyAttributeDTO.setUiType(attributeDetails.getUiType());
            familyAttributeDTO.setScopable("Y");
            familyAttributeDTO.setAttributeId(attributeDetails.getFullId());
            familyAttributeDTO.getScope().put(channel.getChannelId(), FamilyAttribute.Scope.OPTIONAL);
            familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
            familyAttributeDTO.setAttribute(attributeDetails);

            family.addAttribute(familyAttributeDTO);

            FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attributeDetails.getId());

            List<AttributeOption> attributeOptionList = new ArrayList(attributeDetails.getOptions().values());
            attributeOptionList.forEach(attributeOption -> {
                FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                familyAttributeOption.setActive("Y");
                familyAttributeOption.setValue(attributeOption.getValue());
                familyAttributeOption.setId(attributeOption.getId());
                familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                familyAttributeDTO.getOptions().put(attributeOption.getId(), familyAttributeOption);
                family.addAttributeOption(familyAttributeOption, attributeOption);
            });

            //set parentAttribute //TODO

            //create variantGroup
            family.setGroup("VARIANT_GROUPS");
            VariantGroup variantGroup = new VariantGroup();
            variantGroup.setName(attributeDetails.getName());
            variantGroup.setId(attributeDetails.getId());
            variantGroup.setActive("Y");
            variantGroup.setLevel(1);
            variantGroup.setFamilyId(family.getFamilyId());
            variantGroup.getVariantAxis().put(1, Arrays.asList(attributeDetails.getName()));
            variantGroup.getVariantAttributes().put(1, Arrays.asList(attributes.get(2).getName(), attributes.get(9).getName()));
            family.addVariantGroup(variantGroup);
            family.getChannelVariantGroups().put(channel.getChannelId(), variantGroup.getId());

            familyDAO.save(family);

        });

        Family familyDetails = familyDAO.findByExternalId(familiesData.get(0).get("externalId").toString()).orElse(null);

        //create Product instance
        Product productDTO = new Product();
        productDTO.setProductName("Test1");
        productDTO.setProductId("TEST1");
        productDTO.setChannelId(channel.getChannelId());
        productDTO.setProductFamilyId(familyDetails.getId());
        productDTO.setActive("Y");
        Product product = productDAO.insert(productDTO);
        Assert.assertTrue(product.diff(productDTO).isEmpty());

        //create productVariantInstance
        Product newProduct = productDAO.findById(product.getProductId(), FindBy.EXTERNAL_ID).orElse(null);

        ProductVariant productVariantDTO = new ProductVariant();
        productVariantDTO.setProductVariantName("Test1");
        productVariantDTO.setProductVariantId("TEST1");
        productVariantDTO.setProductId(newProduct.getId());
        productVariantDTO.setActive("Y");
        productVariantDTO.setChannelId(channel.getChannelId());
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

        productDAO.getMongoTemplate().dropCollection(Product.class);

        familyDAO.getMongoTemplate().dropCollection(Family.class);

        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);

        channelDAO.getMongoTemplate().dropCollection(Channel.class);
    }
}
