package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.service.mongo.BaseServiceSupport;
import com.m7.xtreme.xcore.util.FindBy;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;

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
    public Page<Map<String, Object>> findAllWebsiteCatalogs(String websiteId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, findBy, false)
                .map(catalog -> websiteDAO.findAllWebsiteCatalogs(catalog.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<WebsiteCatalog> getAllWebsiteCatalogs(String websiteInternalId) {
        return websiteCatalogDAO.findByWebsiteId(websiteInternalId);
    }

    @Override
    public Page<Catalog> findAvailableCatalogsForWebsite(String websiteId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, findBy, false)
                .map(catalog -> websiteDAO.findAvailableCatalogsForWebsite(catalog.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
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
    public Page<Catalog> getAvailableCatalogsForWebsite(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Website> website = get(id, findBy, false);
        Set<String> catalogIds = new HashSet<>();
        website.ifPresent(catalog1 -> websiteCatalogDAO.findByWebsiteId(catalog1.getId()).forEach(rc -> catalogIds.add(rc.getCatalogId())));
        return catalogService.getAllWithExclusions(catalogIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, true);
    }


    /**
     * Method to get catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param findBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, FindBy findBy, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, findBy, false)
                .map(website -> websiteDAO.getWebsiteCatalogs(website.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
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
    public Optional<Website> getWebsiteByName(String name) {
        return websiteDAO.findByWebsiteName(name);
    }

    @Override
    public Optional<Website> getWebsiteByUrl(String url) {
        return websiteDAO.findByUrl(url);
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, Website website, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, website, group);
        Website existing = ValidationUtil.isNotEmpty(context.get("id")) ? get((String)context.get("id"), FindBy.EXTERNAL_ID, false).orElse(null) : null;

        if(ValidationUtil.isEmpty(context.get("id")) || (existing != null && !existing.getWebsiteName().equals(website.getWebsiteName()))) {
            findOne(CollectionsUtil.toMap("websiteName", website.getWebsiteName()))
                    .ifPresent(website1 -> fieldErrors.put("websiteName", Pair.with("Website name must be unique, but already exists", website.getWebsiteName())));
        }

        if(ValidationUtil.isEmpty(context.get("id")) || (existing != null && !existing.getUrl().equals(website.getUrl()))) {
            findOne(CollectionsUtil.toMap("url", website.getUrl()))
                    .ifPresent(website1 -> fieldErrors.put("url", Pair.with("Website url must be unique, but already exists", website.getUrl())));
        }

        return _fieldErrors;
    }

    /*@Override
    @Caching(evict = {@CacheEvict(value = "websites", key = "#findBy.INTERNAL_ID+\"|\"+#website.id"), @CacheEvict(value = "websites", key = "#findBy.EXTERNAL_ID+\"|\"+#website.externalId")})
    public Website update(String id, FindBy findBy, Website website) {
        return super.update(id, findBy, website);
    }

    @Override
    @Cacheable(value = "websites", key = "#findBy+\"|\"+#id")
    public Optional<Website> get(String id, FindBy findBy, boolean... activeRequired) {
        return super.get(id, findBy, activeRequired);
    }*/


}
