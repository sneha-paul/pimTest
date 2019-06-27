package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.javatuples.Pair;
import org.javatuples.Triplet;
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
public class FamilyServiceImpl extends BaseServiceSupport<Family, FamilyDAO, FamilyService> implements FamilyService {

    private FamilyDAO familyDAO;

    @Autowired
    public FamilyServiceImpl(FamilyDAO familyDAO, Validator validator) {
        super(familyDAO, "family", validator);
        this.familyDAO = familyDAO;
    }

    @Override
    public List<Family> saveAll(List<Family> families) {
        List<Family> _families = familyDAO.saveAll(families);
        _families.forEach(family -> family.getVariantGroups().forEach((k, variantGroup) -> variantGroup.setFamily(family))); //TODO - verify if this is required
        return _families;
    }

    @Override
    public Page<FamilyAttribute> getFamilyAttributes(ID<String> familyId, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        final Map<String, FamilyAttributeGroup> attributeGroups = new HashMap<>();
        List<FamilyAttribute> attributes = new ArrayList<>();
        get(familyId, false).ifPresent(family -> attributeGroups.putAll(family.getAttributes()));
        FamilyAttributeGroup.getAllAttributeGroups(attributeGroups, FamilyAttributeGroup.GetMode.LEAF_ONLY, true).forEach(g -> g.getAttributes().forEach((k, a) -> attributes.add(a)));
        //            TODO - sort this based on the requested sort
        return paginate(attributes, page, size);
    }

    @Override
    public Page<VariantGroup> getVariantGroups(ID<String> familyId, int page, int size, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        final Map<String, VariantGroup> variantGroupsMap = new HashMap<>();
        get(familyId, false).ifPresent(family -> variantGroupsMap.putAll(family.getVariantGroups()));
        List<VariantGroup> variantGroups = variantGroupsMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        //            TODO - sort this based on the requested sort
        return paginate(variantGroups, page, size);
    }

    @Override
    public List<Pair<String, String>> getAttributeGroupsIdNamePair(ID<String> familyId, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<Family> family = get(familyId, false);
        family.ifPresent(family1 -> FamilyAttributeGroup.getAllAttributeGroups(family1.getAttributes(), FamilyAttributeGroup.GetMode.LEAF_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), FamilyAttributeGroup.getUniqueLeafGroupLabel(attributeGroup, " > ")))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    @Override
    public List<Pair<String, String>> getParentAttributeGroupsIdNamePair(ID<String> familyId, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<Family> family = get(familyId, false);
        family.ifPresent(family1 -> FamilyAttributeGroup.getAllAttributeGroups(family1.getAttributes(), FamilyAttributeGroup.GetMode.MASTER_ONLY, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), attributeGroup.getLabel()))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    @Override
    public Page<FamilyAttributeOption> getFamilyAttributeOptions(ID<String> familyId, String attributeId, int page, int size, Sort sort) {
         /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttributeOption> options = new ArrayList<>();
        Optional<Family> family = get(familyId, false);
        if(family.isPresent()) {
            attributeId = family.get().getAttributeFullId(attributeId);
            options = FamilyAttributeGroup.getLeafGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), family.get().getAttributes())
                    .getAttributes()
                    .get(attributeId.substring(attributeId.lastIndexOf("|") + 1))
                    .getOptions().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            //TODO - sort this based on the requested sort
        }
        return paginate(options, page, size);
    }

    @Override
    public List<FamilyAttribute> getVariantAxisAttributes(ID<String> familyId, String variantGroupId, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttribute> attributes = new ArrayList<>();
        Optional<Family> _family = get(familyId, false);
        _family.ifPresent(family ->
                family.getVariantGroups().get(variantGroupId).getVariantAxis().forEach((key, value) -> value
                        .forEach(attributeId -> {
                            FamilyAttribute attribute = family.getAllAttributesMap().get(attributeId);
                            attribute.setLevel(key);
                            attributes.add(attribute);
                        }))
        );
        return attributes;
    }

    @Override
    public List<FamilyAttribute> getAvailableVariantAxisAttributes(ID<String> familyId, String variantGroupId, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttribute> attributes = new ArrayList<>();
        List<FamilyAttribute> setAttributes = new ArrayList<>();
        Optional<Family> _family = get(familyId, false);

        //All valid variant axis attributes -- TODO refactor using family.getAllAttributes()
        _family.ifPresent(family -> FamilyAttributeGroup.getAllAttributeGroups(family.getAttributes(), FamilyAttributeGroup.GetMode.LEAF_ONLY, true)
                .forEach(attributeGroup -> attributeGroup.getAttributes().entrySet().stream()
                        .filter(e -> e.getValue().getActive().equals("Y") && e.getValue().getSelectable().equals("Y") && e.getValue().getRequired().equals("Y"))
                        .forEach(entry -> attributes.add(entry.getValue()))));

        //Already set axis attributes
        _family.ifPresent(family -> family.getVariantGroups().get(variantGroupId).getVariantAxis().forEach((key, axisAttributeIds) -> setAttributes.addAll(axisAttributeIds.stream().map(axisAttributeId -> family.getAllAttributesMap().get(axisAttributeId)).collect(Collectors.toList()))));



        attributes.removeAll(setAttributes);
        return attributes;

//        return family.<List<FamilyAttribute>>map(Family::getAttributes).orElse(null);
    }

    @Override
    public List<Triplet<String, String, String>> getFamilyVariantGroups() {
        List<Triplet<String, String, String>> familyVariantGroups = new ArrayList<>();
        getAll(0, 100, null).forEach(family -> { //TODO - JIRA BNPIM-6
            StringBuilder sb = new StringBuilder();
            family.getVariantGroups().entrySet().stream().filter(entry -> entry.getValue().getActive().equals("Y")).forEach(entry-> sb.append(sb.length() > 0 ? "|" : "").append(entry.getKey()).append("|").append(entry.getValue().getName()));
            familyVariantGroups.add(Triplet.with(family.getFamilyId(), family.getFamilyName(), sb.toString()));
        });
        return familyVariantGroups;
    }

    @Override
    public <String> Optional<Family> get(ID<String> familyId, boolean... activeRequired) {
        Optional<Family> family = super.get(familyId, activeRequired);
        family.ifPresent(family1 -> {
            family1.getAllAttributes();
            family1.getVariantGroups().forEach((k1, variantGroup) -> variantGroup.setFamily(family1));
        });
        return family;
    }

    @Override
    public Page<Family> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        Page<Family> families = super.getAll(page, size, sort, activeRequired);
        families.forEach(family -> family.getVariantGroups().forEach((k, variantGroup) -> variantGroup.setFamily(family)));
        return families;
    }

    @Override
    public <I> List<Family> getAll(List<ID<I>> ids, Sort sort, boolean... activeRequired) {
        List<Family> families =  super.getAll(ids, sort, activeRequired);
        families.forEach(family -> family.getVariantGroups().forEach((k, variantGroup) -> variantGroup.setFamily(family)));
        return families;
    }

    @Override
    public boolean toggleVariantGroup(ID<String> familyId, String variantGroupId, Toggle active) {
        return get(familyId, false)
                .map(family -> {
                    family.getVariantGroups().get(variantGroupId).setActive(active.state());
                    familyDAO.save(family);
                    return true;
                })

                .orElseThrow(() -> new EntityNotFoundException("Unable to find family with id: " + familyId));
    }
}
