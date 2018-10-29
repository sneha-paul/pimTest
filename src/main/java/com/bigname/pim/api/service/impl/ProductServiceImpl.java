package com.bigname.pim.api.service.impl;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.GenericEntityException;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 19-09-2018.
 */
@Service
public class ProductServiceImpl extends BaseServiceSupport<Product, ProductDAO> implements ProductService {

    private ProductDAO productDAO;
    private ProductVariantDAO productVariantDAO;
    private FamilyService productFamilyService;


    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, Validator validator, ProductVariantDAO productVariantDAO, FamilyService productFamilyService) {
        super(productDAO, "product", validator);
        this.productDAO = productDAO;
        this.productVariantDAO = productVariantDAO;
        this.productFamilyService = productFamilyService;
    }

    @Override
    public Product createOrUpdate(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Page<ProductVariant> getProductVariants(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Product> product = get(productId, findBy, false); //TODO - JIRA BNPIM-7

        return product.map(product1 -> productVariantDAO.findByProductIdAndActiveIn(product1.getId(), PimUtil.getActiveOptions(activeRequired), pageable)).orElseGet(() -> new PageImpl<>(new ArrayList<>(), pageable, 0));
    }

    /**
     * Override the crete method to replace the passed in productFamily EXTERNAL_ID to a valid INTERNAL_ID
     *
     * @param product
     * @return
     */
    @Override
    public Product create(Product product) {
        /**
         *  No need to check if product.getProductFamilyId is empty.
         *  There is @NotEmpty validation constraint on the Product bean
         */

        setProductFamily(product, FindBy.EXTERNAL_ID);
        if(ValidationUtil.isEmpty(product.getProductFamily())) {
            throw new GenericEntityException("Unable to create product, invalid product family id : " + product.getProductFamilyId());
        } else {
            return super.create(product);
        }
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     * @param id
     * @param findBy
     * @param activeRequired
     * @return
     */
    @Override
    public Optional<Product> get(String id, FindBy findBy, boolean... activeRequired) {
        Optional<Product> product = super.get(id, findBy, activeRequired);
        product.ifPresent(product1 -> setProductFamily(product1, FindBy.INTERNAL_ID));
        return product;
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     *
     * @param page
     * @param size
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public Page<Product> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        Page<Product> products = super.getAll(page, size, sort, activeRequired);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     *
     * @param ids
     * @param findBy
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public List<Product> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {
        List<Product> products = super.getAll(ids, findBy, sort, activeRequired);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    private void setProductFamily(Product product, FindBy findBy) {
        productFamilyService.get(product.getProductFamilyId(), findBy).ifPresent(productFamily -> product.setProductFamily(productFamily));
    }


    @Override
    public Page<Product> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "productId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products =  findBy == FindBy.INTERNAL_ID ? productDAO.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : productDAO.findByProductIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Pair<String, Object>> fieldErrors, Product product, String group) {
        FamilyAttributeGroup masterGroup = product.getProductFamily().getAttributes().get(group + "_GROUP");
        if(ValidationUtil.isNotEmpty(masterGroup)) {
            FamilyAttributeGroup sectionGroup = masterGroup.getChildGroups().get(AttributeGroup.DEFAULT_GROUP_ID);
            sectionGroup.getChildGroups().forEach((k, attributeGroup) ->
                attributeGroup.getAttributes().forEach((k1, attribute) -> {
                    /*if(attribute.getUiType() == Attribute.UIType.CHECKBOX && !product.getFamilyAttributes().containsKey(k1)) {
                        product.getFamilyAttributes().put(k1, new String[0]);
                    } else if(attribute.getUiType() == Attribute.UIType.YES_NO && !product.getFamilyAttributes().containsKey(k1)) {
                        product.getFamilyAttributes().put(k1, "N");
                    }*/

                    if(attribute.getUiType() == Attribute.UIType.CHECKBOX && !product.getChannelFamilyAttributes().containsKey(k1)) {
                        product.getChannelFamilyAttributes().put(k1, new String[0]);
                    } else if(attribute.getUiType() == Attribute.UIType.YES_NO && !product.getChannelFamilyAttributes().containsKey(k1)) {
                        product.getChannelFamilyAttributes().put(k1, "N");
                    }

                    /*Pair<String, Object> error = null;
                    if(product.getFamilyAttributes().containsKey(attribute.getId())) {
//                        error = attribute.validate(product.getFamilyAttributes().get(attribute.getId()));
                    } else if(product.getChannelFamilyAttributes().containsKey(attribute.getId())) {
                        error = attribute.validate(product.getChannelFamilyAttributes().get(attribute.getId()), product.getChannelId(), 0);
                    }*/

                    Pair<String, Object> error = attribute.validate(product.getChannelFamilyAttributes().get(attribute.getId()), product.getChannelId(), 0);//TODO - check if the above commented out logic is required
                    if(ValidationUtil.isNotEmpty(error)) {
                        fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return fieldErrors;
    }
}
