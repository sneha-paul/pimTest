package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.AssetFamily;
import com.bigname.pim.api.persistence.dao.AssetFamilyDAO;
import com.bigname.pim.api.service.AssetFamilyService;
import com.m7.xtreme.xcore.service.mongo.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;


/**
 * Created by sanoop on 14/02/2019.
 */
@Service
public class AssetFamilyServiceImpl extends BaseServiceSupport<AssetFamily, AssetFamilyDAO, AssetFamilyService> implements AssetFamilyService{

    private AssetFamilyDAO assetFamilyDAO;

    @Autowired
    public AssetFamilyServiceImpl(AssetFamilyDAO assetFamilyDAO, Validator validator) {
        super(assetFamilyDAO, "AssetFamily", validator);
        this.assetFamilyDAO = assetFamilyDAO;
    }
}
