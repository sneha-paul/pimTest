package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by manu on 9/4/18.
 */
public interface FamilyService extends BaseService<Family, FamilyDAO>  {
    Page<FamilyAttribute> getFamilyAttributes(String familyId, FindBy findBy, int page, int size, Sort sort);

    Page<VariantGroup> getVariantGroups(String familyId, FindBy findBy, int page, int size, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort);

    List<Pair<String, String>> getParentAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort);

    Page<FamilyAttributeOption> getFamilyAttributeOptions(String familyId, FindBy findBy, String attributeId, int pageNumber, int size, Sort sort);
}
