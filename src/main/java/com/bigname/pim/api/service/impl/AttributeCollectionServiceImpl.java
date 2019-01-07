package com.bigname.pim.api.service.impl;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Service
public class AttributeCollectionServiceImpl extends BaseServiceSupport<AttributeCollection, AttributeCollectionDAO, AttributeCollectionService> implements AttributeCollectionService {

    private AttributeCollectionDAO attributeCollectionDAO;

    @Autowired
    public AttributeCollectionServiceImpl(AttributeCollectionDAO attributeCollectionDAO, Validator validator) {
        super(attributeCollectionDAO, "attributeCollection", validator);
        this.attributeCollectionDAO = attributeCollectionDAO;
    }

    @Override
    public AttributeCollection createOrUpdate(AttributeCollection attributeCollection) {
        return attributeCollectionDAO.save(attributeCollection);
    }

    @Override
    public List<AttributeCollection> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<AttributeCollection> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Optional<AttributeCollection> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<AttributeCollection> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Page<AttributeCollection> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        Page<AttributeCollection> attributeCollections = super.getAll(page, size, sort, activeRequired);
        attributeCollections.forEach(this::setAttributeList);
        return attributeCollections;
    }

    @Override
    public Optional<AttributeCollection> get(String id, FindBy findBy, boolean... activeRequired) {
        Optional<AttributeCollection> collection =  super.get(id, findBy, activeRequired);
        collection.ifPresent(this::setAttributeList);
        return collection;
    }

    private void setAttributeList(AttributeCollection collection) {
        collection.setAllAttributes(AttributeGroup.getAllAttributes(collection.getMappedAttributes()));
    }

    /**
     * Method to get attributes of an attributeCollection in paginated format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param findBy Type of the attributeCollection id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @return
     */
    @Override
    public Page<Attribute> getAttributes(String collectionId, FindBy findBy, int page, int size, Sort sort) {
        if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }
        final Map<String, AttributeGroup> attributeGroups = new HashMap<>();
        List<Attribute> attributes = new ArrayList<>();
        get(collectionId, findBy, false).ifPresent(attributeCollection -> attributeGroups.putAll(attributeCollection.getAttributes()));
        AttributeGroup.getAllAttributeGroups(attributeGroups, AttributeGroup.GetMode.ALL, true).forEach(g -> g.getAttributes().forEach((k, a) -> attributes.add(a)));
        return paginate(attributes, page, size, sort);
    }

    /**
     * Method to get attributeGroups of an attributeCollection in list format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param findBy Type of the attributeCollection id, INTERNAL_ID or EXTERNAL_ID
     * @param sort sort Object
     * @return
     */
    @Override
    public List<Pair<String, String>> getAttributeGroupsIdNamePair(String collectionId, FindBy findBy, Sort sort) {
        List<Pair<String, String>> idNamePairs = new ArrayList<>();
        Optional<AttributeCollection> attributeCollection = get(collectionId, findBy, false);
        attributeCollection.ifPresent(attributeCollection1 -> AttributeGroup.getAllAttributeGroups(attributeCollection1.getAttributes(), AttributeGroup.GetMode.ALL, true).forEach(attributeGroup -> idNamePairs.add(Pair.with(attributeGroup.getFullId(), AttributeGroup.getFullGroupLabel(attributeGroup, " > ")))));
//        idNamePairs.sort(Comparator.comparing(Pair::getValue0)); // TODO -replace after implementing sorting based on sort parameter
        return idNamePairs;
    }

    /**
     * Method to get attributeOptions of an attribute in paginated format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param findBy Type of the attributeCollection id, INTERNAL_ID or EXTERNAL_ID
     * @param attributeId Internal or External id of the Attribute
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @return
     */
    @Override
    public Page<AttributeOption> getAttributeOptions(String collectionId, FindBy findBy, String attributeId, int page, int size, Sort sort) {
         /*if(sort == null) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }*/
        List<AttributeOption> options = new ArrayList<>();
        Optional<AttributeCollection> attributeCollection = get(collectionId, findBy, false);
        if(attributeCollection.isPresent()) {
            options = AttributeGroup.getAttributeGroup(attributeId.substring(0, attributeId.lastIndexOf("|")), attributeCollection.get().getMappedAttributes())
                    .getAttributes()
                    .get(attributeId.substring(attributeId.lastIndexOf("|") + 1))
                    .getOptions().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            //TODO - sort this based on the requested sort
        }
        return paginate(options, page, size);
    }

    /**
     * Method to find an attribute from an attributeCollection.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param findBy Type of the attributeCollection id, INTERNAL_ID or EXTERNAL_ID
     * @param attributeFullId Internal or External id of the Attribute
     * @return
     */
    @Override
    public Optional<Attribute> findAttribute(String collectionId, FindBy findBy, String attributeFullId) {
        List<Attribute> attributes = new ArrayList<>();
        get(collectionId, findBy).ifPresent(attributeCollection -> attributeCollection.getAllAttributes().stream().filter(attribute -> attribute.getFullId().equalsIgnoreCase(attributeFullId)).forEach(attributes::add));
        return attributes.isEmpty() ? Optional.empty() : Optional.of(attributes.get(0));
    }

    /**
     * Method to find attributeOptions of an attribute.
     *
     * @param familyAttribute familyAttribute Object
     * @param attributeOptionId Internal or External id of the attributeOption
     * @return
     */
    @Override
    public Optional<AttributeOption> findAttributeOption(FamilyAttribute familyAttribute, String attributeOptionId) {
        Optional<Attribute> attribute = findAttribute(familyAttribute.getCollectionId(), FindBy.EXTERNAL_ID, familyAttribute.getAttributeId());
        return attribute.map(attribute1 -> attribute1.getOptions().entrySet().stream().filter(e -> {e.getValue().setCollectionId(familyAttribute.getCollectionId());return e.getKey().equals(attributeOptionId);}).map(Map.Entry::getValue).findFirst()).orElse(Optional.empty());
    }
}
