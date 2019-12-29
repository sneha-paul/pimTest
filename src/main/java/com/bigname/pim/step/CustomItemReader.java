package com.bigname.pim.step;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.service.ProductService;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomItemReader implements ItemReader<ProductVariant> {

    private ItemReader<ProductVariant> delegate;
    @Autowired
    private ProductService productService;

    @Override
    public ProductVariant read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(readData());
        }
        return delegate.read();
    }

    private List<ProductVariant> readData() {

        List<ProductVariant> productVariants = productService.getProductVariants(ID.EXTERNAL_ID("10_REGULAR"), "ECOMMERCE", null, false);

        return productVariants;

    }
}
