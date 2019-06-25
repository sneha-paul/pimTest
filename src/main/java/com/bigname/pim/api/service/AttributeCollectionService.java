package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.domain.FamilyAttribute;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AttributeCollectionService extends BaseService<AttributeCollection, AttributeCollectionDAO> {

    /**
     * Method to get attributes of an attributeCollection in paginated format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @return
     */
    Page<Attribute> getAttributes(ID<String> collectionId, int page, int size, Sort sort);

    /**
     * Method to get attributeGroups of an attributeCollection in list format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param sort sort Object
     * @return
     */
    List<Pair<String, String>> getAttributeGroupsIdNamePair(ID<String> collectionId, Sort sort);

    /**
     * Method to get attributeOptions of an attribute in paginated format.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param attributeId Internal or External id of the Attribute
     * @param pageNumber page number
     * @param size page size
     * @param sort sort Object
     * @return
     */
    Page<AttributeOption> getAttributeOptions(ID<String> collectionId, String attributeId, int pageNumber, int size, Sort sort);

    /**
     * Method to find an attribute from an attributeCollection.
     *
     * @param collectionId Internal or External id of the AttributeCollection
     * @param attributeFullId Internal or External id of the Attribute
     * @return
     */
    Optional<Attribute> findAttribute(ID<String> collectionId, String attributeFullId);

    /**
     * Method to find attributeOptions of an attribute.
     *
     * @param familyAttribute familyAttribute Object
     * @param attributeOptionId Internal or External id of the attributeOption
     * @return
     */
    Optional<AttributeOption> findAttributeOption(FamilyAttribute familyAttribute, String attributeOptionId);

}
