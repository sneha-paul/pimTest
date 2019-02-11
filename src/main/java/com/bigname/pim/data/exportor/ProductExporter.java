package com.bigname.pim.data.exportor;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.data.exporter.BaseExporter;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.POIUtil;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by sruthi on 31-01-2019.
 */
@Component
public class ProductExporter implements BaseExporter<Product, ProductService> {

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private FamilyService familyService;

    public boolean exportData(String filePath) {
        List<Map<String, Object>> productVariantData = productVariantService.getAll();

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
                if(productFamilyId != null){
                    Optional<Family> family = familyService.get(productFamilyId, FindBy.INTERNAL_ID, false);
                    familyId = family.get().getFamilyId();
                }

                Map<String, Object> scopedProductAttributes = (Map<String, Object>)((Map<String, Object>)variant.get("scopedFamilyAttributes")).get("ECOMMERCE");
                Map<String, Object> pricingDetails = (Map<String, Object>)(Map<String, Object>)variant.get("pricingDetails");
                Map<String, Object> variantAttributes = (Map<String, Object>)(Map<String, Object>)variant.get("variantAttributes");

                variantAttributesMap.putAll(scopedProductAttributes);
                variantAttributesMap.put("PRICING_DETAILS", CollectionsUtil.buildMapString(pricingDetails,0).toString());
                variantAttributesMap.putAll(variantAttributes);
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
        data.remove("createdUser");
        POIUtil.writeData(filePath, "Product", data);
        return true;
    }

    @Override
    public String getFileName(Type fileType) {
        return "ProductExport" + PimUtil.getTimestamp() + fileType.getExt();
    }
}

