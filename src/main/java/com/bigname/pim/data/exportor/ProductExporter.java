package com.bigname.pim.data.exportor;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.core.data.exporter.BaseExporter;
import com.bigname.core.domain.Entity;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.POIUtil;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.bigname.common.util.StringUtil.*;

/**
 * Created by sruthi on 31-01-2019.
 */
@Component
public class ProductExporter implements BaseExporter<Product, ProductService> {

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CategoryService categoryService;

    public boolean exportData(String filePath) {
        List<Map<String, Object>> productVariantData = productVariantService.getAll();
        Map<String, Family> familyLookup = familyService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, f -> f));

        List<Map<String, Object>> variantsAttributes = new ArrayList<>();
        Set<String> header = new HashSet<>();
        productVariantData.forEach(variant -> {
            Map<String, Object> variantAttributesMap = new HashMap<>();
            variant.forEach((key, value) -> {
                if(value instanceof String) {
                    variantAttributesMap.put(key, value);
                }

                String productFamilyId = (String) variant.get("productFamilyId");
                String familyId = null;
                if(productFamilyId != null && familyLookup.containsKey(productFamilyId)){
                    familyId = familyLookup.get(productFamilyId).getFamilyId();
                }

                Map<String, Object> scopedProductAttributes = (Map<String, Object>)((Map<String, Object>)variant.get("scopedFamilyAttributes")).get("ECOMMERCE");
                Map<String, Object> pricingDetails = (Map<String, Object>)variant.get("pricingDetails");
                Map<String, Object> variantAttributes = (Map<String, Object>)variant.get("variantAttributes");
                if(scopedProductAttributes != null) {
                    variantAttributesMap.putAll(scopedProductAttributes);
                } else {
                    System.out.println(variant);
                }
                variantAttributesMap.put("PRICING_DETAILS", CollectionsUtil.buildMapString(pricingDetails,0).toString());
                if(variantAttributes != null) {
                    variantAttributesMap.putAll(variantAttributes);
                } else {
                    System.out.println(variant);
                }
                variantAttributesMap.replace("productFamilyId", familyId);
                variantAttributesMap.remove("createdUser");
                variantAttributesMap.remove("lastModifiedUser");

            });
            header.addAll(variantAttributesMap.keySet());
            variantsAttributes.add(variantAttributesMap);
        });

        List<List<Object>> data = new ArrayList<>();
        List<Object> headerColumns = new ArrayList<>(header);
        data.add(headerColumns);
        for (Map<String, Object> variantAttributes : variantsAttributes) {
            List<Object> variantData = new ArrayList<>();
            for(int i = 0; i < headerColumns.size(); i++){
                String key = (String)headerColumns.get(i);
                variantData.add(variantAttributes.get(key));
            }
            data.add(variantData);

        }
        POIUtil.writeData(filePath, "Product", data);
        return true;
    }

    @Override
    public String getFileName(Type fileType) {
        return "ProductExport" + PimUtil.getTimestamp() + fileType.getExt();
    }

    public boolean exportFullJsonData(String filePath) {
        Map<String, Object> jsonData = new LinkedHashMap<>();
        List<Map<String, Object>> attributeOptionsNode = new ArrayList<>();
        Map<String, AttributeOption> attributeOptions = new HashMap<>();
        attributeCollectionService.getAll(null, true, true, true)
                .forEach(attributeCollection -> attributeCollection.getAllAttributes()
                        .forEach(attribute -> {
                            Map<String, AttributeOption> options = attribute.getOptions();
                            options.forEach((attributeOptionId, attributeOption) -> attributeOption.setCollectionId(attributeCollection.getCollectionId()).setAttributeId(attribute.getId()));
                            attributeOptions.putAll(options);
                        }));

        attributeOptions.forEach((optionId, attributeOption) -> {
            Map<String, Object> optionNode = CollectionsUtil.toMap(
                    "_ID",    attributeOption.getId(),
                    "VALUE",         attributeOption.getValue(),
                    "_COLLECTION_ID",attributeOption.getCollectionId(),
                    "_ATTRIBUTE_ID", attributeOption.getAttributeId());

            attributeOptionsNode.add(optionNode);
        });
        jsonData.put("ATTRIBUTE_OPTIONS", attributeOptionsNode);


        List<Map<String, Object>> familiesNode = new ArrayList<>();
        List<Family> familiesList = familyService.getAll(null, true, true, true);
        familiesList.forEach(family -> {
            Map<String, Object> familyNode = CollectionsUtil.toMap(
                    "_ID",   family.getId(),
                    "FAMILY_ID",    family.getFamilyId(),
                    "NAME",         family.getFamilyName(),
                    "ACTIVE",       family.getActive(),
                    "DISCONTINUED", family.getDiscontinued());

            int[] seqNum = {0};
            familyNode.put("FAMILY_ATTRIBUTES", family.getAllAttributesMap(false).entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList()).stream()
                    .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                    .map(familyAttribute -> {
                        Map<String, Object> familyAttributeNode = CollectionsUtil.toMap(
                                "_ID",                 familyAttribute.getId(),
                                "NAME",                       familyAttribute.getName(),
                                "TYPE",                       familyAttribute.getUiType().name(),
                                "DATA_TYPE",                  familyAttribute.getDataType(),
                                "SELECTABLE",                 familyAttribute.getUiType().isSelectable(),
                                "MULTI_SELECT",               familyAttribute.getUiType().isMultiSelect() ? "Y" : "N",
                                "SCOPE",                      familyAttribute.getScope().get(PIMConstants.DEFAULT_CHANNEL_ID).name(),
                                "COLLECTION_ID",              familyAttribute.getCollectionId(),
                                "ATTRIBUTE_ID",               getSimpleId(familyAttribute.getAttributeId()),
                                "PARENT_FAMILY_ATTRIBUTE_ID", getSimpleId(familyAttribute.getParentAttributeId()),
                                "ACTIVE",                     familyAttribute.getActive(),
                                "SEQUENCE_NUM",               seqNum[0] ++);

                        int[] seqNum1 = {0};
                        List<Map<String, Object>> familyAttributeOptionsNode = new ArrayList<>();
                        familyAttribute.getOptions().entrySet().stream()
                                .map(entry -> entry.getValue()).collect(Collectors.toList()).stream()
                                .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                                .forEach(familyAttributeOption -> {
                                    Map<String, Object> familyAttributeOptionNode = CollectionsUtil.toMap(
                                            "_ID",   familyAttributeOption.getId(),
                                            "VALUE",        familyAttributeOption.getValue(),
                                            "ACTIVE",       familyAttributeOption.getActive(),
                                            "SEQUENCE_NUM", seqNum1[0] ++);

                                    familyAttributeOptionsNode.add(familyAttributeOptionNode);
                                });

                        familyAttributeNode.put("FAMILY_ATTRIBUTE_OPTIONS", familyAttributeOptionsNode);

                        return familyAttributeNode;
                    }).collect(Collectors.toList()));

            familiesNode.add(familyNode);
        });
        jsonData.put("FAMILIES", familiesNode);


        List<Map<String, Object>> websitesNode = new ArrayList<>();
        List<Website> websitesList = websiteService.getAll(null, true, true, true);
        websitesList.forEach(website -> {
            Map<String, Object> websiteNode = CollectionsUtil.toMap(
                                                        "_ID",  website.getId(),
                                                        "WEBSITE_ID",   website.getWebsiteId(),
                                                        "NAME",         website.getWebsiteName(),
                                                        "URL",          website.getUrl(),
                                                        "ACTIVE",       website.getActive(),
                                                        "DISCONTINUED", website.getDiscontinued());
            int[] seqNum = {0};
            websiteNode.put("CATALOGS", websiteService.getAllWebsiteCatalogs(website.getId()).stream()
                                                            .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                                                            .map(websiteCatalog -> CollectionsUtil.toMap("_ID", websiteCatalog.getCatalogId(), "ACTIVE", websiteCatalog.getActive(), "SEQUENCE_NUM", seqNum[0] ++))
                                                            .collect(Collectors.toList()));

            websitesNode.add(websiteNode);
        });
        jsonData.put("WEBSITES", websitesNode);

        List<Map<String, Object>> catalogsNode = new ArrayList<>();
        List<Catalog> catalogsList = catalogService.getAll(null, true, true, true);
        catalogsList.forEach(catalog -> {
            Map<String, Object> catalogNode =  CollectionsUtil.toMap(
                                                        "_ID",  catalog.getId(),
                                                        "CATALOG_ID",   catalog.getCatalogId(),
                                                        "NAME",         catalog.getCatalogName(),
                                                        "DESCRIPTION",  catalog.getDescription(),
                                                        "ACTIVE",       catalog.getActive(),
                                                        "DISCONTINUED", catalog.getDiscontinued());
            int[] seqNum = {0};
            catalogNode.put("CATEGORIES", catalogService.getAllRootCategories(catalog.getId()).stream()
                                                            .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                                                            .map(rootCategory -> CollectionsUtil.toMap("_ID", rootCategory.getRootCategoryId(), "ACTIVE", rootCategory.getActive(), "SEQUENCE_NUM", seqNum[0] ++))
                                                            .collect(Collectors.toList()));

            catalogsNode.add(catalogNode);
        });
        jsonData.put("CATALOGS", catalogsNode);

        List<Map<String, Object>> categoriesNode = new ArrayList<>();
//        List<Category> categoriesList = categoryService.getAll(0, 10, null, true, true, true).getContent();
        List<Category> categoriesList = categoryService.getAll(null, true, true, true);
        categoriesList.forEach(category -> {
            Map<String, Object> categoryNode =  CollectionsUtil.toMap(
                                                        "_ID",      category.getId(),
                                                        "CATEGORY_ID",      category.getCategoryId(),
                                                        "NAME",             category.getCategoryName(),
                                                        "DESCRIPTION",      category.getDescription(),
                                                        "LONG_DESCRIPTION", category.getLongDescription(),
                                                        "META_DESCRIPTION", category.getMetaDescription(),
                                                        "META_KEYWORDS",    category.getMetaKeywords(),
                                                        "META_TITLE",       category.getMetaTitle(),
                                                        "ACTIVE",           category.getActive(),
                                                        "DISCONTINUED",     category.getDiscontinued());
            int[] seqNum = {0};
            categoryNode.put("PRODUCTS", categoryService.getAllCategoryProducts(category.getId()).stream()
                    .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                    .map(categoryProduct -> CollectionsUtil.toMap("_ID", categoryProduct.getProductId(), "ACTIVE", categoryProduct.getActive(), "SEQUENCE_NUM", seqNum[0] ++))
                    .collect(Collectors.toList()));

            categoriesNode.add(categoryNode);
        });
        jsonData.put("CATEGORIES", categoriesNode);

        List<Map<String, Object>> productsNode = new ArrayList<>();
//        List<Product> productsList = productService.getAll(0, 10, null, true, true, true).getContent();
        List<Product> productsList = productService.getAll(null, true, true, true);
        productsList.forEach(product -> {
            Map<String, Object> productNode =  CollectionsUtil.toMap(
                    "_ID",       product.getId(),
                    "PRODUCT_ID",       product.getProductId(),
                    "NAME",             product.getProductName(),
                    "_FAMILY_ID",       product.getProductFamilyId(),
                    "FAMILY_ID",        product.getProductFamily().getFamilyId(),
                    "ACTIVE",           product.getActive(),
                    "DISCONTINUED",     product.getDiscontinued());

            productNode.putAll(product.getScopedFamilyAttributes().get(PIMConstants.DEFAULT_CHANNEL_ID));

            Map<String, Object> digitalAssets = new LinkedHashMap<>();
            if(product.getScopedAssets().get(PIMConstants.DEFAULT_CHANNEL_ID) != null) {
                product.getScopedAssets().get(PIMConstants.DEFAULT_CHANNEL_ID)
                        .forEach((assetFamilyId, familyAssets) -> {
                            List<Map<String, Object>> assetsMap = (List<Map<String, Object>>) familyAssets;
                            digitalAssets.put(assetFamilyId, assetsMap.stream().map(assetMap -> CollectionsUtil.toMap(
                                    "_ID",   assetMap.get("id"),
                                    "NAME",         assetMap.get("name"),
                                    "FILE_NAME",    assetMap.get("internalName"),
                                    "IS_DEFAULT",   assetMap.get("defaultFlag"),
                                    "SEQUENCE_NUM", assetMap.get("sequenceNum"),
                                    "TYPE",         assetMap.get("type")
                            )).collect(Collectors.toList()));
                        });
            }
            productNode.put("DIGITAL_ASSETS", digitalAssets);
            int[] seqNum = {0};
            productNode.put("PRODUCT_VARIANTS", productService.getProductVariants(product.getProductId(), FindBy.EXTERNAL_ID, PIMConstants.DEFAULT_CHANNEL_ID, null, false).stream()
                    .sorted((e1, e2) -> (int)(e1.getSequenceNum() == e2.getSequenceNum() ? e2.getSubSequenceNum() - e1.getSubSequenceNum() : e1.getSequenceNum() - e2.getSequenceNum()))
                    .map(productVariant -> {
                        Map<String, Object> variantNode =  CollectionsUtil.toMap(
                                "_ID",           productVariant.getId(),
                                "PRODUCT_VARIANT_ID",   productVariant.getProductVariantId(),
                                "NAME",                 productVariant.getProductVariantName(),
                                "_PRODUCT_ID",          product.getId(),
                                "PRODUCT_ID",           product.getProductId(),
                                "_FAMILY_ID",           product.getProductFamilyId(),
                                "FAMILY_ID",            product.getProductFamily().getFamilyId(),
                                "SEQUENCE_NUM",         seqNum[0] ++,
                                "ACTIVE",               productVariant.getActive(),
                                "DISCONTINUED",         productVariant.getDiscontinued());
                        variantNode.putAll(product.getScopedFamilyAttributes().get(PIMConstants.DEFAULT_CHANNEL_ID));
                        variantNode.putAll(productVariant.getVariantAttributes());
                        variantNode.put("PRICING_DETAILS", productVariant.getPricingDetails());
                        Map<String, Object> variantDigitalAssets = new LinkedHashMap<>();
                        if(productVariant.getVariantAssets() != null) {
                            productVariant.getVariantAssets()
                                    .forEach((assetFamilyId, familyAssets) -> {
                                        List<Map<String, Object>> assetsMap = (List<Map<String, Object>>) familyAssets;
                                        variantDigitalAssets.put(assetFamilyId, assetsMap.stream().map(assetMap -> CollectionsUtil.toMap(
                                                "_ID",   assetMap.get("id"),
                                                "NAME",         assetMap.get("name"),
                                                "FILE_NAME",    assetMap.get("internalName"),
                                                "IS_DEFAULT",   assetMap.get("defaultFlag"),
                                                "SEQUENCE_NUM", assetMap.get("sequenceNum"),
                                                "TYPE",         assetMap.get("type")
                                        )).collect(Collectors.toList()));
                                    });
                        }
                        variantNode.put("DIGITAL_ASSETS", variantDigitalAssets);


                        return variantNode;
                    })
                    .collect(Collectors.toList()));

            productsNode.add(productNode);
        });
        jsonData.put("PRODUCTS", productsNode);

        POIUtil.writeJsonData(filePath, "PIMExport", ConversionUtil.toJSONString(jsonData));
        return true;
    }

    public boolean exportJsonData(String filePath) {
        List<Map<String, Object>> productVariantData = productVariantService.getAll();
        Map<String, Family> familyLookup = familyService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, f -> f));

        List<Map<String, Object>> variantsAttributes = new ArrayList<>();
        Set<String> header = new HashSet<>();
        productVariantData.forEach(variant -> {
            Map<String, Object> variantAttributesMap = new HashMap<>();
            variant.forEach((key, value) -> {
                if(value instanceof String) {
                    variantAttributesMap.put(key, value);
                }

                String productFamilyId = (String) variant.get("productFamilyId");
                String familyId = null;
                if(productFamilyId != null && familyLookup.containsKey(productFamilyId)){
                    familyId = familyLookup.get(productFamilyId).getFamilyId();
                }

                Map<String, Object> scopedProductAttributes = (Map<String, Object>)((Map<String, Object>)variant.get("scopedFamilyAttributes")).get("ECOMMERCE");
                Map<String, Object> pricingDetails = (Map<String, Object>)variant.get("pricingDetails");
                Map<String, Object> variantAttributes = (Map<String, Object>)variant.get("variantAttributes");
                if(scopedProductAttributes != null) {
                    variantAttributesMap.putAll(scopedProductAttributes);
                } else {
                    System.out.println(variant);
                }
                variantAttributesMap.put("PRICING_DETAILS", CollectionsUtil.buildMapString(pricingDetails,0).toString());
                if(variantAttributes != null) {
                    variantAttributesMap.putAll(variantAttributes);
                } else {
                    System.out.println(variant);
                }
                variantAttributesMap.replace("productFamilyId", familyId);
                variantAttributesMap.remove("createdUser");
                variantAttributesMap.remove("lastModifiedUser");

            });
            header.addAll(variantAttributesMap.keySet());
            variantsAttributes.add(variantAttributesMap);
        });

        POIUtil.writeJsonData(filePath, "Product", ConversionUtil.toJSONString(variantsAttributes));
        return true;
    }

    public boolean exportProductsData(String filePath){
        List<Product> products =  productService.getAll(null, false);
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[]{"PARENT PRODUCT ID", "PARENT PRODUCT NAME", "CATEGORY_ID", "SEQUENCE_NUM"}));
        products.forEach(product -> data.add(Arrays.asList(product.getExternalId(), product.getProductName())));
        POIUtil.writeData(filePath, "Parent Products", data);
        return true;
    }

    public boolean exportChildProductsData(String filePath){
        List<List<Object>> data = new ArrayList<>();
        List<Product> products =  productService.getAll(null, false);
        data.add(Arrays.asList(new String[]{"CHILD PRODUCT NAME", "CHILD PRODUCT ID", "PARENT PRODUCT ID"}));
        products.forEach(product -> {
            List<ProductVariant> variants =  productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, product.getChannelId(), null, false);
            variants.forEach(variant -> data.add(Arrays.asList(variant.getProductVariantName(), variant.getProductVariantId(),product.getExternalId())));
        });

        POIUtil.writeData(filePath, "Child Products", data);
        return true;
    }
}

