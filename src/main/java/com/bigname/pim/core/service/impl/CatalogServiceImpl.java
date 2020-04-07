package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.*;
import com.bigname.pim.core.persistence.dao.mongo.*;
import com.bigname.pim.core.service.CatalogService;
import com.bigname.pim.core.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.Archive;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xplatform.domain.SyncStatus;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.SyncStatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.core.util.PIMConstants.ReorderingDirection;
import static com.bigname.pim.core.util.PIMConstants.ReorderingDirection.DOWN;
import static com.bigname.pim.core.util.PIMConstants.ReorderingDirection.UP;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO, CatalogService> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private RootCategoryDAO rootCategoryDAO;
    private RelatedCategoryDAO relatedCategoryDAO;
    private CategoryService categoryService;
    private CategoryDAO categoryDAO;
    private SyncStatusDAO syncStatusDAO;

    @Autowired
    public CatalogServiceImpl(CatalogDAO catalogDAO, Validator validator, WebsiteCatalogDAO websiteCatalogDAO, RootCategoryDAO rootCategoryDAO, RelatedCategoryDAO relatedCategoryDAO, CategoryService categoryService, CategoryDAO categoryDAO, SyncStatusDAO syncStatusDAO) {
        super(catalogDAO, "catalog", validator);
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.rootCategoryDAO = rootCategoryDAO;
        this.relatedCategoryDAO = relatedCategoryDAO;
        this.categoryService = categoryService;
        this.categoryDAO = categoryDAO;
        this.syncStatusDAO = syncStatusDAO;
    }

    @Override
    public Page<Map<String, Object>> findAllRootCategories(ID<String> catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(catalogId, false)
                .map(category -> catalogDAO.findAllRootCategories(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<RootCategory> getAllRootCategories(String catalogInternalId) {
        return rootCategoryDAO.findByCatalogId(catalogInternalId);
    }

    @Override
    public Page<Category> findAvailableRootCategoriesForCatalog(ID<String> catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return get(catalogId, false)
                .map(category -> catalogDAO.findAvailableRootCategoriesForCatalog(category.getId(), searchField, keyword, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to get available categories of a catalog in paginated format.
     *
     * @param id Internal or External id of the Catalog
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableRootCategoriesForCatalog(ID<String> id, int page, int size, Sort sort, boolean... activeRequired) {
        Optional<Catalog> catalog = get(id, false);
        Set<String> categoryIds = new HashSet<>();
        catalog.ifPresent(catalog1 -> rootCategoryDAO.findByCatalogId(catalog1.getId()).forEach(rc -> categoryIds.add(rc.getRootCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), page, size, sort, true);
    }

    @Override
    public boolean toggleRootCategory(ID<String> catalogId, ID<String> rootCategoryId, Toggle active) {
        return get(catalogId, false)
                .map(catalog -> categoryService.get(rootCategoryId, false)
                        .map(rootCategory -> rootCategoryDAO.findFirstByCatalogIdAndRootCategoryId(catalog.getId(), rootCategory.getId())
                                .map(rootCategory1 -> {
                                    rootCategory1.setActive(active.state());
                                    rootCategory1.setLastExportedTimeStamp(null);
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
     * @param pageable The pageable object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<Map<String, Object>> getRootCategories(ID<String> catalogId, Pageable pageable, boolean... activeRequired) {
        return get(catalogId, false)
                .map(catalog -> catalogDAO.getRootCategories(catalog.getId(), pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public List<Map<String, Object>> getCategoryHierarchy(ID<String> catalogId, boolean... activeRequired) {
        List<Map<String, Object>> hierarchy = new ArrayList<>();
        get(catalogId, false)
                .ifPresent(catalog -> {
                    //Sorted rootCategories
                    Map<String, RootCategory> rootCategoriesMap = rootCategoryDAO.findByCatalogIdOrderBySequenceNumAscSubSequenceNumDesc(catalog.getId()).stream().collect(CollectionsUtil.toLinkedMap(RootCategory::getRootCategoryId, e -> e));

                    //Unsorted relatedCategories
                    List<RelatedCategory> relatedCategories = relatedCategoryDAO.findByActiveIn(PlatformUtil.getActiveOptions(activeRequired));

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
                    Map<String, Category> categoriesMap = categoryService.getAll(categoryIds.stream().map(ID::INTERNAL_ID).collect(Collectors.toList()), null, false).stream().collect(Collectors.toMap(c -> c.getId(), c -> c));

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
        node.put("discontinued", category.getDiscontinued());
        return node;
    }



    /**
     * Method to set the sequencing of two root categories
     *
     * @param catalogId           Internal or External id of the Catalog
     * @param sourceId            Internal or External id of the rootCategory, whose sequencing needs to be set
     * @param destinationId       Internal or External id of the rootCategory at the destination slot
     * @return true if sequencing got modified, false otherwise
     */
    @Override
    public boolean setRootCategorySequence(ID<String> catalogId, ID<String> sourceId, ID<String> destinationId) {
        List<ID<String>> ids = new ArrayList<>();
        ids.add(sourceId);
        ids.add(destinationId);
        return get(catalogId, false)
                .map(catalog -> {
                    Map<String, Category> categoriesMap = categoryService.getAll(ids, null, false)
                            .stream().collect(Collectors.toMap(Category::getCategoryId, category -> category));

                    List<String> rootCategoryIds = categoriesMap.entrySet().stream().map(entry -> entry.getValue().getId()).collect(Collectors.toList());

                    Map<String, RootCategory> rootCategoriesMap = rootCategoryDAO.findByCatalogIdAndRootCategoryIdIn(catalog.getId(), rootCategoryIds.toArray(new String[0]))
                            .stream().collect(Collectors.toMap(RootCategory::getRootCategoryId, rootCategory -> rootCategory));

                    RootCategory source = rootCategoriesMap.get(categoriesMap.get(sourceId.getId()).getId());
                    RootCategory destination = rootCategoriesMap.get(categoriesMap.get(destinationId.getId()).getId());

                    ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<RootCategory> modifiedRootCategories = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        source.setLastExportedTimeStamp(null);
                        modifiedRootCategories.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        destination.setLastExportedTimeStamp(null);
                        modifiedRootCategories.add(destination);
                        modifiedRootCategories.addAll(rearrangeOtherRootCategories(catalogId.getId(), source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        source.setLastExportedTimeStamp(null);
                        modifiedRootCategories.add(source);
                        modifiedRootCategories.addAll(rearrangeOtherRootCategories(catalogId.getId(), source, destination, direction));
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
                rootCategory.setLastExportedTimeStamp(null);
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
     * @param rootCategoryId Internal or External id of the Category
     * @return
     */
    @Override
    public RootCategory addRootCategory(ID<String> id, ID<String> rootCategoryId) {
        Optional<Catalog> catalog = get(id, false);
        if(catalog.isPresent()) {
            Optional<Category> rootCategory = categoryService.get(rootCategoryId, false);
            if(rootCategory.isPresent()) {
                Optional<RootCategory> top = rootCategoryDAO.findTopByCatalogIdAndSequenceNumOrderBySubSequenceNumDesc(catalog.get().getId(), 0);
                RootCategory savedRootCategory = rootCategoryDAO.save(new RootCategory(catalog.get().getId(), rootCategory.get().getId(), top.map(rootCategory1 -> rootCategory1.getSubSequenceNum() + 1).orElse(0)));
                SyncStatus syncStatus = new SyncStatus();
                syncStatus.setEntity("rootCategory");
                syncStatus.setTimeStamp(LocalDateTime.now());
                syncStatus.setExportedTimeStamp(null);
                syncStatus.setStatus("pending");
                syncStatus.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                syncStatus.setEntityId(savedRootCategory.getId());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> dataMap = objectMapper.convertValue(savedRootCategory, Map.class);
                syncStatus.setData(dataMap);
                syncStatusDAO.save(syncStatus);
                return savedRootCategory;
            }
        }
        return null;
    }

    @Override
    public List<WebsiteCatalog> getAllWebsiteCatalogsWithCatalogId(String catalogInternalId) {
        return websiteCatalogDAO.findByCatalogId(catalogInternalId);
    }

    @Override
    public boolean toggleCatalog(ID<String> catalogId, Toggle toggle) {

        return get(catalogId, false)
                .map(catalog -> {

                    catalog.setGroup("DETAILS");
                    catalog.setLastExportedTimeStamp(null);
                    catalog.setActive(toggle.state());
                    catalogDAO.save(catalog);

                    List<WebsiteCatalog> websiteCatalogs = websiteCatalogDAO.findByCatalogId(catalog.getId());
                    websiteCatalogs.forEach(websiteCatalog -> {
                            websiteCatalog.setActive(toggle.state());
                            websiteCatalogDAO.save(websiteCatalog);
                    });
                    return true;
                }).orElseThrow(() -> new EntityNotFoundException("unable to find"));
    }

    @Override
    public void updateWebsiteCatalog(WebsiteCatalog websiteCatalog) {
        websiteCatalogDAO.save(websiteCatalog);
    }

    @Override
    public void archiveCatalogAssociations(ID<String> catalogId, Archive archived, Catalog catalog) {
        /*Catalog catalog = get(ID.EXTERNAL_ID(catalogId), false).orElse(null);
        if(isEmpty(catalog)) {
            catalog = get(ID.EXTERNAL_ID(catalogId), false, false, false, true).orElse(null);
        }*/

        if(archived == Archive.NO) {
            List<WebsiteCatalog> websiteCatalogs = getAllWebsiteCatalogsWithCatalogId(catalog.getId());
            websiteCatalogs.forEach(websiteCatalog -> catalogDAO.archiveAssociationById(ID.INTERNAL_ID(websiteCatalog.getId()), archived, WebsiteCatalog.class));

            List<RootCategory> rootCategories = getAllRootCategories(catalog.getId());
            rootCategories.forEach(rootCategory -> catalogDAO.archiveAssociationById(ID.INTERNAL_ID(rootCategory.getId()), archived, RootCategory.class));
        } else {
            List<RootCategory> rootCategories = getAllRootCategories(catalog.getId());
            rootCategories.forEach(rootCategory -> catalogDAO.archiveAssociationById(ID.INTERNAL_ID(rootCategory.getId()), archived, RootCategory.class));
        }
    }

    @Override
    public List<RootCategory> loadRootCategoryToBOS() {
        return rootCategoryDAO.findAll();
    }

    @Override
    public List<RootCategory> syncRootCategories(List<RootCategory> rootCategories) {
        return rootCategoryDAO.saveAll(rootCategories);
    }
}
