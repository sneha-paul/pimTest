package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
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
    Page<FamilyAttribute> getFamilyAttributes(String productFamilyId, FindBy findBy, int page, int size, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, Sort sort);

    List<Pair<String, String>> getParentAttributeGroupsIdNamePair(String productFamilyId, FindBy findBy, Sort sort);

    Page<FamilyAttributeOption> getFamilyAttributeOptions(String productFamilyId, FindBy findBy, String attributeId, int pageNumber, int size, Sort sort);
}
