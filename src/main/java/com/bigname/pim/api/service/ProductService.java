package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductService extends BaseService<Product, ProductDAO> {
    Page<ProductVariant> getProductVariants(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

}
