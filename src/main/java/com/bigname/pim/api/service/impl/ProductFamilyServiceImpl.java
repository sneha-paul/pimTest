package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.Feature;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.persistence.dao.ProductFamilyDAO;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.util.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by manu on 9/4/18.
 */
@Service
public class ProductFamilyServiceImpl extends BaseServiceSupport<ProductFamily, ProductFamilyDAO> implements ProductFamilyService {

    private ProductFamilyDAO productFamilyDAO;

    @Autowired
    public ProductFamilyServiceImpl(ProductFamilyDAO productFamilyDAO, Validator validator) {
        super(productFamilyDAO, "productFamily", validator);
        this.productFamilyDAO = productFamilyDAO;
    }

    @Override
    public ProductFamily createOrUpdate(ProductFamily productFamily) {
        return productFamilyDAO.save(productFamily);
    }


    @Override
    public Page<Attribute> getFamilyAttributes(String productFamilyId, FindBy findBy, String type, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<Attribute> attributes = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        /*if(productFamily.isPresent()) {
            if(type.equals("PRODUCT")) {
                attributes = productFamily.get().getProductFamilyAttributes();
            } else if(type.equals("VARIANT")) {
                attributes = productFamily.get().getProductVariantFamilyAttributes();
            }
            attributes.sort(Comparator.comparing(Attribute::getName));
        }*/
        return paginate(attributes, page, size);
    }

    @Override
    public Page<Feature> getFamilyFeatures(String productFamilyId, FindBy findBy, String type, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<Feature> features = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        if(productFamily.isPresent()) {
            if(type.equals("PRODUCT")) {
                features = productFamily.get().getProductFamilyFeatures();
            } else if(type.equals("VARIANT")) {
                features = productFamily.get().getProductVariantFamilyFeatures();
            }
            features.sort(Comparator.comparing(Feature::getName));
        }
        return paginate(features, page, size);
    }
}
