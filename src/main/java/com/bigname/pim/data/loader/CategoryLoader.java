package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class CategoryLoader {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    public boolean load(String filePath) {

        Set<Category> savableCategories = new LinkedHashSet<>();
        Set<RelatedCategory> savableRelatedCategories = new LinkedHashSet<>();

        Map<String, Integer> sequenceMap = new HashMap<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> data = POIUtil.readData(filePath);

        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Category::getCategoryId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = relatedCategoryDAO.findAll().stream().collect(Collectors.toMap( e -> e.getCategoryId() + "|" + e.getSubCategoryId(), e -> e));

        System.out.println("Categories to process -------------->"+  (data.size() - 1));
        System.out.println("# of category attributes-------------->"+data.get(0).size());

        List<String> attributeNamesMetadata = data.remove(0);
        // Sort categories data by PARENT_ID and NAME
        data.sort((c1, c2) ->
                c1.get(attributeNamesMetadata.indexOf("PARENT_ID")).equals(c2.get(attributeNamesMetadata.indexOf("PARENT_ID"))) ?
                    c1.get(attributeNamesMetadata.indexOf("NAME")).compareTo(c2.get(attributeNamesMetadata.indexOf("NAME"))) : c1.get(attributeNamesMetadata.indexOf("PARENT_ID")).compareTo(c2.get(attributeNamesMetadata.indexOf("PARENT_ID"))));

        //Skip the header row and process each category row.
        for(int i = 0; i < data.size(); i ++) {
            String categoryId = data.get(i).get(attributeNamesMetadata.indexOf("CATEGORY_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("NAME"));
            String parentId = data.get(i).get(attributeNamesMetadata.indexOf("PARENT_ID")).toUpperCase();
            String description = data.get(i).get(attributeNamesMetadata.indexOf("DESCRIPTION"));
            boolean skip = false;
            //Create the category is another one with the same CATEGORY_ID won't exists
            if(categoriesLookupMap.containsKey(categoryId)) {
                //SKIP without updating
                skip = true;
            } else {
                Category category = new Category();
                category.setCategoryId(categoryId);
                category.setCategoryName(name);
                category.setDescription(description);
                category.setActive("Y");
                category.setDiscontinued("N");
                //Add this for batch saving
                savableCategories.add(category);

                //Add this for checking duplicates in the feed
                categoriesLookupMap.put(categoryId, category);

                if(!parentId.isEmpty()) {
                    if(!sequenceMap.containsKey(parentId)) {
                        sequenceMap.put(parentId, 0);
                    }
                    if(categoriesLookupMap.containsKey(parentId)) {
                        Category parentCategory = categoriesLookupMap.get(parentId);
                        if(relatedCategoriesLookupMap.containsKey(parentCategory.getId() + "|" + category.getId())) {
                            //Skip, subCategory already exists
                            skip = true;
                        } else {
                            int sequenceNum = sequenceMap.get(parentId);
                            RelatedCategory subCategory = new RelatedCategory(parentCategory.getId(), category.getId(), "", sequenceNum, 0);
                            subCategory.setActive("Y");
                            //Add this for batch saving
                            savableRelatedCategories.add(subCategory);

                            //Add this for checking duplicates in the feed
                            relatedCategoriesLookupMap.put(parentCategory.getId() + "|" + category.getId(), subCategory);
                            sequenceMap.put(parentId, sequenceNum + 1);
                        }
                    } else {
                        //Parent ID invalid, skip the subCategory mapping
                        skip = true;
                    }
                }
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }
        categoryDAO.saveAll(savableCategories);
        relatedCategoryDAO.saveAll(savableRelatedCategories);
        System.out.println("skipped ---------->" + skippedItems.size());
        return true;
    }
}
