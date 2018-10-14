package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.util.FindBy;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AttributeCollectionService extends BaseService<AttributeCollection, AttributeCollectionDAO>  {
    Page<Attribute> getAttributes(String collectionId, FindBy findBy, int page, int size, Sort sort);

    List<Pair<String, String>> getAttributeGroupsIdNamePair(String collectionId, FindBy findBy, Sort sort);

    Page<AttributeOption> getAttributeOptions(String collectionId, FindBy findBy, String attributeId, int pageNumber, int size, Sort sort);

    Optional<Attribute> findAttribute(String collectionId, FindBy findBy, String attributeFullId);
}
