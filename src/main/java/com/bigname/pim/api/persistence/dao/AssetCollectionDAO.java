package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericDAO;
import com.bigname.pim.api.domain.AssetCollection;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionDAO extends GenericDAO<AssetCollection>, AssetCollectionRepository {
    Optional<AssetCollection> findByCollectionName(String collectionName); //TODO - handle case insensitive lookup
}
