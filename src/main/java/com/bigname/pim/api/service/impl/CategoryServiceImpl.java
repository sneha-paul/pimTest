package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.CategoryProductDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sruthi on 29-08-2018.
 */
@Service
public class CategoryServiceImpl extends BaseServiceSupport<Category, CategoryDAO, CategoryService> implements CategoryService{

    private CategoryDAO categoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;
    private CategoryProductDAO categoryProductDAO;
    private ProductService productService;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO, Validator validator, RelatedCategoryDAO relatedCategoryDAO, CategoryProductDAO categoryProductDAO, ProductService productService) {
        super(categoryDAO, "category", validator);
        this.categoryDAO = categoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
        this.categoryProductDAO = categoryProductDAO;
        this.productService = productService;
    }

    @Override
    public Category createOrUpdate(Category category) {
        return categoryDAO.save(category);
    }

    @Override
    public List<Map<String, Object>> getCategoryHierarchy(boolean... activeRequired) {
        Map<String, Map<String, Object>> nodes = new LinkedHashMap<>();

        List<Category> categories = categoryDAO.findByActiveIn(PimUtil.getActiveOptions(activeRequired));
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
        List<RelatedCategory> relatedCategories = relatedCategoryDAO.findByActiveIn(PimUtil.getActiveOptions(activeRequired));
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
    }

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
     * @param id Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableSubCategoriesForCategory(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        category.ifPresent(category1 -> relatedCategoryDAO.findByCategoryId(category1.getId()).forEach(rc -> categoryIds.add(rc.getSubCategoryId())));
        categoryIds.add(category.get().getId());
        return getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, true);
    }

    /**
     * Method to get subCategories of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<RelatedCategory> getSubCategories(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, findBy, false);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<RelatedCategory> relatedCategories = relatedCategoryDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> subCategoryIds = new ArrayList<>();
            relatedCategories.forEach(rc -> subCategoryIds.add(rc.getSubCategoryId()));
            if(subCategoryIds.size() > 0) {
                Map<String, Category> categoriesMap = PimUtil.getIdedMap(getAll(subCategoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                List<RelatedCategory> _relatedCategories = relatedCategories.filter(rc -> categoriesMap.containsKey(rc.getSubCategoryId())).stream().collect(Collectors.toList());
                _relatedCategories.forEach(rc -> rc.init(category, categoriesMap.get(rc.getSubCategoryId())));
                relatedCategories = new PageImpl<>(_relatedCategories,pageable,_relatedCategories.size());//TODO : verify this logic

            }
            return relatedCategories;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to add subCategory for a category.
     *
     * @param id Internal or External id of the Category
     * @param findBy1 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param subCategoryId Internal or External id of the subCategory
     * @param findBy2 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public RelatedCategory addSubCategory(String id, FindBy findBy1, String subCategoryId, FindBy findBy2) {
        Optional<Category> category = get(id, findBy1, false);
        if(category.isPresent()) {
            Optional<Category> subCategory = get(subCategoryId, findBy2, false);
            if(subCategory.isPresent()) {
                Optional<RelatedCategory> top = relatedCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                List<RelatedCategory> subCategories = relatedCategoryDAO.findBySubCategoryId(category.get().getId());
                String fullSubCategoryId = subCategories.isEmpty() ? category.get().getId() : subCategories.get(0).getFullSubCategoryId();
                return relatedCategoryDAO.save(new RelatedCategory(category.get().getId(), subCategory.get().getId(), fullSubCategoryId, top.map(EntityAssociation::getSubSequenceNum).orElse(0)));
            }
        }
        return null;
    }

    /**
     * Method to get products of a Category in paginated format.
     *
     * @param categoryId Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<CategoryProduct> getCategoryProducts(String categoryId, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Category> _category = get(categoryId, findBy, false);
        if(_category.isPresent()) {
            Category category = _category.get();
            Page<CategoryProduct> categoryProducts = categoryProductDAO.findByCategoryIdAndActiveIn(category.getId(), PimUtil.getActiveOptions(activeRequired), pageable);
            List<String> productIds = new ArrayList<>();
            categoryProducts.forEach(cp -> productIds.add(cp.getProductId()));
            if(productIds.size() > 0) {
                Map<String, Product> productsMap = PimUtil.getIdedMap(productService.getAll(productIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                categoryProducts.forEach(cp -> cp.init(category, productsMap.get(cp.getProductId())));
            }
            return categoryProducts;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Method to get available products of a category in paginated format.
     *
     * @param id Internal or External id of the Category
     * @param findBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Product> getAvailableProductsForCategory(String id, FindBy findBy, int page, int size, Sort sort) {
        Optional<Category> category = get(id, findBy, false);
        Set<String> productIds = new HashSet<>();
        category.ifPresent(category1 -> categoryProductDAO.findByCategoryId(category1.getId()).forEach(cp -> productIds.add(cp.getProductId())));
        return productService.getAllWithExclusions(productIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, false);
    }

    /**
     * Method to add product for a category.
     *
     * @param id Internal or External id of the Category
     * @param findBy1 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @param productId Internal or External id of the Product
     * @param findBy2 Type of the product id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public CategoryProduct addProduct(String id, FindBy findBy1, String productId, FindBy findBy2) {
        Optional<Category> category = get(id, findBy1, false);
        if(category.isPresent()) {
            Optional<Product> product = productService.get(productId, findBy2, false);
            if(product.isPresent()) {
                Optional<CategoryProduct> top = categoryProductDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return categoryProductDAO.save(new CategoryProduct(category.get().getId(), product.get().getId(), top.map(categoryProduct -> categoryProduct.getSubSequenceNum() + 1).orElse(0)));
            }
        }
        return null;
    }
}
