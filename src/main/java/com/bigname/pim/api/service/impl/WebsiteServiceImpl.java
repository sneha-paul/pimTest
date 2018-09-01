package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebsiteServiceImpl extends BaseServiceSupport<Website, WebsiteDAO> implements WebsiteService {


    private WebsiteDAO websiteDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private CatalogService catalogService;


    @Autowired
    public WebsiteServiceImpl(WebsiteDAO websiteDAO, WebsiteCatalogDAO websiteCatalogDAO, CatalogService catalogService) {
        super(websiteDAO, "website");
        this.websiteDAO = websiteDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.catalogService = catalogService;
    }


    @Override
    protected Website createOrUpdate(Website website) {
        return websiteDAO.save(website);
    }

    @Override
    public List<Catalog> getAvailableCatalogsForWebsite(String id, FindBy findBy) {
        Optional<Website> website = get(id, findBy, false);
        Set<String> catalogIds = new HashSet<>();
        if(website.isPresent()) {
            websiteCatalogDAO.findByWebsiteId(website.get().getId()).forEach(wc -> catalogIds.add(wc.getCatalogId()));
        }
        return catalogService.getAllWithExclusions(catalogIds.toArray(new String[0]), FindBy.INTERNAL_ID);
    }

    @Override
    public Page<WebsiteCatalog> getWebsiteCatalogs(String websiteId, FindBy findBy, int page, int size, boolean... activeRequired) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")));
        Optional<Website> _website = get(websiteId, findBy, activeRequired);
        if(_website.isPresent()) {
            Website website = _website.get();
            Page<WebsiteCatalog> websiteCatalogs = websiteCatalogDAO.findByWebsiteIdAndActiveIn(website.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> catalogIds = new ArrayList<>();
            websiteCatalogs.forEach(wc -> catalogIds.add(wc.getCatalogId()));
            if(catalogIds.size() > 0) {
                Map<String, Catalog> catalogsMap = PimUtil.getIdedMap(catalogService.getAll(catalogIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                websiteCatalogs.forEach(wc -> wc.init(website, catalogsMap.get(wc.getCatalogId())));
            }
            return websiteCatalogs;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public WebsiteCatalog addCatalog(String id, FindBy findBy1, String catalogId, FindBy findBy2) {
        Optional<Website> website = get(id, findBy1, false);
        if(website.isPresent()) {
            Optional<Catalog> catalog = catalogService.get(catalogId, findBy2);
            if(catalog.isPresent()) {
                Optional<WebsiteCatalog> top = websiteCatalogDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return websiteCatalogDAO.save(new WebsiteCatalog(website.get().getId(), catalog.get().getId(), top.isPresent() ? top.get().getSubSequenceNum() + 1 : 0));
            }
        }
        return null;
    }
}
