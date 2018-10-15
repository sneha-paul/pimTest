package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by manu on 9/4/18.
 */
@Service
public class FamilyServiceImpl extends BaseServiceSupport<Family, FamilyDAO> implements FamilyService {

    private FamilyDAO familyDAO;

    @Autowired
    public FamilyServiceImpl(FamilyDAO familyDAO, Validator validator) {
        super(familyDAO, "family", validator);
        this.familyDAO = familyDAO;
    }

    @Override
    public Family createOrUpdate(Family family) {
        return familyDAO.save(family);
    }


    @Override
    public Page<FamilyAttribute> getFamilyAttributes(String familyId, FindBy findBy, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        final Map<String, FamilyAttributeGroup> attributeGroups = new HashMap<>();
        List<FamilyAttribute> attributes = new ArrayList<>();
        get(familyId, findBy, false).ifPresent(family -> attributeGroups.putAll(family.getAttributes()));
        FamilyAttributeGroup.getAllAttributeGroups(attributeGroups, FamilyAttributeGroup.GetMode.LEAF_ONLY, true).forEach(g -> g.getAttributes().forEach((k, a) -> attributes.add(a)));
        //            TODO - sort this based on the requested sort
        return paginate(attributes, page, size);
    }

    @Override
    public Page<VariantGroup> getVariantGroups(String familyId, FindBy findBy, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        final Map<String, VariantGroup> variantGroupsMap = new HashMap<>();
        get(familyId, findBy, false).ifPresent(family -> variantGroupsMap.putAll(family.getVariantGroups()));
        List<VariantGroup> variantGroups = variantGroupsMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        //            TODO - sort this based on the requested sort
        return paginate(variantGroups, page, size);
    }

    @Override
    public List<Pair<String, String>> getAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<Family> family = get(familyId, findBy, false);
        family.ifPresent(family1 -> FamilyAttributeGroup.getAllAttributeGroups(family1.getAttributes(), FamilyAttributeGroup.GetMode.LEAF_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), FamilyAttributeGroup.getUniqueLeafGroupLabel(attributeGroup, " > ")))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    @Override
    public List<Pair<String, String>> getParentAttributeGroupsIdNamePair(String familyId, FindBy findBy, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<Family> family = get(familyId, findBy, false);
        family.ifPresent(family1 -> FamilyAttributeGroup.getAllAttributeGroups(family1.getAttributes(), FamilyAttributeGroup.GetMode.MASTER_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), attributeGroup.getLabel()))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    @Override
    public Page<FamilyAttributeOption> getFamilyAttributeOptions(String familyId, FindBy findBy, String attributeId, int page, int size, Sort sort) {
         /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttributeOption> options = new ArrayList<>();
        Optional<Family> family = get(familyId, findBy, false);
        if(family.isPresent()) {
            options = FamilyAttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), family.get().getAttributes())
                    .getAttributes()
                    .get(attributeId.substring(attributeId.lastIndexOf("|") + 1))
                    .getOptions().entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
            //TODO - sort this based on the requested sort
        }
        return paginate(options, page, size);
    }
}
