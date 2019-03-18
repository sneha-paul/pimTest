package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.EntityAssociation;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.*;
import com.bigname.pim.data.exportor.ProductExporter;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.ProductUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;

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

    public ProductController(ProductService productService, @Lazy ProductExporter productExporter, ProductVariantService productVariantService, FamilyService productFamilyService, ChannelService channelService, CategoryService categoryService, CatalogService catalogService, WebsiteService websiteService, VirtualFileService assetService){
        super(productService, Product.class, productExporter, websiteService, categoryService, catalogService);
        this.productService = productService;
        this.productVariantService = productVariantService;
        this.productFamilyService = productFamilyService;
        this.channelService = channelService;
        this.assetService = assetService;
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
        productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product1 -> product.setProductFamily(product1.getProductFamily()));
        product.setAttributeValues(getAttributesMap(request));
        return update(productId, product, "/pim/products/", product.getGroup().length == 1 && product.getGroup()[0].equals("DETAILS") ? Product.DetailsGroup.class : null);
    }

    @RequestMapping(value = "/{id}/channels/{channelId}/assets", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAssets(@PathVariable(value = "id") String id,
                                         @PathVariable(value = "channelId") String channelId,
                                         @RequestParam(value="assetIds[]") String[] assetIds,
                                         @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productService.addAssets(id, FindBy.EXTERNAL_ID, channelId, assetIds, FileAsset.AssetFamily.getFamily(assetFamily));
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
        productService.deleteAsset(id, FindBy.EXTERNAL_ID, channelId, assetId, FileAsset.AssetFamily.getFamily(assetFamily));
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
        productService.setAsDefaultAsset(id, FindBy.EXTERNAL_ID, channelId, assetId, FileAsset.AssetFamily.getFamily(assetFamily));
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
        productService.reorderAssets(id, FindBy.EXTERNAL_ID, channelId, assetIds, FileAsset.AssetFamily.getFamily(assetFamily));
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
        return id == null ? super.details(model) : productService.get(id, FindBy.findBy(true), false)
                .map(product -> {
                    product.setChannelId(channelId);
                    model.put("product", product);
                    return super.details(id, parameterMap, request, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Product with Id: " + id));
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
        return all(request, "productName");
    }

    @RequestMapping("/{id}/categories/data")
    @ResponseBody
    public Result<Map<String, Object>> getProductCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(request,
                ProductCategory.class,
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return productService.getCategories(id, FindBy.EXTERNAL_ID, dataTableRequest.getPageRequest(associationSortPredicate), false);
                    } else {
                        return productService.findAllProductCategories(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), dataTableRequest.getPageRequest(associationSortPredicate), false);
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
                        return productService.getAvailableCategoriesForProduct(id, FindBy.EXTERNAL_ID, pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort(), false);
                    } else {
                        return productService.findAvailableCategoriesForProduct(id, FindBy.EXTERNAL_ID, "categoryName", dataTableRequest.getSearch(), pageRequest, false);
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
        boolean success = productService.addCategory(id, FindBy.EXTERNAL_ID, categoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = "/{id}/categories/{categoryId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleProductCategory(@PathVariable(value = "id") String productId,
                                                  @PathVariable(value = "categoryId") String categoryId,
                                                  @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productService.toggleProductCategory(productId, FindBy.EXTERNAL_ID, categoryId, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping("/downloadDigitalAsset")
    public ResponseEntity<Resource> downloadDigitalAssetsImage(@RequestParam(value = "fileId") String fileId, HttpServletRequest request)  {
        VirtualFile asset = assetService.get(fileId, FindBy.INTERNAL_ID,false).orElse(null);
        return downloadAsset(asset.getInternalFileName(), request);
    }
}