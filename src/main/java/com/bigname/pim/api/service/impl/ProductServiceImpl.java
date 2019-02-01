package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.exception.GenericEntityException;
import com.bigname.pim.api.persistence.dao.CategoryProductDAO;
import com.bigname.pim.api.persistence.dao.ProductCategoryDAO;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.service.*;
import com.bigname.pim.util.*;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;

import static com.bigname.common.util.ValidationUtil.*;

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
    public Product createOrUpdate(Product product) {
        return productDAO.save(product);
    }

    @Override
    public List<Product> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<Product> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Page<Product> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return productDAO.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Page<Map<String, Object>> findAllProductCategories(String productId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(productId, findBy, false)
                .map(category -> productDAO.findAllProductCategories(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Category> findAvailableCategoriesForProduct(String productId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(productId, findBy, false)
                .map(category -> productDAO.findAvailableCategoriesForProduct(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Optional<Product> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<Product> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    /**
     * Use this method to get variants of a product with activeRequired options for both product and variants. Otherwise use productVariantService.getAll().
     *
     * See JIRA ticket BNPIM-7 for more details
     *
     * @param productId internal or external id of the product
     * @param findBy productId type (INTERNAL_ID or EXTERNAL_ID)
     * @param channelId external id of the channel
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @param activeRequired - activeRequired[0] - activeRequired boolean flag for ProductVariant,  activeRequired[1] - activeRequired boolean flag for Product
     * @return
     */
    @Override
    public Page<ProductVariant> getProductVariants(String productId, FindBy findBy, String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        final Sort _sort = sort == null ? Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")) : sort;
        return get(productId, findBy, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, channelId, page, size, _sort, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get variants of a product in list format.
     *
     * @param productId Internal or External id of the Product
     * @param productIdFindBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public List<ProductVariant> getProductVariants(String productId, FindBy productIdFindBy, String channelId, Sort sort, boolean... activeRequired) {
        return getProductVariants(productId, productIdFindBy, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }


    /**
     * Method to get variant of a product
     *
     * @param productId Internal or External id of the Product
     * @param productIdFindBy
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param productVariantId Internal or External id of the ProductVariant of the product
     * @param variantIdFindBy
     * @param activeRequired activeRequired Boolean flag
     * @return
     */

    @Override
    public Optional<ProductVariant> getProductVariant(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired) {
        return get(productId, productIdFindBy, ConversionUtil.toList(activeRequired).size() == 2 && activeRequired[1])
                .map(product -> productVariantService.get(product.getId(), FindBy.INTERNAL_ID, channelId, productVariantId, variantIdFindBy, activeRequired))
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

    @Override
    public List<Product> create(List<Product> products) {
        products.forEach(product -> {product.setCreatedUser(getCurrentUser());product.setCreatedDateTime(LocalDateTime.now());});
        return productDAO.insert(products);
    }

    @Override
    public List<Product> update(List<Product> products) {
        products.forEach(product -> {product.setLastModifiedUser(getCurrentUser());product.setLastModifiedDateTime(LocalDateTime.now());});
        return productDAO.saveAll(products);
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
        familyService.get(product.getProductFamilyId(), findBy).ifPresent(product::setProductFamily);
    }

    /**
     * Method to get available variants of a product in paginated format.
     *
     * @param productId Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param channelId Internal or External id of the Channel to which the product belongs
     * @param pageNumber page number
     * @param pageSize page size
     * @param sort sort Object
     * @return
     */
    @Override
    public Page<Map<String, String>> getAvailableVariants(String productId, FindBy findBy, String channelId, Integer pageNumber, Integer pageSize, Sort sort) {
        int page = isNull(pageNumber) ? 0 : pageNumber;
        int size = isNull(pageSize) ? 25 : pageSize;
        int level = 1;
        List<Map<String, String>> variantMatrix = new ArrayList<>();
        String _channelId = isEmpty(channelId) ? PIMConstants.DEFAULT_CHANNEL_ID : channelId;
        get(productId, findBy, false).ifPresent((Product product) -> {
            List<ProductVariant> existingVariants = productVariantService.getAll(product.getId(), FindBy.INTERNAL_ID, channelId, null, false );
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
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<ProductCategory> getProductCategories(String productId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Product> _product = get(productId, findBy, false);
        if(_product.isPresent()) {
            Product product = _product.get();
            Page<ProductCategory> productCategories = productCategoryDAO.findByProductIdAndActiveIn(product.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> categoryIds = new ArrayList<>();
            productCategories.forEach(pc -> categoryIds.add(pc.getCategoryId()));
            if(categoryIds.size() > 0) {
                Map<String, Category> categoriesMap = PimUtil.getIdedMap(categoryService.getAll(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                productCategories.forEach(pc -> pc.init(product, categoriesMap.get(pc.getCategoryId())));
            }
            return productCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to get available categories of a product in paginated format.
     *
     * @param id Internal or External id of the Product
     * @param findBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableCategoriesForProduct(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Product> product = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        product.ifPresent(product1 -> productCategoryDAO.findByProductId(product1.getId()).forEach(pc -> categoryIds.add(pc.getCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, false);

    }

    /**
     * Method to add category for a product.
     *
     * @param productId Internal or External id of the Product
     * @param productIdFindBy Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param categoryId Internal or External id of the Category
     * @param categoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public ProductCategory addCategory(String productId, FindBy productIdFindBy, String categoryId, FindBy categoryIdFindBy) {
        Optional<Product> product = get(productId, productIdFindBy, false);
        if(product.isPresent()) {
            Optional<Category> category = categoryService.get(categoryId, categoryIdFindBy, false);
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
    public boolean toggleProductCategory(String productId, FindBy productIdFindBy, String categoryId, FindBy categoryIdFindBy, Toggle active) {
        return get(productId, productIdFindBy, false)
                .map(product -> categoryService.get(categoryId, categoryIdFindBy, false)
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
     * @param findBy         Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable       The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getCategories(String productId, FindBy findBy, Pageable pageable, boolean... activeRequired) {
        return get(productId, findBy, false)
                .map(catalog -> productDAO.getCategories(catalog.getId(), pageable))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Product addAssets(String productId, FindBy findBy, String channelId, String[] assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, findBy, false)
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


                    assetService.getAll(assetIds, FindBy.INTERNAL_ID, null)
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
                    update(productId, FindBy.EXTERNAL_ID, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }

    @Override
    public Product deleteAsset(String productId, FindBy findBy, String channelId, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, findBy, false)
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
                    update(productId, FindBy.EXTERNAL_ID, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }

    @Override
    public Product reorderAssets(String productId, FindBy findBy, String channelId, String[] assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, findBy, false)
                .map(product -> {
                    product.setChannelId(channelId);

                    //Existing product assets for the given channel
                    Map<String, Object> productAssetsForChannel = product.getScopedAssets().containsKey(channelId) ? product.getChannelAssets() : new HashMap<>();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> productAssets = productAssetsForChannel.containsKey(_assetFamily) ? (List<Object>)productAssetsForChannel.get(_assetFamily) : new ArrayList<>();

                    //AssetIds arrays contains the assetIds in the required order
                    productAssetsForChannel.put(_assetFamily, ProductUtil.reorderAssets(ConversionUtil.toGenericMap(productAssets), Arrays.asList(assetIds)));
                    product.setChannelAssets(productAssetsForChannel);
                    product.setGroup("ASSETS");
                    update(productId, FindBy.EXTERNAL_ID, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));

    }

    @Override
    public Product setAsDefaultAsset(String productId, FindBy findBy, String channelId, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, findBy, false)
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
                    update(productId, FindBy.EXTERNAL_ID, product);
                    return product;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id:" + productId));
    }


}
