package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

/**
 * Created by manu on 9/4/18.
 */
public interface FamilyService extends BaseService<Family, FamilyDAO>  {


    Page<FamilyAttribute> getFamilyAttributes(String familyId, FindBy findBy, int page, int size, Sort sort);

    Page<VariantGroup> getVariantGroups(String familyId, FindBy findBy, int page, int size, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort);

    List<Pair<String, String>> getParentAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort);

    Page<FamilyAttributeOption> getFamilyAttributeOptions(String familyId, FindBy findBy, String attributeId, int pageNumber, int size, Sort sort);

    List<FamilyAttribute> getVariantAxisAttributes(String familyId, String variantGroupId, FindBy findBy, Sort sort);

    List<FamilyAttribute> getAvailableVariantAxisAttributes(String familyId, String variantGroupId, FindBy findBy, Sort sort);

    List<Triplet<String, String, String>> getFamilyVariantGroups();

    boolean toggleVariantGroup(String familyId, FindBy familyIdFindBy, String variantGroupId, FindBy variantGroupIdFindBy, Toggle active);
}
