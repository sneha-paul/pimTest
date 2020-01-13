package com.bigname.pim.core.util;

import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductFeedFinalizer {
    public static final String consolidatedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Cleanedup_Data/PIM_Active_products_ENVELOPES_1-08-20-FINAL.xlsx";
    public static final String finalizedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Cleanedup_Data/Finalized_PIM_Active_products_ENVELOPES_1-08-20.xlsx";
    public static List<List<Object>> consolidatedProducts = POIUtil.readMultiSheetData(consolidatedProductFeedFilePath, 56).get("ENVELOPES");

    static Map<String, List<String>> headerData = new LinkedHashMap<>();
    static {
        headerData.put("AttributeName", List.of("AttributeType", "Parent Attribute", "Delimitters", "Family Group", "FamilySub-Group", "Attribute Level", ""));
        headerData.put("Website", List.of("WEBSITES", "", "", "", "", "0", ""));
        headerData.put("Code", List.of("VARIANT_ID", "", "", "", "", "1", "VARIANT_PRODUCT_ID"));
        headerData.put("Parent Code", List.of("PRODUCT_ID", "", "", "", "", "0", "PARENT_PRODUCT_ID"));
        headerData.put("Parent Name", List.of("PRODUCT_NAME", "", "", "", "", "0", "PARENT_PRODUCT_NAME"));
        headerData.put("Product Type", List.of("FAMILY_ID", "", "", "", "", "0", "PRODUCT_TYPE"));
        headerData.put("Category", List.of("CATEGORY_ID", "", "", "", "", "0", "ROOT_CATEGORY"));
        headerData.put("Style", List.of("DROPDOWN", "", "", "Details", "", "1", "PRODUCT_CATEGORY"));
        headerData.put("Product Page Name", List.of("INPUTBOX", "", "", "Details", "", "1", "PRODUCT_PAGE_NAME"));
        headerData.put("Netsuite Category", List.of("DROPDOWN", "", "", "Details", "", "1", "NETSUITE_CATEGORY"));
        headerData.put("Color Group", List.of("MULTI_SELECT", "", "|", "Product Features", "", "1", "COLOR_GROUP"));
        headerData.put("Color", List.of("DROPDOWN", "", "", "Product Features", "", "1", "COLOR"));
        headerData.put("Color Name", List.of("DROPDOWN", "", "", "Product Features", "", "1", "NEW_COLOR_NAME"));
        headerData.put("Paper Weight", List.of("DROPDOWN", "", "", "Product Features", "", "1", "PAPER_WEIGHT"));
        headerData.put("Paper Texture", List.of("DROPDOWN", "", "", "Product Features", "", "1", "PAPER_TEXTURE"));
        headerData.put("Collection", List.of("DROPDOWN", "", "", "Details", "", "1", "COLLECTION"));
        headerData.put("Size Code", List.of("DROPDOWN", "", "", "Product Features", "", "0", "SIZE_CODE"));
        headerData.put("Size", List.of("DROPDOWN", "", "", "Product Features", "", "0", "SIZE"));
        headerData.put("Metric Size", List.of("DROPDOWN", "", "", "Product Features", "", "0", "METRIC_SIZE"));
        headerData.put("Pricing", List.of("PRICING", "", "", "", "", "1", "PRICING"));
        headerData.put("Base Quantity Price", List.of("INPUTBOX", "", "", "Details", "", "1", "BASE_QUANTITY_PRICE"));
        headerData.put("Each Weight", List.of("INPUTBOX", "", "", "Product Features", "", "1", "EACH_WEIGHT"));
        headerData.put("Style Description", List.of("TEXTAREA", "", "", "Details", "", "1", "STYLE_DESCRIPTION"));
        headerData.put("Color Family Description", List.of("TEXTAREA", "", "", "Details", "", "1", "COLOR_FAMILY_DESCRIPTION"));
        headerData.put("Availability", List.of("DROPDOWN", "", "", "Details", "", "1", "AVAILABILITY"));
        headerData.put("Recycled Content", List.of("DROPDOWN", "", "", "Product Features", "", "1", "RECYCLED_CONTENT"));
        headerData.put("Compare to Brand", List.of("DROPDOWN", "", "", "Product Features", "", "1", "COMPARE_TO_BRAND"));
        headerData.put("Window Size", List.of("DROPDOWN", "", "", "Product Features", "", "1", "WINDOW_SIZE"));
        headerData.put("Window Position", List.of("DROPDOWN", "", "", "Product Features", "", "1", "WINDOW_POSITION"));
        headerData.put("Sealing Method", List.of("DROPDOWN", "", "", "Product Features", "", "1", "SEALING_METHOD"));
        headerData.put("Recycled", List.of("YES_NO", "", "", "Details", "General Options", "1", "RECYCLED"));
        headerData.put("Laser", List.of("YES_NO", "", "", "Details", "General Options", "1", "LASER"));
        headerData.put("Inkjet", List.of("YES_NO", "", "", "Details", "General Options", "1", "INKJET"));
        headerData.put("Carton Quantity", List.of("INPUTBOX", "", "", "Details", "", "1", "CARTON_QUANTITY"));
        headerData.put("Recycled Percent", List.of("DROPDOWN", "", "", "Product Features", "", "1", "RECYCLED_PERCENT"));
        headerData.put("Tagline", List.of("INPUTBOX", "", "", "Details", "", "1", "TAGLINE"));
        headerData.put("Shape", List.of("INPUTBOX", "", "", "Product Features", "", "1", "SHAPE"));
        headerData.put("Labels Per Sheet", List.of("INPUTBOX", "", "", "Details", "", "1", "LABELS_PER_SHEET"));
        headerData.put("Top Margin", List.of("INPUTBOX", "", "", "Details", "", "1", "TOP_MARGIN"));
        headerData.put("Bottom Margin", List.of("INPUTBOX", "", "", "Details", "", "1", "BOTTOM_MARGIN"));
        headerData.put("Left Margin", List.of("INPUTBOX", "", "", "Details", "", "1", "LEFT_MARGIN"));
        headerData.put("Right Margin", List.of("INPUTBOX", "", "", "Details", "", "1", "RIGHT_MARGIN"));
        headerData.put("Corner Radius", List.of("INPUTBOX", "", "", "Details", "", "1", "CORNER_RADIUS"));
        headerData.put("Vertical Spacing", List.of("INPUTBOX", "", "", "Details", "", "1", "VERTICAL_SPACING"));
        headerData.put("Horizontal Spacing", List.of("INPUTBOX", "", "", "Details", "", "1", "HORIZONTAL_SPACING"));
        headerData.put("Backslits", List.of("INPUTBOX", "", "", "Details", "", "1", "BACKSLITS"));
        headerData.put("Rush Production", List.of("YES_NO", "", "", "Details", "General Options", "1", "RUSH_PRODUCTION"));
        //headerData.put("Image Url", List.of("IGNORE", "", "", "", "", "1", ""));
        //headerData.put("Created Date", List.of("IGNORE", "", "", "", "", "1", ""));
        //headerData.put("URL", List.of("INPUTBOX", "", "", "Details", "", "1", ""));
        //headerData.put("Printable", List.of("YES_NO", "", "", "Details", "General Options", "0", ""));
        headerData.put("Plain Lead Time", List.of("INPUTBOX", "", "", "Details", "Product Lead Time", "0", "PLAIN_LEAD_TIME"));
        headerData.put("Standard Lead Time", List.of("INPUTBOX", "", "", "Details", "Product Lead Time", "0", "STANDARD_LEAD_TIME"));
        headerData.put("Rush Lead Time", List.of("INPUTBOX", "", "", "Details", "Product Lead Time", "0", "RUSH_LEAD_TIME"));
        //headerData.put("Pocket Style", List.of("MULTI_SELECT", "", "|", "Product Features", "", "1", ""));
        //headerData.put("Reinforced Edge", List.of("DROPDOWN", "", "", "Product Features", "", "1", ""));
        //headerData.put("Dimension Closed", List.of("DROPDOWN", "Pocket Style", "|", "Product Features", "", "1", ""));
        //headerData.put("Dimension Open", List.of("DROPDOWN", "Pocket Style", "|", "Product Features", "", "1", ""));
        //headerData.put("Individual Availability", List.of("DROPDOWN", "Pocket Style", "|", "Product Features", "", "1", ""));
        //headerData.put("Imprint Method", List.of("DROPDOWN", "", "", "Product Features", "", "1", ""));
        //headerData.put("Vendor", List.of("DROPDOWN", "", "", "Product Features", "", "1", ""));
        //headerData.put("Vendor Sku", List.of("MULTI_SELECT", "", ";", "Product Features", "", "1", ""));
        //headerData.put("Card Slits", List.of("MULTI_SELECT", "Pocket Style", "|;", "Product Features", "", "1", ""));
        //headerData.put("Number Of Pockets", List.of("DROPDOWN", "Pocket Style", "|", "Product Features", "", "1", ""));
        //headerData.put("Number Of Panels", List.of("DROPDOWN", "", "", "Product Features", "", "1", ""));
        //headerData.put("Pocket Spec", List.of("DROPDOWN", "", "", "Product Features", "", "1", ""));
        //headerData.put("Image Size", List.of("DROPDOWN", "", "", "Details", "", "0", ""));
    }

    public static List<List<Object>> finalizeFeed() {
        Map<String, Map<String, Object>> productsMap1 = getProductsMap(consolidatedProducts);
        return null;
    }

    public static Map<String, Map<String, Object>> getProductsMap(List<List<Object>> productsList) {
        List<Object> header = productsList.get(0);
        Map<String, Map<String, Object>> productsMap = new LinkedHashMap<>();
        for(int i = 1; i < productsList.size(); i ++) {

            List<Object> productAttributeList = productsList.get(i);
            Map<String, Object> productAttributeMap = new LinkedHashMap<>();

            for(int j = 0; j < header.size(); j++) {
                String key = (String) header.get(j);
                if(ValidationUtil.isNotEmpty(key)) {
                    productAttributeMap.put(key, productAttributeList.get(j));
                }
            }
            productsMap.put((String)productAttributeMap.get("VARIANT_PRODUCT_ID"), productAttributeMap);
        }
        return productsMap;
    }

    public static void main(String[] args) {
        List<List<Object>> finalizedData = finalizeFeed();
        POIUtil.writeData(consolidatedProductFeedFilePath, "ENVELOPES", finalizedData);
    }
}
