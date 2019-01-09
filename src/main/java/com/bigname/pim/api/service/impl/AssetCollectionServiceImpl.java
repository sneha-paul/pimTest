package com.bigname.pim.api.service.impl;

import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.bigname.pim.api.service.AssetCollectionService;
import com.bigname.pim.util.FindBy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Service
public class AssetCollectionServiceImpl extends BaseServiceSupport<AssetCollection, AssetCollectionDAO, AssetCollectionService> implements AssetCollectionService {

    private AssetCollectionDAO assetCollectionDAO;
    private VirtualFileDAO assetDAO;

    protected AssetCollectionServiceImpl(AssetCollectionDAO assetCollectionDAO, Validator validator, VirtualFileDAO assetDAO) {
        super(assetCollectionDAO, "assetCollection", validator);
        this.assetCollectionDAO = assetCollectionDAO;
        this.assetDAO = assetDAO;
    }

    @Override
    public List<AssetCollection> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<AssetCollection> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Optional<AssetCollection> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<AssetCollection> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    protected AssetCollection createOrUpdate(AssetCollection assetCollection) {
        return assetCollectionDAO.save(assetCollection);
    }

    @Override
    public List<Map<String, Object>> getAssetsHierarchy(String collectionId, FindBy findBy, boolean... activeRequired) {
        List<Map<String, Object>> hierarchy = new ArrayList<>();
        get(collectionId, findBy, activeRequired)
                .ifPresent(assetCollection -> {
                    List<Map<String, Object>> _hierarchy = assetDAO.getHierarchy(assetCollection.getRootId());
                    Map<String, String> lookUpMap = new HashMap<>();
                    _hierarchy.forEach(map -> {
                                lookUpMap.put((String) map.get("_id"), (String) map.get("externalId"));
                                String parentId = (String) map.get("parentDirectoryId");
                                String parentName = !parentId.isEmpty() ? lookUpMap.get(parentId) : "";

                                if(!parentId.isEmpty() && !parentName.startsWith("+")) {
                                    lookUpMap.put(parentId, "+" + parentName);
                                }
                            });
                    List<String> parentChain = new ArrayList<>();
                    _hierarchy.forEach(map -> {

                        String parentId = (String) map.get("parentDirectoryId");
                        String parentName = !parentId.isEmpty() ? lookUpMap.get(parentId) : "";
                        boolean isParent = lookUpMap.get(map.get("_id")).startsWith("+");
                        if(parentName.startsWith("+")) {
                            parentName = parentName.substring(1);
                        }

                        if(ValidationUtil.isNotEmpty(parentId)) {
                            if(!parentChain.contains(parentName)) {
                                parentChain.add(parentName);
                            } else {
                                while(!parentChain.get(parentChain.size() - 1).equals(parentName)) {
                                    parentChain.remove(parentChain.size() - 1);
                                }
                            }
                        }
                        Map<String, Object> asset = new HashMap<>();
                        asset.put("id", map.get("_id"));
                        asset.put("key", map.get("externalId"));
                        asset.put("name", map.get("fileName"));
                        asset.put("active", map.get("active"));
                        asset.put("isParent", isParent);
                        asset.put("parent", parentName.isEmpty() ? "0" : parentName);
                        asset.put("level", parentChain.size());
                        asset.put("parentChain", StringUtil.concatinate(parentChain, "|"));
                        hierarchy.add(asset);
                    });
                });


        return hierarchy;
    }
}
