package com.bigname.pim.core.util;

import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;

import java.util.*;

public class ProductFeedComparator {
    public static final String categorizedProductFeedFilePath1 = "/usr/local/pim/uploads/data/cleanup/Categorized_Active_Products_ENVELOPES-20200108133224.xlsx";
    public static final String categorizedProductFeedFilePath2 = "/usr/local/pim/uploads/data/cleanup/PIM - Categorized_Active_Products_4-23-19.xlsx";
    public static final String categorizedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Merged_Categorized_Active_products_ENVELOPES-" + PlatformUtil.getTimestamp() + ".xlsx";

    public static void compare() {
        Map<String, List<List<Object>>> categorizedProducts = new LinkedHashMap<>();

        Map<String, List<List<Object>>> groupedProducts1 = POIUtil.readMultiSheetData(categorizedProductFeedFilePath1, 56);
        Map<String, List<List<Object>>> groupedProducts2 = POIUtil.readMultiSheetData(categorizedProductFeedFilePath2, 56);

        List<List<Object>> consolidatedProductsList1 = consolidate(groupedProducts1);
        List<List<Object>> consolidatedProductsList2 = consolidate(groupedProducts2);

        Map<String, Map<String, Object>> productsMap1 = getProductsMap(consolidatedProductsList1);
        Map<String, Map<String, Object>> productsMap2 = getProductsMap(consolidatedProductsList2);

        int[] match = {0}, not = {0};
        List<Object> header = new ArrayList<>(consolidatedProductsList1.get(0));
        header.add("NEW_PRODUCT");
        System.out.println("TOTAL = " + productsMap1.size());

        productsMap1.forEach((variant_id, product) -> {
            String [] rootCategory = new String[]{(String)product.get("ROOT_CATEGORY")};
            if(ValidationUtil.isEmpty(rootCategory[0])) {
                rootCategory[0] = "NO_CATEGORY";
            }

            if(!categorizedProducts.containsKey(rootCategory[0])) {
                categorizedProducts.put(rootCategory[0], new ArrayList<>());
                categorizedProducts.get(rootCategory[0]).add(header);
            }

            if(!productsMap2.containsKey(variant_id)) {
                System.out.println(variant_id);
                not[0] = not[0] + 1;
                productsMap1.get(variant_id).put("NEW_PRODUCT", 0);
            } else {
                if(ValidationUtil.isNotEmpty(productsMap2.get(variant_id).get("OLD_COLOR_NAME_UPDATED"))) {
                    productsMap1.get(variant_id).put("COLOR", productsMap2.get(variant_id).get("OLD_COLOR_NAME_UPDATED"));
                }
                productsMap1.get(variant_id).put("NEW_COLOR_NAME", productsMap2.get(variant_id).get("NEW_COLOR_NAME"));
                productsMap1.get(variant_id).put("COMPARE_TO_BRAND", productsMap2.get(variant_id).get("COMPARE_TO_BRAND"));
                productsMap1.get(variant_id).put("BRAND", productsMap2.get(variant_id).get("BRAND"));
                productsMap1.get(variant_id).put("BRAND COLLECTION", productsMap2.get(variant_id).get("BRAND COLLECTION"));
                productsMap1.get(variant_id).put("SEALING_METHOD_TYPE", productsMap2.get(variant_id).get("SEALING_METHOD_TYPE"));
                productsMap1.get(variant_id).put("NEW_PRODUCT", 1);
                match[0] = match[0] + 1;
            }

            List<Object> _product = new ArrayList<>();
            for(int i = 0; i < header.size(); i ++) {
                _product.add(productsMap1.get(variant_id).get((String)header.get(i)));
            }
            categorizedProducts.get(rootCategory[0]).add(_product);
        });

        System.out.println(match[0] + "[ " + not[0] + " ]");

        POIUtil.writeData(categorizedProductFeedFilePath, categorizedProducts);

    }

    public static List<List<Object>> consolidate(Map<String, List<List<Object>>> groupedProducts) {
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
        return consolidatedData;
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
        compare();
    }




}
