package com.bigname.pim.data.loader.exporter;

import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sruthi on 31-01-2019.
 */
@Component
public class ProductExporter {

    @Autowired
    private ProductService productService;

    public boolean exportData(String filePath) {
        List<Product> productData = productService.getAll(null, true);
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"PRODUCT_ID", "PRODUCT_NAME", "PRODUCT_FAMILY", "ACTIVE", "DISCONTINUED", "ID" });
        int i=2;
        for (Iterator<Product> iter = productData.iterator(); iter.hasNext(); ) {
            Product element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getProductName(), element.getProductFamilyId(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }
        POIUtil.writeData(filePath, "product", data);
        return true;
    }
}
