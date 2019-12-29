package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.ProductVariant;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
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
public class ProductVariantRepositoryImpl extends GenericRepositoryImpl<ProductVariant, Criteria> implements ProductVariantRepository {

    public ProductVariantRepositoryImpl() {
        super(ProductVariant.class);
    }

    @Override
    public Page<ProductVariant> findAll(String searchField, String keyword, String productId, String channelId, Pageable pageable, boolean... activeRequired) {
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("active").in(Arrays.asList(PlatformUtil.getActiveOptions(activeRequired))), Criteria.where("productId").regex(productId),Criteria.where("channelId").regex(channelId));
        query.addCriteria(criteria).with(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, ProductVariant.class),
                pageable,
                () -> mongoTemplate.count(query, ProductVariant.class));
    }


    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAll() {
        /*Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if (sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "productId").and(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }*/
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("product")
                .localField("productId")
                .foreignField("_id")
                .as("product");

        Aggregation aggregation = newAggregation(
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(product, 0)")).mergeWith(ROOT)),
                project().andExclude("product")
//                limit(100)
                //  sortOperation,
                //  limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "productVariant", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return results;
    }
}
