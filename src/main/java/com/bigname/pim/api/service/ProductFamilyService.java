package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.Feature;
import com.bigname.pim.api.domain.ProductFamily;
import com.bigname.pim.api.persistence.dao.ProductFamilyDAO;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by manu on 9/4/18.
 */
public interface ProductFamilyService extends BaseService<ProductFamily, ProductFamilyDAO>  {
    Page<Attribute> getFamilyAttributes(String productFamilyId, FindBy findBy, String type, int page, int size, Sort sort);

    List<AttributeGroup> getAttributeGroups(String productFamilyId, FindBy findBy, String type, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, String type, Sort sort);

    Page<Feature> getFamilyFeatures(String productFamilyId, FindBy findBy, String type, int page, int size, Sort sort);
}
