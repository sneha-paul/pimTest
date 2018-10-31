package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;

/**
 * Created by sruthi on 20-09-2018.
 */

@Service
public class ProductVariantServiceImpl extends BaseServiceSupport<ProductVariant, ProductVariantDAO> implements ProductVariantService {

    private ProductVariantDAO productVariantDAO;


    @Autowired
    public ProductVariantServiceImpl(ProductVariantDAO productVariantDAO, Validator validator) {
        super(productVariantDAO, "productVariant", validator);
        this.productVariantDAO = productVariantDAO;
    }

    @Override
    public ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }

    @Override
    public Page<ProductVariant> getAll(String productId, String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = new Sort(Sort.Direction.ASC, "externalId");
        }
        return productVariantDAO.findAllByProductIdAndChannelIdAndActiveIn(productId, channelId, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    @Override
    public List<ProductVariant> getAll(String productId, String channelId, Sort sort, boolean... activeRequired) {
        return getAll(productId, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }
}
