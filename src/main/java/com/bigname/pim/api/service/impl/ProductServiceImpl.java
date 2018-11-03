package com.bigname.pim.api.service.impl;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.GenericEntityException;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.*;

/**
 * Created by sruthi on 19-09-2018.
 */
@Service
public class ProductServiceImpl extends BaseServiceSupport<Product, ProductDAO> implements ProductService {

    private ProductDAO productDAO;
    private ProductVariantService productVariantService;
    private FamilyService productFamilyService;


    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, Validator validator, ProductVariantService productVariantService, FamilyService productFamilyService) {
        super(productDAO, "product", validator);
        this.productDAO = productDAO;
        this.productVariantService = productVariantService;
        this.productFamilyService = productFamilyService;
    }

    @Override
    public Product createOrUpdate(Product product) {
        return productDAO.save(product);
    }

    /**
     * Use this method to get variants of a product with activeRequired options for both product and variants. Otherwise use productVariantService.getAll().
     *
     * See JIRA ticket BNPIM-7 for more details
     *
     * @param productId
     * @param findBy
     * @param channelId
     * @param page
     * @param size
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public Page<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        final Sort _sort = sort == null ? Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")) : sort;
        return get(productId, findBy, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.getAll(product.getId(), channelId, page, size, sort, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size, _sort), 0));
    }

    @Override
    public List<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, Sort sort, boolean... activeRequired) {
        return getProductVariants(productId, findBy, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }



    @Override
    public Optional<ProductVariant> getProductVariant(String productId, FindBy findBy, String channelId, String productVariantId, boolean... activeRequired) {
        return get(productId, findBy, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.get(product.getId(), channelId, productVariantId, activeRequired))
                .orElse(Optional.empty());
    }

    /**
     * Override the crete method to replace the passed in productFamily EXTERNAL_ID to a valid INTERNAL_ID
     *
     * @param product
     * @return
     */
    @Override
    public Product create(Product product) {
        /**
         *  No need to check if product.getProductFamilyId is empty.
         *  There is @NotEmpty validation constraint on the Product bean
         */

        setProductFamily(product, FindBy.EXTERNAL_ID);
        if(isEmpty(product.getProductFamily())) {
            throw new GenericEntityException("Unable to create product, invalid product family id : " + product.getProductFamilyId());
        } else {
            return super.create(product);
        }
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     * @param id
     * @param findBy
     * @param activeRequired
     * @return
     */
    @Override
    public Optional<Product> get(String id, FindBy findBy, boolean... activeRequired) {
        Optional<Product> product = super.get(id, findBy, activeRequired);
        product.ifPresent(product1 -> setProductFamily(product1, FindBy.INTERNAL_ID));
        return product;
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     *
     * @param page
     * @param size
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public Page<Product> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        Page<Product> products = super.getAll(page, size, sort, activeRequired);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     *
     * @param ids
     * @param findBy
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public List<Product> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {
        List<Product> products = super.getAll(ids, findBy, sort, activeRequired);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    private void setProductFamily(Product product, FindBy findBy) {
        productFamilyService.get(product.getProductFamilyId(), findBy).ifPresent(productFamily -> product.setProductFamily(productFamily));
    }


    @Override
    public Page<Product> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "productId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products =  findBy == FindBy.INTERNAL_ID ? productDAO.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : productDAO.findByProductIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
        products.forEach(product -> setProductFamily(product, FindBy.INTERNAL_ID));
        return products;
    }

    /*@Override
    public Page<List<Pair<String, String>>> getAvailableVariants(String productId, FindBy findBy, String channelId, Integer pageNumber, Integer pageSize, Sort sort) {
        int page = isNull(pageNumber) ? 0 : pageNumber;
        int size = isNull(pageSize) ? 25 : pageSize;
        int level = 1;
        List<List<Pair<String, String>>> variantMatrix = new ArrayList<>();
        String _channelId = isEmpty(channelId) ? PIMConstants.DEFAULT_CHANNEL_ID : channelId;
        get(productId, findBy, false).ifPresent(product -> {
            product.setChannelId(_channelId);
            Family productFamily = product.getProductFamily();
            String variantGroupId = productFamily.getChannelVariantGroups().get(channelId);
            Map<String, FamilyAttribute> familyAttributesMap = productFamily.getAllAttributesMap();
            if(isNotEmpty(variantGroupId)) {
                VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup)) {
//                    List<String> axisAttributeIds = variantGroup.getVariantAxis().get(level);
//                    List<FamilyAttribute> axisAttributes = variantGroup.getVariantAxis().get(level).stream().map(familyAttributesMap::get).collect(Collectors.toList());
//                    List<List<FamilyAttributeOption>> axisAttributesOptions = axisAttributes.stream().map(axisAttribute -> new ArrayList<>(axisAttribute.getOptions().values())).collect(Collectors.toList());
                    List<List<FamilyAttributeOption>> axisAttributesOptions = variantGroup.getVariantAxis().get(level).stream().map(familyAttributesMap::get).map(axisAttribute -> new ArrayList<>(axisAttribute.getOptions().values())).collect(Collectors.toList());

                    int counters = axisAttributesOptions.size();
                    int[] idx = new int[counters];
                    int[] max = new int[counters];
                    for(int i = 0; i < counters; i ++) {
                        idx[i] = 0;
                        max[i] = axisAttributesOptions.get(i).size();
                    }

                    while(idx[0] < max[0]) {
                        List<Pair<String, String>> variantAttributes = new ArrayList<>();
                        for(int i = 0; i < counters; i ++) {
                            variantAttributes.add(Pair.with(axisAttributesOptions.get(i).get(idx[i]).getId(), axisAttributesOptions.get(i).get(idx[i]).getValue()));
                        }
                        idx[counters - 1] ++;
                        for(int i = counters - 1; i >= 0; i --) {
                            if(idx[i] >= max[i]) {
                                if(i > 0) {
                                    idx[i] = 0;
                                    idx[i - 1] ++;
                                }
                            } else {
                                break;
                            }
                        }
                        variantMatrix.add(variantAttributes);
                    }
                }
            }
        });
        return paginate(variantMatrix, page, size);
    }*/

    @Override
    public Page<Map<String, String>> getAvailableVariants(String productId, FindBy findBy, String channelId, Integer pageNumber, Integer pageSize, Sort sort) {
        int page = isNull(pageNumber) ? 0 : pageNumber;
        int size = isNull(pageSize) ? 25 : pageSize;
        int level = 1;
        List<Map<String, String>> variantMatrix = new ArrayList<>();
        String _channelId = isEmpty(channelId) ? PIMConstants.DEFAULT_CHANNEL_ID : channelId;
        get(productId, findBy, false).ifPresent((Product product) -> {
            List<ProductVariant> existingVariants = productVariantService.getAll(product.getId(), channelId, null, false );
            List<String> existingVariantsAxisAttributeIds = new ArrayList<>();
            product.setChannelId(_channelId);
            Family productFamily = product.getProductFamily();
            String variantGroupId = productFamily.getChannelVariantGroups().get(channelId);
            Map<String, FamilyAttribute> familyAttributesMap = productFamily.getAllAttributesMap();
            if(isNotEmpty(variantGroupId)) {
                VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup)) {
                    Map<String, List<FamilyAttributeOption>> axisAttributesOptions = variantGroup.getVariantAxis().get(level).stream().map(familyAttributesMap::get).collect(Collectors.toMap(FamilyAttribute::getId, axisAttribute -> new ArrayList<>(axisAttribute.getOptions().values())));
                    existingVariants.forEach(productVariant -> {
                        StringBuilder axisAttributeIds = new StringBuilder();
                        for (Map.Entry<String, List<FamilyAttributeOption>> entry : axisAttributesOptions.entrySet()) {
                            axisAttributeIds.append(axisAttributeIds.length() > 0 ? "|" : "").append(productVariant.getAxisAttributes().get(entry.getKey()));
                        }
                        existingVariantsAxisAttributeIds.add(axisAttributeIds.toString());
                    });
                    int counters = axisAttributesOptions.size();
                    int[] idx = new int[counters];
                    int[] max = new int[counters];

                    int j = 0;
                    for (Map.Entry<String, List<FamilyAttributeOption>> entry : axisAttributesOptions.entrySet()) {
                        idx[j] = 0;
                        max[j++] = entry.getValue().size();
                    }

                    while(idx[0] < max[0]) {
                        Map<String, String> variantAttributes = new HashMap<>();
                        StringBuilder variantId = new StringBuilder();
                        StringBuilder axisAttributeIds = new StringBuilder();
                        int x = 0;
                        for (Map.Entry<String, List<FamilyAttributeOption>> entry : axisAttributesOptions.entrySet()) {
                            axisAttributeIds.append(axisAttributeIds.length() > 0 ? "|" : "").append(entry.getValue().get(idx[x]).getId());
                            variantId.append(variantId.length() > 0 ? "|" : "").append(entry.getKey()).append("|").append(entry.getValue().get(idx[x]).getId());
                            variantAttributes.put(entry.getKey(), entry.getValue().get(idx[x++]).getValue());
                        }
                        variantAttributes.put("id", variantId.toString());

                        idx[counters - 1] ++;
                        for(int i = counters - 1; i >= 0; i --) {
                            if(idx[i] >= max[i]) {
                                if(i > 0) {
                                    idx[i] = 0;
                                    idx[i - 1] ++;
                                }
                            } else {
                                break;
                            }
                        }
                        if(!existingVariantsAxisAttributeIds.contains(axisAttributeIds.toString())) {
                            variantMatrix.add(variantAttributes);
                        }
                    }
                }
            }
        });
        return paginate(variantMatrix, page, size);
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Pair<String, Object>> fieldErrors, Product product, String group) {
        FamilyAttributeGroup masterGroup = product.getProductFamily().getAttributes().get(group + "_GROUP");
        if(isNotEmpty(masterGroup)) {
            FamilyAttributeGroup sectionGroup = masterGroup.getChildGroups().get(AttributeGroup.DEFAULT_GROUP_ID);
            sectionGroup.getChildGroups().forEach((k, attributeGroup) ->
                attributeGroup.getAttributes().forEach((k1, attribute) -> {
                    /*if(attribute.getUiType() == Attribute.UIType.CHECKBOX && !product.getFamilyAttributes().containsKey(k1)) {
                        product.getFamilyAttributes().put(k1, new String[0]);
                    } else if(attribute.getUiType() == Attribute.UIType.YES_NO && !product.getFamilyAttributes().containsKey(k1)) {
                        product.getFamilyAttributes().put(k1, "N");
                    }*/

                    if(attribute.getUiType() == Attribute.UIType.CHECKBOX && !product.getChannelFamilyAttributes().containsKey(k1)) {
                        product.getChannelFamilyAttributes().put(k1, new String[0]);
                    } else if(attribute.getUiType() == Attribute.UIType.YES_NO && !product.getChannelFamilyAttributes().containsKey(k1)) {
                        product.getChannelFamilyAttributes().put(k1, "N");
                    }

                    /*Pair<String, Object> error = null;
                    if(product.getFamilyAttributes().containsKey(attribute.getId())) {
//                        error = attribute.validate(product.getFamilyAttributes().get(attribute.getId()));
                    } else if(product.getChannelFamilyAttributes().containsKey(attribute.getId())) {
                        error = attribute.validate(product.getChannelFamilyAttributes().get(attribute.getId()), product.getChannelId(), 0);
                    }*/

                    Pair<String, Object> error = attribute.validate(product.getChannelFamilyAttributes().get(attribute.getId()), product.getChannelId(), 0);//TODO - check if the above commented out logic is required
                    if(isNotEmpty(error)) {
                        fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return fieldErrors;
    }
}
