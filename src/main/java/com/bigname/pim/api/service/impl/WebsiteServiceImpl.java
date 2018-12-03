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
import com.bigname.pim.util.Toggle;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebsiteServiceImpl extends BaseServiceSupport<Website, WebsiteDAO, WebsiteService> implements WebsiteService {


    private WebsiteDAO websiteDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private CatalogService catalogService;


    @Autowired
    public WebsiteServiceImpl(WebsiteDAO websiteDAO, Validator validator, WebsiteCatalogDAO websiteCatalogDAO, CatalogService catalogService) {
        super(websiteDAO, "website", validator);
        this.websiteDAO = websiteDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.catalogService = catalogService;
    }


    @Override
    public Website createOrUpdate(Website website) {
        return websiteDAO.save(website);
    }

    /**
     * Method to get available catalogs of a website in paginated format.
     *
     * @param id Internal or External id of the Website
     * @param findBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Catalog> getAvailableCatalogsForWebsite(String id, FindBy findBy, int page, int size, Sort sort) {
        return proxy().get(id, findBy, false)
                .map(website -> {
                    Set<String> catalogIds = new HashSet<>();
                    websiteCatalogDAO.findByWebsiteId(website.getId()).forEach(wc -> catalogIds.add(wc.getCatalogId()));
                    return catalogService.getAllWithExclusions(catalogIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, true);
                }).orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param findBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<WebsiteCatalog> getWebsiteCatalogs(String websiteId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Website> _website = proxy().get(websiteId, findBy, false);
        if(_website.isPresent()) {
            Website website = _website.get();
            Page<WebsiteCatalog> websiteCatalogs = websiteCatalogDAO.findByWebsiteIdAndActiveIn(website.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> catalogIds = new ArrayList<>();
            websiteCatalogs.forEach(wc -> catalogIds.add(wc.getCatalogId()));
            if(catalogIds.size() > 0) {
                Map<String, Catalog> catalogsMap = PimUtil.getIdedMap(catalogService.getAll(catalogIds.toArray(new String[0]), FindBy.INTERNAL_ID, null), FindBy.INTERNAL_ID);
                List<WebsiteCatalog> _websiteCatalogs = websiteCatalogs.filter(wc -> catalogsMap.containsKey(wc.getCatalogId())).stream().collect(Collectors.toList());
                _websiteCatalogs.forEach(wc -> wc.init(website, catalogsMap.get(wc.getCatalogId())));
                websiteCatalogs = new PageImpl<>(_websiteCatalogs,pageable,_websiteCatalogs.size());//TODO : verify this logic
            }
            return websiteCatalogs;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to add catalog for a website.
     *
     * @param id Internal or External id of the Website
     * @param findBy1 Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param catalogId Internal or External id of the Catalog
     * @param findBy2 Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public WebsiteCatalog addCatalog(String id, FindBy findBy1, String catalogId, FindBy findBy2) {
        Optional<Website> website = proxy().get(id, findBy1, false);
        if(website.isPresent()) {
            Optional<Catalog> catalog = catalogService.get(catalogId, findBy2, false);
            if(catalog.isPresent()) {
                Optional<WebsiteCatalog> top = websiteCatalogDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return websiteCatalogDAO.save(new WebsiteCatalog(website.get().getId(), catalog.get().getId(), top.isPresent() ? top.get().getSubSequenceNum() + 1 : 0));
            }
        }
        return null;
    }

    @Override
    public Optional<WebsiteCatalog> getWebsiteCatalog(String websiteCatalogId) {
        return websiteCatalogDAO.findById(websiteCatalogId);
    }

    @Override
    public Optional<WebsiteCatalog> getWebsiteCatalog(String websiteId, FindBy websiteIdFindBy, String catalogId, FindBy catalogIdFindBy) {
        return websiteDAO.findById(websiteId, websiteIdFindBy, Website.class)
                .map(website -> catalogService.get(catalogId, catalogIdFindBy, false)
                        .map(catalog -> websiteCatalogDAO.findByWebsiteIdAndCatalogId(website.getId(), catalog.getId()))
                        .orElse(Optional.empty()))
                .orElse(Optional.empty());
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "websites", key = "#findBy.INTERNAL_ID+\"|\"+#website.id"), @CacheEvict(value = "websites", key = "#findBy.EXTERNAL_ID+\"|\"+#website.externalId")})
    public Website update(String id, FindBy findBy, Website website) {
        return super.update(id, findBy, website);
    }

    @Override
    @Cacheable(value = "websites", key = "#findBy+\"|\"+#id")
    public Optional<Website> get(String id, FindBy findBy, boolean... activeRequired) {
        return super.get(id, findBy, activeRequired);
    }


}
