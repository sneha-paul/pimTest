package com.bigname.pim.core.util;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.service.*;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.common.util.URLUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.Event;
import com.m7.xtreme.xplatform.domain.JobInstance;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.model.Breadcrumbs;
import com.m7.xtreme.xplatform.service.EventService;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import com.m7.xtreme.xplatform.service.UserService;
import com.m7.xtreme.xplatform.util.BaseBreadcrumbsBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class BreadcrumbsBuilder extends BaseBreadcrumbsBuilder {

    public BreadcrumbsBuilder(){

    }

    @Override
    protected void parseServices(BaseService... services) {
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
            } else if(baseService instanceof ConfigService) {
                this.services.put("configService", baseService);
            } else if(baseService instanceof AssetFamilyService) {
                this.services.put("assetFamilyService", baseService);
            } else if(baseService instanceof JobInstanceService) {
                this.services.put("jobInstanceService", baseService);
            } else if(baseService instanceof WebsitePageService) {
                this.services.put("websitePageService", baseService);
            }

        });
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

    protected String[] getUrls(String id, Class<?> entity) {


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
        if(entity.equals(WebsitePage.class)) {
            if(isNotEmpty(websiteId)) {
                baseURL = "/pim/" + getNames(Website.class)[1] + "/" + websiteId;
                url1 = buildURL(baseURL,"#websitePages", urlParams);
                urlParams.put("websiteId", websiteId);
                url2 = buildURL(url2, "", urlParams);
            }
        }
        return new String[] {url1, url2};
    }

    protected String[] getNames(Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.core.domain.Website":
                return new String[] {"Websites", "websites"};
            case "com.bigname.pim.core.domain.Catalog":
                return new String[] {"Catalogs", "catalogs"};
            case "com.bigname.pim.core.domain.Category":
                return new String[] {"Categories", "categories"};
            case "com.bigname.pim.core.domain.Product":
                return new String[] {"Parent Products", "products"};
            case "com.bigname.pim.core.domain.ProductVariant":
                return new String[] {"Child Products", "variants"};
            case "com.bigname.pim.core.domain.AssetCollection":
                return new String[] {"Asset Collections", "assetCollections"};
            case "com.bigname.pim.core.domain.AttributeCollection":
                return new String[] {"Attribute Collections", "attributeCollections"};
            case "com.bigname.pim.core.domain.PricingAttribute":
                return new String[] {"Pricing Attributes", "pricingAttributes"};
            case "com.bigname.pim.core.domain.Family":
                return new String[] {"Product Types", "families"};
            case "com.m7.xtreme.xplatform.domain.User":
                return new String[] {"Users", "users"};
            case "com.m7.xtreme.xplatform.domain.Event":
                return new String[] {"Events", "events"};
            case "com.bigname.pim.core.domain.Config":
                return new String[] {"Config", "configs"};
            case "com.bigname.pim.core.domain.AssetFamily":
                return new String[] {"AssetFamily", "assetFamilies"};
            case "com.m7.xtreme.xplatform.domain.JobInstance":
                return new String[] {"Jobs", "jobs"};
            case "com.bigname.pim.core.domain.WebsitePage":
                return new String[] {"Pages", "pages"};
        }
        return new String[] {"", "", "", ""};
    }

    protected String getCrumbName(String id, Class<?> entity) {
        switch(entity.getCanonicalName()) {
            case "com.bigname.pim.core.domain.Website":
                return ((WebsiteService)services.get("websiteService")).get(ID.EXTERNAL_ID(id), false).map(Website::getWebsiteName).orElse("");
            case "com.bigname.pim.core.domain.Catalog":
                return ((CatalogService)services.get("catalogService")).get(ID.EXTERNAL_ID(id), false).map(Catalog::getCatalogName).orElse("");
            case "com.bigname.pim.core.domain.Category":
                return ((CategoryService)services.get("categoryService")).get(ID.EXTERNAL_ID(id), false).map(Category::getCategoryName).orElse("");
            case "com.bigname.pim.core.domain.Product":
                return ((ProductService)services.get("productService")).get(ID.EXTERNAL_ID(id), false).map(Product::getProductName).orElse("");
            case "com.bigname.pim.core.domain.ProductVariant":
                return ((ProductVariantService)services.get("productVariantService")).get(ID.EXTERNAL_ID(id), getParameter("channelId"), false).map(ProductVariant::getProductVariantName).orElse("");
            case "com.bigname.pim.core.domain.AttributeCollection":
                return ((AttributeCollectionService)services.get("attributeCollectionService")).get(ID.EXTERNAL_ID(id), false).map(AttributeCollection::getCollectionName).orElse("");
            case "com.bigname.pim.core.domain.AssetCollection":
                return ((AssetCollectionService)services.get("assetCollectionService")).get(ID.EXTERNAL_ID(id), false).map(AssetCollection::getCollectionName).orElse("");
            case "com.bigname.pim.core.domain.PricingAttribute":
                return ((PricingAttributeService)services.get("pricingAttributeService")).get(ID.EXTERNAL_ID(id), false).map(PricingAttribute::getPricingAttributeName).orElse("");
            case "com.bigname.pim.core.domain.Family":
                return ((FamilyService)services.get("familyService")).get(ID.EXTERNAL_ID(id), false).map(Family::getFamilyName).orElse("");
            case "com.m7.xtreme.xplatform.domain.User":
                return ((UserService)services.get("userService")).get(ID.EXTERNAL_ID(id), false).map(User::getUserName).orElse("");
            case "com.m7.xtreme.xplatform.domain.Event":
                return ((EventService)services.get("eventService")).get(ID.EXTERNAL_ID(id.toUpperCase()), false).map(Event::getUser).orElse("");
            case "com.bigname.pim.core.domain.Config":
                return ((ConfigService)services.get("configService")).get(ID.EXTERNAL_ID(id), false).map(Config::getConfigName).orElse("");
            case "com.bigname.pim.core.domain.AssetFamily":
                return ((AssetFamilyService)services.get("assetFamilyService")).get(ID.EXTERNAL_ID(id), false).map(AssetFamily::getAssetFamilyName).orElse("");
            case "com.m7.xtreme.xplatform.domain.JobInstance":
                return ((JobInstanceService)services.get("jobInstanceService")).get(ID.EXTERNAL_ID(id), false).map(JobInstance::getJobName).orElse("");
            case "com.bigname.pim.core.domain.WebsitePage":
                return ((WebsitePageService)services.get("websitePageService")).get(ID.EXTERNAL_ID(id), false).map(WebsitePage::getPageName).orElse("");
        }
        return "";
    }

    protected void addCrumbs(String id, Class<?> entity, boolean... endNode) {
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

    protected void addParentCrumbs() {
        if(isNotEmpty(getParameter("parentId"))) {
            String parentId = getParameter("parentId");
            String[] parentIds = StringUtil.splitPipeDelimited(parentId);
            Map<String, Category> parentsMap = ((CategoryService) services.get("categoryService")).getAll(Arrays.stream(parentIds).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false).stream().collect(Collectors.toMap(Entity::getExternalId, c -> c));

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
}
