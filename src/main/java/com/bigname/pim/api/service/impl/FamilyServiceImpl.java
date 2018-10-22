package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.util.FindBy;
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
                    .getOptions().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            //TODO - sort this based on the requested sort
        }
        return paginate(options, page, size);
    }

    @Override
    public List<FamilyAttribute> getVariantAxisAttributes(String familyId, String variantGroupId, FindBy findBy, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttribute> attributes = new ArrayList<>();
        Optional<Family> _family = get(familyId, findBy, false);
        _family.ifPresent(family ->
                family.getVariantGroups().get(variantGroupId).getVariantAxis().forEach((key, value) -> value
                        .forEach(attribute -> {
                            attribute.setLevel(key);
                            attributes.add(attribute);
                        }))
        );
        return attributes;
    }

    @Override
    public List<FamilyAttribute> getAvailableVariantAxisAttributes(String familyId, String variantGroupId, FindBy findBy, Sort sort) {
        /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<FamilyAttribute> attributes = new ArrayList<>();
        List<FamilyAttribute> setAttributes = new ArrayList<>();
        Optional<Family> _family = get(familyId, findBy, false);

        //All valid variant axis attributes
        _family.ifPresent(family -> FamilyAttributeGroup.getAllAttributeGroups(family.getAttributes(), FamilyAttributeGroup.GetMode.LEAF_ONLY, true)
                .forEach(attributeGroup -> attributeGroup.getAttributes().entrySet().stream()
                        .filter(e -> e.getValue().getActive().equals("Y") && e.getValue().getSelectable().equals("Y") && e.getValue().getRequired().equals("Y"))
                        .forEach(entry -> attributes.add(entry.getValue()))));

        //Already set axis attributes
        _family.ifPresent(family -> family.getVariantGroups().get(variantGroupId).getVariantAxis().forEach((key, value) -> setAttributes.addAll(value)));



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
    public Optional<Family> get(String id, FindBy findBy, boolean... activeRequired) {
        Optional<Family> family = super.get(id, findBy, activeRequired);
        family.ifPresent(family1 -> {
            Map<String, FamilyAttribute> attributeMap = FamilyAttributeGroup.getAllAttributes(family1.getAttributes()).stream().collect(Collectors.toMap(FamilyAttribute::getId, e -> e));
            family1.getVariantGroups().forEach((k1, variantGroup) -> {
                variantGroup.getVariantAxis().forEach((k2, axisAttributes) -> axisAttributes.forEach(axisAttribute -> {
                    attributeMap.get(axisAttribute.getId()).setLevel(k2);
                    attributeMap.get(axisAttribute.getId()).setType(FamilyAttribute.Type.AXIS);
                }));
                variantGroup.getVariantAttributes().forEach((k3, variantAttributes) -> variantAttributes.forEach(variantAttribute -> {
                    attributeMap.get(variantAttribute.getId()).setLevel(k3);
                    attributeMap.get(variantAttribute.getId()).setType(FamilyAttribute.Type.VARIANT);
                }));
            });
        });
        return family;
    }
}
