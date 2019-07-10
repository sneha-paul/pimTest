package com.bigname.pim.data.exportor;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.mongo.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.data.exporter.BaseExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by sanoop on 31/01/2019.
 */
@Component
public class CategoryExporter implements BaseExporter<Category, CategoryService> {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    @Override
    public String getFileName(Type fileType) {
        return "CategoryExport" + PimUtil.getTimestamp() + fileType.getExt();
    }


    public boolean exportData(String filePath) {

        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, true).stream().collect(Collectors.toMap(Category::getId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = relatedCategoryDAO.findAll().stream().collect(Collectors.toMap(e -> e.getSubCategoryId(), e -> e));

        List<Map<String, Object>> hierarchy =  categoryService.getCategoryHierarchy(true);
        Map<String, Object[]> data = new TreeMap<>();
        data.put("1", new Object[]{ "CATEGORY_ID", "NAME", "PARENT_ID", "DESCRIPTION" });
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
        POIUtil.writeData(filePath,"Category", data);
        return  true;
    }
}
