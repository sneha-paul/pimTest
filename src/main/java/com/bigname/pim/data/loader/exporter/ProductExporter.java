package com.bigname.pim.data.loader.exporter;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sruthi on 31-01-2019.
 */
@Component
public class ProductExporter {

    @Autowired
    private ProductVariantService productVariantService;

    public boolean exportData(String filePath) {
        List<Map<String, Object>> productVariantData = productVariantService.getAll();

        Map<String, Object[]> data = new TreeMap<String, Object[]>();
      //  data.put("1", new Object[]{"PRODUCT_ID", "PRODUCT_NAME", "PRODUCT_FAMILY", "ACTIVE", "DISCONTINUED", "ID" });
       /* int i=2;
        for (Iterator<Map<String, Object>> iter = productVariantData.iterator(); iter.hasNext(); ) {
            ProductVariant element = (ProductVariant) iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getProduct().getProductName(), element.getProduct().getProductFamily().getFamilyName(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }*/

        List<Map<String, Object>> variantsAttributes = new ArrayList<>();
        Set<String> header = new HashSet<>();
        productVariantData.forEach(variant -> {
            int i=2;
            Map<String, Object> variantAttributesMap = new HashMap<>();
            variant.forEach((key, value) -> {
                if(value instanceof String) {
                    variantAttributesMap.put(key, value);
                }
                Map<String, Object> scopedProductAttributes = (Map<String, Object>)((Map<String, Object>)variant.get("scopedFamilyAttributes")).get("ECOMMERCE");
                Map<String, Object> pricingDetails = (Map<String, Object>)(Map<String, Object>)variant.get("pricingDetails");
                Map<String, Object> variantAttributes = (Map<String, Object>)(Map<String, Object>)variant.get("variantAttributes");
                variantAttributesMap.putAll(scopedProductAttributes);
                variantAttributesMap.putAll(pricingDetails);
                variantAttributesMap.putAll(variantAttributes);
            });
            header.addAll(variantAttributesMap.keySet());
            variantsAttributes.add(variantAttributesMap);
            data.put(Integer.toString(i), new Object[]{variantAttributesMap.values().toString()});
            i++;
        });

        data.put("1", header.toArray());
        POIUtil.writeData(filePath, "product", data);
        return true;
    }
}

