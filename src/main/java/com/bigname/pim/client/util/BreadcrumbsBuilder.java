package com.bigname.pim.client.util;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.URLUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.service.BaseService;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.*;
import com.bigname.pim.client.model.Breadcrumbs;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;
import static com.bigname.common.util.ValidationUtil.isNotEmpty;
import static com.bigname.pim.util.PIMConstants.Character.EQUALS;

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
            } else if(baseService instanceof ProductService) {
                this.services.put("productService", baseService);
            } else if(baseService instanceof ProductVariantService) {
                this.services.put("productVariantService", baseService);
            } else if(baseService instanceof AssetCollectionService) {
                this.services.put("assetCollectionService", baseService);
            } else if(baseService instanceof AttributeCollectionService) {
                this.services.put("attributeCollectionService", baseService);
            } else if(baseService instanceof PricingAttributeService) {
                this.services.put("pricingAttributeService", baseService);
            } else if(baseService instanceof FamilyService) {
                this.services.put("familyService", baseService);
            } else if(baseService instanceof UserService) {
                this.services.put("userService", baseService);
            } else if(baseService instanceof EventService) {
                this.services.put("eventService", baseService);
            }


        });
    }

    private String getParameter(String name) {
        return parameterMap.containsKey(name) ? (String) parameterMap.get(name) : "";
    }

    public Breadcrumbs build() {
        breadcrumbs = new Breadcrumbs(getNames(entity)[0]);

        String websiteId = getParameter("websiteId");
        String catalogId = getParameter("catalogId");
        String categoryId = getParameter("categoryId");
        String productId = getParameter("productId");

        if(!websiteId.isEmpty()) {
            addCrumbs(websiteId, Website.class);
        }

        if(!catalogId.isEmpty()) {
            addCrumbs(catalogId, Catalog.class);
        }

        if(!categoryId.isEmpty()) {
            addCrumbs(categoryId, Category.class);
        }

        if(!productId.isEmpty()) {
            addCrumbs(productId, Product.class);
        }

        addCrumbs(id, entity, true);

        return breadcrumbs;
    }

    private String[] getUrls(String id, Class<?> entity) {


        String url1 = "/pim/" + getNames(entity)[1];
        String url2 = url1 + "/" + id;
        String websiteId = getParameter("websiteId");
        String catalogId = getParameter("catalogId");
        String parentId = getParameter("parentId");
        /*if(parentId.contains("|")) {
            parentId = parentId.substring(0, parentId.lastIndexOf("|"));
        } else {
            parentId = "";
        }*/
        String categoryId = getParameter("categoryId");
        String productId = getParameter("productId");
        String hash = getParameter("hash");

        Map<String, Object> urlParams = new LinkedHashMap<>();
        String baseURL;
        if(entity.equals(Catalog.class)) {
            if(isNotEmpty(websiteId)) {
                baseURL = "/pim/" + getNames(Website.class)[1] + "/" + websiteId;
                url1 = buildURL(baseURL,"#catalogs", urlParams);
                urlParams.put("websiteId", websiteId);
                url2 = buildURL(url2, "", urlParams);
            }
        }
        if(entity.equals(Category.class)) {
            if(isNotEmpty(websiteId)) {
                urlParams.put("websiteId", websiteId);
            }
            if(isNotEmpty(catalogId)) {
                baseURL = "/pim/" + getNames(Catalog.class)[1] + "/" + catalogId;
                url1 = buildURL(baseURL, "#" + hash, urlParams);
                urlParams.put("catalogId", catalogId);
            }

            if(isNotEmpty(parentId)) {
                urlParams.put("parentId", parentId);
            }

            if(isNotEmpty(getParameter("hash"))) {
                urlParams.put("hash", getParameter("hash"));
            }

            if(isEmpty(websiteId) && isEmpty(catalogId) && isNotEmpty(hash)) {
                url1 += "#" + hash;
            }

            url2 = buildURL(url2, "", urlParams);
        }
        if(entity.equals(Product.class)) {
            if(isNotEmpty(websiteId)) {
                urlParams.put("websiteId", websiteId);
            }
            if(isNotEmpty(catalogId)) {
                urlParams.put("catalogId", catalogId);
            }

            if(isNotEmpty(parentId)) {
                urlParams.put("parentId", URLUtil.encode(parentId));
            }
            if(isNotEmpty(getParameter("hash"))) {
                urlParams.put("hash", getParameter("hash"));
            }

            if(isNotEmpty(categoryId)) {
                baseURL = "/pim/" + getNames(Category.class)[1] + "/" + categoryId;
                url1 = buildURL(baseURL, "#products", urlParams);
                urlParams.put("categoryId", categoryId);
            }

            if(isEmpty(websiteId) && isEmpty(catalogId) && isEmpty(categoryId) && isNotEmpty(hash)) {
                url1 += "#" + hash;
            }
            url2 = buildURL(url2, "", urlParams);
        }
        if(entity.equals(ProductVariant.class)) {
            if(isNotEmpty(websiteId)) {
                urlParams.put("websiteId", websiteId);
            }
            if(isNotEmpty(catalogId)) {
                urlParams.put("catalogId", catalogId);
            }
            if(isNotEmpty(categoryId)) {
                urlParams.put("categoryId", categoryId);
            }
            if(isNotEmpty(parentId)) {
                urlParams.put("parentId", URLUtil.encode(parentId));
            }
            if(isNotEmpty(getParameter("hash"))) {
                urlParams.put("hash", getParameter("hash"));
            }

            if(isNotEmpty(productId)) {
                baseURL = "/pim/" + getNames(Product.class)[1] + "/" + productId;
                url1 = buildURL(baseURL, "#productVariants", urlParams);
            }

            if(isEmpty(websiteId) && isEmpty(catalogId) && isEmpty(categoryId) && isEmpty(productId) && isNotEmpty(hash)) {
                url1 += "#" + hash;
            }
        }
        return new String[] {url1, url2};
    }

    private String[] getNames(Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.api.domain.Website":
                return new String[] {"Websites", "websites"};
            case "com.bigname.pim.api.domain.Catalog":
                return new String[] {"Catalogs", "catalogs"};
            case "com.bigname.pim.api.domain.Category":
                return new String[] {"Categories", "categories"};
            case "com.bigname.pim.api.domain.Product":
                return new String[] {"Products", "products"};
            case "com.bigname.pim.api.domain.ProductVariant":
                return new String[] {"Product Variants", "variants"};
            case "com.bigname.pim.api.domain.AssetCollection":
                return new String[] {"Asset Collections", "assetCollections"};
            case "com.bigname.pim.api.domain.AttributeCollection":
                return new String[] {"Attribute Collections", "attributeCollections"};
            case "com.bigname.pim.api.domain.PricingAttribute":
                return new String[] {"Pricing Attributes", "pricingAttributes"};
            case "com.bigname.pim.api.domain.Family":
                return new String[] {"Families", "families"};
            case "com.bigname.pim.api.domain.User":
                return new String[] {"User", "users"};
            case "com.bigname.pim.api.domain.Event":
                return new String[] {"Event", "events"};
        }
        return new String[] {"", "", "", ""};
    }

    private String getCrumbName(String id, Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.api.domain.Website":
                return ((WebsiteService)services.get("websiteService")).get(id, FindBy.EXTERNAL_ID, false).map(Website::getWebsiteName).orElse("");
            case "com.bigname.pim.api.domain.Catalog":
                return ((CatalogService)services.get("catalogService")).get(id, FindBy.EXTERNAL_ID, false).map(Catalog::getCatalogName).orElse("");
            case "com.bigname.pim.api.domain.Category":
                return ((CategoryService)services.get("categoryService")).get(id, FindBy.EXTERNAL_ID, false).map(Category::getCategoryName).orElse("");
            case "com.bigname.pim.api.domain.Product":
                return ((ProductService)services.get("productService")).get(id, FindBy.EXTERNAL_ID, false).map(Product::getProductName).orElse("");
            case "com.bigname.pim.api.domain.ProductVariant":
                return ((ProductVariantService)services.get("productVariantService")).get(id, FindBy.EXTERNAL_ID, getParameter("channelId"), false).map(ProductVariant::getProductVariantName).orElse("");
            case "com.bigname.pim.api.domain.AttributeCollection":
                return ((AttributeCollectionService)services.get("attributeCollectionService")).get(id, FindBy.EXTERNAL_ID, false).map(AttributeCollection::getCollectionName).orElse("");
            case "com.bigname.pim.api.domain.AssetCollection":
                return ((AssetCollectionService)services.get("assetCollectionService")).get(id, FindBy.EXTERNAL_ID, false).map(AssetCollection::getCollectionName).orElse("");
            case "com.bigname.pim.api.domain.PricingAttribute":
                return ((PricingAttributeService)services.get("pricingAttributeService")).get(id, FindBy.EXTERNAL_ID, false).map(PricingAttribute::getPricingAttributeName).orElse("");
            case "com.bigname.pim.api.domain.Family":
                return ((FamilyService)services.get("familyService")).get(id, FindBy.EXTERNAL_ID, false).map(Family::getFamilyName).orElse("");
            case "com.bigname.pim.api.domain.User":
                return ((UserService)services.get("userService")).get(id, FindBy.EXTERNAL_ID, false).map(User::getUserName).orElse("");
            case "com.bigname.pim.api.domain.Event":
                return ((EventService)services.get("eventService")).get(id, FindBy.EXTERNAL_ID, false).map(Event::getUserName).orElse("");
        }
        return "";
    }

    private void addCrumbs(String id, Class<?> entity, boolean... endNode) {
        if(!id.isEmpty()) {
            String[] names = getNames(entity);
            String[] urls = getUrls(id, entity);
            if(endNode != null && endNode.length > 0 && endNode[0]) {
                urls[1] = "";
            }
            breadcrumbs.addCrumbs(names[0], urls[0]);
            if(entity.equals(Category.class)) {
                addParentCrumbs();
//                if(this.entity.equals(Category.class)) {
                    breadcrumbs.addCrumbs(getCrumbName(id, entity), urls[1]);
//                }
            } else {
                breadcrumbs.addCrumbs(getCrumbName(id, entity), urls[1]);
            }

        }
    }

    private void addParentCrumbs() {
        if(isNotEmpty(getParameter("parentId"))) {
            String parentId = getParameter("parentId");
            String[] parentIds = StringUtil.splitPipeDelimited(parentId);
            Map<String, Category> parentsMap = ((CategoryService) services.get("categoryService")).getAll(parentIds, FindBy.EXTERNAL_ID, null, false).stream().collect(Collectors.toMap(Entity::getExternalId, c -> c));

            String _parentId = "";
            for (int i = 0; i < parentIds.length; i++) {
                if (i > 0) {
                    _parentId += (_parentId.isEmpty() ? "" : "|") + parentIds[i - 1];
                }
                Category parentCategory = parentsMap.get(parentIds[i]);
                String baseURL = "/pim/categories/" + parentCategory.getCategoryId();
                Map<String, Object> urlParams = new LinkedHashMap<>();
                if(isNotEmpty(getParameter("websiteId"))) {
                    urlParams.put("websiteId", getParameter("websiteId"));
                }
                if(isNotEmpty(getParameter("catalogId"))) {
                    urlParams.put("catalogId", getParameter("catalogId"));
                }
                if(isNotEmpty(_parentId)) {
                    urlParams.put("parentId", URLUtil.encode(_parentId));
                }
                if(isNotEmpty(getParameter("hash"))) {
                    urlParams.put("hash", getParameter("hash"));
                }
                breadcrumbs.addCrumbs(parentCategory.getCategoryName(), buildURL(baseURL, "", urlParams));
            }
        }
    }

    private String buildURL(String baseURL, String hash, Map<String, Object> parameters) {
        StringBuilder url = new StringBuilder(baseURL);
        if(!parameters.isEmpty()) {
            if(!baseURL.contains("?")) {
                url.append("?");
            } else {
                url.append("&");
            }

            boolean[] first = {true};
            parameters.forEach((name, value) -> {
                url.append(first[0] ? "" : "&").append(name).append(EQUALS).append(value);
                first[0] = false;
            });
        }
        return isEmpty(hash) ? url.toString() : url.append(hash).toString();
    }

}
