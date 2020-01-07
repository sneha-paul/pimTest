package com.bigname.pim.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductFeedConsolidator {
    public static final String categorizedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/PIM - Categorized_Active_Products_4-23-19.xlsx";
    public static final String consolidatedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Consolidated_Active_Products_ENVELOPES-" + PimUtil.getTimestamp() + ".xlsx";

    public static Map<String, List<List<Object>>> groupedProducts = POIUtil.readMultiSheetData(categorizedProductFeedFilePath, 52);
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

    public static void consolidate() {
        List<List<Object>> consolidatedData = new ArrayList<>();
        groupedProducts.forEach((k, v) -> {
            if(consolidatedData.isEmpty()) {
                consolidatedData.addAll(v);
            } else {
                List<List<Object>> _v = new ArrayList<>(v);
                _v.remove(0);
                consolidatedData.addAll(_v);
            }
        });
        POIUtil.writeData(consolidatedProductFeedFilePath, "ENVELOPES", consolidatedData);
    }


    public static void main(String[] args) {
        consolidate();
    }
}
