package com.bigname.pim.data.loader.exporter;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by sanoop on 31/01/2019.
 */
@Component
public class CategoryExporter {

    @Autowired
    private CategoryService categoryService;

    public boolean exportData(String filepath){
        List<Category> categoryData = categoryService.getAll(null,true);
        Map<String,Object[]>data = new TreeMap<>();
        data.put("1", new Object[]{"CATEGORY_ID","CATEGORY_NAME","DESCRIPTION",/*"LONG_DESCRIPTION","META_TITLE","META_DESCRIPTION","META_KEYWORDS",*/"ACTIVE","DISCONTINUED","ID" });

        int i=2;
        for (Iterator<Category> iter = categoryData.iterator(); iter.hasNext(); ) {
            Category element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getCategoryId(), element.getCategoryName(),element.getDescription(), /*element.getLongDescription(),element.getMetaTitle(),element.getMetaDescription(),element.getMetaKeywords(),*/element.getActive(),element.getDiscontinued(),element.getId() });
            i++;
        }
        POIUtil.writeData(filepath,"category",data);

       return true;
    }



}
