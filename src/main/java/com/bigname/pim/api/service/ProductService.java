package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 19-09-2018.
 */
public interface ProductService extends BaseService<Product, ProductDAO> {
    Page<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, int page, int size, Sort sort, boolean... activeRequired);
    List<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, Sort sort, boolean... activeRequired);
    Page<Product> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

//    Page<List<Pair<String, String>>> getAvailableVariants(String productId, FindBy externalId, String channelId, Integer pageNumber, Integer pageSize, Sort sort);
    Page<Map<String, String>> getAvailableVariants(String productId, FindBy externalId, String channelId, Integer pageNumber, Integer pageSize, Sort sort);
}
