package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.*;

/**
 * Created by sruthi on 20-09-2018.
 */
@Controller
@RequestMapping("pim/products")
public class ProductVariantController extends BaseController<ProductVariant, ProductVariantService> {

    private ProductVariantService productVariantService;

    private ProductService productService;

    private ChannelService channelService;

    public ProductVariantController( ProductVariantService productVariantService, ProductService productService, ChannelService channelService){
        super(productVariantService);
        this.productVariantService = productVariantService;
        this.productService = productService;
        this.channelService = channelService;
    }

    @RequestMapping("/{productId}/channels/{channelId}/variants/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableVariants(@PathVariable(value = "productId") String productId, @PathVariable(value = "channelId") String channelId, HttpServletRequest request) {

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

    @RequestMapping("/{productId}/variants/list")
    @ResponseBody
    @Override
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        return null;
    }

    @RequestMapping("/{productId}/channels/{channelId}/variants/list")
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
                Page<ProductVariant> paginatedResult = productVariantService.getAll(product.getId(), channelId, pagination.getPageNumber(), pagination.getPageSize(), sort, false);
                List<Map<String, String>> dataObjects = new ArrayList<>();
                paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                result.setDataObjects(dataObjects);
                result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
            });
        } else {
            //TODO - send error message
        }
        return result;
    }

    @RequestMapping(value = {"/{productId}/variants/{variantId}", "/{productId}/variants/create"})
    public ModelAndView variantDetails(@PathVariable(value = "productId") String productId,
                                       @PathVariable(value = "variantId", required = false) String variantId,
                                       @RequestParam(name = "channelId", defaultValue = PIMConstants.DEFAULT_CHANNEL_ID) String channelId) {
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
                Optional<ProductVariant> _productVariant = productVariantService.get(variantId, FindBy.EXTERNAL_ID, false);
                if(_productVariant.isPresent()) {
                    ProductVariant productVariant = _productVariant.get();
                    productVariant.setProduct(product);
                    model.put("mode", "DETAILS");
                    model.put("productVariant", productVariant);
                    model.put("productFamily", productVariant.getProduct().getProductFamily());
                    model.put("breadcrumbs", new Breadcrumbs("Product",
                            "Products", "/pim/products",
                            product.getProductName(), "/pim/products/" + productId,
                            "Product Variants", "/pim/products/" + productId + "#productVariants",
                            productVariant.getProductVariantName(), ""));
                } else {
                    throw new EntityNotFoundException("Unable to find ProductVariant with Id: " + variantId);
                }
            }
        } else {
            throw new EntityNotFoundException("Unable to find Product with Id: " + productId);
        }

        return new ModelAndView("product/productVariant", model);
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    @Override
    public Map<String, Object> toggle(@PathVariable(value = "variantId") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productVariantService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = "/{productId}/channels/{channelId}/variants/{variantId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> customToggle(@PathVariable(value = "productId") String productId,
                                            @PathVariable(value = "channelId") String channelId,
                                            @PathVariable(value = "variantId") String variantId,
                                            @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        productService.get(productId, FindBy.EXTERNAL_ID, false).ifPresent(product ->
                model.put("success", productVariantService.toggle(product.getId(), channelId, variantId, Toggle.get(active))));
        return model;
    }

    @RequestMapping(value = "/{productId}/variants/{variantId}/clone/{cloneType}", method = RequestMethod.PUT)
    @Override
    public Map<String, Object> clone(String id, String type) {
        return super.clone(id, type);
    }

    /*@RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable(value = "id") String id, @ModelAttribute("productVariant") @Valid ProductVariant productVariant, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ModelAndView("product/productVariant");
        }
        productVariantService.update(id, FindBy.EXTERNAL_ID, productVariant);
        return new ModelAndView("redirect:/pim/productVariants");
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", productVariantService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }*/


}
