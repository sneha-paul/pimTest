package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.AssetCollection;
import com.m7.xtreme.xcore.persistence.mongo.dao.GenericDAO;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionDAO extends GenericDAO<AssetCollection>, AssetCollectionRepository {
    Optional<AssetCollection> findByCollectionName(String collectionName); //TODO - handle case insensitive lookup
}
