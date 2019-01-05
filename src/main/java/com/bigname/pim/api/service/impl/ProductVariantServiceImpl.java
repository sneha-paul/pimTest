package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bigname.common.util.ValidationUtil.isNotEmpty;

/**
 * Created by sruthi on 20-09-2018.
 */

@Service
public class ProductVariantServiceImpl extends BaseServiceSupport<ProductVariant, ProductVariantDAO, ProductVariantService> implements ProductVariantService {

    private ProductVariantDAO productVariantDAO;
    private ProductDAO productDAO;


    @Autowired
    public ProductVariantServiceImpl(ProductVariantDAO productVariantDAO, Validator validator, ProductDAO productDAO) {
        super(productVariantDAO, "productVariant", validator);
        this.productVariantDAO = productVariantDAO;
        this.productDAO = productDAO;
    }

    @Override
    public ProductVariant update(String variantId, FindBy variantIdFindBy, ProductVariant productVariant) {
        if(variantIdFindBy == FindBy.EXTERNAL_ID) {
            Optional<ProductVariant> _variant = get(productVariant.getProductId(), FindBy.INTERNAL_ID, productVariant.getChannelId(), variantId, variantIdFindBy, false);
            if(_variant.isPresent()) {
                variantId = _variant.get().getId();
                variantIdFindBy = FindBy.INTERNAL_ID;
            } else {
                throw new EntityNotFoundException("Unable to find product variant with variantId:" + variantId + ", productId:" + productVariant.getProductId() + ", channelId:" + productVariant.getChannelId());
            }
        }
        return super.update(variantId, variantIdFindBy, productVariant);
    }

    @Override
    protected ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public Optional<ProductVariant> get(String id, FindBy findBy, boolean... activeRequired) {
        return findBy == FindBy.INTERNAL_ID ? super.get(id, findBy, activeRequired) : Optional.empty();
    }

    @Override
    public Optional<ProductVariant> get(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired) {
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return get(productVariantId, FindBy.INTERNAL_ID, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return Optional.empty();
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdAndActiveIn(productId, channelId, productVariantId, PimUtil.getActiveOptions(activeRequired));
        }
    }

    @Override
    public Optional<ProductVariant> get(String productVariantId, FindBy findBy, String channelId, boolean... activeRequired) {
        return findBy == FindBy.INTERNAL_ID ? productVariantDAO.findByIdAndChannelIdAndActiveIn(productVariantId, channelId, PimUtil.getActiveOptions(activeRequired)) : productVariantDAO.findByExternalIdAndChannelIdAndActiveIn(productVariantId, channelId, PimUtil.getActiveOptions(activeRequired));
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public boolean toggle(String id, FindBy findBy, Toggle active) {
        return findBy == FindBy.INTERNAL_ID && super.toggle(id, findBy, active);
    }

    @Override
    public boolean toggle(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Toggle active) {
        Optional<ProductVariant> _variant = get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy, false);

        if(_variant.isPresent()) {
            ProductVariant variant = _variant.get();
            variant.setActive(active.state());
            createOrUpdate(variant);
            return true;
        } else {
            return false;
        }
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public ProductVariant cloneInstance(String id, FindBy findBy, Entity.CloneType type) {
        return findBy == FindBy.INTERNAL_ID ? super.cloneInstance(id, findBy, type) : null;
    }

    @Override
    public ProductVariant cloneInstance(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Entity.CloneType type) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy, false)
                .map(productVariant -> cloneInstance(productVariant.getId(), FindBy.INTERNAL_ID, type)).orElse(null);
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = new Sort(Sort.Direction.ASC, "externalId");
        }
        if(productIdFindBy == FindBy.EXTERNAL_ID) {
            Optional<Product> _product = productDAO.findByExternalId(productId);
            if(_product.isPresent()) {
                productId = _product.get().getId();
            } else {
                return null;
            }
        }
        return productVariantDAO.findByProductIdAndChannelIdAndActiveIn(productId, channelId, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAll(Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, Sort sort, boolean... activeRequired) {
        return getAll(productId, productIdFindBy, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAll(String[] ids, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        return findBy == FindBy.INTERNAL_ID ? productVariantDAO.findByIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort)) : new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, String[] variantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return getAll(variantIds, FindBy.INTERNAL_ID, page, size, sort, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdInAndActiveIn(productId, channelId, variantIds, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {
        return getAll(ids, findBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
    public List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, String[] variantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired) {
        return getAll(productId, productIdFindBy, channelId, variantIds, variantIdFindBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy, String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return getAllWithExclusions(excludedVariantIds, FindBy.INTERNAL_ID, page, size, sort, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdNotInAndActiveIn(productId, channelId, excludedVariantIds, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAllWithExclusions(String[] excludedIds, FindBy findBy, Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy, String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired) {
        return getAllWithExclusions(productId, productIdFindBy, channelId, excludedVariantIds, variantIdFindBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, ProductVariant productVariant, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, productVariant, group);
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
                        _fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return _fieldErrors;
    }

    @Override
    public Page<ProductVariant> getProductVariantPricing(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<ProductVariant> _productVariant = get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false);
        if(_productVariant.isPresent()) {
            ProductVariant productVariant = _productVariant.get();
            Page<ProductVariant> productVariants = productVariantDAO.findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(productVariant.getProductId(), productVariant.getChannelId(), productVariant.getId(), PimUtil.getActiveOptions(activeRequired), pageable);

            return productVariants;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }
}
