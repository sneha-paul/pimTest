package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bigname.common.util.ValidationUtil.isNotEmpty;

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

    @Override
    public Optional<ProductVariant> get(String productId, String channelId, String productVariantId, boolean... activeRequired) {
        return productVariantDAO.findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(productId, channelId, productVariantId, PimUtil.getActiveOptions(activeRequired));
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Pair<String, Object>> fieldErrors, ProductVariant productVariant, String group) {
        FamilyAttributeGroup masterGroup = productVariant.getProduct().getProductFamily().getAttributes().get(group + "_GROUP");
        if(isNotEmpty(masterGroup)) {
            FamilyAttributeGroup sectionGroup = masterGroup.getChildGroups().get(AttributeGroup.DEFAULT_GROUP_ID);
            sectionGroup.getChildGroups().forEach((k, attributeGroup) ->
                attributeGroup.getAttributes().forEach((k1, attribute) -> {
                    if(attribute.getType(productVariant.getChannelId()) == FamilyAttribute.Type.VARIANT) {
                        if (attribute.getUiType() == Attribute.UIType.CHECKBOX && !productVariant.getVariantAttributes().containsKey(k1)) {
                            productVariant.getVariantAttributes().put(k1, new String[0]);
                        } else if (attribute.getUiType() == Attribute.UIType.YES_NO && !productVariant.getVariantAttributes().containsKey(k1)) {
                            productVariant.getVariantAttributes().put(k1, "N");
                        }
                    }
                    Pair<String, Object> error = attribute.validate(productVariant.getVariantAttributes().get(attribute.getId()), productVariant.getChannelId(), productVariant.getLevel());
                    if(isNotEmpty(error)) {
                        fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return fieldErrors;
    }
}
