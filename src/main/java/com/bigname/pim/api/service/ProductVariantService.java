package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * Created by sruthi on 20-09-2018.
 */
public interface ProductVariantService extends BaseService<ProductVariant, ProductVariantDAO> {

    Page<ProductVariant> getAll(String productId, String channelId, int page, int size, Sort sort, boolean... activeRequired);

    List<ProductVariant> getAll(String productId, String channelId, Sort sort, boolean... activeRequired);

    Optional<ProductVariant> get(String productId, String channelId, String productVariantId, boolean... activeRequired);

    boolean toggle(String productId, String channelId, String productVariantId, Toggle active);
    
}
