package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.*;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.Pageable;
import com.bigname.pim.util.Toggle;
import org.apache.commons.collections4.MapUtils;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;
import static com.bigname.common.util.ValidationUtil.isNotEmpty;

/**
 * Created by sruthi on 20-09-2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductVariantController extends ControllerSupport {

    private ProductVariantService productVariantService;

    private PricingAttributeService pricingAttributeService;

    private ProductService productService;

    private ChannelService channelService;

    private CategoryService categoryService;

    private CatalogService catalogService;

    private WebsiteService websiteService;

    public ProductVariantController( ProductVariantService productVariantService,
                                     ProductService productService,
                                     ChannelService channelService,
                                     PricingAttributeService pricingAttributeService,
                                     CategoryService categoryService,
                                     CatalogService catalogService,
                                     WebsiteService websiteService){
        this.productVariantService = productVariantService;
        this.productService = productService;
        this.channelService = channelService;
        this.pricingAttributeService = pricingAttributeService;
        this.websiteService = websiteService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
    }

    @RequestMapping("/{productId}/channels/{channelId}/variants/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableVariants(@PathVariable(value = "productId") String productId,
                                                            @PathVariable(value = "channelId") String channelId,
                                                            HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Map<String, String>> paginatedResult = productService.getAvailableVariants(productId, FindBy.EXTERNAL_ID, channelId, pagination.getPageNumber(), pagination.getPageSize(), sort);
        dataObjects.addAll(paginatedResult.getContent());
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }



    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{variantIdentifier}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(@PathVariable(value = "productId") String productId,
                                      @PathVariable(value = "channelId") String channelId,
                                      @PathVariable(value = "variantIdentifier") String variantIdentifier) {
        Map<String, Object> model = new HashMap<>();
        boolean[] success = {false};
        if(isNotEmpty(productId) && isNotEmpty(channelId) && isNotEmpty(variantIdentifier)) {
            productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product -> {
                Family productFamily = product.getProductFamily();
                String variantGroupId = productFamily.getChannelVariantGroups().get(channelId);
                VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup) && isNotEmpty(variantGroup.getVariantAxis().get(1))) {
                    List<String> axisAttributeTokens = StringUtil.splitPipeDelimitedAsList(variantIdentifier);
                    Map<String, String> axisAttributes = new HashMap<>();
                    StringBuilder tempId = new StringBuilder();
                    StringBuilder tempName = new StringBuilder();
                    for (int i = 0; i < axisAttributeTokens.size(); i = i + 2) {
                        axisAttributes.put(axisAttributeTokens.get(i), axisAttributeTokens.get(i + 1));
                        tempId.append("_").append(axisAttributeTokens.get(i + 1));
                        String nameToken = axisAttributeTokens.get(i + 1);
                        FamilyAttribute axisAttribute = productFamily.getAllAttributesMap().get(axisAttributeTokens.get(i));
                        if(isNotEmpty(axisAttribute) && axisAttribute.getOptions().containsKey(axisAttributeTokens.get(i + 1))) {
                            nameToken = axisAttribute.getOptions().get(axisAttributeTokens.get(i + 1)).getValue();
                        }
                        tempName.append(tempName.length() > 0 ? " - " : "").append(nameToken);
                    }
                    ProductVariant productVariant = new ProductVariant(product);
                    productVariant.setProductVariantName(product.getProductName() + " - " + tempName.toString());
                    productVariant.setProductVariantId(tempId.toString());
                    productVariant.setChannelId(channelId);
                    productVariant.setAxisAttributes(axisAttributes);
                    productVariant.setActive("N");
                    productVariantService.create(productVariant);
                    success[0] = true;
                }
            });
        }
        model.put("success", success[0]);
        return model;
    }

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{variantId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "productId") String productId,
                                      @PathVariable(value = "variantId") String variantId,
                                      ProductVariant productVariantDTO,
                                      HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productVariantDTO.setLevel(1); //TODO - make this dynamic
        setVariantAttributeValues(productVariantDTO, request);
        if(isValid(productVariantDTO, model, productVariantDTO.getGroup().length == 1 && productVariantDTO.getGroup()[0].equals("DETAILS") ? ProductVariant.DetailsGroup.class : null)) {
            productVariantService.update(variantId, FindBy.EXTERNAL_ID, productVariantDTO);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/assets", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAssets(@PathVariable(value = "productId") String productId,
                                         @PathVariable(value = "variantId") String variantId,
                                         @RequestParam(name = "channelId") String channelId,
                                         @RequestParam(value="assetIds[]") String[] assetIds,
                                         @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productVariantService.addAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, assetIds, FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/assets", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteAsset(@PathVariable(value = "productId") String productId,
                                           @PathVariable(value = "variantId") String variantId,
                                           @RequestParam(name = "channelId") String channelId,
                                           @RequestParam(value="assetId") String assetId,
                                           @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productVariantService.deleteAsset(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, assetId, FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }


    @RequestMapping(value = "/{productId}/variants/{variantId}/assets/setDefault", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setAsDefaultAsset(@PathVariable(value = "productId") String productId,
                                                 @PathVariable(value = "variantId") String variantId,
                                                 @RequestParam(name = "channelId") String channelId,
                                                 @RequestParam(value="assetId") String assetId,
                                                 @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productVariantService.setAsDefaultAsset(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, assetId, FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/assets/reorder", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> reorderAsset(@PathVariable(value = "productId") String productId,
                                            @PathVariable(value = "variantId") String variantId,
                                            @RequestParam(name = "channelId") String channelId,
                                            @RequestParam(value="assetIds[]") String[] assetIds,
                                            @RequestParam(value="assetFamily") String assetFamily) {
        Map<String, Object> model = new HashMap<>();
        productVariantService.reorderAssets(productId, FindBy.EXTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, assetIds, FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    private void setVariantAttributeValues(ProductVariant productVariantDTO, HttpServletRequest request) {
        productService.get(productVariantDTO.getProductId(), FindBy.EXTERNAL_ID, false).ifPresent(product -> {
            product.setChannelId(productVariantDTO.getChannelId());
            productVariantDTO.setProduct(product);
            Family productFamily = product.getProductFamily();
            String variantGroupId = productFamily.getChannelVariantGroups().get(productVariantDTO.getChannelId());
            VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
            if(isNotEmpty(variantGroup)) {
                Map<String, Object> attributesMap = getAttributesMap(request);
                List<String> variantAttributeIds = variantGroup.getVariantAttributes().get(productVariantDTO.getLevel());
                variantAttributeIds.forEach(attributeId -> {
                    if(attributesMap.containsKey(attributeId)) {
                        productVariantDTO.getVariantAttributes().put(attributeId, attributesMap.get(attributeId));
                    }
                });
            }
        });
    }

    @RequestMapping("/{productId}/channels/{channelId}/variants/data")
    @ResponseBody
    public Result<Map<String, String>> allChannelVariants(@PathVariable(name = "productId") String productId, @PathVariable(name = "channelId") String channelId, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        if(isNotEmpty(productId) && isNotEmpty(channelId)) {
            productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product -> {
                Sort sort = null;
                if (pagination.hasSorts()) {
                    sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                } else {
                    sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
                }
                Page<ProductVariant> paginatedResult = isEmpty(dataTableRequest.getSearch()) ? productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, channelId, pagination.getPageNumber(), pagination.getPageSize(), sort, false):
                        productVariantService.findAll("productVariantName", dataTableRequest.getSearch(),product.getId(), FindBy.INTERNAL_ID, channelId, new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
                List<Map<String, String>> dataObjects = new ArrayList<>();
                paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                result.setDataObjects(dataObjects);
                result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            });
        } else {
            //TODO - send error message
        }
        return result;
    }

    @RequestMapping(value = {"/{productId}/variants/{variantId}/pricingDetails/{pricingAttributeId}", "/{productId}/variants/{variantId}/pricingDetails"})
    public ModelAndView variantPricingDetails(@PathVariable(value = "productId") String productId,
                                       @PathVariable(value = "variantId") String variantId,
                                       @PathVariable(value = "pricingAttributeId", required = false) String pricingAttributeId,
                                       @RequestParam(name = "channelId") String channelId) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        Optional<Product> _product = productService.get(productId, FindBy.findBy(true), false);
        if(_product.isPresent()) {
            Product product = _product.get();
            Optional<ProductVariant> _productVariant = productVariantService.get(product.getId(), FindBy.INTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, false);
            if(_productVariant.isPresent()) {
                ProductVariant productVariant = _productVariant.get();
                productVariant.setProduct(product);
                model.put("productVariant", productVariant);
                model.put("availablePricingAttributes", pricingAttributeService.getAllWithExclusions(new ArrayList<>(productVariant.getPricingDetails().keySet()).toArray(new String[0]), FindBy.EXTERNAL_ID, null));
                PricingAttribute pricingAttribute = isEmpty(pricingAttributeId) ? null : pricingAttributeService.get(pricingAttributeId, FindBy.EXTERNAL_ID, false).orElse(null);
                model.put("pricingDetails", getAttributePricingDetails(productVariant.getPricingDetails(), pricingAttribute));
                if(isNotEmpty(pricingAttributeId)) {
                    model.put("pricingAttribute", pricingAttribute);
                }
            } else {
                throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + variantId);
            }
        } else {
            throw new EntityNotFoundException("Unable to find Product with Id: " + productId);
        }
        return new ModelAndView("product/pricingDetails", model);
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/pricingDetails", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> savePricingDetails(@PathVariable(value = "productId") String productId,
                                                  @PathVariable(value = "variantId") String variantId,
                                                  @RequestParam Map<String, Object> pricingDetailMap,
                                                  PricingDetail pricingDetail) {
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        pricingDetail.setPricing(pricingDetailMap.entrySet().stream().filter(e -> e.getKey().startsWith("q.") && isNotEmpty(e.getValue())).collect(CollectionsUtil.toTreeMap(e -> new Integer(e.getKey().replaceAll("q.", "")), e -> new BigDecimal((String)e.getValue()))));
        if(isValid(pricingDetail, model)) {
            Optional<Product> _product = productService.get(productId, FindBy.findBy(true), false);
            if (_product.isPresent()) {
                String channelId = pricingDetail.getChannelId();
                Product product = _product.get();
                Optional<Channel> _channel = channelService.get(channelId, FindBy.EXTERNAL_ID);
                if (_channel.isPresent()) {
                    Optional<ProductVariant> _productVariant = productVariantService.get(product.getId(), FindBy.INTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, false);
                    if (_productVariant.isPresent()) {
                        ProductVariant productVariant = _productVariant.get();
                        productVariant.setProduct(product);
                        productVariant.setGroup("PRICING_DETAILS");
                        model.put("refreshPage", !getQuantityBreaks(productVariant.getPricingDetails()).containsAll(pricingDetail.getPricing().keySet()));
                        productVariant.getPricingDetails().put(pricingDetail.getPricingAttributeId(), pricingDetail.getPricing());
                        productVariantService.update(productVariant.getId(), FindBy.INTERNAL_ID, productVariant);
                        success = true;
                    } else {
                        throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + variantId);
                    }
                } else {
                    throw new EntityNotFoundException("Unable to find Channel with Id: " + channelId);
                }
            } else {
                throw new EntityNotFoundException("Unable to find Product with Id: " + productId);
            }
        }
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = {"/{productId}/variants/{variantId}", "/{productId}/variants/create"})
    public ModelAndView variantDetails(@PathVariable(value = "productId") String productId,
                                       @PathVariable(value = "variantId", required = false) String variantId,
                                       @RequestParam(name = "channelId", defaultValue = PIMConstants.DEFAULT_CHANNEL_ID) String channelId,
                                       @RequestParam(name = "reload", required = false) boolean reload,
                                       @RequestParam Map<String, Object> parameterMap,
                                       HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        Optional<Product> _product = productService.get(productId, FindBy.findBy(true), false);
        if(_product.isPresent()) {
            Product product = _product.get();
            if(variantId == null) {
                ProductVariant productVariant = new ProductVariant(product);
                Family family = product.getProductFamily();
                String variantGroupId = family.getChannelVariantGroups().get(channelId);
                model.put("mode", "CREATE");
                model.put("productVariant", productVariant);
                model.put("axisAttributes", ConversionUtil.toJSONString(family.getVariantGroups().get(variantGroupId).getVariantAxis().get(1).stream().collect(CollectionsUtil.toLinkedMap(id -> id, id -> product.getProductFamily().getAllAttributesMap().get(id).getName()))));
            } else {
                Optional<ProductVariant> _productVariant = productVariantService.get(product.getId(), FindBy.INTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, false);
                if(_productVariant.isPresent()) {
                    parameterMap.put("productId", productId);
                    ProductVariant productVariant = _productVariant.get();
                    productVariant.setProduct(product);
                    model.put("mode", "DETAILS");
                    model.put("productVariant", productVariant);
                    model.put("productFamily", productVariant.getProduct().getProductFamily());
                    model.put("pricingGridColumns", ConversionUtil.toJSONString(getPricingGridColumns(productVariant.getPricingDetails())));
                    model.put("breadcrumbs", new BreadcrumbsBuilder(variantId, ProductVariant.class, request, parameterMap, new BaseService[] {websiteService, catalogService, categoryService, productService, productVariantService}).build());

                } else {
                    throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + variantId);
                }
            }
        } else {
            throw new EntityNotFoundException("Unable to find Product with Id: " + productId);
        }

        return new ModelAndView("product/productVariant" + (reload ? "_body" : ""), model);
    }

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{variantId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> customToggle(@PathVariable(value = "productId") String productId,
                                            @PathVariable(value = "channelId") String channelId,
                                            @PathVariable(value = "variantId") String variantId,
                                            @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product ->
                model.put("success", productVariantService.toggle(product.getId(), FindBy.INTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, Toggle.get(active))));
        return model;
    }

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>[] groups) {
        return productVariantService.validate(e, context, groups);
    }

    /*@RequestMapping(value = "/{productId}/variants/{variantId}/clone/{cloneType}", method = RequestMethod.PUT)
    public Map<String, Object> clone(String id, String type) {
        return super.clone(id, type);
    }*/


    @RequestMapping("/{productId}/channels/{channelId}/variants/{variantId}/pricing")
    @ResponseBody
    public Result<Map<String, String>> getProductVariantPricing(@PathVariable(value = "productId") String productId,
                                                                @PathVariable(value = "channelId") String channelId,
                                                                @PathVariable(value = "variantId") String variantId,
                                                                HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        productService.get(productId, FindBy.EXTERNAL_ID, false)
                .ifPresent(product -> productVariantService.get(product.getId(), FindBy.INTERNAL_ID, channelId, variantId, FindBy.EXTERNAL_ID, false)
                        .ifPresent(productVariant -> dataObjects.addAll(getPricingGridData(productVariant.getPricingDetails(), pricingAttributeService.getAll(null, false)))));

        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(dataObjects.size()));
        result.setRecordsFiltered(Long.toString(dataObjects.size()));
        return result;
    }

    private static Set<Integer> getQuantityBreaks(Map<String, Map<Integer, BigDecimal>> pricingDetails) {
        Set<Integer> qtyBreaks = new TreeSet<>();
        pricingDetails.forEach((a, pd) -> pd.forEach((q, p) -> qtyBreaks.add(q)));
        return qtyBreaks.isEmpty() ? PIMConstants.DEFAULT_QUANTITY_BREAKS : qtyBreaks;
    }

    private static Map<String, String> getAttributePricingDetails(Map<String, Map<Integer, BigDecimal>> pricingDetails, PricingAttribute pricingAttribute) {
        Map<String, String> consolidatedAttributePricingDetails = new LinkedHashMap<>();
        Map<Integer, BigDecimal> attributePricingDetails = isNotEmpty(pricingAttribute) && pricingDetails.containsKey(pricingAttribute.getPricingAttributeId()) ? pricingDetails.get(pricingAttribute.getPricingAttributeId()) : new HashMap<>();
        getQuantityBreaks(pricingDetails).forEach(qty -> {
            if (attributePricingDetails.containsKey(qty)) {
                consolidatedAttributePricingDetails.put(Integer.toString(qty), attributePricingDetails.get(qty).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
            } else {
                consolidatedAttributePricingDetails.put(Integer.toString(qty), "");
            }
        });
        return consolidatedAttributePricingDetails;
    }

    private static List<Map<String, Object>> getPricingGridColumns(Map<String, Map<Integer, BigDecimal>> pricingDetails) {
        List<Map<String, Object>> columnMetadata = new ArrayList<>();
        columnMetadata.add(MapUtils.putAll(new HashMap<>(), new Object[][] {{"data", "name"}, {"name", "name"}, {"title", "Attribute"}}));
        int[] i = {1};
        getQuantityBreaks(pricingDetails).forEach(q -> columnMetadata.add(MapUtils.putAll(new HashMap<>(), new Object[][] {{"data", "C_" + i[0]}, {"name", "C_" + i[0]++}, {"title", q.toString()}, {"orderable", false}})));
        return columnMetadata;
    }

    private static List<Map<String, String>> getPricingGridData(Map<String, Map<Integer, BigDecimal>> pricingDetails, List<PricingAttribute> pricingAttributes) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, PricingAttribute> pricingAttributesMap = pricingAttributes.stream().collect(Collectors.toMap(Entity::getExternalId, pricingAttribute -> pricingAttribute));
        pricingDetails.forEach((attributeId, attributeDetails) -> {
            Map<String, String> attributeDetailsMap = new LinkedHashMap<>();
            attributeDetailsMap.put("externalId", attributeId);
            attributeDetailsMap.put("name", pricingAttributesMap.containsKey(attributeId) ? pricingAttributesMap.get(attributeId).getPricingAttributeName() : attributeId);
            int[] i = {1};
            getQuantityBreaks(pricingDetails).forEach(q -> {
                if(attributeDetails.containsKey(q)) {
                    attributeDetailsMap.put("C_" + i[0]++, attributeDetails.get(q).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
                } else {
                    attributeDetailsMap.put("C_" + i[0]++, "");
                }
            });
            data.add(attributeDetailsMap);
        });
        data.sort(Comparator.comparing(o -> o.get("name")));
        return data;
    }

}
