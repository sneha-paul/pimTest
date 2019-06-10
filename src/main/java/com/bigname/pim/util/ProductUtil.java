package com.bigname.pim.util;

import com.bigname.pim.api.domain.FileAsset;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.m7.common.util.ConversionUtil;
import com.m7.common.util.ValidationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductUtil {

    public static List<Map<String, Object>> orderAssets(List<Map<String, Object>> productAssets) {
        //order the list by sequenceNum ascending
        productAssets.sort((a1, a2) -> {
            int seq1 = (int) a1.get("sequenceNum");
            int seq2 = (int) a2.get("sequenceNum");
            return seq1 > seq2 ? 1 : -1;
        });
        return productAssets;
    }

    public static void setDefaultAsset(List<Map<String, Object>> productAssets, String assetId) {
        resetDefaultAsset(productAssets);
        productAssets.forEach(asset -> {
            if(asset.get("id").equals(assetId)) {
                asset.put("defaultFlag", "Y");
            }
        });
    }

    public static void resetDefaultAsset(List<Map<String, Object>> productAssets) {
        productAssets.forEach(asset -> asset.put("defaultFlag", "N"));
    }

    public static List<Map<String, Object>> reorderAssets(List<Map<String, Object>> productAssets, List<String> assetIds){

        //Set the sequence number with the index of their ids in the assetIds list
        productAssets.forEach(asset -> asset.put("sequenceNum", assetIds.indexOf((String)asset.get("id"))));

        //Order the assets by sequence number before saving to the database
        return ProductUtil.orderAssets(productAssets);
    }

    public static void validateDefaultAsset(List<Map<String, Object>> productAssets) {
        // Check if there is one default asset available and also check if there are multiple default assets set for the given product.
        if(!productAssets.isEmpty()) {
            List<Integer> defaultIndices = new ArrayList<>();
            for (int i = 0; i < productAssets.size(); i++) {
                Map<String, Object> assetMap = productAssets.get(i);
                if ("Y".equals(assetMap.get("defaultFlag"))) {
                    defaultIndices.add(i);
                }
            }
            if (defaultIndices.isEmpty()) { // No default asset available, so set the first one as the default
                productAssets.get(0).put("defaultFlag", "Y");
            } else if(defaultIndices.size() > 1) { // More than one default asset is available, so reset everything except the last one
                for(int i = 0; i < defaultIndices.size() - 1; i ++) {
                    productAssets.get(i).put("defaultFlag", "N");
                }
            }
        }
    }

    public static List<Map<String, Object>> deleteAsset(List<Map<String, Object>> productAssets, String assetId) {
        //Find the index of the item that needs to be removed
        int removeIdx = productAssets.indexOf(productAssets.stream().filter(asset -> asset.get("id").equals(assetId)).findFirst().orElse(null));
        if(removeIdx > -1) {
            // Remove the asset
            productAssets.remove(removeIdx);
            // Reset the sequence nums
            productAssets = ProductUtil.orderAssets(productAssets);
            // Validate the default asset, in case we removed the default asset
            validateDefaultAsset(productAssets);
        }
        return productAssets;
    }

    public static Map<String, Object> getDefaultAsset(Product product, FileAsset.AssetFamily family) {
        Map<String, Object> defaultAsset = null;
        if(product.getChannelAssets() != null && product.getChannelAssets().containsKey(family.name())) {
            List<Map<String, Object>> assets = ConversionUtil.toGenericMap(product.getChannelAssets().get(family.name()));
            defaultAsset = assets.stream().filter(assetMap -> "Y".equals(assetMap.get("defaultFlag"))).findFirst().orElse(assets.isEmpty() ? null : assets.get(0));
        }
        return defaultAsset;
    }

    public static Map<String, Object> getDefaultAsset(Map<String, Object> channelAssets, FileAsset.AssetFamily family) {
        Map<String, Object> defaultAsset = null;
        if(channelAssets != null && channelAssets.containsKey(family.name())) {
            List<Map<String, Object>> assets = ConversionUtil.toGenericMap(channelAssets.get(family.name()));
            defaultAsset = assets.stream().filter(assetMap -> "Y".equals(assetMap.get("defaultFlag"))).findFirst().orElse(assets.isEmpty() ? null : assets.get(0));
        }
        return defaultAsset;
    }

    public static Map<String, Object> getDefaultAsset(ProductVariant productVariant, FileAsset.AssetFamily family) {
        Map<String, Object> defaultAsset = null;
        if(productVariant.getVariantAssets().containsKey(family.name())) {
            List<Map<String, Object>> assets = ConversionUtil.toGenericMap(productVariant.getVariantAssets().get(family.name()));
            defaultAsset = assets.stream().filter(assetMap -> "Y".equals(assetMap.get("defaultFlag"))).findFirst().orElse(assets.isEmpty() ? null : assets.get(0));
        }
        return defaultAsset;
    }

    public static Map<String, List<ProductVariant>> groupVariantsByProduct(List<ProductVariant> variants) {
        Map<String, List<ProductVariant>> productVariantsMap = new HashMap<>();
        variants.forEach(productVariant -> {
            if(!productVariantsMap.containsKey(productVariant.getProductId())) {
                productVariantsMap.put(productVariant.getProductId(), new ArrayList<>());
            }
            productVariantsMap.get(productVariant.getProductId()).add(productVariant);
        });

        return productVariantsMap;
    }

    public static Map<String, Map<String, Object>> getVariantDetailsForProducts(List<String> productIds, List<ProductVariant> variants, int maxCount) {
        Map<String, Map<String, Object>> details = new HashMap<>();
        Map<String, List<ProductVariant>> productVariantsMap = groupVariantsByProduct(variants);
        productIds.forEach(productId -> {
            List<ProductVariant> productVariants = productVariantsMap.containsKey(productId) ? productVariantsMap.get(productId) : new ArrayList<>();

            Map<String, Object> info = new HashMap<>();
            info.put("totalVariants", productVariants.size());
            List<String> variantImages = new ArrayList<>();
            for(int i = 0; i < productVariants.size() && i < maxCount; i ++) {
                Map<String, Object> defaultAsset = productVariants.get(i).getDefaultAsset();
                if(ValidationUtil.isNotEmpty(defaultAsset)) {
                    variantImages.add((String) defaultAsset.get("internalName"));
                } else {
                    variantImages.add("noimage.png");
                }
            }
            info.put("variantImages", variantImages);
            details.put(productId, info);
        });

        return details;
    }

}
