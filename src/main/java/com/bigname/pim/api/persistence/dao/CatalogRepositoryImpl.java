package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class CatalogRepositoryImpl extends GenericRepositoryImpl<Catalog> implements CatalogRepository {

    public CatalogRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Catalog.class);
    }

    @SuppressWarnings("unchecked")
    public Page<Map<String, Object>> getRootCategories(String catalogId, Pageable pageable) {
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
                .localField("rootCategoryId")
                .foreignField("_id")
                .as("rootCategory");

        Aggregation aggregation = newAggregation(
                match(Criteria.where("catalogId").is(catalogId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(rootCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "rootCategory"),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "rootCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Page<Map<String, Object>> findAllRootCategories(String catalogId, String searchField, String keyword, Pageable pageable, boolean[] activeRequired) {

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
                .localField("rootCategoryId")
                .foreignField("_id")
                .as("rootCategory");

        keyword = "(?i)" + keyword;
        Criteria searchCriteria = new Criteria();
        searchCriteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        searchCriteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));

        Aggregation aggregation = newAggregation(
                match(Criteria.where("catalogId").is(catalogId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(rootCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "rootCategory"),
                match(searchCriteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "rootCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public List<RootCategory> getAllRootCategories(String catalogId, boolean... activeRequired) {
        Query query = new Query();
        query.addCriteria(Criteria.where("catalogId").is(catalogId).andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired)))));
        return mongoTemplate.find(query, RootCategory.class);
    }

    @Override
    public Page<Category> findAvailableRootCategoriesForCatalog(String catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        List<String> excludeIds = getAllRootCategories(catalogId, false).stream().map(RootCategory::getRootCategoryId).collect(Collectors.toList());
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("_id").nin(excludeIds),Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));
        query.addCriteria(criteria).with(pageable);
        List<Category> results = mongoTemplate.find(query, Category.class);
        return new PageImpl<>(results, pageable, results.size());
    }
}
