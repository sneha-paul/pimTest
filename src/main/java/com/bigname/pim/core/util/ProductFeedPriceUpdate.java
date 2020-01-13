package com.bigname.pim.core.util;

import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductFeedPriceUpdate {
    public static final String consolidatedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Cleanedup_Data/PIM_Active_products_ENVELOPES_1-08-20-FINAL.xlsx";
    public static final String patchFeedFilePath = "/usr/local/pim/uploads/data/cleanup/01132020/FULL_ActiveFeed_PROD_011320201200-1578939659491.xlsx";
    public static final String patchedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Cleanedup_Data/PIM_Active_products_ENVELOPES_1-08-20-FINAL-PATCHED.xlsx";

    public static void compare() {
        Map<String, List<List<Object>>> categorizedProducts = new LinkedHashMap<>();
        categorizedProducts.put("ENVELOPES", new ArrayList<>());

        Map<String, List<List<Object>>> groupedProducts1 = POIUtil.readMultiSheetData(consolidatedProductFeedFilePath, 56);
        Map<String, List<List<Object>>> groupedProducts2 = POIUtil.readMultiSheetData(patchFeedFilePath, 74);

        List<List<Object>> consolidatedProductsList1 = consolidate(groupedProducts1);
        List<List<Object>> consolidatedProductsList2 = consolidate(groupedProducts2);

        Map<String, Map<String, Object>> productsMap1 = getProductsMap(consolidatedProductsList1);
        Map<String, Map<String, Object>> productsMap2 = getProductsMap(consolidatedProductsList2);

        categorizedProducts.get("ENVELOPES").add(consolidatedProductsList1.get(0));
        int[] match = {0}, not = {0};
        List<Object> header = new ArrayList<>(consolidatedProductsList1.get(0));
        productsMap1.forEach((variant_id, product) -> {

            if(!productsMap2.containsKey(variant_id)) {
                System.out.println(variant_id);
                not[0] = not[0] + 1;
            } else {
                if(ValidationUtil.isNotEmpty(productsMap2.get(variant_id).get("pricing"))) {
                    productsMap1.get(variant_id).put("PRICING", productsMap2.get(variant_id).get("pricing"));
                }
                match[0] = match[0] + 1;
            }

            List<Object> _product = new ArrayList<>();
            for(int i = 0; i < header.size(); i ++) {
                _product.add(productsMap1.get(variant_id).get((String)header.get(i)));
            }
            categorizedProducts.get("ENVELOPES").add(_product);

        });

        System.out.println(match[0] + "[ " + not[0] + " ]");

        POIUtil.writeData(patchedProductFeedFilePath, categorizedProducts);

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
