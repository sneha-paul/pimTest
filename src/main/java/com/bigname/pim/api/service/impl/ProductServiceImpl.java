package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.mongo.CategoryProductDAO;
import com.bigname.pim.api.persistence.dao.mongo.ProductCategoryDAO;
import com.bigname.pim.api.persistence.dao.mongo.ProductDAO;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.ProductUtil;
import com.m7.xtreme.common.criteria.model.SimpleCriteria;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.exception.GenericEntityException;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.GenericCriteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sruthi on 19-09-2018.
 */
@Service
public class ProductServiceImpl extends BaseServiceSupport<Product, ProductDAO, ProductService> implements ProductService {

    private ProductVariantService productVariantService;
    private VirtualFileService assetService;
    private ProductCategoryDAO productCategoryDAO;
    private CategoryProductDAO categoryProductDAO;
    private CategoryService categoryService;
    private FamilyService familyService;
    private ProductDAO productDAO;


    @Autowired
    public ProductServiceImpl(ProductDAO productDAO, Validator validator, ProductVariantService productVariantService, FamilyService productFamilyService, VirtualFileService assetService, ProductCategoryDAO productCategoryDAO, CategoryProductDAO categoryProductDAO, @Lazy CategoryService categoryService) {
        super(productDAO, "product", validator);
        this.productDAO = productDAO;
        this.productVariantService = productVariantService;
        this.familyService = productFamilyService;
        this.assetService = assetService;
        this.productCategoryDAO = productCategoryDAO;
        this.categoryProductDAO = categoryProductDAO;
        this.categoryService = categoryService;
    }

    @Override
    public Page<Map<String, Object>> findAllProductCategories(ID<String> productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(productId, false)
                .map(category -> productDAO.findAllProductCategories(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Category> findAvailableCategoriesForProduct(ID<String> productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(productId, false)
                .map(category -> productDAO.findAvailableCategoriesForProduct(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }


    /**
     * Use this method to get variants of a product with activeRequired options for both product and variants. Otherwise use productVariantService.getAll().
     *
     * See JIRA ticket BNPIM-7 for more details
     *
     * @param productId internal or external id of the product
     * @param channelId external id of the channel
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @param activeRequired - activeRequired[0] - activeRequired boolean flag for ProductVariant,  activeRequired[1] - activeRequired boolean flag for Product
     * @return
     */
    @Override
    public Page<ProductVariant> getProductVariants(ID<String> productId, String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        final Sort _sort = sort == null ? Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")) : sort;
        return get(productId, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.getAll(ID.INTERNAL_ID(product.getId()), channelId, page, size, _sort, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get variants of a product in list format.
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public List<ProductVariant> getProductVariants(ID<String> productId, String channelId, Sort sort, boolean... activeRequired) {
        return getProductVariants(productId, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }


    /**
     * Method to get variant of a product
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param productVariantId Internal or External id of the ProductVariant of the product
     * @param activeRequired activeRequired Boolean flag
     * @return
     */

    @Override
    public Optional<ProductVariant> getProductVariant(ID<String> productId, String channelId, ID<String> productVariantId, boolean... activeRequired) {
        return get(productId, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.get(ID.INTERNAL_ID(product.getId()), channelId, productVariantId, activeRequired))
                .orElse(Optional.empty());
    }

    /**
     * Override the crete method to replace the passed in productFamily EXTERNAL_ID to a valid INTERNAL_ID
     *
     * @param product details of Product as Object
     * @return
     */
    @Override
    public Product create(Product product) {
        /**
         *  No need to check if product.getProductFamilyId is empty.
         *  There is @NotEmpty validation constraint on the Product bean
         */

        setProductFamily(product, ID.Type.EXTERNAL_ID);
        if(isEmpty(product.getProductFamily())) {
            throw new GenericEntityException("Unable to create product, invalid product family id : " + product.getProductFamilyId());
        } else {
            return super.create(product);
        }
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     * @param productId
     * @param activeRequired
     * @return
     */
    @Override
    public <I> Optional<Product> get(ID<I> productId, boolean... activeRequired) {
        Optional<Product> product = super.get(productId, activeRequired);
        product.ifPresent(product1 -> setProductFamily(product1, ID.Type.INTERNAL_ID));
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
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    @Override
    public Page<Product> findAll(SimpleCriteria criteria, Pageable pageable, boolean... activeRequired){
        Page<Product> products = super.findAll(criteria, pageable, activeRequired);
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    @Override
    public Page<Product> findAll(Pageable pageable, boolean... activeRequired) {
        Page<Product> products = super.findAll(pageable, activeRequired);
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    @Override
    public Page<Product> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        Page<Product> products = super.findAll(searchField, keyword, pageable, activeRequired);
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    @Override
    public Page<Product> findAll(GenericCriteria criteria, Pageable pageable, boolean... activeRequired){
        Page<Product> products = super.findAll(criteria, pageable, activeRequired);
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    /**
     * Overriding the base service method to inject the productFamily instance
     *
     * @param productIds
     * @param sort
     * @param activeRequired
     * @return
     */
    @Override
    public <I> List<Product> getAll(List<ID<I>> productIds, Sort sort, boolean... activeRequired) {
        List<Product> products = super.getAll(productIds, sort, activeRequired);
        products.forEach(product -> setProductFamily(product, ID.Type.INTERNAL_ID));
        return products;
    }

    private void setProductFamily(Product product, ID.Type type) {
        familyService.get(type == ID.Type.EXTERNAL_ID ? ID.EXTERNAL_ID(product.getProductFamilyId()) : ID.INTERNAL_ID(product.getProductFamilyId())).ifPresent(product::setProductFamily);
    }

    /**
     * Method to get available variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @return
     */
    @Override
    public Page<Map<String, String>> getAvailableVariants(ID<String> productId, String channelId, int page, int size, Sort sort) {
        int level = 1;
        List<Map<String, String>> variantMatrix = new ArrayList<>();
        String _channelId = isEmpty(channelId) ? PIMConstants.DEFAULT_CHANNEL_ID : channelId;
        get(productId, false).ifPresent((Product product) -> {
            List<ProductVariant> existingVariants = productVariantService.getAll(ID.INTERNAL_ID(product.getId()), channelId, null, false );
            List<String> existingVariantsAxisAttributeIds = new ArrayList<>();
            product.setChannelId(_channelId);
            Family productFamily = product.getProductFamily();
            String variantGroupId = productFamily.getChannelVariantGroups().get(channelId);
            Map<String, FamilyAttribute> familyAttributesMap = productFamily.getAllAttributesMap();
            if(isNotEmpty(variantGroupId)) {
                VariantGroup variantGroup = productFamily.getVariantGroups().get(variantGroupId);
                if(isNotEmpty(variantGroup)) {
                    Map<String, List<FamilyAttributeOption>> axisAttributesOptions = variantGroup.getVariantAxis().get(level).stream().map(familyAttributesMap::get).collect(CollectionsUtil.toLinkedMap(FamilyAttribute::getId, axisAttribute -> new ArrayList<>(axisAttribute.getOptions().values())));
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
        sort(variantMatrix, sort);
        return paginate(variantMatrix, page, size);
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, Product product, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, product, group);
        FamilyAttributeGroup masterGroup = null;
        if(ValidationUtil.isNotEmpty(context.get("id"))){
            masterGroup = product.getProductFamily().getAttributes().get(group + "_GROUP");
        }
        if(isNotEmpty(masterGroup)) {
            FamilyAttributeGroup sectionGroup = masterGroup.getChildGroups().get(AttributeGroup.DEFAULT_GROUP_ID);
            sectionGroup.getChildGroups().forEach((k, attributeGroup) ->
                attributeGroup.getAttributes().forEach((k1, attribute) -> {
                    if(attribute.getType(product.getChannelId()) == FamilyAttribute.Type.COMMON) {
                        if (attribute.getUiType() == Attribute.UIType.CHECKBOX && !product.getChannelFamilyAttributes().containsKey(k1)) {
                            product.getChannelFamilyAttributes().put(k1, new String[0]);
                        } else if (attribute.getUiType() == Attribute.UIType.YES_NO && !product.getChannelFamilyAttributes().containsKey(k1)) {
                            product.getChannelFamilyAttributes().put(k1, "N");
                        }
                    }
                    Pair<String, Object> error = attribute.validate(product.getChannelFamilyAttributes().get(attribute.getId()), product.getChannelId(), 0);//TODO - check if the above commented out logic is required
                    if(isNotEmpty(error)) {
                        _fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return _fieldErrors;
    }

    /**
     * Method to get categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<ProductCategory> getProductCategories(ID<String> productId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Product> _product = get(productId, false);
        if(_product.isPresent()) {
            Product product = _product.get();
            Page<ProductCategory> productCategories = productCategoryDAO.findByProductIdAndActiveIn(product.getId(), PlatformUtil.getActiveOptions(activeRequired), pageable);
            List<ID<String>> categoryIds = new ArrayList<>();
            productCategories.forEach(pc -> categoryIds.add(ID.INTERNAL_ID(pc.getCategoryId())));
            if(categoryIds.size() > 0) {
                Map<String, Category> categoriesMap = PlatformUtil.getIdedMap(categoryService.getAll(categoryIds, null, activeRequired), ID.Type.INTERNAL_ID);
                productCategories.forEach(pc -> pc.init(product, categoriesMap.get(pc.getCategoryId())));
            }
            return productCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to get available categories of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableCategoriesForProduct(ID<String> productId, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Product> product = get(productId, false);
        Set<String> categoryIds = new HashSet<>();
        product.ifPresent(product1 -> productCategoryDAO.findByProductId(product1.getId()).forEach(pc -> categoryIds.add(pc.getCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), page, size, sort, false);

    }

    /**
     * Method to add category for a product.
     *
     * @param productId Internal or External id of the Product
     * @param categoryId Internal or External id of the Category
     * @return
     */
    @Override
    public ProductCategory addCategory(ID<String> productId, ID<String> categoryId) {
        Optional<Product> product = get(productId, false);
        if(product.isPresent()) {
            Optional<Category> category = categoryService.get(categoryId, false);
            if(category.isPresent()) {
                Optional<CategoryProduct> top1 = categoryProductDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                categoryProductDAO.save(new CategoryProduct(category.get().getId(), product.get().getId(), top1.map(categoryProduct -> categoryProduct.getSubSequenceNum() + 1).orElse(0)));

                Optional<ProductCategory> top2 = productCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return productCategoryDAO.save(new ProductCategory(product.get().getId(), category.get().getId(), top2.map(productCategory -> productCategory.getSubSequenceNum() + 1).orElse(0)));
            }
        }
        return null;
    }

    @Override
    public boolean toggleProductCategory(ID<String> productId, ID<String> categoryId, Toggle active) {
        return get(productId, false)
                .map(product -> categoryService.get(categoryId, false)
                        .map(category -> productCategoryDAO.findFirstByProductIdAndCategoryId(product.getId(), category.getId())
                                .map(productCategory -> {
                                    productCategory.setActive(active.state());
                                    productCategoryDAO.save(productCategory);
                                    return true;
                                })
                                .orElse(false))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find category with id: " + categoryId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id: " + productId));
    }
    /**
     * Method to get categories of a Product in paginated format.
     *
     * @param productId      Internal or External id of the Product
     * @param pageable       The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getCategories(ID<String> productId, Pageable pageable, boolean... activeRequired) {
        return get(productId, false)
                .map(catalog -> productDAO.getCategories(catalog.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Product addAssets(ID<String> productId, String channelId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, false)
                .map(product -> {
                    product.setChannelId(channelId);

                    //Existing product assets for the given channel
                    Map<String, Object> productAssetsForChannel = product.getScopedAssets().containsKey(channelId) ? product.getChannelAssets() : new HashMap<>();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> _productAssets = productAssetsForChannel.containsKey(_assetFamily) ? (List<Object>)productAssetsForChannel.get(_assetFamily) : new ArrayList<>();

                    //order the list by sequenceNum ascending
                    List<Map<String, Object>> productAssets = ProductUtil.orderAssets(ConversionUtil.toGenericMap(_productAssets));

                    //List of all existing asset ids
                    List<String> existingAssetIds = new ArrayList<>();

                    //reassign the sequenceNum, so that they are continuous
                    int[] seq = {0};

                    productAssets.forEach(asset -> {
                        asset.put("sequenceNum", seq[0] ++);
                        //Add the id to the existing ids list
                        existingAssetIds.add((String)asset.get("id"));
                    });


                    assetService.getAll(assetIds, null)
                            .forEach(asset -> {
                                //Only add, if the asset is a file, not a directory
                                if(!"Y".equals(asset.getIsDirectory()) && "Y".equals(asset.getActive())) {
                                    //Only add, if the asset won't exists already
                                    if(!existingAssetIds.contains(asset.getId())) {
                                        FileAsset productAsset = new FileAsset(asset, seq[0] ++);
                                        productAssets.add(productAsset.toMap());
                                        existingAssetIds.add(productAsset.getId());
                                    }
                                }
                            });


                    ProductUtil.validateDefaultAsset(productAssets);
                    productAssetsForChannel.put(_assetFamily, productAssets);
                    product.setChannelAssets(productAssetsForChannel);
                    product.setGroup("ASSETS");
                    update(productId, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }

    @Override
    public Product deleteAsset(ID<String> productId, String channelId, ID<String> assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, false)
                .map(product -> {
                    product.setChannelId(channelId);

                    //Existing product assets for the given channel
                    Map<String, Object> productAssetsForChannel = product.getScopedAssets().containsKey(channelId) ? product.getChannelAssets() : new HashMap<>();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> _productAssets = productAssetsForChannel.containsKey(_assetFamily) ? (List<Object>)productAssetsForChannel.get(_assetFamily) : new ArrayList<>();

                    List<Map<String, Object>> productAssets = ConversionUtil.toGenericMap(_productAssets);

                    productAssetsForChannel.put(_assetFamily, ProductUtil.deleteAsset(productAssets, assetId));
                    product.setChannelAssets(productAssetsForChannel);
                    product.setGroup("ASSETS");
                    update(productId, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }

    @Override
    public Product reorderAssets(ID<String> productId, String channelId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, false)
                .map(product -> {
                    product.setChannelId(channelId);

                    //Existing product assets for the given channel
                    Map<String, Object> productAssetsForChannel = product.getScopedAssets().containsKey(channelId) ? product.getChannelAssets() : new HashMap<>();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> productAssets = productAssetsForChannel.containsKey(_assetFamily) ? (List<Object>)productAssetsForChannel.get(_assetFamily) : new ArrayList<>();

                    //AssetIds arrays contains the assetIds in the required order
                    productAssetsForChannel.put(_assetFamily, ProductUtil.reorderAssets(ConversionUtil.toGenericMap(productAssets), assetIds.stream().map(ID::getId).collect(Collectors.toList())));
                    product.setChannelAssets(productAssetsForChannel);
                    product.setGroup("ASSETS");
                    update(productId, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));

    }

    @Override
    public Product setAsDefaultAsset(ID<String> productId, String channelId, ID<String> assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, false)
                .map(product -> {
                    product.setChannelId(channelId);

                    //Existing product assets for the given channel
                    Map<String, Object> productAssetsForChannel = product.getScopedAssets().containsKey(channelId) ? product.getChannelAssets() : new HashMap<>();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> productAssets = productAssetsForChannel.containsKey(_assetFamily) ? (List<Object>)productAssetsForChannel.get(_assetFamily) : new ArrayList<>();


                    ProductUtil.setDefaultAsset(ConversionUtil.toGenericMap(productAssets), assetId);
                    productAssetsForChannel.put(_assetFamily, productAssets);
                    product.setChannelAssets(productAssetsForChannel);
                    product.setGroup("ASSETS");
                    update(productId, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }

    @Override
    public boolean toggleProduct(ID<String> productId, Toggle toggle) {
        return get(productId, false)
                .map(product -> {
                    product.setGroup("DETAILS");
                    product.setActive(toggle.state());
                    productDAO.save(product);

                    categoryProductDAO.findByProductId(product.getId())
                            .forEach(categoryProduct -> {
                                categoryProduct.setActive(toggle.state());
                                categoryProductDAO.save(categoryProduct);
                            });
                    return true;
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find"));
    }

    @Override
    public List<CategoryProduct> getAllCategoryProductsWithProductId(ID<String> productId) {
        productId = getInternalId(productId);
        //String internalProductId = productId.isInternalId() ? productId.getId() : get(productId, false).map(MongoEntity::getId).orElse("");
        return isNotEmpty(productId) ? categoryProductDAO.findByProductId(productId.getId()) : new ArrayList<>();
    }

    @Override
    public void updateCategoryProduct(CategoryProduct categoryProduct) {
        categoryProductDAO.save(categoryProduct);
    }
}
