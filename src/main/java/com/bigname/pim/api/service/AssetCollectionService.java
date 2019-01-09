package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.util.FindBy;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionService extends BaseService<AssetCollection, AssetCollectionDAO> {
    List<Map<String, Object>> getAssetsHierarchy(String collectionId, FindBy findBy, boolean... activeRequired);
}
