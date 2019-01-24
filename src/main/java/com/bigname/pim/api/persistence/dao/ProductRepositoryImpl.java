package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ProductCategory;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductRepositoryImpl extends GenericRepositoryImpl<Product> implements ProductRepository {

    public ProductRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Product.class);
    }

    @SuppressWarnings("unchecked")
    public Page<Map<String, Object>> getCategories(String productId, Pageable pageable) {
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
                .localField("categoryId")
                .foreignField("_id")
                .as("productCategory");

        Aggregation aggregation = newAggregation(
                match(Criteria.where("productId").is(productId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(productCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "productCategory"),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "productCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<Map<String, Object>> findAllProductCategories(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
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
                .localField("categoryId")
                .foreignField("_id")
                .as("productCategory");

        keyword = "(?i)" + keyword;
        Criteria searchCriteria = new Criteria();
        searchCriteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        searchCriteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));

        Aggregation aggregation = newAggregation(
                match(Criteria.where("productId").is(productId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(productCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "productCategory"),
                match(searchCriteria)
        );

        //TODO - need to find an option to get the total count along with the paginated result, instead of running two separate aggregation queries
        long totalCount = mongoTemplate.aggregate(aggregation, "productCategory", Map.class).getMappedResults().size();


        aggregation = newAggregation(
                match(Criteria.where("productId").is(productId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(productCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "productCategory"),
                match(searchCriteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "productCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public List<ProductCategory> getAllProductCategories(String productId, boolean... activeRequired) {
        Query query = new Query();
        query.addCriteria(Criteria.where("productId").is(productId).andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired)))));
        return mongoTemplate.find(query, ProductCategory.class);
    }

    @Override
    public Page<Category> findAvailableCategoriesForProduct(String productId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        List<String> excludeIds = getAllProductCategories(productId, false).stream().map(ProductCategory::getCategoryId).collect(Collectors.toList());
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
}
