package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.*;
import com.bigname.pim.core.service.CategoryService;
import com.bigname.pim.core.service.ProductService;
import com.bigname.pim.core.util.PIMConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.EntityAssociation;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.SyncStatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.core.util.PIMConstants.ReorderingDirection.DOWN;
import static com.bigname.pim.core.util.PIMConstants.ReorderingDirection.UP;

/**
 * Created by sruthi on 29-08-2018.
 */
@Service
public class CategoryServiceImpl extends BaseServiceSupport<Category, CategoryDAO, CategoryService> implements CategoryService{

    private CategoryDAO categoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;
    private ProductCategoryDAO productCategoryDAO;
    private CategoryProductDAO categoryProductDAO;
    private ProductService productService;
    private RootCategoryDAO rootCategoryDAO;
    private ParentCategoryProductDAO parentCategoryProductDAO;
    private SyncStatusDAO syncStatusDAO;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO, Validator validator, RelatedCategoryDAO relatedCategoryDAO, CategoryProductDAO categoryProductDAO, ProductCategoryDAO productCategoryDAO, ProductService productService, RootCategoryDAO rootCategoryDAO, ParentCategoryProductDAO parentCategoryProductDAO, SyncStatusDAO syncStatusDAO) {
        super(categoryDAO, "category", validator);
        this.categoryDAO = categoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
        this.categoryProductDAO = categoryProductDAO;
        this.productCategoryDAO = productCategoryDAO;
        this.productService = productService;
        this.rootCategoryDAO = rootCategoryDAO;
        this.parentCategoryProductDAO = parentCategoryProductDAO;
        this.syncStatusDAO = syncStatusDAO;
    }

    @Override
    public Page<Map<String, Object>> findAllSubCategories(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(category -> categoryDAO.findAllSubCategories(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Map<String, Object>> findAllCategoryProducts(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(product -> categoryDAO.findAllCategoryProducts(product.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Category> findAvailableSubCategoriesForCategory(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(category -> categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Product> findAvailableProductsForCategory(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(product -> categoryDAO.findAvailableProductsForCategory(product.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<Map<String, Object>> getCategoryHierarchy(boolean... activeRequired) {

        List<Map<String, Object>> hierarchy = new ArrayList<>();
        //Unsorted relatedCategories
        List<RelatedCategory> relatedCategories = relatedCategoryDAO.findByActiveIn(PlatformUtil.getActiveOptions(activeRequired));

        Map<String, List<RelatedCategory>> parentCategoriesMap = new LinkedHashMap<>();

        Set<String> nonRootCategoryIds = new HashSet<>();

        for (RelatedCategory relatedCategory : relatedCategories) {
            nonRootCategoryIds.add(relatedCategory.getSubCategoryId());
            if(parentCategoriesMap.containsKey(relatedCategory.getCategoryId())) {
                parentCategoriesMap.get(relatedCategory.getCategoryId()).add(relatedCategory);
            } else {
                List<RelatedCategory> childList = new ArrayList<>();
                childList.add(relatedCategory);
                parentCategoriesMap.put(relatedCategory.getCategoryId(), childList);
            }
        }

        //Sorted rootCategories
        Map<String, Category> rootCategoriesMap = getAllWithExclusions(nonRootCategoryIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), Sort.by(new Sort.Order(Sort.Direction.ASC, "categoryName"))).stream().collect(CollectionsUtil.toLinkedMap(Category::getId, e -> e));

        //Sort all subCategories within each parent category
        parentCategoriesMap.forEach((parentCategoryId, subCategories) -> {
            if(subCategories.size() > 1) {
                subCategories.sort((sc1, sc2) -> sc1.getSequenceNum() > sc2.getSequenceNum() ? 1 : sc1.getSequenceNum() < sc2.getSequenceNum() ? -1 : sc1.getSubSequenceNum() < sc2.getSubSequenceNum() ? 1 : -1);
            }
        });

        //Categories lookup map
        Map<String, Category> categoriesMap = getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, c -> c));

        //Build the full node hierarchy for each root category
        rootCategoriesMap.forEach((rootCategoryId, rootCategory) -> hierarchy.addAll(buildFullNode(rootCategoryId, 0, "", parentCategoriesMap, categoriesMap)));

        return hierarchy;
    }

    private List<Map<String, Object>> buildFullNode(String categoryId, int level, String parentId, Map<String, List<RelatedCategory>> parentCategoriesMap, Map<String, Category> categoriesLookup) {
        List<Map<String, Object>> fullNode = new ArrayList<>();
        Map<String, Object> node = buildNode(categoryId, level, parentId, categoriesLookup);
        fullNode.add(node);
        if(parentCategoriesMap.containsKey(categoryId)) {
            node.put("isParent", true);
            StringBuilder builder = new StringBuilder(parentId);
            builder.append(parentId.isEmpty() ? "" : "|" ).append(node.get("key"));
            List<RelatedCategory> subCategories = parentCategoriesMap.get(categoryId);
            subCategories.forEach(subCategory -> {
                List<Map<String, Object>> childNode = buildFullNode(subCategory.getSubCategoryId(), level + 1, builder.toString(),  parentCategoriesMap, categoriesLookup);
                childNode.get(0).put("level", ((int) node.get("level")) + 1);
                childNode.get(0).put("parent", node.get("key"));
                fullNode.addAll(childNode);
            });
        }
        return fullNode;
    }

    private Map<String, Object> buildNode(String categoryId, int level, String parentId, Map<String, Category> categoriesLookup) {
        Category category = categoriesLookup.get(categoryId);
        Map<String, Object> node = new HashMap<>();
        node.put("id", category.getId());
        node.put("key", category.getExternalId());
        node.put("level", level);
        node.put("parentChain", parentId);
        node.put("parent", "0");
        node.put("name", category.getCategoryName());
        node.put("isParent", false);
        node.put("active", category.getActive());
//        node.put("sequenceNum", 0);
//        node.put("subSequenceNum", 0);
        return node;
    }
    /*public List<Map<String, Object>> getCategoryHierarchy1(boolean... activeRequired) {
        Map<String, Map<String, Object>> nodes = new LinkedHashMap<>();

        List<Category> categories = categoryDAO.findByActiveIn(PlatformUtil.getActiveOptions(activeRequired));
        for(Category category : categories) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", category.getId());
            node.put("key", category.getExternalId());
            node.put("level", 0);
            node.put("parent", "0");
            node.put("name", category.getCategoryName());
            node.put("isParent", false);
            node.put("active", category.getActive());
            node.put("sequenceNum", 0);
            node.put("subSequenceNum", 0);
            nodes.put(category.getId(), node);
        }

        Map<String, List<RelatedCategory>> childCategoriesMap = new LinkedHashMap<>();
        List<RelatedCategory> relatedCategories = relatedCategoryDAO.findByActiveIn(PlatformUtil.getActiveOptions(activeRequired));
        for (RelatedCategory relatedCategory : relatedCategories) {
            if(childCategoriesMap.containsKey(relatedCategory.getCategoryId())) {
                childCategoriesMap.get(relatedCategory.getCategoryId()).add(relatedCategory);
            } else {
                List<RelatedCategory> childList = new ArrayList<>();
                childList.add(relatedCategory);
                childCategoriesMap.put(relatedCategory.getCategoryId(), childList);
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : nodes.entrySet()) {
            if(((int)entry.getValue().get("level")) == 0) {
                setChildNodes(entry.getKey(), nodes, childCategoriesMap);
            }
        }

        nodes.entrySet().stream().sorted((o1, o2) -> (int) o1.getValue().get("level") > (int) o2.getValue().get("level") ? 1 : -1);

        //TODO - sort level 0 nodes by sequenceNum ASC and subSequenceNum DESC

        List<Map<String, Object>> nodesList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : nodes.entrySet()) {
            if(((int)entry.getValue().get("level")) == 0) {
                nodesList.addAll(getCompleteNode(entry.getKey(), nodes, childCategoriesMap));
            }
        }
        return new ArrayList<>(nodesList);
    }*/

    private List<Map<String, Object>> getCompleteNode(String nodeId, Map<String, Map<String, Object>> nodes, Map<String, List<RelatedCategory>> childCategoriesMap) {
        List<Map<String, Object>> nodesList = new ArrayList<>();

        nodesList.add(nodes.get(nodeId));
        if(childCategoriesMap.containsKey(nodeId)) {
            List<RelatedCategory> childNodes = childCategoriesMap.get(nodeId);
            childNodes.sort(Comparator.comparing(EntityAssociation::getSequenceNum));//TODO - sort sequenceNum ASC and subSequenceNum DESC
            childNodes.forEach(childCategory -> nodesList.addAll(getCompleteNode(childCategory.getSubCategoryId(), nodes, childCategoriesMap)));
        }
        return nodesList;
    }

    private void setChildNodes(String nodeId, Map<String, Map<String, Object>> nodes, Map<String, List<RelatedCategory>> childCategoriesMap) {

        if(childCategoriesMap.containsKey(nodeId)) {
            List<RelatedCategory> childNodes = childCategoriesMap.get(nodeId);
            childNodes.forEach(childCategory -> {
                String childNodeId = childCategory.getSubCategoryId();
                Map<String, Object> node = nodes.get(nodeId);
                Map<String, Object> childNode = nodes.get(childNodeId);
                node.put("isParent", true);
                childNode.put("level", ((int) node.get("level")) + 1);
                childNode.put("parent", node.get("key"));
                setChildNodes(childNodeId, nodes, childCategoriesMap);
            });
        }
    }

    /**
     * Method to get available subCategories of a category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableSubCategoriesForCategory(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Category> category = get(categoryId, false);
        Set<String> categoryIds = new HashSet<>();
        category.ifPresent(category1 -> relatedCategoryDAO.findByCategoryId(category1.getId()).forEach(rc -> categoryIds.add(rc.getSubCategoryId())));
        categoryIds.add(category.get().getId());
        return getAllWithExclusions(categoryIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), page, size, sort, true);
    }

    /**
     * Method to get subCategories of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getSubCategories(ID<String> categoryId, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(category -> categoryDAO.getSubCategories(category.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to set the sequencing of two subCategories
     *
     * @param categoryId          Internal or External id of the Category
     * @param sourceId            Internal or External id of the subCategory, whose sequencing needs to be set
     * @param destinationId       Internal or External id of the subCategory at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    @Override
    public boolean setSubCategorySequence(ID<String> categoryId, ID<String> sourceId, ID<String> destinationId) {
        List<ID<String>> ids = new ArrayList<>();
        ids.add(sourceId);
        ids.add(destinationId);
        return get(categoryId, false)
                .map(category -> {
                    Map<String, Category> categoriesMap = getAll(ids, null, false)
                            .stream().collect(Collectors.toMap(Category::getCategoryId, category1 -> category1));

                    List<String> subCategoryIds = categoriesMap.entrySet().stream().map(entry -> entry.getValue().getId()).collect(Collectors.toList());

                    Map<String, RelatedCategory> subCategoriesMap = relatedCategoryDAO.findByCategoryIdAndSubCategoryIdIn(category.getId(), subCategoryIds.toArray(new String[0]))
                            .stream().collect(Collectors.toMap(RelatedCategory::getSubCategoryId, subCategory -> subCategory));

                    RelatedCategory source = subCategoriesMap.get(categoriesMap.get(sourceId.getId()).getId());
                    RelatedCategory destination = subCategoriesMap.get(categoriesMap.get(destinationId.getId()).getId());

                    PIMConstants.ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<RelatedCategory> modifiedSubCategories = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        modifiedSubCategories.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        modifiedSubCategories.add(destination);
                        modifiedSubCategories.addAll(rearrangeOtherSubCategories(category.getId(), source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedSubCategories.add(source);
                        modifiedSubCategories.addAll(rearrangeOtherSubCategories(category.getId(), source, destination, direction));
                    }
                    relatedCategoryDAO.saveAll(modifiedSubCategories);
                    return true;
                }).orElse(false);
    }

    private List<RelatedCategory> rearrangeOtherSubCategories(String internalCategoryId, RelatedCategory source, RelatedCategory destination, PIMConstants.ReorderingDirection direction) {
        List<RelatedCategory> adjustedSubCategories = new ArrayList<>();
        List<RelatedCategory> subCategories = relatedCategoryDAO.findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(internalCategoryId, destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
        int subSequenceNum = direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum();
        for(RelatedCategory subCategory : subCategories) {
            if(subCategory.getSubCategoryId().equals(source.getSubCategoryId()) || subCategory.getSubCategoryId().equals(destination.getSubCategoryId())) {
                continue;
            }
            if(subCategory.getSubSequenceNum() == subSequenceNum) {
                subCategory.setSubSequenceNum(++subSequenceNum);
                adjustedSubCategories.add(subCategory);
            } else {
                break;
            }
        }
        return adjustedSubCategories;
    }

    /**
     * Method to set the sequencing of two products inside a category
     *
     * @param categoryId          Internal or External id of the Category
     * @param sourceId            Internal or External id of the product, whose sequencing needs to be set
     * @param destinationId       Internal or External id of the product at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    @Override
    public boolean setProductSequence(ID<String> categoryId, ID<String> sourceId, ID<String> destinationId) {
        List<ID<String>> ids = new ArrayList<>();
        ids.add(sourceId);
        ids.add(destinationId);
        return get(categoryId, false)
                .map(category -> {

                    // Get the product instances corresponding to the source and destination ids and store in to a map with productId as the key
                    Map<String, Product> productsMap = productService.getAll(ids, null, false)
                            .stream().collect(Collectors.toMap(Product::getProductId, product -> product));

                    // Get a list of internal product ids of both source and destination products
                    List<String> categoryProductIds = productsMap.entrySet().stream().map(entry -> entry.getValue().getId()).collect(Collectors.toList());


                    Map<String, CategoryProduct> categoryProductsMap = categoryProductDAO.findByCategoryIdAndProductIdIn(category.getId(), categoryProductIds.toArray(new String[0]))
                            .stream().collect(Collectors.toMap(CategoryProduct::getProductId, categoryProduct -> categoryProduct));

                    CategoryProduct source = categoryProductsMap.get(productsMap.get(sourceId.getId()).getId());
                    CategoryProduct destination = categoryProductsMap.get(productsMap.get(destinationId.getId()).getId());

                    PIMConstants.ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<CategoryProduct> modifiedcategoryProducts = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        modifiedcategoryProducts.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        modifiedcategoryProducts.add(destination);
                        modifiedcategoryProducts.addAll(rearrangeOtherCategoryProducts(category.getId(), source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedcategoryProducts.add(source);
                        modifiedcategoryProducts.addAll(rearrangeOtherCategoryProducts(category.getId(), source, destination, direction));
                    }
                    categoryProductDAO.saveAll(modifiedcategoryProducts);
                    return true;
                }).orElse(false);
    }

    private List<CategoryProduct> rearrangeOtherCategoryProducts(String internalCategoryId, CategoryProduct source, CategoryProduct destination, PIMConstants.ReorderingDirection direction) {
        List<CategoryProduct> adjustedCategoryProducts = new ArrayList<>();
        List<CategoryProduct> categoryProducts = categoryProductDAO.findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(internalCategoryId, destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
        int subSequenceNum = direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum();
        for(CategoryProduct categoryProduct : categoryProducts) {
            if(categoryProduct.getProductId().equals(source.getProductId()) || categoryProduct.getProductId().equals(destination.getProductId())) {
                continue;
            }
            if(categoryProduct.getSubSequenceNum() == subSequenceNum) {
                categoryProduct.setSubSequenceNum(++subSequenceNum);
                adjustedCategoryProducts.add(categoryProduct);
            } else {
                break;
            }
        }
        return adjustedCategoryProducts;
    }

    /**
     * Method to add subCategory for a category.
     *
     * @param categoryId Internal or External id of the Category
     * @param subCategoryId Internal or External id of the subCategory
     * @return
     */
    @Override
    public RelatedCategory addSubCategory(ID<String> categoryId, ID<String> subCategoryId) {
        Optional<Category> category = get(categoryId, false);
        if(category.isPresent()) {
            Optional<Category> subCategory = get(subCategoryId, false);
            if(subCategory.isPresent()) {
                Optional<RelatedCategory> top = relatedCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                List<RelatedCategory> subCategories = relatedCategoryDAO.findBySubCategoryId(category.get().getId());
                String fullSubCategoryId = subCategories.isEmpty() ? category.get().getId() : subCategories.get(0).getFullSubCategoryId();
                RelatedCategory relatedCategory = relatedCategoryDAO.save(new RelatedCategory(category.get().getId(), subCategory.get().getId(), fullSubCategoryId, 0, top.map(EntityAssociation::getSubSequenceNum).orElse(0)));
                SyncStatus syncStatus = new SyncStatus();
                syncStatus.setEntity("relatedCategory");
                syncStatus.setTimeStamp(LocalDateTime.now());
                syncStatus.setExportedTimeStamp(null);
                syncStatus.setStatus("pending");
                syncStatus.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                syncStatus.setEntityId(relatedCategory.getId());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> dataMap = objectMapper.convertValue(relatedCategory, Map.class);
                syncStatus.setData(dataMap);
                syncStatusDAO.save(syncStatus);
                return relatedCategory;
            }
        }
        return null;
    }

    @Override
    public boolean toggleSubCategory(ID<String> categoryId, ID<String> subCategoryId, Toggle active) {
        return get(categoryId, false)
                .map(category -> get(subCategoryId, false)
                        .map(subCategory -> relatedCategoryDAO.findFirstByCategoryIdAndSubCategoryId(category.getId(), subCategory.getId())
                                .map(relatedCategory -> {
                                    relatedCategory.setActive(active.state());
                                    relatedCategory.setLastExportedTimeStamp(null);
                                    relatedCategoryDAO.save(relatedCategory);
                                    return true;
                                })
                                .orElse(false))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find subCategory with id: " + subCategoryId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find category with id: " + categoryId));
    }

    @Override
    public boolean toggleProduct(ID<String> categoryId, ID<String> productId, Toggle active) {
        return get(categoryId, false)
                .map(category -> productService.get(productId, false)
                        .map(product -> categoryProductDAO.findFirstByCategoryIdAndProductId(category.getId(), product.getId())
                                .map(categoryProduct -> {
                                    categoryProduct.setActive(active.state());
                                    categoryProduct.setLastExportedTimeStamp(null);
                                    categoryProductDAO.save(categoryProduct);
                                    return true;
                                })
                                .orElse(false))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id: " + productId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find category with id: " + categoryId));
    }

    @Override
    public Page<CategoryProduct> getCategoryProducts(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, false);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<CategoryProduct> categoryProducts = categoryProductDAO.findByCategoryIdAndActiveIn(category.getId(), PlatformUtil.getActiveOptions(activeRequired), pageable);
            List<String> productIds = new ArrayList<>();
            categoryProducts.forEach(cp -> productIds.add(cp.getProductId()));
            if(productIds.size() > 0) {
                Map<String, Product> productsMap = PlatformUtil.getIdedMap(productService.getAll(productIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), null, activeRequired), ID.Type.INTERNAL_ID);
                categoryProducts.forEach(cp -> cp.init(category, productsMap.get(cp.getProductId())));
            }
            return categoryProducts;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to get products of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param pageable The pageable instance
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getCategoryProducts(ID<String> categoryId, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(category -> categoryDAO.getProducts(category.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get available products of a category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Product> getAvailableProductsForCategory(ID<String> categoryId, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Category> category = get(categoryId, false);
        Set<String> productIds = new HashSet<>();
        category.ifPresent(category1 -> categoryProductDAO.findByCategoryId(category1.getId()).forEach(cp -> productIds.add(cp.getProductId())));
        return productService.getAllWithExclusions(productIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), page, size, sort, false);
    }

    /**
     * Method to add product for a category.
     *
     * @param categoryId Internal or External id of the Category
     * @param productId Internal or External id of the Product
     * @return
     */
    @Override
    public CategoryProduct addProduct(ID<String> categoryId, ID<String> productId) {
        Optional<Category> category = get(categoryId, false);
        if(category.isPresent()) {
            Optional<Product> product = productService.get(productId, false);
            if(product.isPresent()) {
                Optional<ProductCategory> top1 = productCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                productCategoryDAO.save(new ProductCategory(product.get().getId(), getInternalId(categoryId).getId().toString(), top1.map(productCategory -> productCategory.getSubSequenceNum() + 1).orElse(0)));
                Optional<CategoryProduct> top2 = categoryProductDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                CategoryProduct savedCategoryProduct = categoryProductDAO.save(new CategoryProduct(getInternalId(categoryId).getId().toString(), product.get().getId(), top2.map(categoryProduct -> categoryProduct.getSubSequenceNum() + 1).orElse(0)));
                SyncStatus syncStatus = new SyncStatus();
                syncStatus.setEntity("categoryProduct");
                syncStatus.setTimeStamp(LocalDateTime.now());
                syncStatus.setExportedTimeStamp(null);
                syncStatus.setStatus("pending");
                syncStatus.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                syncStatus.setEntityId(savedCategoryProduct.getId());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> dataMap = objectMapper.convertValue(savedCategoryProduct, Map.class);
                syncStatus.setData(dataMap);
                syncStatusDAO.save(syncStatus);
                return savedCategoryProduct;
            }
        }
        return null;
    }

    @Override
    public boolean toggleCategory(ID<String> categoryId, Toggle toggle) {
        return get(categoryId, false)
                .map(category -> {

                    category.setGroup("DETAILS");
                    category.setLastExportedTimeStamp(null);
                    category.setActive(toggle.state());
                    categoryDAO.save(category);

                    rootCategoryDAO.findByRootCategoryId(category.getId())
                            .forEach(rootCategory -> {
                                rootCategory.setActive(toggle.state());
                                rootCategoryDAO.save(rootCategory);
                            });
                    relatedCategoryDAO.findBySubCategoryId(category.getId())
                            .forEach(relatedCategory -> {
                                relatedCategory.setActive(toggle.state());
                                relatedCategoryDAO.save(relatedCategory);
                            });
                    productCategoryDAO.findByCategoryId(category.getId())
                            .forEach(productCategory -> {
                                productCategory.setActive(toggle.state());
                                productCategoryDAO.save(productCategory);
                            });

                    return true;

                }).orElseThrow(() -> new EntityNotFoundException("unable to find"));
    }

    @Override
    public List<RootCategory> getAllRootCategoriesWithCategoryId(ID<String> categoryId) {
        ID<String> internalId = getInternalId(categoryId);
        return isNotEmpty(internalId) ? rootCategoryDAO.findByRootCategoryId(internalId.getId()) : new ArrayList<>();
    }

    @Override
    public List<RelatedCategory> getAllRelatedCategoriesWithSubCategoryId(ID<String> categoryId) {
        ID<String> internalId = getInternalId(categoryId);
        return isNotEmpty(internalId) ? relatedCategoryDAO.findBySubCategoryId(internalId.getId()) : new ArrayList<>();
    }

    @Override
    public List<RelatedCategory> getAllRelatedCategoriesWithCategoryId(ID<String> categoryId) {
        ID<String> internalId = getInternalId(categoryId);
        return isNotEmpty(internalId) ? relatedCategoryDAO.findByCategoryId(internalId.getId()) : new ArrayList<>();
    }

    @Override
    public List<ProductCategory> getAllProductCategoriesWithCategoryId(ID<String> categoryId) {
        ID<String> internalId = getInternalId(categoryId);
        return isNotEmpty(internalId) ? productCategoryDAO.findByCategoryId(internalId.getId()) : new ArrayList<>();
    }

    @Override
    public List<CategoryProduct> getAllCategoryProducts(ID<String> categoryId) {
        ID<String> internalId = getInternalId(categoryId);
        return isNotEmpty(internalId) ? categoryProductDAO.findByCategoryId(internalId.getId()) : new ArrayList<>();
    }

    @Override
    public void updateRootCategory(RootCategory rootCategory) {
        rootCategoryDAO.save(rootCategory);
    }

    @Override
    public void updateRelatedCategory(RelatedCategory relatedCategory) {
        relatedCategoryDAO.save(relatedCategory);
    }

    @Override
    public void updateProductCategory(ProductCategory productCategory) {
        productCategoryDAO.save(productCategory);
    }

    @Override
    public void archiveCategoryAssociations(ID<String> categoryId, Archive archived, Category category) {
        if(archived == Archive.NO) {
            List<RelatedCategory> relatedCategories = getAllRelatedCategoriesWithSubCategoryId(categoryId);
            relatedCategories.forEach(relatedCategory -> categoryDAO.archiveAssociationById(ID.INTERNAL_ID(relatedCategory.getId()), archived, RelatedCategory.class));

            List<CategoryProduct> categoryProducts = getAllCategoryProducts(categoryId);
            categoryProducts.forEach(categoryProduct -> categoryDAO.archiveAssociationById(ID.INTERNAL_ID(categoryProduct.getId()), archived, CategoryProduct.class));
        } else {
            List<CategoryProduct> categoryProducts = getAllCategoryProducts(categoryId);
            categoryProducts.forEach(categoryProduct -> categoryDAO.archiveAssociationById(ID.INTERNAL_ID(categoryProduct.getId()), archived, CategoryProduct.class));
        }
    }

    @Override
    public List<RelatedCategory> getAll() {
        return relatedCategoryDAO.findAll();
    }

    @Override
    public void addParentCategory(ID<String> categoryId, ID<String> productId) {
        Optional<Category> category = get(categoryId, false);
        if(category.isPresent()) {
            Optional<Product> product = productService.get(productId, false);
            if(product.isPresent()) {
                Optional<ParentCategoryProduct> top2 = parentCategoryProductDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                parentCategoryProductDAO.save(new ParentCategoryProduct(getInternalId(categoryId).getId().toString(), product.get().getId(), top2.map(parentCategoryProduct -> parentCategoryProduct.getSubSequenceNum() + 1).orElse(0)));
            }
        }
    }

    @Override
    public Page<Map<String, Object>> getAllParentCategoryProducts(ID<String> categoryId, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(category -> categoryDAO.getAllParentCategoryProducts(category.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public boolean setAllParentCategoryProductsSequence(ID<String> categoryId, ID<String> sourceId, ID<String> destinationId) {
        List<ID<String>> ids = new ArrayList<>();
        ids.add(sourceId);
        ids.add(destinationId);
        return get(categoryId, false)
                .map(category -> {

                    // Get the product instances corresponding to the source and destination ids and store in to a map with productId as the key
                    Map<String, Product> productsMap = productService.getAll(ids, null, false)
                            .stream().collect(Collectors.toMap(Product::getProductId, product -> product));

                    // Get a list of internal product ids of both source and destination products
                    List<String> categoryProductIds = productsMap.entrySet().stream().map(entry -> entry.getValue().getId()).collect(Collectors.toList());


                    Map<String, ParentCategoryProduct> categoryProductsMap = parentCategoryProductDAO.findByCategoryIdAndProductIdIn(category.getId(), categoryProductIds.toArray(new String[0]))
                            .stream().collect(Collectors.toMap(ParentCategoryProduct::getProductId, categoryProduct -> categoryProduct));

                    ParentCategoryProduct source = categoryProductsMap.get(productsMap.get(sourceId.getId()).getId());
                    ParentCategoryProduct destination = categoryProductsMap.get(productsMap.get(destinationId.getId()).getId());

                    PIMConstants.ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<ParentCategoryProduct> modifiedcategoryProducts = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        modifiedcategoryProducts.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        modifiedcategoryProducts.add(destination);
                        modifiedcategoryProducts.addAll(rearrangeOtherParentCategoryProducts(category.getId(), source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedcategoryProducts.add(source);
                        modifiedcategoryProducts.addAll(rearrangeOtherParentCategoryProducts(category.getId(), source, destination, direction));
                    }
                    parentCategoryProductDAO.saveAll(modifiedcategoryProducts);
                    return true;
                }).orElse(false);
    }

    private List<ParentCategoryProduct> rearrangeOtherParentCategoryProducts(String internalCategoryId, ParentCategoryProduct source, CategoryProduct destination, PIMConstants.ReorderingDirection direction) {
        List<ParentCategoryProduct> adjustedCategoryProducts = new ArrayList<>();
        List<ParentCategoryProduct> categoryProducts = parentCategoryProductDAO.findByCategoryIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(internalCategoryId, destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
        int subSequenceNum = direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum();
        for(ParentCategoryProduct categoryProduct : categoryProducts) {
            if(categoryProduct.getProductId().equals(source.getProductId()) || categoryProduct.getProductId().equals(destination.getProductId())) {
                continue;
            }
            if(categoryProduct.getSubSequenceNum() == subSequenceNum) {
                categoryProduct.setSubSequenceNum(++subSequenceNum);
                adjustedCategoryProducts.add(categoryProduct);
            } else {
                break;
            }
        }
        return adjustedCategoryProducts;
    }

    @Override
    public boolean toggleParentCategoryProduct(ID<String> categoryId, ID<String> productId, Toggle active) {
        return get(categoryId, false)
                .map(category -> productService.get(productId, false)
                        .map(product -> parentCategoryProductDAO.findFirstByCategoryIdAndProductId(category.getId(), product.getId())
                                .map(parentCategoryProduct -> {
                                    parentCategoryProduct.setActive(active.state());
                                    parentCategoryProductDAO.save(parentCategoryProduct);
                                    return true;
                                })
                                .orElse(false))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find product with id: " + productId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find category with id: " + categoryId));
    }

    @Override
    public Page<Map<String, Object>> findAllParentCategoryProducts(ID<String> categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(categoryId, false)
                .map(product -> categoryDAO.findAllParentCategoryProducts(product.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public boolean syncAllParentCategoryProducts(String categoryId, Pageable pageable) {
        List<Map<String, Object>> categoryList = getSubCategories(ID.EXTERNAL_ID(categoryId), pageable, false).getContent();
        List<String> productIds = new ArrayList<>();
        categoryList.forEach(category -> {
            List<CategoryProduct> categoryProductList = getAllCategoryProducts(ID.EXTERNAL_ID(String.valueOf(category.get("externalId"))));
            categoryProductList.forEach(categoryProduct -> {
                productIds.add(categoryProduct.getProductId());
            });
        });

        List<String> parentCatProductIds = new ArrayList<>();
        ID<String> internalId = getInternalId(ID.EXTERNAL_ID(categoryId));
        List<ParentCategoryProduct> parentCategoryProductList = parentCategoryProductDAO.findByCategoryIdAndProductIdIn(internalId.getId(), productIds.toArray(new String[0]));
        parentCategoryProductList.forEach(parentCategoryProduct -> {
            parentCatProductIds.add(parentCategoryProduct.getProductId());
        });

        productIds.removeAll(parentCatProductIds);
        List<String> finalProductIds = new ArrayList<>();
        for(String str : productIds) {
            if(!finalProductIds.contains(str)) {
                finalProductIds.add(str);
            }
        }
        finalProductIds.forEach(productId -> {
            ParentCategoryProduct _parentCategoryProduct = new ParentCategoryProduct();
            _parentCategoryProduct.setCategoryId(internalId.getId());
            _parentCategoryProduct.setProductId(productId);
            _parentCategoryProduct.setSequenceNum(0);
            _parentCategoryProduct.setSubSequenceNum(0);
            _parentCategoryProduct.setActive("Y");
            parentCategoryProductDAO.save(_parentCategoryProduct);
        });
        return true;
    }

    @Override
    public List<CategoryProduct> loadCategoryProductToBOS() {
        return categoryProductDAO.findAll();
    }

    @Override
    public List<RelatedCategory> syncSubCategories(List<RelatedCategory> finalSubCategories) {
        return relatedCategoryDAO.saveAll(finalSubCategories);
    }

    @Override
    public List<CategoryProduct> syncCategoryProducts(List<CategoryProduct> finalCategoryProducts) {
        return categoryProductDAO.saveAll(finalCategoryProducts);
    }
}
