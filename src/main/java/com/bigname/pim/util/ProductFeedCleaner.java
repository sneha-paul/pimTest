package com.bigname.pim.util;

import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductFeedCleaner {

    public static final String productFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Active_Products_ENVELOPES.xlsx";
    public static final String updatedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Active_Products_ENVELOPES-" + PimUtil.getTimestamp() + ".xlsx";
    public static final String categorizedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Categorized_Active_Products_ENVELOPES-" + PimUtil.getTimestamp() + ".xlsx";
    public static final String attributeOptionsFilePath = "/usr/local/pim/uploads/data/cleanup/AttributeOptions_ENVELOPES.xlsx";
    public static final String missingAttributeOptionsFilePath = "/usr/local/pim/uploads/data/cleanup/MissingAttributeOptions_ENVELOPES-" + PimUtil.getTimestamp() + ".xlsx";
    public static final String parentProductsFilePath = "/usr/local/pim/uploads/data/cleanup/ParentProductsData.xlsx";
    public static final String missingParentProductsFilePath = "/usr/local/pim/uploads/data/cleanup/MissingParentProductsData-" + PimUtil.getTimestamp() + ".xlsx";

    private static Map<String, Map<String, Map<String, Object>>> attributeOptionsMap = readAttributesData(attributeOptionsFilePath);
    private static Map<String, Map<String, Object>> parentProductsMap = readParentProductsData(parentProductsFilePath).entrySet().stream().findFirst().get().getValue();
    private static List<List<Object>> products = readData(productFeedFilePath);

    static Map<String, String> requiredOutputFieldsmap = new LinkedHashMap<>();

    static {
        requiredOutputFieldsmap.put("Code", "PARENT_PRODUCT_ID");
        requiredOutputFieldsmap.put("Product Type", "PRODUCT_TYPE");
        requiredOutputFieldsmap.put("Netsuite Category", "NETSUITE_CATEGORY");
        requiredOutputFieldsmap.put("Category", "ROOT_CATEGORY");
        requiredOutputFieldsmap.put("Style", "PRODUCT_CATEGORY/STYLE");
        requiredOutputFieldsmap.put("Parent Code", "PARENT_PRODUCT_ID");
        requiredOutputFieldsmap.put("Parent Name", "PARENT_PRODUCT_NAME");
        requiredOutputFieldsmap.put("Product Page Name", "PRODUCT_PAGE_NAME");
        requiredOutputFieldsmap.put("Collection", "COLLECTION");
        requiredOutputFieldsmap.put("Size", "SIZE");
        requiredOutputFieldsmap.put("Size Code", "SIZE_CODE");
        requiredOutputFieldsmap.put("Metric Size", "METRIC_SIZE");
        requiredOutputFieldsmap.put("Paper Texture", "PAPER_TEXTURE");
        requiredOutputFieldsmap.put("Availability", "AVAILABILITY");
        requiredOutputFieldsmap.put("Recycled Content", "RECYCLED_CONTENT");
        requiredOutputFieldsmap.put("Color Group", "COLOR_GROUP");
        requiredOutputFieldsmap.put("Color", "COLOR");
        requiredOutputFieldsmap.put("Existing Color Name", "COLOR_NAME");
        requiredOutputFieldsmap.put("New Color Name", "NEW_COLOR_NAME");
        requiredOutputFieldsmap.put("Paper Weight", "PAPER_WEIGHT");
        requiredOutputFieldsmap.put("Recycled Percent", "RECYCLED_PERCENT");
        requiredOutputFieldsmap.put("Sealing Method", "SEALING_METHOD");
        requiredOutputFieldsmap.put("Compare To Brand", "COMPARE_TO_BRAND");
        requiredOutputFieldsmap.put("Brand", "BRAND");
        requiredOutputFieldsmap.put("Brand Collection", "BRAND COLLECTION");
        requiredOutputFieldsmap.put("Old Sealing Method", "OLD_SEALING_METHOD");
        requiredOutputFieldsmap.put("Sealing Method Type", "SEALING_METHOD_TYPE");
        requiredOutputFieldsmap.put("Old Window Size", "OLD_WINDOW_SIZE");
        requiredOutputFieldsmap.put("Old Window Position", "OLD_WINDOW_POSITION");
        requiredOutputFieldsmap.put("Window Size", "WINDOW_SIZE");
        requiredOutputFieldsmap.put("Window Position", "WINDOW_POSITION");
        requiredOutputFieldsmap.put("Window Location", "WINDOW_LOCATION");
        requiredOutputFieldsmap.put("Base Quantity Price", "BASE_QUANTITY_PRICE");
        requiredOutputFieldsmap.put("Each Weight", "EACH_WEIGHT");
        requiredOutputFieldsmap.put("Recycled", "RECYCLED");
        requiredOutputFieldsmap.put("Laser", "LASER");
        requiredOutputFieldsmap.put("Inkjet", "INKJET");
        requiredOutputFieldsmap.put("Carton Quantity", "CARTON_QUANTITY");
        requiredOutputFieldsmap.put("Tagline", "TAGLINE");
        requiredOutputFieldsmap.put("Shape", "SHAPE");
        requiredOutputFieldsmap.put("Labels Per Sheet", "LABELS_PER_SHEET");
        requiredOutputFieldsmap.put("Top Margin", "TOP_MARGIN");
        requiredOutputFieldsmap.put("Bottom Margin", "BOTTOM_MARGIN");
        requiredOutputFieldsmap.put("Left Margin", "LEFT_MARGIN");
        requiredOutputFieldsmap.put("Right Margin", "RIGHT_MARGIN");
        requiredOutputFieldsmap.put("Corner Radius", "CORNER_RADIUS");
        requiredOutputFieldsmap.put("Vertical Spacing", "VERTICAL_SPACING");
        requiredOutputFieldsmap.put("Horizontal Spacing", "HORIZONTAL_SPACING");
        requiredOutputFieldsmap.put("Backslits", "BACKSLITS");
        requiredOutputFieldsmap.put("Rush Production", "RUSH_PRODUCTION");
        requiredOutputFieldsmap.put("Plain Lead Time", "PLAIN_LEAD_TIME");
        requiredOutputFieldsmap.put("Standard Lead Time", "STANDARD_LEAD_TIME");
        requiredOutputFieldsmap.put("Rush Lead Time", "RUSH_LEAD_TIME");
        requiredOutputFieldsmap.put("Pricing", "PRICING");
        requiredOutputFieldsmap.put("Style Description", "STYLE_DESCRIPTION");
        requiredOutputFieldsmap.put("Color Family Description", "COLOR_FAMILY_DESCRIPTION");
    }

    public static void doCleanup() {
        List<Object> header = products.get(0);

        int parentCodeIdx = header.indexOf("Parent Code");
        int parentNameIdx = header.indexOf("Old Parent Name");
        int productPageNameIdx = header.indexOf("Old Product Page Name");
        int collectionIdx = header.indexOf("Old Collection");
        int sizeIdx = header.indexOf("Old Size");
        int sizeCodeIdx = header.indexOf("Old Size Code");
        int metricSizeIdx = header.indexOf("Old Metric Size");
        int paperWeightIdx = header.indexOf("Old Paper Weight");
        int paperTextureIdx = header.indexOf("Old Paper Texture");
        int availabilityIdx = header.indexOf("Old Availability");
        int recycledPercentIdx = header.indexOf("Old Recycled Percent");
        int recycledContentIdx = header.indexOf("Old Recycled Content");
        int compareToBrandIdx = header.indexOf("Old Compare To Brand");
        int brandCollectionIdx = header.indexOf("Old Brand Collection");
        int sealingMethodIdx = header.indexOf("Old Sealing Method");
        int sealingMethodTypeIdx = header.indexOf("Old Sealing Method Type");
        int windowSizeIdx = header.indexOf("Old Window Size");
        int windowPositionIdx = header.indexOf("Old Window Position");
        int windowLocationIdx = header.indexOf("Old Window Location");
//        int colorNameIdx = header.indexOf("Old Color Name");
        int colorGroupIdx = header.indexOf("Old Color Group");
        int colorIdx = header.indexOf("Old Color");
        Map<String, Map<String, Object>> collectionData = attributeOptionsMap.get("COLLECTION");
        Map<String, Map<String, Object>> sizeData = attributeOptionsMap.get("SIZE");
        Map<String, Map<String, Object>> sizeCodeData = attributeOptionsMap.get("SIZE_CODE");
        Map<String, Map<String, Object>> metricSizeData = attributeOptionsMap.get("METRIC_SIZE");
        Map<String, Map<String, Object>> paperWeightData = attributeOptionsMap.get("PAPER_WEIGHT");
        Map<String, Map<String, Object>> paperTextureData = attributeOptionsMap.get("PAPER_TEXTURE");
        Map<String, Map<String, Object>> availabilityData = attributeOptionsMap.get("AVAILABILITY");
        Map<String, Map<String, Object>> recycledPercentData = attributeOptionsMap.get("RECYCLED_PERCENT");
        Map<String, Map<String, Object>> recycledContentData = attributeOptionsMap.get("RECYCLED_CONTENT");
        Map<String, Map<String, Object>> compareToBrandData = attributeOptionsMap.get("BRAND");
        Map<String, Map<String, Object>> brandCollectionData = attributeOptionsMap.get("BRAND_COLLECTION");
        Map<String, Map<String, Object>> sealingMethodData = attributeOptionsMap.get("SEALING_METHOD");
        Map<String, Map<String, Object>> sealingMethodTypeData = attributeOptionsMap.get("SEALING_METHOD_TYPE");
        Map<String, Map<String, Object>> windowSizeData = attributeOptionsMap.get("EXISTING_WINDOW_SIZE");
        Map<String, Map<String, Object>> windowPositionData = attributeOptionsMap.get("EXISTING_WINDOW_POSITION");
        Map<String, Map<String, Object>> windowLocationData = attributeOptionsMap.get("EXISTING_WINDOW_LOCATION");
//        Map<String, Map<String, Object>> colorNameData = attributeOptionsMap.get("COLOR_NAME");
        Map<String, Map<String, Object>> colorGroupData = attributeOptionsMap.get("COLOR_GROUP");
        Map<String, Map<String, Object>> colorData = attributeOptionsMap.get("COLOR");
        Set<List<Object>> missingParentProductsData = new HashSet<>();
        Map<String, Set<List<Object>>> missingAttributesMap = new LinkedHashMap<>();
        missingAttributesMap.put("COLLECTION", new HashSet<>());
        missingAttributesMap.put("SIZE", new HashSet<>());
        missingAttributesMap.put("SIZE_CODE", new HashSet<>());
        missingAttributesMap.put("METRIC_SIZE", new HashSet<>());
        missingAttributesMap.put("PAPER_WEIGHT", new HashSet<>());
        missingAttributesMap.put("PAPER_TEXTURE", new HashSet<>());
        missingAttributesMap.put("AVAILABILITY", new HashSet<>());
        missingAttributesMap.put("RECYCLED_PERCENT", new HashSet<>());
        missingAttributesMap.put("RECYCLED_CONTENT", new HashSet<>());
        missingAttributesMap.put("BRAND", new HashSet<>());
        missingAttributesMap.put("BRAND_COLLECTION", new HashSet<>());
        missingAttributesMap.put("SEALING_METHOD", new HashSet<>());
        missingAttributesMap.put("SEALING_METHOD_TYPE", new HashSet<>());
        missingAttributesMap.put("EXISTING_WINDOW_SIZE", new HashSet<>());
        missingAttributesMap.put("EXISTING_WINDOW_POSITION", new HashSet<>());
        missingAttributesMap.put("EXISTING_WINDOW_LOCATION", new HashSet<>());
//        missingAttributesMap.put("COLOR_NAME", new HashSet<>());
        missingAttributesMap.put("COLOR_GROUP", new HashSet<>());
        missingAttributesMap.put("COLOR", new HashSet<>());
        int numOfHeaderRows = 5;
        for(int i = numOfHeaderRows; i < products.size(); i ++) {
            String oldParentProductName = (String)products.get(i).get(parentNameIdx);
            if(!oldParentProductName.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(parentCodeIdx);
                if (parentProductsMap.containsKey(key)) {

                    Map<String, Object> map = parentProductsMap.get(key);
                    if (map.get("NEW_VALUE").toString().isEmpty()) {
                        status = 0;
                        products.get(i).set(parentNameIdx + 1, map.get("VALUE"));
                        products.get(i).set(productPageNameIdx + 1, map.get("VALUE"));
                    } else {
                        status = 1;
                        products.get(i).set(parentNameIdx + 1, map.get("NEW_VALUE"));
                        products.get(i).set(productPageNameIdx + 1, map.get("NEW_VALUE"));
                    }
                } else {
                    products.get(i).set(parentNameIdx + 1, oldParentProductName);
                    products.get(i).set(productPageNameIdx + 1, oldParentProductName);
                    missingParentProductsData.add(Arrays.asList(key, products.get(i).get(parentNameIdx)));
                }
                products.get(i).set(parentNameIdx + 2, status);
                products.get(i).set(productPageNameIdx + 2, status);
            }

            //##########################################################################################################

            int idx = collectionIdx;
            String existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (collectionData.containsKey(key)) {
                    Map<String, Object> map = collectionData.get(key);
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("COLLECTION").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue));
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = sizeIdx;
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (sizeData.containsKey(key)) {
                    Map<String, Object> map = sizeData.get(key);
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("SIZE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue));
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = sizeCodeIdx;
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (sizeCodeData.containsKey(key)) {
                    Map<String, Object> map = sizeCodeData.get(key);
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("SIZE_CODE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue));
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = metricSizeIdx;
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (metricSizeData.containsKey(key)) {
                    Map<String, Object> map = metricSizeData.get(key);
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("METRIC_SIZE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue));
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = paperWeightIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (paperWeightData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = paperWeightData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("PAPER_WEIGHT").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = paperTextureIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (paperTextureData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = paperTextureData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("PAPER_TEXTURE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = availabilityIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (availabilityData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = availabilityData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("AVAILABILITY").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = recycledPercentIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (recycledPercentData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = recycledPercentData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("RECYCLED_PERCENT").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = recycledContentIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (recycledContentData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = recycledContentData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("RECYCLED_CONTENT").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = compareToBrandIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (compareToBrandData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = compareToBrandData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("BRAND").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            /*idx = brandCollectionIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (brandCollectionData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = brandCollectionData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("BRAND_COLLECTION").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }*/
            //##########################################################################################################

            idx = sealingMethodIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (sealingMethodData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = sealingMethodData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("SEALING_METHOD").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = sealingMethodTypeIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (sealingMethodTypeData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = sealingMethodTypeData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("SEALING_METHOD_TYPE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = windowSizeIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (windowSizeData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = windowSizeData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("EXISTING_WINDOW_SIZE").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = windowPositionIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (windowPositionData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = windowPositionData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("EXISTING_WINDOW_POSITION").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            idx = windowLocationIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (windowLocationData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = windowLocationData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("EXISTING_WINDOW_LOCATION").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            /*idx = colorNameIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (colorNameData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = colorNameData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("COLOR_NAME").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }*/


            //##########################################################################################################

            idx = colorGroupIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (colorGroupData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = colorGroupData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("COLOR_GROUP").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }


            //##########################################################################################################

            idx = colorIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (colorData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = colorData.get(key); // TODO - 3
                    if (existingValue.equals(map.get("NEW_VALUE").toString())) {
                        status = 0;
                    } else {
                        System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                        status = 1;
                    }
                    products.get(i).set(idx + 1, map.get("NEW_VALUE"));
                } else {
                    products.get(i).set(idx + 1, existingValue);
                    missingAttributesMap.get("COLOR").add(Arrays.asList(ValidatableEntity.toId(existingValue), existingValue)); // TODO - 4
                }
                products.get(i).set(idx + 2, status);
            }

            //##########################################################################################################

            if("Y".equalsIgnoreCase((String)products.get(i).get(header.indexOf("Recycled")))) {
                products.get(i).set(header.indexOf("Recycled"), "Y");
            } else {
                products.get(i).set(header.indexOf("Recycled"), "N");
            }

            //##########################################################################################################

            if("Y".equalsIgnoreCase((String)products.get(i).get(header.indexOf("Laser")))) {
                products.get(i).set(header.indexOf("Laser"), "Y");
            } else {
                products.get(i).set(header.indexOf("Laser"), "N");
            }

            //##########################################################################################################

            if("Y".equalsIgnoreCase((String)products.get(i).get(header.indexOf("Inkjet")))) {
                products.get(i).set(header.indexOf("Inkjet"), "Y");
            } else {
                products.get(i).set(header.indexOf("Inkjet"), "N");
            }
        }
        Map<String, List<List<Object>>> groupedProducts = new LinkedHashMap<>();
        List<Object> headerRow = requiredOutputFieldsmap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        for(int i = numOfHeaderRows; i < products.size(); i ++) {
            String category = (String)products.get(i).get(header.indexOf("Category"));
            if(category.isEmpty()) {
                category = "NO_CATEGORY";
            }
            if(!groupedProducts.containsKey(category)) {
                groupedProducts.put(category, new ArrayList<>());
                groupedProducts.get(category).add(headerRow);
            }
            List<Object> filteredProduct = new ArrayList<>();
            int _i = i;
            requiredOutputFieldsmap.forEach((k,v) -> filteredProduct.add(products.get(_i).get(header.indexOf(k))));
            groupedProducts.get(category).add(filteredProduct);
        }
        Map<String, List<List<Object>>> missingParentProductsMap = new HashMap<>();
        missingParentProductsMap.put("Parent Products", new ArrayList<>(missingParentProductsData));
        POIUtil.writeData(missingParentProductsFilePath, missingParentProductsMap);
        POIUtil.writeData(missingAttributeOptionsFilePath, missingAttributesMap.entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(CollectionsUtil.toLinkedMap(e -> e.getKey(), e -> new ArrayList(e.getValue()))));
        POIUtil.writeData(updatedProductFeedFilePath, "PRODUCTS", products);
        POIUtil.writeData(categorizedProductFeedFilePath, groupedProducts);
    }

    public static Map<String, Map<String, Map<String, Object>>> readAttributesData(String filePath) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                data.put(sheet.getSheetName(), new LinkedHashMap<>());
                Iterator<Row> rows = sheet.iterator();
                List<String> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                while(!headers.isEmpty() && rows.hasNext()) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                String cellData = cell.getStringCellValue().replaceAll("┬«", "®").replaceAll("Γäó", "™").replaceAll("ΓÇ¥", "”");
                                rowData.put(headers.get(cellNum), cellData);
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.put(headers.get(cellNum), cell.getNumericCellValue());
                                } else {
                                    rowData.put(headers.get(cellNum), Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.put(headers.get(cellNum), "");
                                break;
                            default:
                                rowData.put(headers.get(cellNum), "");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                    }
                    if(rowData.entrySet().stream().anyMatch(e -> !e.getValue().toString().isEmpty())) {
                        data.get(sheet.getSheetName()).put((String)rowData.get("VALUE"), rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Map<String, Map<String, Map<String, Object>>> readParentProductsData(String filePath) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                data.put(sheet.getSheetName(), new LinkedHashMap<>());
                Iterator<Row> rows = sheet.iterator();
                List<String> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                while(!headers.isEmpty() && rows.hasNext()) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                String cellData = cell.getStringCellValue().replaceAll("┬«", "®").replaceAll("Γäó", "™").replaceAll("ΓÇ¥", "”");
                                rowData.put(headers.get(cellNum), cellData);
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.put(headers.get(cellNum), cell.getNumericCellValue());
                                } else {
                                    rowData.put(headers.get(cellNum), Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.put(headers.get(cellNum), "");
                                break;
                            default:
                                rowData.put(headers.get(cellNum), "");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                    }
                    if(rowData.entrySet().stream().anyMatch(e -> !e.getValue().toString().isEmpty())) {
                        data.get(sheet.getSheetName()).put((String)rowData.get("PARENT PRODUCT ID"), rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<List<Object>> readData(String filePath) {
        List<List<Object>> data = new ArrayList<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rows = sheet.iterator();
                List<Object> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                data.add(headers);
                while(!headers.isEmpty() && rows.hasNext()) {
                    List<Object> rowData = new ArrayList<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.add("");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                String cellData = cell.getStringCellValue().replaceAll("┬«", "®").replaceAll("Γäó", "™").replaceAll("ΓÇ¥", "”");
                                rowData.add(cellData.trim());
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.add(cell.getNumericCellValue());
                                } else {
                                    rowData.add(Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.add("");
                                break;
                            default:
                                rowData.add("");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.add("");
                            cellNum ++;
                        }
                    }
                    if(rowData.stream().anyMatch(e -> !e.toString().isEmpty())) {
                        data.add(rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        doCleanup();
    }

}
