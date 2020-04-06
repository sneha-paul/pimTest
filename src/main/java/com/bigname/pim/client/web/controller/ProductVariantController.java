package com.bigname.pim.client.web.controller;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.ProductVariantDAO;
import com.bigname.pim.core.service.*;
import com.bigname.pim.core.util.BreadcrumbsBuilder;
import com.bigname.pim.core.util.PIMConstants;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xcore.web.controller.ControllerSupport;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.service.SyncStatusService;
import org.apache.commons.collections4.MapUtils;
import org.javatuples.Pair;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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

    private VirtualFileService assetService;

    private RestTemplate restTemplate;

    private SyncStatusService syncStatusService;

    public ProductVariantController( ProductVariantService productVariantService,
                                     ProductService productService,
                                     ChannelService channelService,
                                     PricingAttributeService pricingAttributeService,
                                     CategoryService categoryService,
                                     CatalogService catalogService,
                                     WebsiteService websiteService,
                                     VirtualFileService assetService,
                                     RestTemplate restTemplate,
                                     SyncStatusService syncStatusService){
        this.productVariantService = productVariantService;
        this.productService = productService;
        this.channelService = channelService;
        this.pricingAttributeService = pricingAttributeService;
        this.websiteService = websiteService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.assetService = assetService;
        this.restTemplate = restTemplate;
        this.syncStatusService = syncStatusService;
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
        Page<Map<String, String>> paginatedResult = productService.getAvailableVariants(ID.EXTERNAL_ID(productId), channelId, pagination.getPageNumber(), pagination.getPageSize(), sort);
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
            productService.get(ID.EXTERNAL_ID(productId), false).ifPresent(product -> {
                Family productFamily = product.getProductFamily();
                String variantGroupId = productFamily.getChannelVariantGroups().get(channelId);
                VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup) && isNotEmpty(variantGroup.getVariantAxis().get(1))) {
                    List<String> axisAttributeTokens = StringUtil.splitPipeDelimitedAsList(variantIdentifier);
                    Map<String, String> axisAttributes = new HashMap<>();
                    StringBuilder tempId = new StringBuilder(productId);
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

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{productVariantId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "productId") String productId,
                                      @PathVariable(value = "productVariantId") String productVariantId,
                                      ProductVariant productVariantDTO,
                                      HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        /*if(isEmpty(productVariantDTO.getProductVariantId())) {
            productVariantService.get(variantId, FindBy.EXTERNAL_ID, productVariantDTO.getChannelId(), false)
                    .ifPresent(productVariant -> productVariantDTO.setProductVariantId(productVariant.getProductVariantId()));
        }*/
        productVariantDTO.setLevel(1); //TODO - make this dynamic
        setVariantAttributeValues(productVariantDTO, request);
        if(isValid(productVariantDTO, model, productVariantDTO.getGroup().length == 1 && productVariantDTO.getGroup()[0].equals("DETAILS") ? ProductVariant.DetailsGroup.class : null)) {
            productVariantDTO.setLastExportedTimeStamp(null);
            productVariantService.update(ID.EXTERNAL_ID(productVariantId), productVariantDTO);
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
        productVariantService.addAssets(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), Arrays.stream(assetIds).map(ID::INTERNAL_ID).collect(Collectors.toList()), FileAsset.AssetFamily.getFamily(assetFamily));
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
        productVariantService.deleteAsset(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), ID.INTERNAL_ID(assetId), FileAsset.AssetFamily.getFamily(assetFamily));
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
        productVariantService.setAsDefaultAsset(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), ID.INTERNAL_ID(assetId), FileAsset.AssetFamily.getFamily(assetFamily));
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
        productVariantService.reorderAssets(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), Arrays.stream(assetIds).map(ID::INTERNAL_ID).collect(Collectors.toList()), FileAsset.AssetFamily.getFamily(assetFamily));
        model.put("success", true);
        return model;
    }

    private void setVariantAttributeValues(ProductVariant productVariantDTO, HttpServletRequest request) {
        productService.get(ID.EXTERNAL_ID(productVariantDTO.getProductId()), false).ifPresent(product -> {
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
                        if(productFamily.getAllAttributesMap().get(attributeId).getUiType().equals(Attribute.UIType.MULTI_SELECT)) {
                            productVariantDTO.getVariantAttributes().put(attributeId, Arrays.asList(attributesMap.get(attributeId)));
                        } else if(attributesMap.get(attributeId) instanceof String[]) {
                            productVariantDTO.getVariantAttributes().put(attributeId, Arrays.asList(attributesMap.get(attributeId)));
                        } else {
                            productVariantDTO.getVariantAttributes().put(attributeId, attributesMap.get(attributeId));
                        }

                        /*if(productFamily.getAllAttributesMap().get(attributeId).getUiType().equals(Attribute.UIType.MULTI_SELECT)) {
                            productVariantDTO.getVariantAttributes().put(attributeId, Arrays.asList(attributesMap.get(attributeId)));
                        }*/
                    } else {
                        Map<String, Object> valueMap = new LinkedHashMap<>();
                        attributesMap.entrySet().stream()
                                .filter(e -> e.getKey().startsWith(attributeId + "."))
                                .forEach(e -> valueMap.put(e.getKey().substring(e.getKey().lastIndexOf(".") + 1), e.getValue()));
                        if(!valueMap.isEmpty()) {
                            productVariantDTO.getVariantAttributes().put(attributeId, valueMap);
                        }
                    }
                });
            }
        });
    }

    /*@RequestMapping("/{productId}/channels/{channelId}/variants/data")
    @ResponseBody
    public Result<Map<String, String>> allChannelVariants(@PathVariable(name = "productId") String productId, @PathVariable(name = "channelId") String channelId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());

        if(isEmpty(dataTableRequest.getSearch())) {
            if(isNotEmpty(productId) && isNotEmpty(channelId)) {
                productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product -> {
                    Sort sort = null;
                    if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    }
                    Page<ProductVariant> paginatedResult = productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, channelId, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    int seq[] = {1};
                    paginatedResult.getContent().forEach(e -> {
                        e.setSequenceNum(Long.parseLong(Integer.toString(seq[0] ++)));
                        dataObjects.add(e.toMap());
                    });

                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getContent().size()));
                });

            }
            return result;
        } else {

            if(isNotEmpty(productId) && isNotEmpty(channelId)) {
                productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product -> {
                    Sort sort = null;
                    if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    } else {
                        sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "productVariantName")); //TODO : verify this logic
                    }
                    Page<ProductVariant> paginatedResult = productVariantService.findAll("productVariantName", dataTableRequest.getSearch(),product.getId(), FindBy.INTERNAL_ID, channelId, PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    int seq[] = {1};
                    paginatedResult.getContent().forEach(e -> {
                        e.setSequenceNum(Long.parseLong(Integer.toString(seq[0] ++)));
                        dataObjects.add(e.toMap());
                    });

                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getContent().size()));
                });

            }
            return result;
        }

        return result;
    }*/

    //================================================================================================================================
    @RequestMapping("/{productId}/channels/{channelId}/variants/data")
    @ResponseBody
    public Result<Map<String, String>> allChannelVariants(@PathVariable(name = "productId") String productId, @PathVariable(name = "channelId") String channelId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());

        if(isEmpty(dataTableRequest.getSearch())) {
            if(isNotEmpty(productId) && isNotEmpty(channelId)) {
                productService.get(ID.EXTERNAL_ID(productId), false).ifPresent(product -> {
                    Sort sort = null;
                    if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    }
                    Page<ProductVariant> paginatedResult = productVariantService.getAll(ID.INTERNAL_ID(product.getId()), channelId, pagination.getPageNumber(), pagination.getPageSize(), sort, dataTableRequest.getStatusOptions());
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    int seq[] = {1};
                    paginatedResult.getContent().forEach(e -> {
                        e.setSequenceNum(Long.parseLong(Integer.toString(seq[0] ++)));
                        dataObjects.add(e.toMap());
                    });

                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
                });

            }
            return result;
        } else {

            if(isNotEmpty(productId) && isNotEmpty(channelId)) {
                productService.get(ID.EXTERNAL_ID(productId), false).ifPresent(product -> {
                    Sort sort = null;
                    if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
                        sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
                    } else {
                        sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "productVariantName")); //TODO : verify this logic
                    }
                    Page<ProductVariant> paginatedResult = productVariantService.findAll("productVariantName", dataTableRequest.getSearch(), ID.INTERNAL_ID(product.getId()), channelId, PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    int seq[] = {1};
                    paginatedResult.getContent().forEach(e -> {
                        e.setSequenceNum(Long.parseLong(Integer.toString(seq[0] ++)));
                        dataObjects.add(e.toMap());
                    });

                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
                });

            }
            return result;
        }
    }

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/data", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> setProductVariantsSequence(@PathVariable(name = "productId") String productId, @PathVariable(name = "channelId") String channelId, @RequestParam Map<String, String> parameterMap) {
        Map<String, Object> model = new HashMap<>();
        boolean success = productVariantService.setProductVariantsSequence(ID.EXTERNAL_ID(productId), ID.EXTERNAL_ID(channelId), ID.EXTERNAL_ID(parameterMap.get("sourceId")), ID.EXTERNAL_ID(parameterMap.get("destinationId")));
        model.put("success", success);
        return model;
    }

    @RequestMapping(value = {"/{productId}/variants/{variantId}/pricingDetails/{pricingAttributeId}", "/{productId}/variants/{variantId}/pricingDetails"})
    public ModelAndView variantPricingDetails(@PathVariable(value = "productId") String productId,
                                              @PathVariable(value = "variantId") String variantId,
                                              @PathVariable(value = "pricingAttributeId", required = false) String pricingAttributeId,
                                              @RequestParam(name = "channelId") String channelId) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRODUCTS");
        Optional<Product> _product = productService.get(ID.EXTERNAL_ID(productId), false);
        if(_product.isPresent()) {
            Product product = _product.get();
            Optional<ProductVariant> _productVariant = productVariantService.get(ID.INTERNAL_ID(product.getId()), channelId, ID.EXTERNAL_ID(variantId), false);
            if(_productVariant.isPresent()) {
                ProductVariant productVariant = _productVariant.get();
                productVariant.setProduct(product);
                model.put("productVariant", productVariant);
                model.put("availablePricingAttributes", pricingAttributeService.getAllWithExclusions(productVariant.getPricingDetails().keySet().stream().map(ID::EXTERNAL_ID).collect(Collectors.toList()), null));
                PricingAttribute pricingAttribute = isEmpty(pricingAttributeId) ? null : pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeId), false).orElse(null);
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
            Optional<Product> _product = productService.get(ID.EXTERNAL_ID(productId), false);
            if (_product.isPresent()) {
                String channelId = pricingDetail.getChannelId();
                Product product = _product.get();
                Optional<Channel> _channel = channelService.get(ID.EXTERNAL_ID(channelId));
                if (_channel.isPresent()) {
                    Optional<ProductVariant> _productVariant = productVariantService.get(ID.INTERNAL_ID(product.getId()), channelId, ID.EXTERNAL_ID(variantId), false);
                    if (_productVariant.isPresent()) {
                        ProductVariant productVariant = _productVariant.get();
                        productVariant.setProduct(product);
                        productVariant.setGroup("PRICING_DETAILS");
                        model.put("refreshPage", !getQuantityBreaks(productVariant.getPricingDetails()).containsAll(pricingDetail.getPricing().keySet()));
                        productVariant.getPricingDetails().put(pricingDetail.getPricingAttributeId(), pricingDetail.getPricing());
                        productVariantService.update(ID.INTERNAL_ID(productVariant.getId()), productVariant);
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
        Optional<Product> _product = productService.get(ID.EXTERNAL_ID(productId), false);
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
                Optional<ProductVariant> _productVariant = productVariantService.get(ID.INTERNAL_ID(product.getId()), channelId, ID.EXTERNAL_ID(variantId), false);
                if(_productVariant.isPresent()) {
                    parameterMap.put("productId", productId);
                    ProductVariant productVariant = _productVariant.get();
                    productVariant.setProduct(product);
                    model.put("mode", "DETAILS");
                    model.put("productVariant", productVariant);
                    model.put("productFamily", productVariant.getProduct().getProductFamily());
                    model.put("pricingGridColumns", ConversionUtil.toJSONString(getPricingGridColumns(productVariant.getPricingDetails())));
                    model.put("breadcrumbs", new BreadcrumbsBuilder().init(variantId, ProductVariant.class, request, parameterMap, new BaseService[] {websiteService, catalogService, categoryService, productService, productVariantService}).build());

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
        productService.get(ID.EXTERNAL_ID(productId), false).ifPresent(product ->
                model.put("success", productVariantService.toggle(ID.INTERNAL_ID(product.getId()), channelId, ID.EXTERNAL_ID(variantId), Toggle.get(active))));
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
        productService.get(ID.EXTERNAL_ID(productId), false)
                .ifPresent(product -> productVariantService.get(ID.INTERNAL_ID(product.getId()), channelId, ID.EXTERNAL_ID(variantId), false)
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

    @RequestMapping("/downloadVariantAsset")
    public ResponseEntity<Resource> downloadVariantAssetsImage(@RequestParam(value = "fileId") String fileId, HttpServletRequest request){

        VirtualFile asset = assetService.get(ID.INTERNAL_ID(fileId), false).orElse(null);
        return downloadAsset(asset.getInternalFileName(), request);
    }

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{variantId}/archive/{archived}", method = RequestMethod.PUT)

    @ResponseBody
    public Map<String, Object> archive(@PathVariable(value = "productId") String productId,
                                       @PathVariable(value = "channelId") String channelId,
                                       @PathVariable(value = "variantId") String variantId,
                                       @PathVariable(value = "archived") String archived) {
        Map<String, Object> model = new HashMap<>();
        ProductVariant variant = productVariantService.get(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), false).orElse(null);
        if(isEmpty(variant)) {
            variant = productVariantService.get(ID.EXTERNAL_ID(productId), channelId, ID.EXTERNAL_ID(variantId), false, false, false, true).orElse(null);
        }
        model.put("success", productVariantService.archive(ID.EXTERNAL_ID(variant.getProductVariantId()), Archive.get(archived)));
        return model;
    }

    @RequestMapping(value ="/productVariantsLoad")
    public String loadProductVariantsToBOS() {
        List<ProductVariant> productVariantList = productVariantService.loadProductVariantsToBOS();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/products/loadProductVariant", productVariantList, String.class, map);
        return response.getBody();
    }

    @RequestMapping(value ="/syncUpdatedProductVariants", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> syncUpdatedProductVariants() {
        Map<String, Object> model = new HashMap<>();
        List<ProductVariant> productVariants = productVariantService.syncUpdatedRecord();
        Map<String, String> map = new HashMap<String, String>();
        ResponseEntity<String> response =  restTemplate.postForEntity("http://localhost:8084/admin/products/syncUpdatedProductVariants", productVariants, String.class, map);
        if(Objects.equals(response.getBody(), "true")) {
            productVariants.forEach(productVariant -> {
                productVariant.setLastExportedTimeStamp(LocalDateTime.now());
                List<SyncStatus> syncStatusList = syncStatusService.getPendingSynStatus(productVariant.getId(), "pending");
                syncStatusList.forEach(syncStatus -> {
                    syncStatus.setStatus("updated");
                    syncStatus.setExportedTimeStamp(productVariant.getLastExportedTimeStamp());
                });
                syncStatusService.update(syncStatusList);
            });
            productVariantService.update(productVariants);
            model.put("success", true);
        } else {
            model.put("success", false);
        }
        return model;
    }

}