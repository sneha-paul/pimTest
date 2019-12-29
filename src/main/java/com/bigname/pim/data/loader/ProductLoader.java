package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.ConvertUtil;
import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.m7.xtreme.common.util.ValidationUtil.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class ProductLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductLoader.class);

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private String attributeCollectionId = "ENVELOPES";

    private List<String> availableAttributeTypes = Arrays.asList("INPUTBOX", "TEXTAREA", "DROPDOWN", "YES_NO");

    private Map<String, Object> familyAttributeGroupLookUp = new LinkedHashMap<>();
    {
        /*Map<String, String> map = new HashMap<>();
        map.put("id", "DETAILS_GROUP");
        map.put("name", "Details Group");
        map.put("label", "Details");
        familyAttributeGroupMetadata.put("Details", map);*/
        familyAttributeGroupLookUp.put("", "DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details", "DETAILS_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Product Features", "FEATURES_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details^", "DETAILS_GROUP");
        familyAttributeGroupLookUp.put("Product Features^", "FEATURES_GROUP");
//        familyAttributeGroupLookUp.put("Details > General Options", "DETAILS_GROUP|DEFAULT_GROUP|GENERAL_OPTIONS");
//        familyAttributeGroupLookUp.put("Details > Product Lead Time", "DETAILS_GROUP|DEFAULT_GROUP|PRODUCT_LEAD_TIME");
    }

    private void resetLookupMap() {
        familyAttributeGroupLookUp = new LinkedHashMap<>();
        familyAttributeGroupLookUp.put("", "DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details", "DETAILS_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Product Features", "FEATURES_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details^", "DETAILS_GROUP");
        familyAttributeGroupLookUp.put("Product Features^", "FEATURES_GROUP");
    }

    public boolean load(String filePath) {
        String channelId = "ECOMMERCE";
        //Product variant data with metadata
        List<List<String>> data = POIUtil.readData(filePath);

        LOGGER.info("size-------------->"+data.size());
        LOGGER.info("size-------------->"+data.get(0).size());

        Map<String, Set<String>> familyVariantGroups = new LinkedHashMap<>();
        if(!attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionId),false).isPresent()) {
            AttributeCollection attributeCollection = new AttributeCollection();
            attributeCollection.setCollectionId(attributeCollectionId);
            attributeCollection.setCollectionName("Envelopes Attributes Collection");
            attributeCollection.setActive("Y");
            attributeCollectionService.create(attributeCollection);
        }

        //Load the attributeCollection from the database for the given collectionId
        attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionId), false).ifPresent(attributeCollection -> {

            attributeCollection.setGroup("ATTRIBUTES");

            //All attributes from the feed are created under the Default attribute group, so build a default attributeGroup instance
            AttributeGroup defaultGroup = new AttributeGroup();
            defaultGroup.setId("DEFAULT_GROUP");
            defaultGroup.setActive("Y");


            List<String> attributeNamesMetadata = data.get(0);
            List<String> attributeTypesMetadata = data.get(1);
            List<String> familyAttributeGroupMetadata = data.get(2);
            List<String> familyAttributeSubgroupMetadata = data.get(3);
            List<String> attributeLevelMetadata = data.get(4);
            int numOfMetadataRows = 5;
            String productFamily = "";
            List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());
            Map<String, Set<String>> familyAttributes = getFamilyAttributes(data);

            List<List<FamilyAttribute>> familyAttributesGrid = new ArrayList<>();

            //Step 1
            //Go through each variant rows
            for (int row = 0; row < variantsData.size(); row++) {
                familyAttributesGrid.add(new ArrayList<>());
                String productId = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID"));
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID"));
                String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
                String categoryId = variantsData.get(row).get(attributeNamesMetadata.indexOf("Category"));
                String style = variantsData.get(row).get(attributeNamesMetadata.indexOf("Style"));
                String pricing = variantsData.get(row).get(attributeNamesMetadata.indexOf("Pricing"));
                if(categoryId.equals("FOLDERS")) {continue;}
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }
                resetLookupMap();
                familyService.getAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                familyService.getParentAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));


                /*if(isNotEmpty(categoryId) && !categoryService.get(categoryId, false).isPresent()) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(categoryId);
                    categoryDTO.setCategoryName(categoryId);
                    categoryDTO.setGroup("CREATE");
                    categoryService.create(categoryDTO);
                    LOGGER.info("======>" + categoryId);
                }*/

                if(isNotEmpty(style) && !categoryService.get(ID.EXTERNAL_ID(style), false).isPresent()) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(style);
                    categoryDTO.setCategoryName(style);
                    categoryDTO.setGroup("CREATE");
                    categoryService.create(categoryDTO);
                    LOGGER.info("======>" + style);
                }



                Family family = null;

                //Get the family in stance, if one exists. Otherwise create a new one

                Optional<Family> _family = familyService.get(ID.EXTERNAL_ID(familyId), false);

                if (!_family.isPresent()) {
                    family = new Family();
                    family.setActive("Y");
                    family.setFamilyName(familyId);
                    family.setFamilyId(familyId);
                    if (familyService.validate(family, new HashMap<>(), Family.CreateGroup.class).isEmpty()) {
                        family = familyService.create(family);
                    }
                } else {
                    family = _family.get();
                }

                familyAttributesGrid.get(row).add(null);//for the first empty column
                //Go through each column starting from the second column and add the attribute to the collection, if it is of known type and won't already exists
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    familyAttributesGrid.get(row).add(null);
                    String cellValue = variantsData.get(row).get(col);
                    String attributeName = attributeNamesMetadata.get(col);
                    int attributeLevel = (int)Double.parseDouble(attributeLevelMetadata.get(col));
                    if(!familyAttributes.get(familyId).contains(attributeName)) {
                        continue;
                    }
                    //check to see if the metadata attribute type is one of the known types
                    boolean isAttribute = availableAttributeTypes.contains(attributeTypesMetadata.get(col));

                    Attribute attribute = null;

                    if (isAttribute) {

                        attribute = new Attribute();
                        attribute.setActive("Y");
                        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                        attribute.setUiType(Attribute.UIType.get(attributeTypesMetadata.get(col)));
                        attribute.setName(attributeName);
                        LOGGER.info(row + " :: Attribute---> " + attribute.toString());

                        //Add the attribute, if it won't exists already in the collection
                        if(!attributeCollection.getAttributes().containsKey(AttributeGroup.DEFAULT_GROUP_ID)
                                || attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().isEmpty()
                                || !attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().containsKey(ValidatableEntity.toId(attribute.getName()))) {
                            attributeCollection.addAttribute(attribute);
                        }
                        //Get the fully orchestrated attribute corresponding to the attribute name
                        attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attribute.getName()));
                        boolean isSelectable = ConvertUtil.toBoolean(attribute.getSelectable());
                        //########################################################################################################################

                        String familyAttributeGroupName = familyAttributeGroupMetadata.get(col);
                        String familyAttributeSubgroupName = isNotEmpty(familyAttributeGroupName) ? familyAttributeSubgroupMetadata.get(col) : "";

                        String lookupKey = isNotNull(familyAttributeGroupName) ? familyAttributeGroupName.trim() : "";
                        if(isNotEmpty(lookupKey) && isNotEmpty(familyAttributeSubgroupName)) {
                            lookupKey += " > " + familyAttributeSubgroupName.trim();
                        }



                        //TODO - Replace lookup with attributeName
                        if(!family.getAllAttributesMap().containsKey(attribute.getId())) {
                            boolean updateLookup = false;
                            FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();
                            familyAttributeGroup.setActive("Y");
                            if(familyAttributeGroupLookUp.containsKey(lookupKey)) { //Existing group
                                familyAttributeGroup.setFullId(((String)familyAttributeGroupLookUp.get(lookupKey)));
                            } else { //New Group
                                if(isEmpty(familyAttributeSubgroupName)) { //New Master Group
                                    familyAttributeGroup.setMasterGroup("Y");
                                } else { // New Subgroup
                                    FamilyAttributeGroup parentGroup = new FamilyAttributeGroup();
                                    parentGroup.setActive("Y");
                                    parentGroup.setId((String)familyAttributeGroupLookUp.get(familyAttributeGroupName + "^"));
                                    familyAttributeGroup.setParentGroup(parentGroup);
                                }
                                familyAttributeGroup.setName(isNotEmpty(familyAttributeSubgroupName) ? familyAttributeSubgroupName : familyAttributeGroupName);
                                updateLookup = true;
                            }

                            FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeName, null);
                            familyAttributeDTO.setActive("Y");
                            familyAttributeDTO.setCollectionId(attributeCollectionId);
                            familyAttributeDTO.setUiType(attribute.getUiType());
                            familyAttributeDTO.setScopable("Y");
                            familyAttributeDTO.setAttributeId(attribute.getFullId());
                            familyAttributeDTO.getScope().put(channelId, FamilyAttribute.Scope.OPTIONAL);
                            familyAttributeDTO.setAttributeGroup(familyAttributeGroup);

//                            Attribute attribute = attributeCollectionService.findAttribute(familyAttribute.getCollectionId(), familyAttribute.getAttributeId()).get();
                            family.setGroup("ATTRIBUTES");
                            familyAttributeDTO.setAttribute(attribute);
                            family.addAttribute(familyAttributeDTO);
                            familyService.update(ID.EXTERNAL_ID(familyId), family);
                            family = familyService.get(ID.EXTERNAL_ID(familyId), false).get();
                            if(updateLookup) {//TODO
                                resetLookupMap();
                                familyService.getAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                                familyService.getParentAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));
                            }
                        }

                        FamilyAttribute familyAttribute = family.getAllAttributesMap().get(attribute.getId());
                        familyAttributesGrid.get(row).set(col, familyAttribute);
                        if(!familyVariantGroups.containsKey(familyId)) {
                            familyVariantGroups.put(familyId, new HashSet<>());
                        }
                        if(attributeLevel == 1) {
                            familyVariantGroups.get(familyId).add(familyAttribute.getId());
                        }
                        //########################################################################################################################

                        //If the current attribute is selectable, add any new attribute options, if any
                        if(isSelectable) {
                            String attributeOptionValue = cellValue;

                            // If the attribute option won't exist already for the attribute, add the attribute option
                            if (isNotEmpty(attributeOptionValue)) {
                                if(!attribute.getOptions().containsKey(ValidatableEntity.toId(attributeOptionValue)))
                                {
                                    AttributeOption attributeOption = new AttributeOption();
                                    attributeOption.setCollectionId(attributeCollectionId);
                                    attributeOption.setAttributeId(attribute.getFullId());
                                    attributeOption.setValue(attributeOptionValue);
                                    attributeOption.setActive("Y");
                                    attributeOption.orchestrate();
                                    attribute.getOptions().put(ValidatableEntity.toId(attributeOptionValue), attributeOption);
                                }

                                AttributeOption attributeOption = attribute.getOptions().get(ValidatableEntity.toId(attributeOptionValue));
                                if(!familyAttribute.getOptions().containsKey(attributeOption.getId())) {
                                    FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                                    familyAttributeOption.setActive("Y");
                                    familyAttributeOption.setValue(attributeOption.getValue());
                                    familyAttributeOption.setId(attributeOption.getId());
                                    familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                                    familyAttribute.getOptions().put(attributeOption.getId(), familyAttributeOption);
                                    family.setGroup("ATTRIBUTES");
                                    familyService.update(ID.EXTERNAL_ID(familyId), family);
                                    family = familyService.get(ID.EXTERNAL_ID(familyId), false).get();
                                }
                            }
                        }

                    }
                }
            }
            //Update attribute collection with the newly added attributes and corresponding attribute options
            attributeCollectionService.update(ID.EXTERNAL_ID(attributeCollectionId), attributeCollection);

            //Attribute Collection updated with the new attributes and new options

            familyVariantGroups.forEach((_familyId, variantAttributeIds) ->
                    familyService.get(ID.EXTERNAL_ID(_familyId), false).ifPresent(family1 -> {
                        VariantGroup variantGroup = null;
                        if(!family1.getVariantGroups().isEmpty() && family1.getVariantGroups().containsKey(channelId)) {
                            variantGroup = family1.getVariantGroups().get(family1.getChannelVariantGroups().get(channelId));
                        } else {
                            variantGroup = new VariantGroup();
                            variantGroup.setName("Color Name");
                            variantGroup.setId("COLOR_NAME");
                            variantGroup.setActive("Y");
                            variantGroup.setLevel(1);
                            variantGroup.setFamilyId(_familyId);
                            variantGroup.getVariantAxis().put(1, Arrays.asList("COLOR_NAME"));
                        }
                        variantGroup.getVariantAttributes().put(1, new ArrayList<>(variantAttributeIds));
                        family1.getVariantGroups().put(variantGroup.getId(), variantGroup);
                        family1.getChannelVariantGroups().put(channelId, variantGroup.getId());
                        family1.setGroup("VARIANT_GROUPS");
                        familyService.update(ID.EXTERNAL_ID(family1.getFamilyId()), family1);
                    })
            );

            final int[] $row = {0}, $col = {0};
            for (int row = 0; row < variantsData.size(); row++) {
                $row[0] = row;
                String productId = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID"));
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID"));
                String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
                String categoryId = variantsData.get(row).get(attributeNamesMetadata.indexOf("Category"));
                String style = variantsData.get(row).get(attributeNamesMetadata.indexOf("Style"));
                String pricing = variantsData.get(row).get(attributeNamesMetadata.indexOf("Pricing"));
                if(categoryId.equals("FOLDERS")) {continue;}
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }
                Optional<Product> _product = productService.get(ID.EXTERNAL_ID(productId), false);


                Map<String, Object> productAttributesMap = new HashMap<>();

                Map<String, Object> variantAttributesMap = new HashMap<>();
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    $col[0] = col;
                    String cellValue = variantsData.get(row).get(col);
                    if(cellValue == null) {
                        cellValue = "";
                    }
                    String attributeName = attributeNamesMetadata.get(col);
                    int attributeLevel = (int) Double.parseDouble(attributeLevelMetadata.get(col));

                    if(familyAttributes.get(familyId).contains(attributeName)) {
                        FamilyAttribute familyAttribute = familyAttributesGrid.get(row).get(col);
                        if(ConvertUtil.toBoolean(familyAttribute.getSelectable())) {
                            Optional<FamilyAttributeOption> cellValueOption = familyAttributesGrid.get(row).get(col).getOptions().values().stream().filter(e -> e.getValue().equals(variantsData.get($row[0]).get($col[0]))).findFirst();
                            if(cellValueOption.isPresent()) {
                                cellValue = cellValueOption.get().getId();
                            }
                        }
                        if(attributeLevel == 0) {
                            productAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), cellValue);
                        } else {
                            variantAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), cellValue);

                        }
                    }
                }
                if(isEmpty(variantAttributesMap.get("COLOR_NAME"))) {
                    continue;
                }
                Product product = null;
                if(!_product.isPresent()) {
                    Product productDTO = new Product();
                    productDTO.setProductId(productId);
                    productDTO.setProductName(productName);
                    productDTO.setProductFamilyId(familyId);
                    productService.create(productDTO);
                    product = productService.get(ID.EXTERNAL_ID(productId), false).get();
                    product.setProductId(productId);
                    product.setChannelId(channelId);
                    product.setGroup("DETAILS");
                    product.setAttributeValues(productAttributesMap);
                    if(productService.validate(product, new HashMap<>(), Product.DetailsGroup.class).isEmpty()) {
                        productService.update(ID.EXTERNAL_ID(productId), product);
                        product = productService.get(ID.EXTERNAL_ID(productId), false).get();
                        product.setProductId(productId);
                    }
                } else {
                    product = _product.get();
                    product.setChannelId(channelId);
                }
                String idOfProduct = product.getId();
//                Category category = categoryService.get(categoryId, false).get();
                Category category = categoryService.get(ID.EXTERNAL_ID(style), false).get();
                List<CategoryProduct> categoryProducts = categoryService.getCategoryProducts(ID.EXTERNAL_ID(category.getCategoryId()), 0, 300, null, false).getContent();
                CategoryProduct categoryProduct = categoryProducts.stream().filter(categoryProduct1 -> categoryProduct1.getProductId().equals(idOfProduct)).findFirst().orElse(null);
                if(isNull(categoryProduct)) {
//                    CategoryProduct categoryProduct1 = new CategoryProduct(category.getId(), product.getId(), 0);
                    categoryService.addProduct(ID.EXTERNAL_ID(category.getCategoryId()), ID.EXTERNAL_ID(product.getProductId()));
//                    productService.addCategory(product.getProductId(), category.getCategoryId());

                }
                String variantIdentifier = "COLOR_NAME|" + (String)variantAttributesMap.get("COLOR_NAME"); //TODO - change this to support multiple axis atribute values
                Family productVariantFamily = product.getProductFamily();
                String variantGroupId = productVariantFamily.getChannelVariantGroups().get(channelId);
                VariantGroup variantGroup = productVariantFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup) && isNotEmpty(variantGroup.getVariantAxis().get(1))) {
                    List<String> axisAttributeTokens = StringUtil.splitPipeDelimitedAsList(variantIdentifier);
                    Map<String, String> axisAttributes = new HashMap<>();
                    StringBuilder tempName = new StringBuilder();
                    for (int i = 0; i < axisAttributeTokens.size(); i = i + 2) {
                        axisAttributes.put(axisAttributeTokens.get(i), axisAttributeTokens.get(i + 1));
                        String nameToken = axisAttributeTokens.get(i + 1);
                        FamilyAttribute axisAttribute = productVariantFamily.getAllAttributesMap().get(axisAttributeTokens.get(i));
                        if(isNotEmpty(axisAttribute) && axisAttribute.getOptions().containsKey(axisAttributeTokens.get(i + 1))) {
                            nameToken = axisAttribute.getOptions().get(axisAttributeTokens.get(i + 1)).getValue();
                        }
                        tempName.append(tempName.length() > 0 ? " - " : "").append(nameToken);
                    }
                    ProductVariant productVariant = new ProductVariant(product);
                    productVariant.setProductVariantName(product.getProductName() + " - " + tempName.toString());
                    productVariant.setProductVariantId(variantId);
                    productVariant.setChannelId(channelId);
                    productVariant.setAxisAttributes(axisAttributes);
                    productVariant.setActive("N");
                    productVariantService.create(productVariant);
                    productVariant.setLevel(1); //TODO - change for multi level variants support
                    setVariantAttributeValues(productVariant, variantAttributesMap);
                    if(productVariantService.validate(productVariant, new HashMap<>(), ProductVariant.DetailsGroup.class).isEmpty()) {
                        productVariant.setGroup("DETAILS");
                        productVariantService.update(ID.EXTERNAL_ID(variantId), productVariant);
                    }
                    if(isNotEmpty(pricing)) {
                        try {
                            productVariant.setGroup("PRICING_DETAILS");
                            setPricingDetails(productVariant, pricing);
                            productVariantService.update(ID.EXTERNAL_ID(variantId), productVariant);
                        } catch (Exception e) {
                            LOGGER.info(pricing);
                            LOGGER.info(productVariant.getProductVariantId());
                            e.printStackTrace();
                        }
                    }
                }
                if(row % 100 == 0) {
                    LOGGER.info(row + " of " + variantsData.size());
                } else {
                    LOGGER.info(".");
                }
            }
        });



        
        return true;
    }

    private void setVariantAttributeValues(ProductVariant productVariantDTO, Map<String, Object> attributesMap) {
        productService.get(ID.EXTERNAL_ID(productVariantDTO.getProduct().getProductId()), false).ifPresent(product -> {
            product.setChannelId(productVariantDTO.getChannelId());
            productVariantDTO.setProduct(product);
            Family productFamily = product.getProductFamily();
            String variantGroupId = productFamily.getChannelVariantGroups().get(productVariantDTO.getChannelId());
            VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
            if(isNotEmpty(variantGroup)) {
                List<String> variantAttributeIds = variantGroup.getVariantAttributes().get(productVariantDTO.getLevel());
                variantAttributeIds.forEach(attributeId -> {
                    if(attributesMap.containsKey(attributeId)) {
                        productVariantDTO.getVariantAttributes().put(attributeId, attributesMap.get(attributeId));
                    }
                });
            }
        });
    }

    private Map<String, Set<String>> getFamilyAttributes(List<List<String>> data) {

        List<String> attributeNamesMetadata = data.get(0);
        List<String> attributeTypesMetadata = data.get(1);
        int numOfMetadataRows = 5;
        List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());

        Map<String, Set<String>> familyAttributes = new HashMap<>();

        for (int row = 0; row < variantsData.size(); row++) {
            String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
            for(int col = 1; col < attributeNamesMetadata.size(); col ++) {
                String attributeName = attributeNamesMetadata.get(col);
                String attributeValue = variantsData.get(row).get(col);
                if(isNotEmpty(attributeValue) && availableAttributeTypes.contains(attributeTypesMetadata.get(col))) {
                    if(!familyAttributes.containsKey(familyId)) {
                        familyAttributes.put(familyId, new HashSet<>());
                    }
                    familyAttributes.get(familyId).add(attributeName);
                }
            }
        }
        return familyAttributes;
    }

    static Map<String, String> atttributeIdMap = new HashMap<>();
    static {
        atttributeIdMap.put("0", "PLAIN");
        atttributeIdMap.put("1", "1_COLOR");
        atttributeIdMap.put("2", "2_COLOR");
        atttributeIdMap.put("4", "4_COLOR");
    }

    private void setPricingDetails(ProductVariant productVariant, String pricing) {
        try {
            Map<String, Map<Integer, BigDecimal>> attributesPricingMap = new HashMap<>();
            List<String> list = new ArrayList<String>(Arrays.asList(pricing.split(";")));

            list.forEach(s -> {
                List<String> attributePricing = new ArrayList<>(Arrays.asList(s.split(",")));
                String attributeId = atttributeIdMap.get(attributePricing.get(1));
                if(attributeId != null) {
                    if (!attributesPricingMap.containsKey(attributeId)) {
                        attributesPricingMap.put(attributeId, new TreeMap<>());
                    }
                    String qty = attributePricing.get(0);
                    String qtyPrice = attributePricing.get(2);

                    attributesPricingMap.get(attributeId).put(new Integer(qty), new BigDecimal(qtyPrice));
                }

            });
            productVariant.setPricingDetails(attributesPricingMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
