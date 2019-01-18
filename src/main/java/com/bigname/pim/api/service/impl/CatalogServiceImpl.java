package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import com.bigname.pim.util.PimUtil;
import com.bigname.pim.util.Toggle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.util.PIMConstants.ReorderingDirection;
import static com.bigname.pim.util.PIMConstants.ReorderingDirection.DOWN;
import static com.bigname.pim.util.PIMConstants.ReorderingDirection.UP;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO, CatalogService> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private RootCategoryDAO rootCategoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;
    private CategoryService categoryService;


    @Autowired
    public CatalogServiceImpl(CatalogDAO catalogDAO, Validator validator, WebsiteCatalogDAO websiteCatalogDAO, RootCategoryDAO rootCategoryDAO, RelatedCategoryDAO relatedCategoryDAO, CategoryService categoryService) {
        super(catalogDAO, "catalog", validator);
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.rootCategoryDAO = rootCategoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
        this.categoryService = categoryService;
    }


    @Override
    public Catalog createOrUpdate(Catalog catalog) {
        return catalogDAO.save(catalog);
    }

    @Override
    public List<Catalog> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<Catalog> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }



    @Override
    public List<Catalog> findAll(String searchField, String keyword, com.bigname.pim.util.Pageable pageable, boolean... activeRequired) {
        return catalogDAO.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Page<Map<String, Object>> findAllRootCategories(String catalogId, FindBy findBy, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(catalogId, findBy, false)
                .map(category -> catalogDAO.findAllRootCategories(category.getId(), searchField, keyword, pageable.getPageRequest(), activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Page<Category> findAvailableRootCategoriesForCatalog(String catalogId, FindBy findBy, String searchField, String keyword, com.bigname.pim.util.Pageable pageable, boolean... activeRequired) {
        return get(catalogId, findBy, false)
                .map(category -> catalogDAO.findAvailableRootCategoriesForCatalog(category.getId(), searchField, keyword, pageable.getPageRequest(), activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public Optional<Catalog> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<Catalog> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    /**
     * Method to get available categories of a catalog in paginated format.
     *
     * @param id Internal or External id of the Catalog
     * @param findBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableRootCategoriesForCatalog(String id, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Catalog> catalog = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        catalog.ifPresent(catalog1 -> rootCategoryDAO.findByCatalogId(catalog1.getId()).forEach(rc -> categoryIds.add(rc.getRootCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, true);
    }

    @Override
    public boolean toggleRootCategory(String catalogId, FindBy catalogIdFindBy, String rootCategoryId, FindBy rootCategoryIdFindBy, Toggle active) {
        return get(catalogId, catalogIdFindBy, false)
                .map(catalog -> categoryService.get(rootCategoryId, rootCategoryIdFindBy, false)
                        .map(rootCategory -> rootCategoryDAO.findFirstByCatalogIdAndRootCategoryId(catalog.getId(), rootCategory.getId())
                                .map(rootCategory1 -> {
                                    rootCategory1.setActive(active.state());
                                    rootCategoryDAO.save(rootCategory1);
                                    return true;
                                })
                                .orElse(false))
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find category with id: " + rootCategoryId)))
                .orElseThrow(() -> new EntityNotFoundException("Unable to find catalog with id: " + catalogId));
    }

    /**
     * Method to get categories of a Catalog in paginated format.
     *
     * @param catalogId Internal or External id of the Catalog
     * @param findBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getRootCategories(String catalogId, FindBy findBy, com.bigname.pim.util.Pageable pageable, boolean... activeRequired) {
        return get(catalogId, findBy, false)
                .map(catalog -> catalogDAO.getRootCategories(catalog.getId(), pageable.getPageRequest()))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<Map<String, Object>> getCategoryHierarchy(String catalogId, boolean... activeRequired) {
        List<Map<String, Object>> hierarchy = new ArrayList<>();
        get(catalogId, FindBy.EXTERNAL_ID, false)
                .ifPresent(catalog -> {
                    //Sorted rootCategories
                    Map<String, RootCategory> rootCategoriesMap = rootCategoryDAO.findByCatalogIdOrderBySequenceNumAscSubSequenceNumDesc(catalog.getId()).stream().collect(CollectionsUtil.toLinkedMap(RootCategory::getRootCategoryId, e -> e));

                    //Unsorted relatedCategories
                    List<RelatedCategory> relatedCategories = relatedCategoryDAO.findByActiveIn(PimUtil.getActiveOptions(activeRequired));

                    Map<String, List<RelatedCategory>> parentCategoriesMap = new LinkedHashMap<>();

                    for (RelatedCategory relatedCategory : relatedCategories) {
                        if(parentCategoriesMap.containsKey(relatedCategory.getCategoryId())) {
                            parentCategoriesMap.get(relatedCategory.getCategoryId()).add(relatedCategory);
                        } else {
                            List<RelatedCategory> childList = new ArrayList<>();
                            childList.add(relatedCategory);
                            parentCategoriesMap.put(relatedCategory.getCategoryId(), childList);
                        }
                    }
                    Set<String> categoryIds = new HashSet<>(rootCategoriesMap.keySet());

                    //Sort all subCategories within each parent category
                    parentCategoriesMap.forEach((parentCategoryId, subCategories) -> {
                        if(subCategories.size() > 1) {
                            subCategories.sort((sc1, sc2) -> sc1.getSequenceNum() > sc2.getSequenceNum() ? 1 : sc1.getSequenceNum() < sc2.getSequenceNum() ? -1 : sc1.getSubSequenceNum() < sc2.getSubSequenceNum() ? 1 : -1);
                        }
                        categoryIds.add(parentCategoryId);
                        subCategories.forEach(subCategory -> categoryIds.add(subCategory.getSubCategoryId()));
                    });

                    //Categories lookup map
                    Map<String, Category> categoriesMap = categoryService.getAll(new ArrayList<>(categoryIds).toArray(new String[0]), FindBy.INTERNAL_ID, null, false).stream().collect(Collectors.toMap(c -> c.getId(), c -> c));

                    //Build the full node hierarchy for each root category
                    rootCategoriesMap.forEach((rootCategoryId, rootCategory) -> hierarchy.addAll(buildFullNode(rootCategoryId, 0, "", parentCategoriesMap, categoriesMap)));

                });

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
        return node;
    }



    /**
     * Method to set the sequencing of two root categories
     *
     * @param catalogId           Internal or External id of the Catalog
     * @param catalogIdFindBy     Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param sourceId            Internal or External id of the rootCategory, whose sequencing needs to be set
     * @param sourceIdFindBy      Type of the source rootCategory id, INTERNAL_ID or EXTERNAL_ID
     * @param destinationId       Internal or External id of the rootCategory at the destination slot
     * @param destinationIdFindBy Type of the destination rootCategory id, INTERNAL_ID or EXTERNAL_ID
     * @return true if sequencing got modified, false otherwise
     */
    @Override
    public boolean setRootCategorySequence(String catalogId, FindBy catalogIdFindBy, String sourceId, FindBy sourceIdFindBy, String destinationId, FindBy destinationIdFindBy) {
        return get(catalogId, catalogIdFindBy, false)
                .map(catalog -> {
                    Map<String, Category> categoriesMap = categoryService.getAll(new String[] {sourceId, destinationId}, FindBy.EXTERNAL_ID, null, false)
                            .stream().collect(Collectors.toMap(Category::getCategoryId, category -> category));

                    List<String> rootCategoryIds = categoriesMap.entrySet().stream().map(entry -> entry.getValue().getId()).collect(Collectors.toList());

                    Map<String, RootCategory> rootCategoriesMap = rootCategoryDAO.findByCatalogIdAndRootCategoryIdIn(catalog.getId(), rootCategoryIds.toArray(new String[0]))
                            .stream().collect(Collectors.toMap(RootCategory::getRootCategoryId, rootCategory -> rootCategory));

                    RootCategory source = rootCategoriesMap.get(categoriesMap.get(sourceId).getId());
                    RootCategory destination = rootCategoriesMap.get(categoriesMap.get(destinationId).getId());

                    ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<RootCategory> modifiedRootCategories = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        modifiedRootCategories.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        modifiedRootCategories.add(destination);
                        modifiedRootCategories.addAll(rearrangeOtherRootCategories(catalogId, source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedRootCategories.add(source);
                        modifiedRootCategories.addAll(rearrangeOtherRootCategories(catalogId, source, destination, direction));
                    }
                    rootCategoryDAO.saveAll(modifiedRootCategories);
                    return true;
                }).orElse(false);
    }

    private List<RootCategory> rearrangeOtherRootCategories(String catalogId, RootCategory source, RootCategory destination, ReorderingDirection direction) {
        List<RootCategory> adjustedRootCategories = new ArrayList<>();
        List<RootCategory> rootCategories = rootCategoryDAO.findByCatalogIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(catalogId, destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
        int subSequenceNum = direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum();
        for(RootCategory rootCategory : rootCategories) {
            if(rootCategory.getRootCategoryId().equals(source.getRootCategoryId()) || rootCategory.getRootCategoryId().equals(destination.getRootCategoryId())) {
                continue;
            }
            if(rootCategory.getSubSequenceNum() == subSequenceNum) {
                rootCategory.setSubSequenceNum(++subSequenceNum);
                adjustedRootCategories.add(rootCategory);
            } else {
                break;
            }
        }
        return adjustedRootCategories;
    }

    /**
     * Method to add category for a catalog.
     *
     * @param id Internal or External id of the Catalog
     * @param findBy1 Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param rootCategoryId Internal or External id of the Category
     * @param findBy2 Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public RootCategory addRootCategory(String id, FindBy findBy1, String rootCategoryId, FindBy findBy2) {
        Optional<Catalog> catalog = get(id, findBy1, false);
        if(catalog.isPresent()) {
            Optional<Category> rootCategory = categoryService.get(rootCategoryId, findBy2, false);
            if(rootCategory.isPresent()) {
                Optional<RootCategory> top = rootCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0);
                return rootCategoryDAO.save(new RootCategory(catalog.get().getId(), rootCategory.get().getId(), top.map(rootCategory1 -> rootCategory1.getSubSequenceNum() + 1).orElse(0)));
            }
        }
        return null;
    }


}
