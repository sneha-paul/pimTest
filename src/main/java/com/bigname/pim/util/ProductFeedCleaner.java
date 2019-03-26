package com.bigname.pim.util;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.domain.ValidatableEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    static Map<String, String> map = new LinkedHashMap<>();

    static {
        map.put("Attribute Name", "");
        map.put("Code", "PARENT_PRODUCT_ID");
        map.put("Product Type", "PRODUCT_TYPE");
        map.put("Netsuite Category", "NETSUITE_CATEGORY");
        map.put("Category", "ROOT_CATEGORY");
        map.put("Style", "PRODUCT_CATEGORY/STYLE");
        map.put("Parent Code", "PARENT_PRODUCT_ID");
        map.put("Old Parent Name", "");
        map.put("Parent Name", "PARENT_PRODUCT_NAME");
        map.put("Parent Name Status", "");
        map.put("Old Product Page Name", "");
        map.put("Product Page Name", "PRODUCT_PAGE_NAME");
        map.put("Product Page Name Status", "");
        map.put("Existing Color Group", "");
        map.put("Old Color Group", "");
        map.put("Color Group", "COLOR_GROUP");
        map.put("Color Group Status", "");
        map.put("Old Color", "");
        map.put("Color", "COLOR");
        map.put("Color Status", "");
        map.put("Existing Color Name", "COLOR_NAME");
        map.put("Old Collection", "");
        map.put("Collection", "COLLECTION");
        map.put("Collection Status", "");
        map.put("Old Size", "");
        map.put("Size", "SIZE");
        map.put("Size Status", "");
        map.put("Old Size Code", "");
        map.put("Size Code", "SIZE_CODE");
        map.put("Size Code Status", "");
        map.put("Old Metric Size", "");
        map.put("Metric Size", "METRIC_SIZE");
        map.put("Metric Size Status", "");
        map.put("Old Paper Weight", "");
        map.put("Paper Weight", "PAPER_WEIGHT");
        map.put("Paper Weight Status", "");
        map.put("Old Paper Texture", "");
        map.put("Paper Texture", "PAPER_TEXTURE");
        map.put("Paper Texture Status", "");
        map.put("Old Availability", "");
        map.put("Availability", "AVAILABILITY");
        map.put("Availability Status", "");
        map.put("Old Recycled Percent", "");
        map.put("Recycled Percent", "RECYCLED_PERCENT");
        map.put("Recycled Percent Status", "");
        map.put("Old Recycled Content", "");
        map.put("Recycled Content", "RECYCLED_CONTENT");
        map.put("Recycled Content Status", "");
        map.put("Old Brand", "");
        map.put("Brand", "BRAND");
        map.put("Brand Status", "");
        map.put("Old Brand Collection", "");
        map.put("Brand Collection", "BRAND COLLECTION");
        map.put("Brand Collection Status", "");
        map.put("Old Window Size", "OLD_WINDOW_SIZE");
        map.put("Window Size", "WINDOW_SIZE");
        map.put("Window Size Status", "");
        map.put("Old Window Position", "OLD_WINDOW_POSITION");
        map.put("Window Position", "WINDOW_POSITION");
        map.put("Window Size Status", "");
        map.put("Old Window Location", "");
        map.put("Window Location", "WINDOW_LOCATION");
        map.put("Window Location Status", "");
        map.put("Old Sealing Method", "OLD_SEALING_METHOD");
        map.put("Sealing Method", "SEALING_METHOD");
        map.put("Sealing Method Status", "");
        map.put("Old Sealing Method Type", "");
        map.put("Sealing Method Type", "SEALING_METHOD_TYPE");
        map.put("Sealing Method Type Status", "");
        map.put("Base Quantity Price", "BASE_QUANTITY_PRICE");
        map.put("Each Weight", "EACH_WEIGHT");
        map.put("Recycled", "RECYCLED");
        map.put("Laser", "LASER");
        map.put("Inkjet", "INKJET");
        map.put("Carton Quantity", "CARTON_QUANTITY");
        map.put("Tagline", "TAGLINE");
        map.put("Shape", "SHAPE");
        map.put("Labels Per Sheet", "LABELS_PER_SHEET");
        map.put("Top Margin", "TOP_MARGIN");
        map.put("Bottom Margin", "BOTTOM_MARGIN");
        map.put("Left Margin", "LEFT_MARGIN");
        map.put("Right Margin", "RIGHT_MARGIN");
        map.put("Corner Radius", "CORNER_RADIUS");
        map.put("Vertical Spacing", "VERTICAL_SPACING");
        map.put("Horizontal Spacing", "HORIZONTAL_SPACING");
        map.put("Backslits", "BACKSLITS");
        map.put("Rush Production", "RUSH_PRODUCTION");
        map.put("Image Url", "");
        map.put("Created Date", "");
        map.put("URL", "");
        map.put("Plain Lead Time", "PLAIN_LEAD_TIME");
        map.put("Standard Lead Time", "STANDARD_LEAD_TIME");
        map.put("Rush Lead Time", "RUSH_LEAD_TIME");
        map.put("Pricing", "PRICING");
        map.put("Style Description", "STYLE_DESCRIPTION");
        map.put("Color Family Description", "COLOR_FAMILY_DESCRIPTION");
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
        int brandIdx = header.indexOf("Old Brand");
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
        Map<String, Map<String, Object>> brandData = attributeOptionsMap.get("BRAND");
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

            idx = brandIdx; // TODO - 1
            existingValue = (String)products.get(i).get(idx);
            if(!existingValue.isEmpty()) {
                int status = -1;
                String key = (String) products.get(i).get(idx);
                if (brandData.containsKey(key)) { // TODO - 2
                    Map<String, Object> map = brandData.get(key); // TODO - 3
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

            idx = brandCollectionIdx; // TODO - 1
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
            }
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
        }
        Map<String, List<List<Object>>> groupedProducts = new LinkedHashMap<>();
        for(int i = numOfHeaderRows; i < products.size(); i ++) {
            String category = (String)products.get(i).get(header.indexOf("Category"));
            if(category.isEmpty()) {
                category = "NO_CATEGORY";
            }
            if(!groupedProducts.containsKey(category)) {
                groupedProducts.put(category, new ArrayList<>());
                groupedProducts.get(category).add(products.get(0));
            }
            groupedProducts.get(category).add(products.get(i));
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
