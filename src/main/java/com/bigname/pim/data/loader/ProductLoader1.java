package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.ConvertUtil;
import com.bigname.pim.util.POIUtil;
import com.m7.common.util.ConversionUtil;
import com.m7.common.util.StringUtil;
import com.m7.xcore.domain.Entity;
import com.m7.xcore.domain.ValidatableEntity;
import com.m7.xcore.util.FindBy;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.common.util.StringUtil.split;
import static com.m7.common.util.StringUtil.trim;
import static com.m7.common.util.ValidationUtil.*;


/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class ProductLoader1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductLoader1.class);

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AssetLoader assetLoader;

    //The attributeCollectionId that needs to be used for the import
    private String attributeCollectionId = "ENVELOPES";

    //These are the five supported attribute types in the current version of PIM. Each importing attribute should be of one of this type
    private List<String> availableAttributeTypes = Arrays.asList("INPUTBOX", "TEXTAREA", "DROPDOWN", "MULTI_SELECT", "YES_NO");

    //Map for family attribute's groupId and parentGroupIdLookup
    private Map<String, Object> familyAttributeGroupLookUp = new LinkedHashMap<>();
    {
        resetLookupMap();
    }

    //Method to reset the lookup map with default groups and parentGroups
    private void resetLookupMap() {
        familyAttributeGroupLookUp = new LinkedHashMap<>();
        familyAttributeGroupLookUp.put("", "DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details", "DETAILS_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Product Features", "FEATURES_GROUP|DEFAULT_GROUP|DEFAULT_GROUP");
        familyAttributeGroupLookUp.put("Details^", "DETAILS_GROUP");
        familyAttributeGroupLookUp.put("Product Features^", "FEATURES_GROUP");
    }

    /*public boolean load(String filePath) {
        String channelId = "ECOMMERCE";

        //Product variant data WITH metadata
        List<List<String>> data = POIUtil.readData(filePath);

        //Create the attributeCollection if it won't already exists
        if(!attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).isPresent()) {
            AttributeCollection attributeCollection = new AttributeCollection();
            attributeCollection.setCollectionId(attributeCollectionId);
            attributeCollection.setCollectionName("Envelopes Attributes Collection");
            attributeCollection.setActive("Y");
            attributeCollectionService.create(attributeCollection);
        }

        //Map of all existing families
        Map<String, Family> families = familyService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing categories
        Map<String, Category> existingCategories = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing and newly creating categories
        Map<String, Category> newCategories = new HashMap<>();

        //Map to store variant level attributes for each families
        Map<String, Set<String>> familyVariantGroups = new LinkedHashMap<>();

        //Load the attributeCollection from the database for the given collectionId
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {
            // The metadata row containing the attribute names, this will be first row in the data sheet
            List<String> attributeNamesMetadata = data.get(0);

            // The metadata row containing the attributeType, this will be second row in the data sheet
            List<String> attributeTypesMetadata = data.get(1);

            // The metadata row containing the familyAttributeGroup name, this will be third row in the data sheet
            List<String> familyAttributeGroupMetadata = data.get(2);

            // The metadata row containing the familyAttributeSubGroup name, this will be fourth row in the data sheet
            List<String> familyAttributeSubgroupMetadata = data.get(3);

            // The metadata row containing the attribute level value of each attribute (0 - Product Level & 1 - Variant Level), this will be fourth row in the data sheet
            List<String> attributeLevelMetadata = data.get(4);

            //Number of metadata rows in the data sheet
            int numOfMetadataRows = 5;

            //Product variants data WITHOUT metadata, this will be a sublist of the complete data without the metadata rows
            List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());

            Map<String, List<List<String>>> variantsAssets = getProductAssets();

            //Map of valid attributeNames, grouped by familyId
            Map<String, Set<String>> familyAttributes = getFamilyAttributes(data);

            //Create all new family instances and add them to the families map
            familyAttributes.keySet().forEach(familyId -> {
                if(!families.containsKey(familyId)) {
                    Family family = new Family();
                    family.setActive("Y");
                    family.setFamilyName(familyId);
                    family.setFamilyId(familyId);
                    families.put(familyId, familyService.create(family));
                }
            });

            //A collection attribute instances corresponding to each of the valid attribute values in the data sheet
            List<List<FamilyAttribute>> familyAttributesGrid = new ArrayList<>();

            //Process each of the variant data row in the data sheet
            for(int row = 0; row < variantsData.size(); row ++) {

                //The list to hold attribute instances for the current row
                familyAttributesGrid.add(new ArrayList<>());

                //Get the non-attribute values for the current productVariant from the variantsData

                //ProductId
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")), true).toUpperCase();
                //ProductName
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                //VariantId
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")), true).toUpperCase();
                //FamilyId
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")), true).toUpperCase();
                //CategoryId
                String categoryId = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Category")), true).toUpperCase();
                //Style
                String style = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Style")), true).toUpperCase();

                //TODO - TEMP CODE - Skip folders category
                if(categoryId.equals("FOLDERS")) {continue;}

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                //Usually, Envelopes.com uses Categories as root categories and styles as subCategories

                //If this is a new category, create a new category instance and add it to the newCategories map
                if(isNotEmpty(categoryId) && !existingCategories.containsKey(categoryId) && !newCategories.containsKey(categoryId)) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(categoryId);
                    categoryDTO.setCategoryName(categoryId);
                    categoryDTO.setGroup("CREATE");
                    newCategories.put(categoryDTO.getCategoryId(), categoryDTO);
                    LOGGER.info("New Root Category ======> " + categoryId);
                }

                //If this is a new style, create a new category instance and add it to the newCategories map
                if(isNotEmpty(style) && !existingCategories.containsKey(style) && !newCategories.containsKey(style)) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(style);
                    categoryDTO.setCategoryName(style);
                    categoryDTO.setGroup("CREATE");
                    newCategories.put(categoryDTO.getCategoryId(), categoryDTO);
                    LOGGER.info("New Style Category ======> " + style);
                }




                Family family = families.getOrDefault(familyId, null);

                if(family == null) {
                    family = new Family();
                    family.setActive("Y");
                    family.setFamilyName(familyId);
                    family.setFamilyId(familyId);
                }

                resetLookupMap();
                getAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                getParentAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));


                //The first column in the variants data is always empty
                familyAttributesGrid.get(row).add(null);//for the first empty column

                *//*
                Go through each column starting from the second column and add the attribute to the corresponding family's
                attributes collection, if it is of known type and won't already exists
                *//*

                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    *//*
                    Add an empty attribute instance for the current column in the familyAttributesGrid to start with,
                    will be replaced with the proper attribute instance down the line
                     *//*
                    familyAttributesGrid.get(row).add(null);

                    //AttributeValue, which is the current cellValue
                    String cellValue = variantsData.get(row).get(col);

                    //AttributeName
                    String attributeName = attributeNamesMetadata.get(col);

                    //AttributeLevel - 0 for product level and 1 for variant level
                    int attributeLevel = (int)Double.parseDouble(attributeLevelMetadata.get(col));

                    //FamilyAttributesMap contains attributeNames of supported attribute types grouped by familyId.
                    //Skip this attribute, if the attributeName is of a not supported attributeType
                    if(!familyAttributes.get(familyId).contains(attributeName)) {
                        continue;
                    }

                    //Add the attribute to the attributeCollection, if it won't exists already
                    if(isEmpty(attributeCollection.getAttributes())
                            || isEmpty(attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID))
                            || isEmpty(attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attributeName)))) {

                        Attribute attribute = new Attribute();
                        attribute.setActive("Y");
                        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                        attribute.setUiType(Attribute.UIType.get(attributeTypesMetadata.get(col)));
                        attribute.setName(attributeName);
                        LOGGER.info(row + " :: Attribute---> " + attribute.toString());
                        attributeCollection.addAttribute(attribute);
                    }

                    //Get the fully orchestrated attribute corresponding to the attribute name
                    Attribute attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attributeName));

                    //If the attribute is of selectable type, one with selectable options
                    boolean isSelectable = ConvertUtil.toBoolean(attribute.getSelectable());

                    //Family attribute's groupName
                    String familyAttributeGroupName = familyAttributeGroupMetadata.get(col);

                    //Family attribute's subGroupName
                    String familyAttributeSubgroupName = isNotEmpty(familyAttributeGroupName) ? familyAttributeSubgroupMetadata.get(col) : "";


                    String lookupKey = isNotNull(familyAttributeGroupName) ? familyAttributeGroupName.trim() : "";

                    if(isNotEmpty(lookupKey) && isNotEmpty(familyAttributeSubgroupName)) {
                        lookupKey += " > " + familyAttributeSubgroupName.trim();
                    }



                    //TODO - Replace lookup with attributeName
                    *//*
                    If this is a new family attribute, add the attribute to the corresponding attributeGroup and attributeSubGroup.
                    If the attributeGroup and/or attributeSubGroup won't exist, add them as well
                    *//*
                    if(!family.getAllAttributesMap(false).containsKey(attribute.getId())) {
                        //Starting with default familyAttributeGroups and subGroups, so lets assume not to update the lookup map
                        boolean updateLookup = false;

                        //The group for this new familyAttribute
                        FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();

                        //Set the new group to active
                        familyAttributeGroup.setActive("Y");

                        //If the lookupMap has the lookup key, this is an existing group
                        if(familyAttributeGroupLookUp.containsKey(lookupKey)) { //Existing group
                            //Set the new group's fullId to the same as that the existing group's fullId from the lookupMap
                            familyAttributeGroup.setFullId(((String)familyAttributeGroupLookUp.get(lookupKey)));
                        } else { //New Group
                            //Check to see if this is a new masterGroup or a subGroup
                            if(isEmpty(familyAttributeSubgroupName)) { //New Master Group
                                familyAttributeGroup.setMasterGroup("Y");
                            } else { // New Subgroup
                                //Create the parentGroup for the subGroup
                                FamilyAttributeGroup parentGroup = new FamilyAttributeGroup();
                                //Mark as active
                                parentGroup.setActive("Y");

                                //If the masterGroup is also a new one, the lookup map won't contain the parent group's lookupKey - TODO - verify this logic
                                if(!familyAttributeGroupLookUp.containsKey(familyAttributeGroupName + "^")) { //New masterGroup
                                    //Set the name of the parentGroup
                                    parentGroup.setName(familyAttributeGroupName);
                                    //Set the id of the parentGroup
                                    parentGroup.setId(parentGroup.getFullId());
                                } else {
                                    //Set the id of this parentGroup from the lookupMap
                                    parentGroup.setId((String) familyAttributeGroupLookUp.get(familyAttributeGroupName + "^"));
                                }
                                //Set the parentGroup for the subGroup
                                familyAttributeGroup.setParentGroup(parentGroup);
                            }
                            //Set the name of the newly creating group
                            familyAttributeGroup.setName(isNotEmpty(familyAttributeSubgroupName) ? familyAttributeSubgroupName : familyAttributeGroupName);
                            //Since we added a new group, the lookup map needs to be updated
                            updateLookup = true;
                        }

                        //Create the new familyAttribute instance
                        FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeName, null);
                        familyAttributeDTO.setActive("Y");
                        familyAttributeDTO.setCollectionId(attributeCollectionId);
                        familyAttributeDTO.setUiType(attribute.getUiType());
                        familyAttributeDTO.setScopable("Y");
                        familyAttributeDTO.setAttributeId(attribute.getFullId());
                        familyAttributeDTO.getScope().put(channelId, FamilyAttribute.Scope.OPTIONAL);
                        familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
                        familyAttributeDTO.setAttribute(attribute);

                        //Add the family attribute to the family
                        family.addAttribute(familyAttributeDTO);

                        //If a new group or subGroup is added as part of the new familyAttributeCreation, we need to update the lookup map
                        if(updateLookup) {
                            resetLookupMap();
                            getAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                            getParentAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));
                        }
                    }

                    //Get the fully orchestrated familyAttribute corresponding to the newly creating attribute - TODO - verify if this is required
                    FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attribute.getId());

                    //Add it to the current index in attributesGrid for later lookup
                    familyAttributesGrid.get(row).set(col, familyAttribute);
                    //If the familyVariantGroups won't contain an entry for the current familyId, add an empty one
                    if(!familyVariantGroups.containsKey(familyId)) {
                        familyVariantGroups.put(familyId, new HashSet<>());
                    }
                    //If the current attribute is for variantLevel, add the current attribute's id to the familyVariantGroups map
                    if(attributeLevel == 1) {
                        familyVariantGroups.get(familyId).add(familyAttribute.getId());
                    }

                    //If the current attribute is selectable, add any new attribute options, if any
                    if(isSelectable) {
                        String attributeOptionValue = cellValue;

                        // If the attribute option won't exist already for the attribute, add the attribute option to the attribute
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

                            // If the familyAttribute option won't exist already for the familyAttribute, add the familyAttribute option to the familyAttribute
                            AttributeOption attributeOption = attribute.getOptions().get(ValidatableEntity.toId(attributeOptionValue));
                            if(!familyAttribute.getOptions().containsKey(attributeOption.getId())) {
                                FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                                familyAttributeOption.setActive("Y");
                                familyAttributeOption.setValue(attributeOption.getValue());
                                familyAttributeOption.setId(attributeOption.getId());
                                familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                                familyAttribute.getOptions().put(attributeOption.getId(), familyAttributeOption);
                            }
                        }
                    }


                }

            }

            attributeCollection.setGroup("ATTRIBUTES");
            //Update attribute collection with the newly added attributes and corresponding attribute options
            attributeCollectionService.update(attributeCollectionId, FindBy.EXTERNAL_ID, attributeCollection);

            //Attribute Collection updated with the new attributes and new options

            //Update the familyVariantGroups for the corresponding families
            familyVariantGroups.forEach((_familyId, variantAttributeIds) -> {
                Family family = families.get(_familyId);
                VariantGroup variantGroup = null;
                if (!family.getVariantGroups().isEmpty() && family.getVariantGroups().containsKey(channelId)) {
                    variantGroup = family.getVariantGroups().get(family.getChannelVariantGroups().get(channelId));
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
                family.getVariantGroups().put(variantGroup.getId(), variantGroup);
                family.getChannelVariantGroups().put(channelId, variantGroup.getId());
            });

            //TODO - Handle creation of related category mapping
            newCategories.forEach((categoryId, category) -> existingCategories.put(categoryId, categoryService.create(category)));
            newCategories.clear();

            families.forEach((familyId, family) -> family.setGroup("ATTRIBUTES", "VARIANT_GROUPS"));

            familyService.saveAll(families.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList())).forEach(family -> families.put(family.getFamilyId(), family));

            // TODO - Batch creation logic - not complete - Need to optimize the below portion
            *//*
            //Map of all existing products
            Map<String, Product> existingProducts = productService.getAll(null, false).stream().collect(Collectors.toMap(Product::getProductId, p -> p));
            existingProducts.forEach((k, product) -> product.setChannelId(channelId));

            //Map of new products
            Map<String, Product> newProducts = new HashMap<>();

            //Loop through each variants and check to se if the product already exists.
            //If not, create the product with productLevel Attributes
            final int[] $row1 = {0}, $col1 = {0};
            for (int row = 0; row < variantsData.size(); row++) {
                $row1[0] = row;
                String productId = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID"));
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID"));
                String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
                String categoryId = variantsData.get(row).get(attributeNamesMetadata.indexOf("Category"));

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                //TODO - TEMP CODE - Skip folders category
                if(categoryId.equals("FOLDERS")) {continue;}

                Map<String, Object> productAttributesMap = new HashMap<>();

                Map<String, Object> variantAttributesMap = new HashMap<>();
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    $col1[0] = col;
                    String cellValue = variantsData.get(row).get(col);
                    if(cellValue == null) {
                        cellValue = "";
                    }
                    String attributeName = attributeNamesMetadata.get(col);
                    int attributeLevel = (int) Double.parseDouble(attributeLevelMetadata.get(col));

                    if(familyAttributes.get(familyId).contains(attributeName)) {
                        FamilyAttribute familyAttribute = familyAttributesGrid.get(row).get(col);
                        if(ConvertUtil.toBoolean(familyAttribute.getSelectable())) {
                            Optional<FamilyAttributeOption> cellValueOption = familyAttributesGrid.get(row).get(col).getOptions().values().stream().filter(e -> e.getValue().equals(variantsData.get($row1[0]).get($col1[0]))).findFirst();
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

                    //TODO - make this conditional and dynamic
                    if(isEmpty(variantAttributesMap.get("COLOR_NAME"))) {
                        continue;
                    }

                    //If the product won't exist or not created yet, create a new product instance and add it to the newProductMap
                    if(!existingProducts.containsKey(productId)) {
                        Product productDTO = new Product();
                        productDTO.setProductId(productId);
                        productDTO.setChannelId(channelId);
                        productDTO.setProductName(productName);
                        productDTO.setProductFamilyId(familyId);
                        productDTO.setProductFamily(families.get(familyId));
                        productDTO.setAttributeValues(productAttributesMap);
                        if(!newProducts.containsKey(productId) && !existingProducts.containsKey(productId)) {
                            newProducts.put(productId, productDTO);
                        }
                    }
                }
            }

            //If there are new products that need to be created, create all of them and add move the newly created products to the existing products map
            if(!newProducts.isEmpty()) {
                productService.saveAll(newProducts.entrySet().stream()
                        .map(Map.Entry::getValue).collect(Collectors.toList()))
                        .forEach(product -> {
                            product.setChannelId(channelId);
                            existingProducts.put(product.getProductId(), product);
                        });


            }*//*

            //TODO - do batch insert of categoryProducts and productCategories

            *//*Map<Pair<String, String>, CategoryProduct> existingCategoryProducts = categoryService.getAllCategoryProducts()
                    .parallelStream().collect(Collectors.toMap(e -> Pair.with(e.getCategoryId(), e.getProductId()), e -> e));

            Map<Pair<String, String>, CategoryProduct> newCategoryProducts = new HashMap<>();*//*

            *//*final int[] $row = {0}, $col = {0};
            for (int row = 0; row < variantsData.size(); row++) {
                $row[0] = row;
                String productId = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID"));
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID"));
                String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
                String categoryId = variantsData.get(row).get(attributeNamesMetadata.indexOf("Category"));
                String style = variantsData.get(row).get(attributeNamesMetadata.indexOf("Style"));
                String pricing = variantsData.get(row).get(attributeNamesMetadata.indexOf("Pricing"));

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                //TODO - TEMP CODE - Skip folders category
                if(categoryId.equals("FOLDERS")) {continue;}

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

                    //TODO - make this conditional and dynamic
                    if(isEmpty(variantAttributesMap.get("COLOR_NAME"))) {
                        continue;
                    }

                    Product product = existingProducts.get(productId);
                    Category category = existingCategories.get(style);

                    //TODO - do batch insert of categoryProducts and productCategories

                    *//**//*Pair<String, String> key = Pair.with(category.getId(), product.getId());
                    if(!existingCategories.containsKey(key) && !newCategoryProducts.containsKey(key)) {
                        newCategoryProducts.put(key, new CategoryProduct(category.getId(), product.getId(), 0));
                    }*//**//*

                    String idOfProduct = product.getId();
                    List<CategoryProduct> categoryProducts = categoryService.getCategoryProducts(category.getCategoryId(), FindBy.EXTERNAL_ID, 0, 300, null, false).getContent();
                    CategoryProduct categoryProduct = categoryProducts.stream().filter(categoryProduct1 -> categoryProduct1.getProductId().equals(idOfProduct)).findFirst().orElse(null);
                    if(isNull(categoryProduct)) {
                        categoryService.addProduct(category.getCategoryId(), FindBy.EXTERNAL_ID, product.getProductId(), FindBy.EXTERNAL_ID);
                    }

                    String variantIdentifier = "COLOR_NAME|" + variantAttributesMap.get("COLOR_NAME"); //TODO - change this to support multiple axis atribute values
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
                        productVariant.setProduct(product);

                        List<String> variantAttributeIds = variantGroup.getVariantAttributes().get(productVariant.getLevel());
                        variantAttributeIds.forEach(attributeId -> {
                            if(variantAttributesMap.containsKey(attributeId)) {
                                productVariant.getVariantAttributes().put(attributeId, variantAttributesMap.get(attributeId));
                            }
                        });

                        if(productVariantService.validate(productVariant, new HashMap<>(), ProductVariant.DetailsGroup.class).isEmpty()) {
                            productVariant.setGroup("DETAILS");
                            productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariant);
                        }
                        if(isNotEmpty(pricing)) {
                            try {
                                productVariant.setGroup("PRICING_DETAILS");
                                setPricingDetails(productVariant, pricing);
                                productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariant);
                            } catch (Exception e) {
                                LOGGER.info(pricing);
                                LOGGER.info(productVariant.getProductVariantId());
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(row % 100 == 0) {
                    LOGGER.info(row + " of " + variantsData.size());
                } else {
                    LOGGER.info(".");
                }
            }*//*
            int assetFoundCount = 0;
            final int[] $row = {0}, $col = {0};
            for (int row = 0; row < variantsData.size(); row++) {
                $row[0] = row;
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")),true).toUpperCase();
                productId = convertCellValue("", productId);
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")),true).toUpperCase();
                variantId = convertCellValue("", variantId);
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")),true).toUpperCase();
                String categoryId = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Category")),true).toUpperCase();
                String style = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Style")),true).toUpperCase();
                String pricing = variantsData.get(row).get(attributeNamesMetadata.indexOf("Pricing"));
                if(categoryId.equals("FOLDERS")) {continue;}
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }
                Optional<Product> _product = productService.get(productId, FindBy.EXTERNAL_ID, false);


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
                        cellValue = convertCellValue(attributeName, cellValue);
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
                    productDTO.setActive("Y");
                    productDTO.setProductId(productId);
                    productDTO.setProductName(productName);
                    productDTO.setProductFamilyId(familyId);
                    productService.create(productDTO);
                    product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                    product.setChannelId(channelId);
                    product.setAttributeValues(productAttributesMap);
                    product.setGroup("DETAILS");
                    if(productService.validate(product, CollectionsUtil.toMap("id", productId), Product.DetailsGroup.class).isEmpty()) {
                        productService.update(productId, FindBy.EXTERNAL_ID, product);
                        product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                        product.setProductId(productId);
                    }
                } else {
                    product = _product.get();
                    product.setChannelId(channelId);
                }
                String idOfProduct = product.getId();
//                Category category = categoryService.get(categoryId, FindBy.EXTERNAL_ID, false).get();
                Category category = categoryService.get(style, FindBy.EXTERNAL_ID, false).get();
                List<CategoryProduct> categoryProducts = categoryService.getCategoryProducts(category.getCategoryId(), FindBy.EXTERNAL_ID, 0, 300, null, false).getContent();
                CategoryProduct categoryProduct = categoryProducts.stream().filter(categoryProduct1 -> categoryProduct1.getProductId().equals(idOfProduct)).findFirst().orElse(null);
                if(isNull(categoryProduct)) {
//                    CategoryProduct categoryProduct1 = new CategoryProduct(category.getId(), product.getId(), 0);
                    categoryService.addProduct(category.getCategoryId(), FindBy.EXTERNAL_ID, product.getProductId(), FindBy.EXTERNAL_ID);
//                    productService.addCategory(product.getProductId(), FindBy.EXTERNAL_ID, category.getCategoryId(), FindBy.EXTERNAL_ID);

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
                    productVariant.setActive("Y");
                    productVariantService.create(productVariant);
                    productVariant.setLevel(1); //TODO - change for multi level variants support
                    setVariantAttributeValues(productVariant, variantAttributesMap);
                    productVariant.setGroup("DETAILS");
                    if(productVariantService.validate(productVariant, CollectionsUtil.toMap("id", variantId), ProductVariant.DetailsGroup.class).isEmpty()) {
                        productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariant);
                    }
                    AssetCollection productAssetsCollection = assetLoader.createCollection("PRODUCT_ASSETS");
                    String assetFileName = variantId + ".png";
                    if(new File(assetLoader.getSourceLocation() + assetFileName).isFile()) {
                        VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                        VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                        VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                        productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[] {variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                        if(isEmpty(product.getChannelAssets())) {
                            productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[] {variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                            product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                        }
                    } else {
//                        System.out.println(assetFileName + " <======= Not Found");
                    }

                    if(variantsAssets.containsKey(variantId)) {
                        List<List<String>> variantAssets = variantsAssets.get(variantId);
                        for(int i = 0; i < variantAssets.size(); i++) {
                            List<String> variantAsset = variantAssets.get(i);
                            assetFileName = variantAsset.get(2) + ".png";
                            if(new File(assetLoader.getSourceLocation() + assetFileName).isFile()) {
                                VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                                VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                                VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                                productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[] {variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                                if(isEmpty(product.getChannelAssets())) {
                                    productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[] {variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                                    product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                                }
                                assetFoundCount ++;
                            } else {
//                                System.out.println(assetFileName + " <======= Asset Not Found");
                            }
                        }
                    }

                    if(isNotEmpty(pricing)) {
                        try {
                            productVariant.setGroup("PRICING_DETAILS");
                            setPricingDetails(productVariant, pricing);
                            productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariant);
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

            System.out.println("Asset found count:" + assetFoundCount);

            System.out.println("Done");
        });

        return true;
    }*/

    public boolean load1(String filePath) {
        String channelId = "ECOMMERCE";

        //Product variant data WITH metadata
        List<List<String>> data = POIUtil.readData(filePath);

        //Create the attributeCollection if it won't already exists
        if(!attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).isPresent()) {
            AttributeCollection attributeCollection = new AttributeCollection();
            attributeCollection.setCollectionId(attributeCollectionId);
            attributeCollection.setCollectionName("Envelopes Attributes Collection");
            attributeCollection.setActive("Y");
            attributeCollectionService.create(attributeCollection);
        }

        //Map of all existing families
        Map<String, Family> families = familyService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing categories
        Map<String, Category> existingCategories = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing and newly creating categories
        Map<String, Category> newCategories = new HashMap<>();

        //Map to store variant level attributes for each families
        Map<String, Set<String>> familyVariantGroups = new LinkedHashMap<>();

        //Load the attributeCollection from the database for the given collectionId
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {
            // The metadata row containing the attribute names, this will be first row in the data sheet
            List<String> attributeNamesMetadata = data.get(0);

            // The metadata row containing the attributeType, this will be second row in the data sheet
            List<String> attributeTypesMetadata = data.get(1);

            // The metadata row containing the parent attribute name, this will be third row in the data sheet
            List<String> parentAttributeMetadata = data.get(2);

            // The metadata row containing the delimiter characters for delimited attribute values, this will be fourth row in the data sheet
            List<String> delimitersMetadata = data.get(3);

            // The metadata row containing the familyAttributeGroup name, this will be fifth row in the data sheet
            List<String> familyAttributeGroupMetadata = data.get(4);

            // The metadata row containing the familyAttributeSubGroup name, this will be sixth row in the data sheet
            List<String> familyAttributeSubgroupMetadata = data.get(5);

            // The metadata row containing the attribute level value of each attribute (0 - Product Level & 1 - Variant Level), this will be seventh row in the data sheet
            List<String> attributeLevelMetadata = data.get(6);

            //Number of metadata rows in the data sheet
            int numOfMetadataRows = 7;

            //Product variants data WITHOUT metadata, this will be a sublist of the complete data without the metadata rows
            List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());

            Map<String, List<List<String>>> variantsAssets = getProductAssets();

            //Map of valid attributeNames, grouped by familyId
            Map<String, Set<String>> familyAttributes = getFamilyAttributes(data);

            //Create all new family instances and add them to the families map
            familyAttributes.keySet().forEach(familyId -> {
                if(!families.containsKey(familyId)) {
                    Family family = new Family();
                    family.setActive("Y");
                    family.setFamilyName(familyId);
                    family.setFamilyId(familyId);
                    families.put(familyId, familyService.create(family));
                }
            });

            //A collection attribute instances corresponding to each of the valid attribute values in the data sheet
            List<List<FamilyAttribute>> familyAttributesGrid = new ArrayList<>();

            //Process each of the variant data row in the data sheet
            for(int row = 0; row < variantsData.size(); row ++) {

                //The list to hold attribute instances for the current row
                familyAttributesGrid.add(new ArrayList<>());

                //Get the non-attribute values for the current productVariant from the variantsData

                //ProductId
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")), true).toUpperCase();
                //ProductName
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                //VariantId
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")), true).toUpperCase();
                //FamilyId
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")), true).toUpperCase();
                //CategoryId
                String categoryId = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Category")), true).toUpperCase();
                //Style
                String style = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Style")), true).toUpperCase();

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                //Usually, Envelopes.com uses Categories as root categories and styles as subCategories

                //If this is a new category, create a new category instance and add it to the newCategories map
                if(isNotEmpty(categoryId) && !existingCategories.containsKey(categoryId) && !newCategories.containsKey(categoryId)) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(categoryId);
                    categoryDTO.setCategoryName(categoryId);
                    categoryDTO.setGroup("CREATE");
                    newCategories.put(categoryDTO.getCategoryId(), categoryDTO);
                    LOGGER.info("New Root Category ======> " + categoryId);
                }

                //If this is a new style, create a new category instance and add it to the newCategories map
                if(isNotEmpty(style) && !existingCategories.containsKey(style) && !newCategories.containsKey(style)) {
                    Category categoryDTO = new Category();
                    categoryDTO.setActive("Y");
                    categoryDTO.setCategoryId(style);
                    categoryDTO.setCategoryName(style);
                    categoryDTO.setGroup("CREATE");
                    newCategories.put(categoryDTO.getCategoryId(), categoryDTO);
                    LOGGER.info("New Style Category ======> " + style);
                }




                Family family = families.getOrDefault(familyId, null);

                if(family == null) {
                    family = new Family();
                    family.setActive("Y");
                    family.setFamilyName(familyId);
                    family.setFamilyId(familyId);
                }

                resetLookupMap();
                getAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                getParentAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));


                //The first column in the variants data is always empty
                familyAttributesGrid.get(row).add(null);//for the first empty column

                /*
                Go through each column starting from the second column and add the attribute to the corresponding family's
                attributes collection, if it is of known type and won't already exists
                */

                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    /*
                    Add an empty attribute instance for the current column in the familyAttributesGrid to start with,
                    will be replaced with the proper attribute instance down the line
                     */
                    familyAttributesGrid.get(row).add(null);

                    //AttributeValue, which is the current cellValue
                    String cellValue = variantsData.get(row).get(col);

                    //Value delimiters for multi select/dependent options
                    String delimiters = delimitersMetadata.get(col);

                    //AttributeName
                    String attributeName = attributeNamesMetadata.get(col);

                    //AttributeLevel - 0 for product level and 1 for variant level
                    int attributeLevel = (int)Double.parseDouble(attributeLevelMetadata.get(col));

                    //FamilyAttributesMap contains attributeNames of supported attribute types grouped by familyId.
                    //Skip this attribute, if the attributeName is of a not supported attributeType
                    if(!familyAttributes.get(familyId).contains(attributeName)) {
                        continue;
                    }

                    //Add the attribute to the attributeCollection, if it won't exists already
                    if(isEmpty(attributeCollection.getAttributes())
                            || isEmpty(attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID))
                            || isEmpty(attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attributeName)))) {

                        Attribute attribute = new Attribute();
                        attribute.setActive("Y");
                        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                        attribute.setUiType(Attribute.UIType.get(attributeTypesMetadata.get(col)));
                        attribute.setName(attributeName);
                        LOGGER.info(row + " :: Attribute---> " + attribute.toString());
                        attributeCollection.addAttribute(attribute);
                    }

                    //Get the fully orchestrated attribute corresponding to the attribute name
                    Attribute attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attributeName));

                    //If the attribute is of selectable type, one with selectable options
                    boolean isSelectable = ConvertUtil.toBoolean(attribute.getSelectable());

                    //Family attribute's groupName
                    String familyAttributeGroupName = familyAttributeGroupMetadata.get(col);

                    //Family attribute's subGroupName
                    String familyAttributeSubgroupName = isNotEmpty(familyAttributeGroupName) ? familyAttributeSubgroupMetadata.get(col) : "";


                    String lookupKey = isNotNull(familyAttributeGroupName) ? familyAttributeGroupName.trim() : "";

                    if(isNotEmpty(lookupKey) && isNotEmpty(familyAttributeSubgroupName)) {
                        lookupKey += " > " + familyAttributeSubgroupName.trim();
                    }



                    //TODO - Replace lookup with attributeName
                    /*
                    If this is a new family attribute, add the attribute to the corresponding attributeGroup and attributeSubGroup.
                    If the attributeGroup and/or attributeSubGroup won't exist, add them as well
                    */
                    if(!family.getAllAttributesMap(false).containsKey(attribute.getId())) {
                        //Starting with default familyAttributeGroups and subGroups, so lets assume not to update the lookup map
                        boolean updateLookup = false;

                        //The group for this new familyAttribute
                        FamilyAttributeGroup familyAttributeGroup = new FamilyAttributeGroup();

                        //Set the new group to active
                        familyAttributeGroup.setActive("Y");

                        //If the lookupMap has the lookup key, this is an existing group
                        if(familyAttributeGroupLookUp.containsKey(lookupKey)) { //Existing group
                            //Set the new group's fullId to the same as that the existing group's fullId from the lookupMap
                            familyAttributeGroup.setFullId(((String)familyAttributeGroupLookUp.get(lookupKey)));
                        } else { //New Group
                            //Check to see if this is a new masterGroup or a subGroup
                            if(isEmpty(familyAttributeSubgroupName)) { //New Master Group
                                familyAttributeGroup.setMasterGroup("Y");
                            } else { // New Subgroup
                                //Create the parentGroup for the subGroup
                                FamilyAttributeGroup parentGroup = new FamilyAttributeGroup();
                                //Mark as active
                                parentGroup.setActive("Y");

                                //If the masterGroup is also a new one, the lookup map won't contain the parent group's lookupKey - TODO - verify this logic
                                if(!familyAttributeGroupLookUp.containsKey(familyAttributeGroupName + "^")) { //New masterGroup
                                    //Set the name of the parentGroup
                                    parentGroup.setName(familyAttributeGroupName);
                                    //Set the id of the parentGroup
                                    parentGroup.setId(parentGroup.getFullId());
                                } else {
                                    //Set the id of this parentGroup from the lookupMap
                                    parentGroup.setId((String) familyAttributeGroupLookUp.get(familyAttributeGroupName + "^"));
                                }
                                //Set the parentGroup for the subGroup
                                familyAttributeGroup.setParentGroup(parentGroup);
                            }
                            //Set the name of the newly creating group
                            familyAttributeGroup.setName(isNotEmpty(familyAttributeSubgroupName) ? familyAttributeSubgroupName : familyAttributeGroupName);
                            //Since we added a new group, the lookup map needs to be updated
                            updateLookup = true;
                        }

                        //Create the new familyAttribute instance
                        FamilyAttribute familyAttributeDTO = new FamilyAttribute(attributeName, null);
                        familyAttributeDTO.setActive("Y");
                        familyAttributeDTO.setCollectionId(attributeCollectionId);
                        familyAttributeDTO.setUiType(attribute.getUiType());
                        familyAttributeDTO.setScopable("Y");
                        familyAttributeDTO.setAttributeId(attribute.getFullId());
                        familyAttributeDTO.getScope().put(channelId, FamilyAttribute.Scope.OPTIONAL);
                        familyAttributeDTO.setAttributeGroup(familyAttributeGroup);
                        familyAttributeDTO.setAttribute(attribute);

                        String parentAttributeName = parentAttributeMetadata.get(col);
                        if(isNotEmpty(parentAttributeName)) {
                            String parentAttributeId = ValidatableEntity.toId(parentAttributeName);
                            FamilyAttribute parentAttribute = family.getAllAttributesMap().get(parentAttributeId);
                            if (isNotEmpty(parentAttributeId)) {
                                familyAttributeDTO.setParentAttributeId(parentAttribute != null ? parentAttribute.getFullId() : "FEATURES_GROUP|DEFAULT_GROUP|DEFAULT_GROUP|" + parentAttributeId);
                            }
                        }
                        //Add the family attribute to the family
                        family.addAttribute(familyAttributeDTO);

                        //If a new group or subGroup is added as part of the new familyAttributeCreation, we need to update the lookup map
                        if(updateLookup) {
                            resetLookupMap();
                            getAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1(), k.getValue0()));
                            getParentAttributeGroupsIdNamePair(family).forEach(k -> familyAttributeGroupLookUp.put(k.getValue1() + "^", k.getValue0()));
                        }
                    }

                    //Get the fully orchestrated familyAttribute corresponding to the newly creating attribute - TODO - verify if this is required
                    FamilyAttribute familyAttribute = family.getAllAttributesMap(false).get(attribute.getId());

                    //Add it to the current index in attributesGrid for later lookup
                    familyAttributesGrid.get(row).set(col, familyAttribute);
                    //If the familyVariantGroups won't contain an entry for the current familyId, add an empty one
                    if(!familyVariantGroups.containsKey(familyId)) {
                        familyVariantGroups.put(familyId, new HashSet<>());
                    }
                    //If the current attribute is for variantLevel, add the current attribute's id to the familyVariantGroups map
                    if(attributeLevel == 1) {
                        familyVariantGroups.get(familyId).add(familyAttribute.getId());
                    }

                    //If the current attribute is selectable, add any new attribute options, if any
                    if(isSelectable) {
                        // cellValue can either be a simple single value or delimited multiple values
                        List<String> attributeOptionValues = split(cellValue, delimiters.split(""));

                        //Add the single or multi option values
                        attributeOptionValues.forEach(attributeOptionValue -> {
                            // If the attribute option won't exist already for the attribute, add the attribute option to the attribute
                            if (isNotEmpty(attributeOptionValue)) {
                                if(!attribute.getOptions().containsKey(ValidatableEntity.toId(attributeOptionValue)))
                                {
                                    AttributeOption attributeOption = new AttributeOption();
                                    attributeOption.setCollectionId(attributeCollectionId);
                                    attributeOption.setValue(attributeOptionValue);
                                    attributeOption.setAttributeId(attribute.getFullId());
                                    attributeOption.setActive("Y");
                                    attributeOption.orchestrate();
                                    attribute.getOptions().put(ValidatableEntity.toId(attributeOptionValue), attributeOption);
                                }

                                // If the familyAttribute option won't exist already for the familyAttribute, add the familyAttribute option to the familyAttribute
                                AttributeOption attributeOption = attribute.getOptions().get(ValidatableEntity.toId(attributeOptionValue));
                                if(!familyAttribute.getOptions().containsKey(attributeOption.getId())) {
                                    FamilyAttributeOption familyAttributeOption = new FamilyAttributeOption();
                                    familyAttributeOption.setActive("Y");
                                    familyAttributeOption.setValue(attributeOption.getValue());
                                    familyAttributeOption.setId(attributeOption.getId());
                                    familyAttributeOption.setFamilyAttributeId(familyAttribute.getId());
                                    familyAttribute.getOptions().put(attributeOption.getId(), familyAttributeOption);
                                }
                            }
                        });
                    }
                }
            }

            attributeCollection.setGroup("ATTRIBUTES");
            //Update attribute collection with the newly added attributes and corresponding attribute options
            attributeCollectionService.update(attributeCollectionId, FindBy.EXTERNAL_ID, attributeCollection);

            //Attribute Collection updated with the new attributes and new options

            //Update the familyVariantGroups for the corresponding families
            familyVariantGroups.forEach((_familyId, variantAttributeIds) -> {
                Family family = families.get(_familyId);
                VariantGroup variantGroup = null;
                if (!family.getVariantGroups().isEmpty() && family.getVariantGroups().containsKey(channelId)) {
                    variantGroup = family.getVariantGroups().get(family.getChannelVariantGroups().get(channelId));
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
                family.getVariantGroups().put(variantGroup.getId(), variantGroup);
                family.getChannelVariantGroups().put(channelId, variantGroup.getId());
            });

            //TODO - Handle creation of related category mapping
            newCategories.forEach((categoryId, category) -> existingCategories.put(categoryId, categoryService.create(category)));
            newCategories.clear();

            families.forEach((familyId, family) -> family.setGroup("ATTRIBUTES", "VARIANT_GROUPS"));

            familyService.saveAll(families.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList())).forEach(family -> families.put(family.getId(), family));

            //Map of all existing products
            Map<String, Product> existingProducts = productService.getAll(null, false).stream()
                    .map(product -> {
                        product.setProductFamily(families.get(product.getProductFamilyId()));
                        product.setChannelId(channelId);
                        return product;
                    })
                    .collect(Collectors.toMap(Product::getProductId, p -> p));


            //Map of all existing product variants grouped by productId
            Map<String, Map<String, ProductVariant>> existingVariants = new HashMap<>();

            productVariantService.getAll(new ArrayList<>(existingProducts.keySet()).toArray(new String[0]), FindBy.EXTERNAL_ID, channelId, false).forEach(productVariant -> {
                String productId = productVariant.getProductId();
                if(!existingVariants.containsKey(productId)) {
                    existingVariants.put(productId, new HashMap<>());
                }
                existingVariants.get(productId).put(productVariant.getProductVariantId(), productVariant);
            });

            //Map of new products
            Map<String, Product> newProducts = new HashMap<>();

            //Map of updated products
            Map<String, Product> modifiedProducts = new HashMap<>();

            //Map of new product variants
            Map<String, ProductVariant> newProductVariants = new HashMap<>();

            //Map of updated product variants
            Map<String, ProductVariant> modifiedProductVariants = new HashMap<>();

            Map<String, String> productCategoryMap = new HashMap<>();

            //Loop through each variants and check to se if the product already exists.
            //If not, create the product with productLevel Attributes
            final int[] $row1 = {0}, $col1 = {0};
            for (int row = 0; row < variantsData.size(); row++) {
                $row1[0] = row;
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")),true).toUpperCase();
                productId = convertCellValue("", productId);
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")),true).toUpperCase();
                variantId = convertCellValue("", variantId);
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")),true).toUpperCase();
                String categoryId = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Category")),true).toUpperCase();
                String style = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Style")),true).toUpperCase();
                String pricing = variantsData.get(row).get(attributeNamesMetadata.indexOf("Pricing"));

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                Map<String, Object> productAttributesMap = new HashMap<>();

                Map<String, Object> variantAttributesMap = new HashMap<>();
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    $col1[0] = col;
                    Object _cellValue = getCellValue(familyAttributesGrid.get(row).get(col), delimitersMetadata.get(col), variantsData.get(row).get(col), productAttributesMap, variantAttributesMap);
                    if(_cellValue instanceof String) {
                        String cellValue = (String) _cellValue;
                        String attributeName = attributeNamesMetadata.get(col);
                        int attributeLevel = (int) Double.parseDouble(attributeLevelMetadata.get(col));

                        if (familyAttributes.get(familyId).contains(attributeName)) {
                            FamilyAttribute familyAttribute = familyAttributesGrid.get(row).get(col);
                            if (ConvertUtil.toBoolean(familyAttribute.getSelectable())) {

                                Optional<FamilyAttributeOption> cellValueOption = familyAttributesGrid.get(row).get(col).getOptions().values().stream().filter(e -> e.getValue().equals(variantsData.get($row1[0]).get($col1[0]))).findFirst();
                                if (cellValueOption.isPresent()) {
                                    cellValue = cellValueOption.get().getId();
                                }
                            }
                            cellValue = convertCellValue(attributeName, cellValue);
                            if (attributeLevel == 0) {
                                productAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), cellValue);
                            } else {
                                variantAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), cellValue);
                            }
                        }
                    } else {
                        String attributeName = attributeNamesMetadata.get(col);
                        int attributeLevel = (int) Double.parseDouble(attributeLevelMetadata.get(col));

                        if (familyAttributes.get(familyId).contains(attributeName)) {
                            if (attributeLevel == 0) {
                                productAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), _cellValue);
                            } else {
                                variantAttributesMap.put(familyAttributesGrid.get(row).get(col).getId(), _cellValue);
                            }
                        }
                    }

                }
                //TODO - make this conditional and dynamic
                if(isEmpty(variantAttributesMap.get("COLOR_NAME"))) {
                    continue;
                }

                //Product processing section
                Product product = modifiedProducts.containsKey(productId) ? modifiedProducts.get(productId) : newProducts.getOrDefault(productId, null);
                Map<String, ProductVariant> existingProductVariants = product == null || !existingVariants.containsKey(product.getId()) ? new HashMap<>() : existingVariants.get(product.getId());
                //If the product is already added to either the modifiedProducts or newProducts map, skip the product
                if(product == null) {
                    //If the product won't exist or not created yet, create a new product instance and add it to the newProductMap
                    if (existingProducts.containsKey(productId)) {
                        product = existingProducts.get(productId);
                        product.setGroup("DETAILS");
                        modifiedProducts.put(productId, product);
                        existingProductVariants = existingVariants.get(product.getId());
                    } else {
                        product = new Product();
                        product.setProductId(productId);
                        product.setActive("Y");
                        product.setChannelId(channelId);
                        product.setProductName(productName);
                        product.setProductFamilyId(familyId);
                        product.setProductFamily(families.get(familyId));
                        product.setAttributeValues(productAttributesMap);
                        if (!newProducts.containsKey(productId) && !existingProducts.containsKey(productId)) {
                            newProducts.put(productId, product);
                            productCategoryMap.put(productId, style);
                        }
                    }
                }


                //Variant processing section

                String variantIdentifier = "COLOR_NAME|" + variantAttributesMap.get("COLOR_NAME"); //TODO - change this to support multiple axis attribute values
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
                        if (isNotEmpty(axisAttribute) && axisAttribute.getOptions().containsKey(axisAttributeTokens.get(i + 1))) {
                            nameToken = axisAttribute.getOptions().get(axisAttributeTokens.get(i + 1)).getValue();
                        }
                        tempName.append(tempName.length() > 0 ? " - " : "").append(nameToken);
                    }

                    ProductVariant productVariant = null;
                    if(existingProductVariants.containsKey(variantId)) {
                        productVariant = existingProductVariants.get(variantId);
                        productVariant.setGroup("DETAILS");
                        modifiedProductVariants.put(variantId, productVariant);
                    } else {

                        productVariant = new ProductVariant(product);
                        productVariant.setProductVariantId(variantId);
                        productVariant.setChannelId(channelId);
                        productVariant.setActive("Y");
                        productVariant.setAxisAttributes(axisAttributes);
                        newProductVariants.put(variantId, productVariant);
                    }
                    productVariant.setLevel(1); //TODO - change for multi level variants support
                    productVariant.setProductVariantName(product.getProductName() + " - " + tempName.toString());
                    setVariantAttributeValues(product, productVariant, variantAttributesMap);
                    if(isNotEmpty(pricing)) {
                        productVariant.setGroup("PRICING_DETAILS");
                        setPricingDetails(productVariant, pricing);
                    }
                }
                if(row % 100 == 0) {
                    System.out.println(row + " of " + variantsData.size());
                } else {
                    System.out.print(".");
                }
            }

            //If there are new products that need to be created, create all of them and add the newly created products to the existing products map
            if(!newProducts.isEmpty()) {
                productService.create(newProducts.entrySet().stream()
                        .map(Map.Entry::getValue).collect(Collectors.toList()))
                        .forEach(product -> {
                            String idOfProduct = product.getId();
                            if(productCategoryMap.containsKey(product.getProductId()) && existingCategories.containsKey(productCategoryMap.get(product.getProductId()))) {
                                Category category = existingCategories.get(productCategoryMap.get(product.getProductId()));
                                List<CategoryProduct> categoryProducts = categoryService.getCategoryProducts(category.getCategoryId(), FindBy.EXTERNAL_ID, 0, 300, null, false).getContent();
                                CategoryProduct categoryProduct = categoryProducts.stream().filter(categoryProduct1 -> categoryProduct1.getProductId().equals(idOfProduct)).findFirst().orElse(null);
                                if (isNull(categoryProduct)) {
                                    categoryService.addProduct(category.getCategoryId(), FindBy.EXTERNAL_ID, product.getProductId(), FindBy.EXTERNAL_ID);
                                }
                            } else {
                                System.out.println("=================##############" + product.getProductId() + "," + productCategoryMap.get(product.getProductId()));
                            }
                            existingProducts.put(product.getProductId(), product);

                        });
            }

            //If there are modified products that need to be updated, update all of them and put the updated products to the existing products map
            if(!modifiedProducts.isEmpty()) {
                productService.update(modifiedProducts.entrySet().stream()
                        .map(Map.Entry::getValue).collect(Collectors.toList()))
                        .forEach(product -> {
//                            product.setChannelId(channelId);
//                            product.setProductFamily(families.get(product.getProductFamilyId()));
                            existingProducts.put(product.getProductId(), product);
                        });
            }

            //If there are new productVariants that need to be created, create all of them and add the newly created productVariants to the existing variants map
            if(!newProductVariants.isEmpty()) {
                productVariantService.create(newProductVariants.entrySet().stream()
                        .map(entry -> {
                            ProductVariant productVariant = entry.getValue();
                            if(isEmpty(productVariant.getProductId())) {
                                productVariant.setProduct(existingProducts.get(productVariant.getProduct().getProductId()));
                            }
                            return productVariant;
                        })
                        .collect(Collectors.toList()))
                        .forEach(productVariant -> {
                            if(!existingVariants.containsKey(productVariant.getProductId())) {
                                existingVariants.put(productVariant.getProductId(), new HashMap<>());
                            }
                            existingVariants.get(productVariant.getProductId()).put(productVariant.getProductVariantId(), productVariant);
                        });
            }
            //If there are modified productVariants that need to be updated, update all of them and put the updated productVariants to the existing variants map
            if(!modifiedProductVariants.isEmpty()) {
                productVariantService.update(modifiedProductVariants.entrySet().stream()
                        .map(Map.Entry::getValue).collect(Collectors.toList()))
                        .forEach(productVariant -> {
                            if(!existingVariants.containsKey(productVariant.getProductId())) {
                                existingVariants.put(productVariant.getProductId(), new HashMap<>());
                            }
                            existingVariants.get(productVariant.getProductId()).put(productVariant.getProductVariantId(), productVariant);
                        });
            }

            for (int row = 0; row < variantsData.size(); row++) {
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")), true).toUpperCase();
                productId = convertCellValue("", productId);
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")), true).toUpperCase();
                variantId = convertCellValue("", variantId);
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")), true).toUpperCase();


                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if (isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId) || "FOLDER_STYLE".equalsIgnoreCase(productId)) {
                    continue;
                }
                Product product = existingProducts.get(productId);
                AssetCollection productAssetsCollection = assetLoader.createCollection("PRODUCT_ASSETS");
                String assetFileName = variantId + ".png";
                List<String> assetNames = new ArrayList<>();
                if (new File(assetLoader.getSourceLocation() + assetFileName).isFile()) {
                    VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                    VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                    VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                    productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                    assetNames.add(assetFileName);
                    if (isEmpty(product.getChannelAssets())) {
                        productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                        product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                        existingProducts.put(productId, product);

                    }
                } else {
                    //                        System.out.println(assetFileName + " <======= Not Found");
                }

                if (variantsAssets.containsKey(variantId)) {
                    List<List<String>> variantAssets = variantsAssets.get(variantId);
                    for (int i = 0; i < variantAssets.size(); i++) {
                        List<String> variantAsset = variantAssets.get(i);
                        assetFileName = variantAsset.get(2) + ".png";
                        if (new File(assetLoader.getSourceLocation() + assetFileName).isFile()) {
                            VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                            VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                            VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                            productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                            assetNames.add(assetFileName);
                            if (isEmpty(product.getChannelAssets())) {
                                productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                                product = productService.get(productId, FindBy.EXTERNAL_ID, false).get();
                                existingProducts.put(productId, product);
                            }
                        } else {
                            //                                System.out.println(assetFileName + " <======= Asset Not Found");
                        }
                    }
                }

                File[] files = getFileNames(new File(assetLoader.getSourceLocation()), variantId + "_");
                for(int x = 0; x < files.length; x ++) {
                    assetFileName = files[x].getName();
                    if(!assetNames.contains(assetFileName)) {
                        assetNames.add(assetFileName);
                        if (x == 0 && isEmpty(product.getChannelAssets())) {
                            VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                            VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                            VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                            productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                            productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                            existingProducts.put(productId, productService.get(productId, FindBy.EXTERNAL_ID, false).get());
                        } else {
                            VirtualFile productFolder = assetLoader.createFolder(productId, productAssetsCollection.getRootId());
                            VirtualFile variantFolder = assetLoader.createFolder(variantId, productFolder.getId());
                            VirtualFile variantDefaultAsset = assetLoader.uploadFile(variantFolder.getId(), "", assetFileName, variantFolder.getRootDirectoryId());
                            productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                            if (isEmpty(product.getChannelAssets())) {
                                productService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, new String[]{variantDefaultAsset.getId()}, FileAsset.AssetFamily.ASSETS);
                                existingProducts.put(productId, productService.get(productId, FindBy.EXTERNAL_ID, false).get());
                            }
                        }
                    }
                }
            }
            System.out.println("Done");
        });

        return true;
    }

    public boolean validate(String filePath) {
        String channelId = "ECOMMERCE";

        //Product variant data WITH metadata
        List<List<String>> data = POIUtil.readData(filePath);

        List<List<String>> assetsData = POIUtil.readData("/usr/local/pim/uploads/data/import/Product_Assets.xlsx");
        Map<String, List<List<String>>> assetMap = new LinkedHashMap<>();
        ((ArrayList) assetsData).forEach(assetData -> {
            if(((List<String>)assetData).get(1).equals("image")) {
                String productId = ((List<String>) assetData).get(0);
                if (!assetMap.containsKey(productId)) {
                    assetMap.put(productId, new ArrayList<>());
                }
                assetMap.get(productId).add((List<String>) assetData);
            }
        });


        //Create the attributeCollection if it won't already exists
        if(!attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).isPresent()) {
            AttributeCollection attributeCollection = new AttributeCollection();
            attributeCollection.setCollectionId(attributeCollectionId);
            attributeCollection.setCollectionName("Envelopes Attributes Collection");
            attributeCollection.setActive("Y");
            attributeCollectionService.create(attributeCollection);
        }

        //Map of all existing families
        Map<String, Family> families = familyService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing categories
        Map<String, Category> existingCategories = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getExternalId, e -> e));

        //Map of all existing and newly creating categories
        Map<String, Category> newCategories = new HashMap<>();

        //Map to store variant level attributes for each families
        Map<String, Set<String>> familyVariantGroups = new LinkedHashMap<>();

        //Load the attributeCollection from the database for the given collectionId
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {
            // The metadata row containing the attribute names, this will be first row in the data sheet
            List<String> attributeNamesMetadata = data.get(0);

            // The metadata row containing the attributeType, this will be second row in the data sheet
            List<String> attributeTypesMetadata = data.get(1);

            // The metadata row containing the familyAttributeGroup name, this will be third row in the data sheet
            List<String> familyAttributeGroupMetadata = data.get(2);

            // The metadata row containing the familyAttributeSubGroup name, this will be fourth row in the data sheet
            List<String> familyAttributeSubgroupMetadata = data.get(3);

            // The metadata row containing the attribute level value of each attribute (0 - Product Level & 1 - Variant Level), this will be fourth row in the data sheet
            List<String> attributeLevelMetadata = data.get(4);

            //Number of metadata rows in the data sheet
            int numOfMetadataRows = 5;

            //Product variants data WITHOUT metadata, this will be a sublist of the complete data without the metadata rows
            List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());
            Map<String, String> four04 = new LinkedHashMap<>();
            int cnt = 0,cnt1 = 0;
            String lastProductId = "";
            //Process each of the variant data row in the data sheet
            for(int row = 0; row < variantsData.size(); row ++) {

                //ProductId
                String productId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_ID")), true).toUpperCase();

                productId = convertCellValue("", productId);
                //ProductName
                String productName = variantsData.get(row).get(attributeTypesMetadata.indexOf("PRODUCT_NAME"));
                //VariantId
                String variantId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("VARIANT_ID")), true);

                variantId = convertCellValue("", variantId);
                //FamilyId
                String familyId = trim(variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID")), true).toUpperCase();
                //CategoryId
                String categoryId = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Category")), true).toUpperCase();
                //Style
                String style = trim(variantsData.get(row).get(attributeNamesMetadata.indexOf("Style")), true).toUpperCase();

                //TODO - TEMP CODE - Skip folders category
                if(categoryId.equals("FOLDERS")) {continue;}

                //Skip the current variant row, if productId, productName, variantId or familyId is empty
                if(isEmpty(productId) || isEmpty(productName) || isEmpty(variantId) || isEmpty(familyId)) {
                    continue;
                }

                String assetFileName = variantId + ".png";
//                if(!lastProductId.equals(productId)) {
                    if (new File(assetLoader.getSourceLocation() + assetFileName).isFile()) {
                        cnt1++;
                    } else {
                        cnt++;
                        four04.put(variantId, assetFileName);
                    }
                    lastProductId = productId;
//                }

            }
            four04.forEach((k, v) ->  System.out.println(k + "   -------    " + v));
            System.out.println(four04.size() + "(" + cnt + ")" + ", Found=" + cnt1);
            System.out.println("Done");
        });

        return true;
    }

    /**
     * Method for extracting the valid attribute names grouped by familyId. Valid attributes are those attributes in the
     * dataSheet with attributeTypeMetadata matches to one of the value in availableAttributeTypes
     *
     * @param data ProductVariants data with the metadata
     *
     * @return A map of valid familyAttributeNames grouped by familyId
     */
    private Map<String, Set<String>> getFamilyAttributes(List<List<String>> data) {

        List<String> attributeNamesMetadata = data.get(0);
        List<String> attributeTypesMetadata = data.get(1);
        int numOfMetadataRows = 7;
        List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());

        Map<String, Set<String>> familyAttributes = new HashMap<>();

        for (int row = 0; row < variantsData.size(); row++) {
            String familyId = variantsData.get(row).get(attributeTypesMetadata.indexOf("FAMILY_ID"));
            //Ignore all variants with empty familyId
            if(!isEmpty(familyId)) {
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    String attributeName = attributeNamesMetadata.get(col);
                    String attributeValue = variantsData.get(row).get(col);
                    if (isNotEmpty(attributeName) && isNotEmpty(attributeValue) && availableAttributeTypes.contains(attributeTypesMetadata.get(col))) {
                        if (!familyAttributes.containsKey(familyId)) {
                            familyAttributes.put(familyId, new HashSet<>());
                        }
                        familyAttributes.get(familyId).add(attributeName);
                    }
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

    private void setVariantAttributeValues(ProductVariant productVariantDTO, Map<String, Object> attributesMap) {
        productService.get(productVariantDTO.getProduct().getProductId(), FindBy.EXTERNAL_ID, false).ifPresent(product -> {
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

    private void setVariantAttributeValues(Product product, ProductVariant productVariantDTO, Map<String, Object> attributesMap) {
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
    }

    private static Map<String, List<List<String>>> getProductAssets() {
        List<List<String>> assetsData = POIUtil.readData("/usr/local/pim/uploads/data/import/Product_Assets.xlsx");
        Map<String, List<List<String>>> productAssets = new LinkedHashMap<>();
        ((ArrayList) assetsData).forEach(assetData -> {
            if(((List<String>)assetData).get(1).equals("image")) {
                String productId = ((List<String>) assetData).get(0);
                if (!productAssets.containsKey(productId)) {
                    productAssets.put(productId, new ArrayList<>());
                }
                productAssets.get(productId).add((List<String>) assetData);
            }
        });
        return productAssets;
    }

    public List<Pair<String, String>> getAttributeGroupsIdNamePair(Family family) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        FamilyAttributeGroup.getAllAttributeGroups(family.getAttributes(), FamilyAttributeGroup.GetMode.LEAF_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), FamilyAttributeGroup.getUniqueLeafGroupLabel(attributeGroup, " > "))));
        return idNamePairs;
    }

    public List<Pair<String, String>> getParentAttributeGroupsIdNamePair(Family family) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        FamilyAttributeGroup.getAllAttributeGroups(family.getAttributes(), FamilyAttributeGroup.GetMode.MASTER_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), attributeGroup.getLabel())));
        return idNamePairs;
    }

    private String convertCellValue(String name, String cellValue) {
        if((!name.equals("Base Quantity Price") && !name.equals("Each Price")) && cellValue.endsWith(".0")) {
            return cellValue.substring(0, cellValue.length() - 2);
        }
        return cellValue;
    }

    /**
     * This version supports only one level of parent attribute linking, multi-level linking will be implemented in future version, if required
     * @param attribute
     * @param delimiters
     * @param cellValue
     * @return
     */
    private Object getCellValue(FamilyAttribute attribute, String delimiters, String cellValue, Map<String, Object> productAttributesMap, Map<String, Object> variantAttributesMap) {
        if(isEmpty(cellValue)) {
            return "";
        }
        if(isNotEmpty(delimiters)) {
            if(isNotEmpty(attribute.getParentAttributeId())) { // Dependent attribute options - multi value - possible multi level depth - attribute value is of type Map<String, Object>, where key will be the parent option Id and value will be the option ID
                Map<String, Object> attributeValue = new LinkedHashMap<>();
                FamilyAttribute parentAttribute = attribute.getFamily().getAttribute(attribute.getParentAttributeId()).orElse(null);
                //Parent values will always be of List<String> type
                List<String> parentAttributeValues = (List<String>) (productAttributesMap.containsKey(parentAttribute.getId()) ? productAttributesMap.get(parentAttribute.getId()) : variantAttributesMap.get(parentAttribute.getId()));
                //If cellValue is multi delimited, then each child value will be a List<String> other wise, child value will be a string
                String[] childValues = split(cellValue, delimiters.substring(0, 1));
                for(int i = 0; i < childValues.length; i ++) {
                    String[] childAttributeValue = {childValues[i]};
                    attributeValue.put(parentAttributeValues.get(i), delimiters.length() == 1 ? attribute.getOptions().values().stream().filter(e -> e.getValue().equals(childAttributeValue[0])).findFirst().map(o -> o.getId()).orElse(childAttributeValue[0]) : ConversionUtil.toList(split(childAttributeValue[0], delimiters.substring(1, 2))).stream().map(value -> attribute.getOptions().values().stream().filter(e -> e.getValue().equals(value)).findFirst().map(o -> o.getId()).orElse(value)).collect(Collectors.toList()));
                }
                return attributeValue;
            } else {    // standalone attribute options - multi values - single level depth - attribute value is of type List<String> where each element will be the option ID
                return ConversionUtil.toList(split(cellValue, delimiters.substring(0, 1))).stream().map(value -> attribute.getOptions().values().stream().filter(e -> e.getValue().equals(value)).findFirst().map(o -> o.getId()).orElse(value)).collect(Collectors.toList());
            }
        }
        return cellValue;
    }

    private File[] getFileNames(File dir, String prefix) {
        FileFilter fileFilter = new RegexFileFilter("^" + prefix + ".*.png$");
        return dir.listFiles(fileFilter);
    }
}
