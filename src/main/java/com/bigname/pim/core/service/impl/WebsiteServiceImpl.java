package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.Website;
import com.bigname.pim.core.domain.WebsiteCatalog;
import com.bigname.pim.core.persistence.dao.mongo.WebsiteCatalogDAO;
import com.bigname.pim.core.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.core.service.CatalogService;
import com.bigname.pim.core.service.WebsiteService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.Criteria;
import com.m7.xtreme.xcore.util.ID;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public Page<Map<String, Object>> findAllWebsiteCatalogs(ID<String> websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, false)
                .map(catalog -> websiteDAO.findAllWebsiteCatalogs(catalog.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<WebsiteCatalog> getAllWebsiteCatalogs(String websiteInternalId) {
        return websiteCatalogDAO.findByWebsiteId(websiteInternalId);
    }

    @Override
    public Page<Catalog> findAvailableCatalogsForWebsite(ID<String> websiteId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, false)
                .map(catalog -> websiteDAO.findAvailableCatalogsForWebsite(catalog.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get available catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Catalog> getAvailableCatalogsForWebsite(ID<String> websiteId, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Website> website = get(websiteId,false);
        Set<String> catalogIds = new HashSet<>();
        website.ifPresent(catalog1 -> websiteCatalogDAO.findByWebsiteId(catalog1.getId()).forEach(rc -> catalogIds.add(rc.getCatalogId())));
        return catalogService.getAllWithExclusions(catalogIds.stream().map(e -> ID.INTERNAL_ID(e)).collect(Collectors.toList()), page, size, sort, true);
    }


    /**
     * Method to get catalogs of a website in paginated format.
     *
     * @param websiteId Internal or External id of the Website
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getWebsiteCatalogs(ID<String> websiteId, Pageable pageable, boolean... activeRequired) {
        return get(websiteId, false)
                .map(website -> websiteDAO.getWebsiteCatalogs(website.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to add catalog for a website.
     *
     * @param websiteId Internal or External id of the Website
     * @param catalogId Internal or External id of the Catalog
     * @return
     */
    @Override
    public WebsiteCatalog addCatalog(ID<String> websiteId, ID<String> catalogId) {
        Optional<Website> website = proxy().get(websiteId, false);
        if(website.isPresent()) {
            Optional<Catalog> catalog = catalogService.get(catalogId, false);
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
        Website existing = ValidationUtil.isNotEmpty(context.get("id")) ? self.get(ID.EXTERNAL_ID(context.get("id")), false).orElse(null) : null;

        if(ValidationUtil.isEmpty(context.get("id")) || (existing != null && !existing.getWebsiteName().equals(website.getWebsiteName()))) {
            findOne(Criteria.where("websiteName").eq(website.getWebsiteName()))
                    .ifPresent(website1 -> fieldErrors.put("websiteName", Pair.with("Website name must be unique, but already exists", website.getWebsiteName())));
        }

        if(ValidationUtil.isEmpty(context.get("id")) || (existing != null && !existing.getUrl().equals(website.getUrl()))) {
            findOne(Criteria.where("url").eq(website.getUrl()))
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

    @Override
    public List<WebsiteCatalog> loadWebsiteCatalogsToBOS() {
        return websiteCatalogDAO.findAll();
    }

    @Override
    public List<WebsiteCatalog> syncWebsiteCatalog(List<WebsiteCatalog> finalWebsiteCatalog) {
        return websiteCatalogDAO.saveAll(finalWebsiteCatalog);
    }
}
