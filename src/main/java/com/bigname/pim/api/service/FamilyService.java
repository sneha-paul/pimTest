package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Family;
import com.bigname.pim.api.domain.FamilyAttribute;
import com.bigname.pim.api.domain.FamilyAttributeOption;
import com.bigname.pim.api.domain.VariantGroup;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by manu on 9/4/18.
 */
public interface FamilyService extends BaseService<Family, FamilyDAO> {


    List<Family> saveAll(List<Family> families);

    Page<FamilyAttribute> getFamilyAttributes(ID<String> familyId, int page, int size, Sort sort);

    Page<VariantGroup> getVariantGroups(ID<String> familyId, int page, int size, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(ID<String> familyId, Sort sort);

    List<Pair<String, String>> getParentAttributeGroupsIdNamePair(ID<String> familyId, Sort sort);

    Page<FamilyAttributeOption> getFamilyAttributeOptions(ID<String> familyId, String attributeId, int pageNumber, int size, Sort sort);

    List<FamilyAttribute> getVariantAxisAttributes(ID<String> familyId, String variantGroupId, Sort sort);

    List<FamilyAttribute> getAvailableVariantAxisAttributes(ID<String> familyId, String variantGroupId, Sort sort);

    List<Triplet<String, String, String>> getFamilyVariantGroups();

    boolean toggleVariantGroup(ID<String> familyId, String  variantGroupId, Toggle active);

}
