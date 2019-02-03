package com.bigname.pim.data.loader.exporter;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by sanoop on 31/01/2019.
 */
@Component
public class CategoryExporter {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    public boolean exportData(String filePath) {

        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Category::getId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = relatedCategoryDAO.findAll().stream().collect(Collectors.toMap(e -> e.getSubCategoryId(), e -> e));
        Map<String, Object[]> data = new TreeMap<>();
        data.put("1", new Object[]{ "CATEGORY_ID", "NAME","PARENT_ID", "DESCRIPTION"});
        int i=2;
        for (Iterator<Map.Entry<String,Category>> iter = categoriesLookupMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Category> element = iter.next();
            String categoryKey = element.getKey();
            Category categoryData = categoriesLookupMap.get(categoryKey);
            if (relatedCategoriesLookupMap.containsKey(categoryKey)) {
                String parentId = relatedCategoriesLookupMap.get(categoryKey).getCategoryId();
                Category parentData = categoriesLookupMap.get(parentId);
                String parentCategoryName = parentData.getExternalId();
                data.put(Integer.toString(i), new Object[]{categoryData.getExternalId(),categoryData.getCategoryName(),parentCategoryName,categoryData.getDescription()});

            } else {
                data.put(Integer.toString(i), new Object[]{categoryData.getExternalId(),categoryData.getCategoryName(),null,categoryData.getDescription()});
            }
            i++;
        }
        POIUtil.writeData(filePath,"Category",data);
        return  true;
    }



   /* @Autowired
    private CategoryService categoryService;

    public boolean exportData(String filepath){
        List<Category> categoryData = categoryService.getAll(null,true);
        Map<String,Object[]>data = new TreeMap<>();
        data.put("1", new Object[]{"CATEGORY_ID","CATEGORY_NAME","DESCRIPTION",/*"LONG_DESCRIPTION","META_TITLE","META_DESCRIPTION","META_KEYWORDS",*//*"ACTIVE","DISCONTINUED","ID" });*/

      /*  int i=2;
        for (Iterator<Category> iter = categoryData.iterator(); iter.hasNext(); ) {
            Category element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getCategoryId(), element.getCategoryName(),element.getDescription(), /*element.getLongDescription(),element.getMetaTitle(),element.getMetaDescription(),element.getMetaKeywords(),*//*element.getActive(),element.getDiscontinued(),element.getId() });
           /* i++;
        }
        POIUtil.writeData(filepath,"category",data);

       return true;
    }*/



}
