package com.bigname.pim.data.loader.exporter;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
       // data.put("1", new Object[]{"PRODUCT_ID", "PRODUCT_NAME", "PRODUCT_FAMILY", "ACTIVE", "DISCONTINUED", "ID" });
       /* int i=2;
        for (Iterator<Map<String, Object>> iter = productVariantData.iterator(); iter.hasNext(); ) {
            ProductVariant element = (ProductVariant) iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getProduct().getProductName(), element.getProduct().getProductFamily().getFamilyName(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }*/

        int i=0;
        for (Map<String, Object> featureService : productVariantData) {
            //for (Map.Entry<String, Object> entry : featureService.entrySet()) {
            data.put(Integer.toString(i), new Object[]{ productVariantData.get(i).values()});
            Map variantAttributes = (Map) featureService.get("variantAttributes");
            CollectionsUtil.flattenMap(variantAttributes);
            data.put(Integer.toString(i), new Object[]{featureService.get("externalId"), featureService.get("productName"), featureService.get("productFamilyId"), featureService.get("active"), featureService.get("discontinued"), featureService.get("_id")});
            //}
            i++;
        }


        POIUtil.writeData(filePath, "product", data);
        return true;
    }
}

