package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.Feature;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.persistence.dao.ProductFamilyDAO;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;

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
        Map<String, AttributeGroup> attributeGroups = new HashMap<>();
        List<Attribute> attributes = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        if(productFamily.isPresent()) {
            if(type.equals("PRODUCT")) {
                attributeGroups = productFamily.get().getProductFamilyAttributes();
            } else if(type.equals("VARIANT")) {
                attributeGroups = productFamily.get().getProductVariantFamilyAttributes();
            }
            attributeGroups.forEach((s, attributeGroup) -> {
                attributeGroup.getAttributes().forEach((s1, attribute) -> {
                    attribute.setAttributeGroupName(attributeGroup.getName());
                    attribute.setAttributeGroupId(attributeGroup.getId());
                    attributes.add(attribute);
                });
            });
//            TODO - sort this based on the requested sort
        }
        return paginate(attributes, page, size);
    }

    @Override
    public List<AttributeGroup> getAttributeGroups(String productFamilyId, FindBy findBy, String type, Sort sort) {
        if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }
        List<AttributeGroup> attributeGroups = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        if(productFamily.isPresent()) {
            Map<String, AttributeGroup> attributeGroups1 = "VARIANTS".equals(type) ? productFamily.get().getProductVariantFamilyAttributes() : productFamily.get().getProductFamilyAttributes();
            attributeGroups1.forEach(((s, attributeGroup) -> attributeGroups.add(attributeGroup)));
        }
        //TODO - Sorting
        return attributeGroups;
    }

    @Override
    public List<Pair<String, String>> getAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, String type, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        getAttributeGroups(productFamilyId, findBy, type, sort).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getId(), attributeGroup.getName())));
        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
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
