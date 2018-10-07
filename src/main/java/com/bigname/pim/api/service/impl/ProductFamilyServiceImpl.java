package com.bigname.pim.api.service.impl;

import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.ProductFamilyDAO;
import com.bigname.pim.api.service.ProductFamilyService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

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
    public Page<Attribute> getFamilyAttributes(String productFamilyId, FindBy findBy, String entityType, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        final Map<String, AttributeGroup> attributeGroups = new HashMap<>();
        List<Attribute> attributes = new ArrayList<>();
        get(productFamilyId, findBy, false).ifPresent(productFamily -> attributeGroups.putAll("VARIANTS".equals(entityType) ? productFamily.getProductVariantFamilyAttributes() : productFamily.getProductFamilyAttributes()));
        AttributeGroup.getAllAttributeGroups(attributeGroups, AttributeGroup.GetMode.LEAF_ONLY, true).forEach(g -> g.getAttributes().forEach((k, a) -> attributes.add(a)));
        //            TODO - sort this based on the requested sort
        return paginate(attributes, page, size);
    }

    @Override
    public List<Pair<String, String>> getAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, String entityType, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        productFamily.ifPresent(productFamily1 -> AttributeGroup.getAllAttributeGroups(entityType.equals("VARIANT") ? productFamily1.getProductVariantFamilyAttributes() : productFamily1.getProductFamilyAttributes(), AttributeGroup.GetMode.LEAF_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), AttributeGroup.getUniqueLeafGroupLabel(attributeGroup, " > ")))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    @Override
    public List<Pair<String, String>> getParentAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, String entityType, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        productFamily.ifPresent(productFamily1 -> AttributeGroup.getAllAttributeGroups(entityType.equals("VARIANT") ? productFamily1.getProductVariantFamilyAttributes() : productFamily1.getProductFamilyAttributes(), AttributeGroup.GetMode.LEAF_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), attributeGroup.getLabel()))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
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

    @Override
    public Page<AttributeOption> getFamilyAttributeOptions(String productFamilyId, FindBy findBy, String entityType, String attributeId, int page, int size, Sort sort) {
         /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<AttributeOption> options = new ArrayList<>();
        Optional<ProductFamily> productFamily = get(productFamilyId, findBy, false);
        if(productFamily.isPresent()) {
            options = AttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), "VARIANT".equals(entityType) ? productFamily.get().getProductVariantFamilyAttributes() : productFamily.get().getProductFamilyAttributes())
                    .getAttributes()
                    .get(attributeId.substring(attributeId.lastIndexOf("|") + 1))
                    .getOptions().entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
            //TODO - sort this based on the requested sort
        }
        return paginate(options, page, size);
    }
}
