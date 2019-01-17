package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;

/**
 * Created by Manu on 8/3/2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductController extends BaseController<Product, ProductService>{

    private ProductService productService;
    private VirtualFileService assetService;
    private FamilyService productFamilyService;
    private ChannelService channelService;

    public ProductController(ProductService productService, FamilyService productFamilyService, ChannelService channelService, CategoryService categoryService, CatalogService catalogService, WebsiteService websiteService, VirtualFileService assetService){
        super(productService, Product.class, websiteService, categoryService, catalogService);
        this.productService = productService;
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

    @RequestMapping(value = "/{id}/channels/{channelId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Product product, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productService.get(id, FindBy.EXTERNAL_ID, false).ifPresent(product1 -> product.setProductFamily(product1.getProductFamily()));
        product.setProductId(id);
        product.setAttributeValues(getAttributesMap(request));
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(product, model, product.getGroup().length == 1 && product.getGroup()[0].equals("DETAILS") ? Product.DetailsGroup.class : null)) {
            productService.update(id, FindBy.EXTERNAL_ID, product);
            model.put("success", true);
        }
        return model;
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

    /*@RequestMapping(value = "/{id}/channels/{channelId}/assets", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateAsset(@PathVariable(value = "id") String id, FileAsset asset, HttpServletRequest request) {

    }

    @RequestMapping(value = "/{id}/channels/{channelId}/assets/{assetId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> deleteAsset(@PathVariable(value = "id") String id, @PathVariable(value = "assetId") String assetId, HttpServletRequest request) {

    }
    */

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

/*
    @RequestMapping(value = "/{id}/channels/{channelId}/assets/reorder", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> reorderAsset(@PathVariable(value = "id") String id,
                                            @PathVariable(value = "channelId") String channelId,
                                            @RequestParam(value = "sourceId") String sourceAssetId,
                                            @RequestParam(value = "destinationId") String destinationAssetId) {

    }*/

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
        return new ModelAndView("product/products", model);
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, String>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort;
            if(pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            } else {
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            List<Product> paginatedResult = productService.findAll("productName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.size()));
            result.setRecordsFiltered(Long.toString(paginatedResult.size()));
            return result;
        }
    }

    @RequestMapping("/{id}/categories/data")
    @ResponseBody
    public Result<Map<String, Object>> getProductCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        return getAssociationGridData(productService.getCategories(id, FindBy.EXTERNAL_ID, getPaginationRequest(request), false), ProductCategory.class, request);

    }

    @RequestMapping(value = "/{id}/categories/available")
    public ModelAndView availableCategories(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("category/availableCategories", model);
    }

    @RequestMapping("/{id}/categories/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableCategories(@PathVariable(value = "id") String id, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        } else {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Category> paginatedResult = productService.getAvailableCategoriesForProduct(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/categories/{categoryId}", method = RequestMethod.POST)
    public Map<String, Object> addCategory(@PathVariable(value = "id") String id, @PathVariable(value = "categoryId") String categoryId) {
        Map<String, Object> model = new HashMap<>();
        boolean success = productService.addCategory(id, FindBy.EXTERNAL_ID, categoryId, FindBy.EXTERNAL_ID) != null;
        model.put("success", success);
        return model;
    }
}
