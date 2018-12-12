package com.bigname.pim.client.util;

import com.bigname.common.util.ConversionUtil;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.service.BaseService;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.ConvertUtil;
import com.bigname.pim.util.FindBy;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class BreadcrumbsBuilder {
    private Map<String, BaseService> services = new HashMap<>();
    private HttpServletRequest request = null;
    private Map<String, Object> parameterMap = new HashMap<>();
    private Breadcrumbs breadcrumbs;
    private String id;
    private Class<?> entity;

    public BreadcrumbsBuilder(String id, Class<?> entity, BaseService... services) {
        this(id, entity, null, null, services);
    }

    public BreadcrumbsBuilder(String id, Class<?> entity, HttpServletRequest request, Map<String, Object> parameterMap, BaseService... services) {
        this.id = id;
        this.entity = entity;
        this.request = request;
        if(parameterMap != null) {
            this.parameterMap = parameterMap;
        }
        ConversionUtil.toList(services).forEach(baseService -> {
            if(baseService instanceof WebsiteService) {
                this.services.put("websiteService", baseService);
            } else if(baseService instanceof CatalogService) {
                this.services.put("catalogService", baseService);
            } else if(baseService instanceof CategoryService) {
                this.services.put("categoryService", baseService);
            }
        });
    }

    private String getParameter(String name) {
        return parameterMap.containsKey(name) ? (String) parameterMap.get(name) : "";
    }

    public Breadcrumbs build() {
        breadcrumbs = new Breadcrumbs(getNames(entity)[0]);

        String websiteId = parameterMap.containsKey("websiteId") ? (String) parameterMap.get("websiteId") : "";
        String catalogId = parameterMap.containsKey("catalogId") ? (String) parameterMap.get("catalogId") : "";
        String hash = parameterMap.containsKey("hash") ? (String) parameterMap.get("hash") : "";


        /*String defaultURL = "/pim/catalogs";
        if(!websiteId.isEmpty()) {
            defaultURL = "/pim/websites/" + websiteId + "#" + hash;
        }
        String referrer = getReferrerURL(request, defaultURL, "");*/

        if(!websiteId.isEmpty()) {
            addCrumbs(websiteId, Website.class);
        }

        if(!catalogId.isEmpty()) {
            addCrumbs(catalogId, Catalog.class);
        }

        addCrumbs(id, entity, true);

        return breadcrumbs;
    }

    private String getReferrerUrl() {
        String websiteId = parameterMap.containsKey("websiteId") ? (String) parameterMap.get("websiteId") : "";
        String catalogId = parameterMap.containsKey("catalogId") ? (String) parameterMap.get("catalogId") : "";
        String hash = parameterMap.containsKey("hash") ? (String) parameterMap.get("hash") : "";

        String referrerUrl = "/pim/" + getNames(entity)[1] + "/";

        if(!websiteId.isEmpty()) {
            referrerUrl = "/pim/" + getNames(Website.class)[1] + "/";
        }

        if(!hash.isEmpty()) {
            referrerUrl += "#" + hash;
        }
        return referrerUrl;
    }

    private String[] getNames(Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.api.domain.Website":
                return new String[] {"Websites", "websites"};
            case "com.bigname.pim.api.domain.Catalog":
                return new String[] {"Catalogs", "catalogs"};
            case "com.bigname.pim.api.domain.Category":
                return new String[] {"Categories", "category"};
        }
        return new String[] {"", "", ""};
    }

    /*private String getBaseURL(Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.api.domain.Website":
                return "/pim/websites/";
            case "com.bigname.pim.api.domain.Catalog":
                return "/pim/catalogs/";
            case "com.bigname.pim.api.domain.Category":
                return "/pim/categories/";
        }
        return "";
    }*/

    private String getCrumbName(String id, Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.api.domain.Website":
                return ((WebsiteService)services.get("websiteService")).get(id, FindBy.EXTERNAL_ID, false).map(Website::getWebsiteName).orElse("");
            case "com.bigname.pim.api.domain.Catalog":
                return ((CatalogService)services.get("catalogService")).get(id, FindBy.EXTERNAL_ID, false).map(Catalog::getCatalogName).orElse("");
            case "com.bigname.pim.api.domain.Category":
                return ((CategoryService)services.get("categoryService")).get(id, FindBy.EXTERNAL_ID, false).map(Category::getCategoryName).orElse("");
        }
        return "";
    }

    private void addCrumbs(String id, Class<?> entity, boolean... endNode) {
        if(!id.isEmpty()) {
            String[] names = getNames(entity);
            String baseURL = "/pim/" + names[1];
            String crumbURL = "";
            String websiteId = getParameter("websiteId");
            if(entity.equals(Catalog.class) && !websiteId.isEmpty()) {
                crumbURL = "/pim/websites/" + websiteId + "#" + getParameter("hash");
            }
            breadcrumbs.addCrumbs(names[0], crumbURL.isEmpty() ? baseURL : crumbURL);
            breadcrumbs.addCrumbs(getCrumbName(id, entity), endNode != null && endNode.length > 0 && endNode[0] ? "" : baseURL + "/" + id);
        }
    }


}
