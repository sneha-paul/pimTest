package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.ProductCategoryDAO;
import com.bigname.pim.core.service.*;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.bigname.pim.core.data.exportor.ProductExporter;
import com.bigname.pim.core.util.PIMConstants;
import com.bigname.pim.core.util.ProductUtil;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xcore.web.controller.BaseController;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.service.JobInstanceService;
import com.m7.xtreme.xplatform.service.SyncStatusService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


/**
 * Created by Manu on 8/3/2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductController extends BaseController<Product, ProductService> {

    private ProductService productService;
    private ProductVariantService productVariantService;
    private VirtualFileService assetService;
    private FamilyService productFamilyService;
    private ChannelService channelService;
    private RestTemplate restTemplate;
    private SyncStatusService syncStatusService;

    public ProductController(ProductService productService, @Lazy ProductExporter productExporter, JobInstanceService jobInstanceService, ProductVariantService productVariantService, FamilyService productFamilyService, ChannelService channelService, CategoryService categoryService, CatalogService catalogService, WebsiteService websiteService, VirtualFileService assetService, RestTemplate restTemplate, SyncStatusService syncStatusService){
        super(productService, Product.class, new BreadcrumbsBuilder(), productExporter, jobInstanceService, websiteService, categoryService, catalogService, productVariantService, productFamilyService, productService);
        this.productService = productService;
        this.productVariantService = productVariantService;
        this.productFamilyService = productFamilyService;
        this.channelService = channelService;
        this.assetService = assetService;
        this.restTemplate = restTemplate;
        this.syncStatusService = syncStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(Product product) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(product, model, Product.CreateGroup.class)) {
            product.setActive("N");
            product.setDiscontinued("N");
            productService.create(product);
            model.put("success", true);
        }
        return model;
    }



    @RequestMapping(value = "/{productId}/channels/{channelId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "productId") String productId, Product product, HttpServletRequest request) {
        productService.get(ID.EXTERNAL_ID(productId), true, true, true).ifPresent(product1 -> product.setProductFamily(product1.getProductFamily()));
        product.setAttributeValues(getAttributesMap(request));
        Product productDetails = productService.get(ID.EXTERNAL_ID(productId), true, true, true).orElse(null);
        productService.getAllCategoryProductsWithProductId(ID.INTERNAL_ID(productDetails.getId()))
                .forEach(categoryProduct -> {
                    categoryProduct.setActive(product.getActive());
                    productService.updateCategoryProduct(categoryProduct);
                });
        product.setLastExportedTimeStamp(null);
        return update(productId, product, "/pim/products/", product.getGroup().length == 1 && product.getGroup()[0].equals("DETAILS") ? Product.DetailsGroup.class : null);
    }

    @RequestMapping(value = "/{id}/channels/{channelId}/assets", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAssets(@PathVariable(value = "id") String id,
                                         @PathVariable(value = "channelId") String channelId,
                                         @RequestParam(value="assetIds[]") String[] assetIds,
                                         @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productService.addAssets(ID.EXTERNAL_ID(id), channelId, Arrays.stream(assetIds).map(ID::INTERNAL_ID).collect(Collectors.toList()), FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = "/{id}/channels/{channelId}/assets", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteAsset(@PathVariable(value = "id") String id,
                                           @PathVariable(value = "channelId") String channelId,
                                           @RequestParam(value="assetId") String assetId,
                                           @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productService.deleteAsset(ID.EXTERNAL_ID(id), channelId, ID.INTERNAL_ID(assetId), FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }


    @RequestMapping(value = "/{id}/channels/{channelId}/assets/setDefault", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setAsDefaultAsset(@PathVariable(value = "id") String id,
                                                 @PathVariable(value = "channelId") String channelId,
                                                 @RequestParam(value="assetId") String assetId,
                                                 @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productService.setAsDefaultAsset(ID.EXTERNAL_ID(id), channelId, ID.INTERNAL_ID(assetId), FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = "/{id}/channels/{channelId}/assets/reorder", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> reorderAsset(@PathVariable(value = "id") String id,
                                            @PathVariable(value = "channelId") String channelId,
                                            @RequestParam(value="assetIds[]") String[] assetIds,
                                            @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productService.reorderAssets(ID.EXTERNAL_ID(id), channelId, Arrays.stream(assetIds).map(ID::INTERNAL_ID).collect(Collectors.toList()), FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "channelId", defaultValue = PIMConstants.DEFAULT_CHANNEL_ID) String channelId,
                                @RequestParam(name = "reload", required = false) boolean reload,
                                @RequestParam Map<String, Object> parameterMap,
                                HttpServletRequest request) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "product/product" + (reload ? "_body" : ""));
        model.put("productFamilies", productFamilyService.getAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "familyName"))));
        model.put("channels", channelService.getAll(0, 100, null).stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName))); //TODO - replace with a separate service method

        if(id == null) {
            return super.details(model);
        } else {
            Product product = productService.get(ID.EXTERNAL_ID(id), true, true, true).orElse(null);
            if(isNotEmpty(product)) {
                product.setChannelId(channelId);
                model.put("product", product);

            } else if(isEmpty(product)) {
                product = productService.get(ID.EXTERNAL_ID(id), false, false, false, true).orElse(null);
                product.setChannelId(channelId);
                model.put("product", product);
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + id);
            }
        }
        return super.details(id, parameterMap, request, model);
    }

    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        model.put("view", "product/products");
        model.put("title", "Products");
        return all(model);
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        if(!isEmpty(dataTableRequest.getAdvancedSearch()) && !dataTableRequest.getAdvancedSearch().isEmpty()) {
                            return productService.findAll(dataTableRequest.getAdvancedSearch(), dataTableRequest.getPageRequest(defaultSort), dataTableRequest.getStatusOptions());
                        } else {
                            return productService.findAll(dataTableRequest.getPageRequest(defaultSort), dataTableRequest.getStatusOptions());
                        }
                    } else {
                        return productService.findAll("productName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(defaultSort), dataTableRequest.getStatusOptions());
                    }
                },
                paginatedResult -> {
                    List<String> productIds = paginatedResult.stream().map(Entity::getId).collect(Collectors.toList());
                    List<ProductVariant> productVariants = productVariantService.getAll(productIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), PIMConstants.DEFAULT_CHANNEL_ID, false);
                    Map<String, Map<String, Object>> productsVariantsInfo = ProductUtil.getVariantDetailsForProducts(productIds, productVariants, 4);

                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.forEach(e -> {
                        Map<String, String> map = e.toMap();
                        Map<String, Object> productVariantsInfo = productsVariantsInfo.get(e.getId());
                        map.put("variantCount", Integer.toString((int)productVariantsInfo.get("totalVariants")));
                        map.put("variantImages", StringUtil.concatinate((List<String>)productVariantsInfo.get("variantImages"), "|"));
                        dataObjects.add(map);
                    });
                    return dataObjects;
                });
    }

    @RequestMapping("/{id}/categories/data")
    @ResponseBody
    public Result<Map<String, Object>> getProductCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                ProductCategory.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return productService.getCategories(ID.EXTERNAL_ID(id), dataTableRequest.getPageRequest(associationSortPredicate), dataTableRequest.getStatusOptions());
                    } else {
                        return productService.findAllProductCategories(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
                    }
                });


    }

    @RequestMapping(value = "/{id}/categories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableCategories", model);
    }

    @RequestMapping("/{id}/categories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    PageRequest pageRequest = dataTableRequest.getPageRequest(defaultSort);
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return productService.getAvailableCategoriesForProduct(ID.EXTERNAL_ID(id), pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return productService.findAvailableCategoriesForProduct(ID.EXTERNAL_ID(id), "categoryName", dataTableRequest.getSearch(), pageRequest, false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
        }

    @ResponseBody
    @RequestMapping(value = "/{id}/categories/{categoryId}", method = RequestMethod.POST)
    public Map<String, Object> addCategory(@PathVariable(value = "id") String id, @PathVariable(value = "categoryId") String categoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = productService.addCategory(ID.EXTERNAL_ID(id), ID.EXTERNAL_ID(categoryId)) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/categories/{categoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleProductCategory(@PathVariable(value = "id") String productId,
                                                  @PathVariable(value = "categoryId") String categoryId,
                                                  @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productService.toggleProductCategory(ID.EXTERNAL_ID(productId), ID.EXTERNAL_ID(categoryId), Toggle.get(active)));
        return model;
    }

    @RequestMapping("/downloadDigitalAsset")
    public ResponseEntity<Resource> downloadDigitalAssetsImage(@RequestParam(value = "fileId") String fileId, HttpServletRequest request)  {
        VirtualFile asset = assetService.get(ID.INTERNAL_ID(fileId),false).orElse(null);
        return downloadAsset(asset.getInternalFileName(), request);
    }

    @RequestMapping(value = "/{productId}/products/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleProducts(@PathVariable(value = "productId") String productId,
                                              @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productService.toggleProduct(ID.EXTERNAL_ID(productId), Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = {"/search"})
    public ModelAndView search() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        model.put("view", "search");
        model.put("title", "Products");
        return super.details(model);
    }

    @RequestMapping(value = "/{productId}/products/archive/{archived}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> archive(@PathVariable(value = "productId") String productId, @PathVariable(value = "archived") String archived) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productService.archive(ID.EXTERNAL_ID(productId), Archive.get(archived)));
        return model;
    }
    @RequestMapping(value ="/productLoad")
    public void loadProductToBOS() {
        List<Product> productList = productService.getAll(null, false);
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/product/loadProduct", productList, String.class, map);
    }

    @RequestMapping(value ="/productCategoryLoad")
    public void loadProductCategoryToBOS() {
        List<ProductCategory> productCategoryList = productService.loadProductCategoryToBOS();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://envelopes.localhost:8084/product/loadProductCategory", productCategoryList, String.class, map);
    }

    @RequestMapping(value ="/syncUpdatedProducts", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncUpdatedProducts() {
        Map<String, Object> model = new HashMap<>();
        List<Product> products = productService.syncUpdatedRecord();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/products/syncUpdatedProducts", products, String.class, map);
        if(Objects.equals(response.getBody(), "true")) {
            products.forEach(product -> {
                product.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(product.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(product.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            productService.update(products);
            model.put("success", true);
        } else {
            model.put("success", false);
        }
        return model;
    }

    @RequestMapping(value ="/{productId}/syncProductCategories", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncProductCategories(@PathVariable(value = "productId") String productId) {
        Map<String, Object> model = new HashMap<>();
        Product product = productService.get(ID.EXTERNAL_ID(productId), false).orElse(null);
        List<ProductCategory> productCategoryList = productService.getAllProductCategories(product.getId());
        List<ProductCategory> finalProductCategory = new ArrayList<>();
        productCategoryList.forEach(productCategory -> {
            if(ValidationUtil.isEmpty(productCategory.getLastExportedTimeStamp())) {
                finalProductCategory.add(productCategory);
            }
        });
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/products/syncProductCategories", finalProductCategory, String.class, new HashMap<>());
        if(Objects.equals(response.getBody(), "true")) {
            finalProductCategory.forEach(productCategory -> {
                productCategory.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(productCategory.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(productCategory.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            productService.syncProductCategory(finalProductCategory);
            model.put("success", true);
        } else {
            model.put("success", null);
        }
        return model;
    }

}