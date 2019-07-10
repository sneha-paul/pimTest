package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.CategoryProduct;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.RelatedCategory;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CategoryRepositoryImpl extends GenericRepositoryImpl<Category, Criteria> implements CategoryRepository {
    public CategoryRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Category.class);
    }

    @Override
    public Page<Map<String, Object>> getSubCategories(String categoryId, Pageable pageable, boolean... activeRequired) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("category")
                .localField("subCategoryId")
                .foreignField("_id")
                .as("subCategory");

        Criteria criteria = new Criteria();
        String[] activeOptions = PimUtil.getActiveOptions(activeRequired);
        Criteria activeCriteria = new Criteria();
        if(activeOptions.length == 2) {
            activeCriteria = Criteria.where("active").in(Arrays.asList(activeOptions));
        } else if(activeOptions.length == 0){
            activeCriteria = null;
            //activeCriteria = Criteria.where("active").nin(Arrays.asList(new String[]{"Y", "N"}));
        } else if(activeOptions.length == 1){
            if(activeOptions[0].equals("Y")) {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("Y"),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).gte(LocalDateTime.now()),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").is(null),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).gte(LocalDateTime.now())
                );
                // activeCriteria = Criteria.where("active").is("Y");
            } else {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("N"),
                        Criteria.where("activeFrom").ne(null).gt(LocalDateTime.now()),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).lt(LocalDateTime.now()),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).lt(LocalDateTime.now())
                );
                //activeCriteria = Criteria.where("active").is("N");
            }
        }

        boolean showDiscontinued = PimUtil.showDiscontinued(activeRequired);
        if(showDiscontinued) {
            //Discontinued
            // (start == null && end == null && disc = 'Y') or
            // (start != null && end != null && start <= now && end >= now) or
            // (start != null && end == null && start <= now) or
            // (start == null && end != null && end >= now)
            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("Y"),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).gte(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").is(null),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).gte(LocalDateTime.now())
            );
            if(activeCriteria != null){
                criteria.orOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.orOperator(discontinueCriteria);
            }

        } else {
            //Not discontinued
            // (start == null && end == null && disc != 'Y') or
            // (start != null && end != null && start > now && end < now) or
            // (start != null && end == null && start > now) or
            // (start == null && end != null && end < now)

            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("N"),
                    Criteria.where("discontinuedFrom").ne(null).gt(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1)),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1))
            );
            if(activeCriteria != null) {
                criteria.andOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.andOperator(discontinueCriteria);
            }
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(subCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "subCategory"),
                match(criteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        return PageableExecutionUtils.getPage(
                mongoTemplate.aggregate(aggregation, "relatedCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList()),
                pageable,
                () -> mongoTemplate.count(new Query().addCriteria(Criteria.where("categoryId").is(categoryId)), RelatedCategory.class));

    }

    @Override
    public List<RelatedCategory> getAllSubCategories(String categoryId, boolean... activeRequired) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryId").is(categoryId).andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired)))));
        return mongoTemplate.find(query, RelatedCategory.class);
    }

    @Override
    public Page<Category> findAvailableSubCategoriesForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        List<String> excludeIds = getAllSubCategories(categoryId, false).stream().map(RelatedCategory::getCategoryId).collect(Collectors.toList());
        excludeIds.add(categoryId);
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("_id").nin(excludeIds),Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));
        query.addCriteria(criteria).with(pageable);
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Category.class),
                pageable,
                () -> mongoTemplate.count(query, Category.class));
    }

    @Override
    public Page<Map<String, Object>> findAllSubCategories(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("category")
                .localField("subCategoryId")
                .foreignField("_id")
                .as("subCategory");

        keyword = "(?i)" + keyword;
        Criteria searchCriteria = new Criteria();
        searchCriteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        searchCriteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));

        Aggregation aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(subCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "subCategory"),
                match(searchCriteria)
        );

        //TODO - need to find an option to get the total count along with the paginated result, instead of running two separate aggregation queries
        long totalCount = mongoTemplate.aggregate(aggregation, "relatedCategory", Map.class).getMappedResults().size();

        aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(subCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "subCategory"),
                match(searchCriteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );
        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "relatedCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<Map<String, Object>> getProducts(String categoryId, Pageable pageable, boolean... activeRequired) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("product")
                .localField("productId")
                .foreignField("_id")
                .as("product");

        Criteria criteria = new Criteria();
        String[] activeOptions = PimUtil.getActiveOptions(activeRequired);
        Criteria activeCriteria = new Criteria();
        if(activeOptions.length == 2) {
            activeCriteria = Criteria.where("active").in(Arrays.asList(activeOptions));
        } else if(activeOptions.length == 0){
            activeCriteria = null;
            //activeCriteria = Criteria.where("active").nin(Arrays.asList(new String[]{"Y", "N"}));
        } else if(activeOptions.length == 1){
            if(activeOptions[0].equals("Y")) {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("Y"),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).gte(LocalDateTime.now()),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").is(null),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).gte(LocalDateTime.now())
                );
                // activeCriteria = Criteria.where("active").is("Y");
            } else {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("N"),
                        Criteria.where("activeFrom").ne(null).gt(LocalDateTime.now()),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).lt(LocalDateTime.now()),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).lt(LocalDateTime.now())
                );
                //activeCriteria = Criteria.where("active").is("N");
            }
        }

        boolean showDiscontinued = PimUtil.showDiscontinued(activeRequired);
        if(showDiscontinued) {
            //Discontinued
            // (start == null && end == null && disc = 'Y') or
            // (start != null && end != null && start <= now && end >= now) or
            // (start != null && end == null && start <= now) or
            // (start == null && end != null && end >= now)
            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("Y"),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).gte(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").is(null),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).gte(LocalDateTime.now())
            );
            if(activeCriteria != null){
                criteria.orOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.orOperator(discontinueCriteria);
            }

        } else {
            //Not discontinued
            // (start == null && end == null && disc != 'Y') or
            // (start != null && end != null && start > now && end < now) or
            // (start != null && end == null && start > now) or
            // (start == null && end != null && end < now)

            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("N"),
                    Criteria.where("discontinuedFrom").ne(null).gt(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1)),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1))
            );
            if(activeCriteria != null) {
                criteria.andOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.andOperator(discontinueCriteria);
            }
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(product, 0)")).mergeWith(ROOT)),
                project().andExclude("scopedFamilyAttributes", "product"),
                match(criteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        return PageableExecutionUtils.getPage(
                mongoTemplate.aggregate(aggregation, "categoryProduct", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList()),
                pageable,
                () -> mongoTemplate.count(new Query().addCriteria(Criteria.where("categoryId").is(categoryId)), CategoryProduct.class));

    }

    @Override
    public Page<Map<String, Object>> findAllCategoryProducts(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("product")
                .localField("productId")
                .foreignField("_id")
                .as("categoryProduct");

        keyword = "(?i)" + keyword;
        Criteria searchCriteria = new Criteria();
        searchCriteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        searchCriteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));

        Aggregation aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(categoryProduct, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "categoryProduct"),
                match(searchCriteria)
        );

        //TODO - need to find an option to get the total count along with the paginated result, instead of running two separate aggregation queries
        long totalCount = mongoTemplate.aggregate(aggregation, "categoryProduct", Map.class).getMappedResults().size();

        aggregation = newAggregation(
                match(Criteria.where("categoryId").is(categoryId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(categoryProduct, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "categoryProduct"),
                match(searchCriteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "categoryProduct", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public List<CategoryProduct> getAllCategoryProducts(String categoryId, boolean... activeRequired) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryId").is(categoryId).andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired)))));
        return mongoTemplate.find(query, CategoryProduct.class);
    }

    @Override
    public Page<Product> findAvailableProductsForCategory(String categoryId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        List<String> excludeIds = getAllCategoryProducts(categoryId, false).stream().map(CategoryProduct::getProductId).collect(Collectors.toList());
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("_id").nin(excludeIds),Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));
        query.addCriteria(criteria).with(pageable);
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Product.class),
                pageable,
                () -> mongoTemplate.count(query, Product.class));
    }
}
