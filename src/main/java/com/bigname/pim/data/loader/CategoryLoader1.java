package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import com.m7.common.util.CollectionsUtil;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.api.domain.Category.Property.*;
import static com.m7.common.util.ValidationUtil.isEmpty;

/**
 * Created by sanoop on 31/01/2019.
 */
@Component
public class CategoryLoader1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryLoader1.class);
    @Autowired
    private CategoryService categoryService;

    private static List<String> header = Arrays.asList(CATEGORY_ID.name(), CATEGORY_NAME.name(), DESCRIPTION.name(), ACTIVE.name(), DISCONTINUED.name(),ID.name()); /*LONG_DESCRIPTION.name(), META_TITLE.name(), META_DESCRIPTION.name(), META_KEYWORDS.name(),*/

    public boolean load(String filePath) {

        List<Category> newSavableCategory = new ArrayList<>();

        List<Category> modifiedSavableCategory = new ArrayList<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> fullData = POIUtil.readData(filePath);

        List<List<String>> data = new ArrayList<>(fullData);

        Map<String, Category> categoryLookupMap = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Category::getId, e -> e));



        List<String> metadataRow = data.size() > 0 ? data.remove(0) : new ArrayList<>();

        boolean validHeader = header.stream().allMatch(metadataRow::contains);

        if(!validHeader) {

            LOGGER.info("Invalid Headers");
            LOGGER.error("Invalid Header Array");
            return false;
        }

        List<List<String>> newCategoryData = new ArrayList<>();

        List<Category> newCategory = data.stream().filter(row -> isEmpty(row.get(metadataRow.indexOf(ID.name())))).map(row -> {

            String categoryId = row.get(metadataRow.indexOf(CATEGORY_ID.name())).toUpperCase();
            String name = row.get(metadataRow.indexOf(CATEGORY_NAME.name()));
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));
           /* String longDescription = row.get(metadataRow.indexOf(LONG_DESCRIPTION.name()));
            String metaTitle = row.get(metadataRow.indexOf(META_TITLE.name()));
            String metaDescription = row.get(metadataRow.indexOf(META_DESCRIPTION));
            String metaKeywords = row.get(metadataRow.indexOf(META_KEYWORDS.name()));*/
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();

            newCategoryData.add(row);
            return new Category(CollectionsUtil.toMap(ID, name,CATEGORY_ID, categoryId, CATEGORY_NAME, name,DESCRIPTION, description, /*LONG_DESCRIPTION, longDescription, META_TITLE, metaTitle, META_DESCRIPTION, metaDescription, META_KEYWORDS, metaKeywords,*/ ACTIVE, active, DISCONTINUED, discontinued));

        }).collect(Collectors.toList());

        data.removeAll(newCategoryData);


        List<List<String>> invalidCategory = data.stream().filter(row -> !categoryLookupMap.containsKey(row.get(metadataRow.indexOf(ID.name())))).collect(Collectors.toList());

        data.removeAll(invalidCategory);

        List<List<String>> modifiedCategoryData = new ArrayList<>();

        List<Category> modifiedCategory = data.stream().filter(row -> {
            String internalId = row.get(metadataRow.indexOf(ID.name()));
            String categoryId = row.get(metadataRow.indexOf(CATEGORY_ID.name())).toUpperCase();
            String name = row.get(metadataRow.indexOf(CATEGORY_NAME.name()));
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));
           /* String longDescription = row.get(metadataRow.indexOf(LONG_DESCRIPTION.name()));
            String metaTitle = row.get(metadataRow.indexOf(META_TITLE.name()));
            String metaDescription = row.get(metadataRow.indexOf(META_DESCRIPTION.name()));
            String metaKeywords = row.get(metadataRow.indexOf(META_KEYWORDS.name()));*/
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();

            return !categoryLookupMap.get(internalId).equals(CollectionsUtil.toMap(ID.name(),internalId, CATEGORY_ID,categoryId, CATEGORY_NAME,name, DESCRIPTION,description, /*LONG_DESCRIPTION,longDescription, META_TITLE,metaTitle, META_DESCRIPTION ,metaDescription , META_KEYWORDS,metaKeywords,*/ ACTIVE,active, DISCONTINUED,discontinued));

        }).map(row -> {
            String categoryId = row.get(metadataRow.indexOf(CATEGORY_ID.name())).toUpperCase();
            String name = row.get(metadataRow.indexOf(CATEGORY_NAME.name()));
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));
           /* String longDescription = row.get(metadataRow.indexOf(LONG_DESCRIPTION.name()));
            String metaTitle = row.get(metadataRow.indexOf(META_TITLE.name()));
            String metaDescription = row.get(metadataRow.indexOf(META_DESCRIPTION.name()));
            String metaKeyWords = row.get(metadataRow.indexOf(META_KEYWORDS.name()));*/
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();


            modifiedCategoryData.add(row);
            Category existingCategory = categoryLookupMap.get(row.get(metadataRow.indexOf(ID.name())));

            existingCategory.setCategoryId(categoryId);
            existingCategory.setCategoryName(name);
            existingCategory.setDescription(description);
           /* existingCategory.setLongDescription(longDescription);
            existingCategory.setMetaTitle(metaTitle);
            existingCategory.setMetaDescription(metaDescription);
            existingCategory.setMetaKeywords(metaKeyWords);*/
            existingCategory.setActive(active);
            existingCategory.setDiscontinued(discontinued);
            return existingCategory;

        }).collect(Collectors.toList());

        data.removeAll(modifiedCategoryData);


        newCategory.forEach(category -> {
            Map<String, Pair<String, Object>> validationResult = categoryService.validate(category, new HashMap<>(), Category.CreateGroup.class);
            if(!isValid(validationResult)) {

                LOGGER.error("ERROR--newCategory--->"+validationResult.values());
            }
            else{
                category.setCreatedDateTime(LocalDateTime.now());
                category.setCreatedUser(categoryService.getCurrentUser());
                newSavableCategory.add(category);
            }
        });


        modifiedCategory.forEach(category -> {
            Map<String, Pair<String, Object>> validationResult = categoryService.validate(category, CollectionsUtil.toMap("id", category.getCategoryId()), Category.DetailsGroup.class);
            if(!isValid(validationResult)) {
                LOGGER.error("ERROR--modifiedCategory-->"+validationResult.values());

            }else{
                category.setLastModifiedDateTime(LocalDateTime.now());
                category.setLastModifiedUser(categoryService.getCurrentUser());
                modifiedSavableCategory.add(category);
            }
        });



        LOGGER.info("Category to process -------------->"+  (data.size() - 1));


        categoryService.create(newSavableCategory);
        categoryService.update(modifiedSavableCategory);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        LOGGER.error("ERROR----->"+validationResult.values());
        return isEmpty(validationResult);
    }

    }
