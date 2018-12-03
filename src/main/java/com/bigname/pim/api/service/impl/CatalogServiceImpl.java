package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogServiceImpl extends BaseServiceSupport<Catalog, CatalogDAO, CatalogService> implements CatalogService {

    private CatalogDAO catalogDAO;
    private WebsiteCatalogDAO websiteCatalogDAO;
    private RootCategoryDAO rootCategoryDAO;
    private CategoryService categoryService;
    private WebsiteService websiteService;


    @Autowired
    public CatalogServiceImpl(CatalogDAO catalogDAO, Validator validator, WebsiteCatalogDAO websiteCatalogDAO, RootCategoryDAO rootCategoryDAO, CategoryService categoryService, @Lazy WebsiteService websiteService) {
        super(catalogDAO, "catalog", validator);
        this.catalogDAO = catalogDAO;
        this.websiteCatalogDAO = websiteCatalogDAO;
        this.rootCategoryDAO = rootCategoryDAO;
        this.categoryService = categoryService;
        this.websiteService = websiteService;
    }


    @Override
    public Catalog createOrUpdate(Catalog catalog) {
        return catalogDAO.save(catalog);
    }



    /**
     * Method to get available categories of a websiteCatalog in paginated format.
     *
     * @param websiteCatalogId Id of the websiteCatalog
     * @param page page number
     * @param size page size
     * @param sort sort object
     * @return
     */
    @Override
    public Page<Category> getAvailableRootCategoriesForCatalog(String websiteCatalogId, int page, int size, Sort sort) {

        return websiteCatalogDAO.findById(websiteCatalogId)
                .map(websiteCatalog -> categoryService.getAllWithExclusions(rootCategoryDAO.findByWebsiteCatalogId(websiteCatalogId).stream().map(RootCategory::getRootCategoryId).collect(Collectors.toList()).toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort))
                .orElse(new PageImpl<>(new ArrayList<>()));
        /*Optional<Catalog> catalog = get(id, findBy, false);
        Set<String> categoryIds = new HashSet<>();
        catalog.ifPresent(catalog1 -> rootCategoryDAO.findByWebsiteCatalogId(catalog1.getId()).forEach(rc -> categoryIds.add(rc.getRootCategoryId())));
        return categoryService.getAllWithExclusions(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, page, size, sort, true);*/
    }

    /**
     * Method to get rootCategories of a WebsiteCatalog in paginated format.
     *
     * @param websiteCatalogId Id of the websiteCatalog
     * @param page page number
     * @param size page size
     * @param sort sort Object
     * @param activeRequired activeRequired Boolean flag
     * @return
     */
    @Override
    public Page<RootCategory> getRootCategories(String websiteCatalogId, int page, int size, Sort sort, boolean... activeRequired) {

        Sort _sort = sort == null ? Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum")) : sort;
        return websiteCatalogDAO.findById(websiteCatalogId)
                .map(websiteCatalog -> {
                    Pageable pageable = PageRequest.of(page, size, _sort);

                    //Get all rootCategories for the given webCatalogId
                    Page<RootCategory> rootCategories = rootCategoryDAO.findByWebsiteCatalogIdAndActiveIn(websiteCatalogId, PimUtil.getActiveOptions(activeRequired), pageable);

                    //List to hold categoryId of all the rootCategories
                    List<String> categoryIds = new ArrayList<>();

                    //Get the categoryId of all rootCategories
                    rootCategories.forEach(rc -> categoryIds.add(rc.getRootCategoryId()));

                    //If there are rootCategory categoryIds,
                    //      1) Get all the category instances for those categoryIds,
                    //      2) Filter those rootCategories that has an invalid categoryId,
                    //      3) Inject the parent and child instance for each root category instances
                    if(categoryIds.size() > 0) {
                        Map<String, Category> categoriesMap = PimUtil.getIdedMap(categoryService.getAll(categoryIds.toArray(new String[0]), FindBy.INTERNAL_ID, null, activeRequired), FindBy.INTERNAL_ID);
                        List<RootCategory> _rootCategories = rootCategories.filter(rc -> categoriesMap.containsKey(rc.getRootCategoryId())).stream().collect(Collectors.toList());
                        _rootCategories.forEach(rc -> rc.init(websiteCatalog, categoriesMap.get(rc.getRootCategoryId())));
                        rootCategories = new PageImpl<>(_rootCategories,pageable,_rootCategories.size());//TODO : verify this logic
                    }
                    return rootCategories;
                })
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    /**
     * Method to add category for a catalog.
     *
     * @param websiteId Internal or External id of the Website
     * @param websiteIdFindBy Type of the website id, INTERNAL_ID or EXTERNAL_ID
     * @param catalogId Internal or External id of the Catalog
     * @param catalogIdFindBy Type of the catalog id, INTERNAL_ID or EXTERNAL_ID
     * @param rootCategoryId Internal or External id of the Category
     * @param rootCategoryIdFindBy Type of the category id, INTERNAL_ID or EXTERNAL_ID
     * @return
     */
    @Override
    public RootCategory addRootCategory(String websiteId, FindBy websiteIdFindBy, String catalogId, FindBy catalogIdFindBy, String rootCategoryId, FindBy rootCategoryIdFindBy) {
        return websiteService.getWebsiteCatalog(websiteId, websiteIdFindBy, catalogId, catalogIdFindBy)
                .map(websiteCatalog -> categoryService.get(rootCategoryId, rootCategoryIdFindBy, false)
                        .map(rootCategory -> rootCategoryDAO.save(new RootCategory(websiteCatalog.getId(), rootCategory.getId(), rootCategoryDAO.findTopBySequenceNumOrderBySubSequenceNumDesc(0).map(top -> top.getSubSequenceNum() + 1).orElse(0))))
                        .orElse(null))
                .orElse(null);

    }
}
