package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.util.Pageable;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductVariantRepositoryImpl extends GenericRepositoryImpl<ProductVariant> implements ProductVariantRepository {

    public ProductVariantRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, ProductVariant.class);
    }

    @Override
    public Page<ProductVariant> findAll(String searchField, String keyword, String productId, String channelId, Pageable pageable, boolean... activeRequired) {
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))), Criteria.where("productId").regex(productId),Criteria.where("channelId").regex(channelId));
        query.addCriteria(criteria).with(PageRequest.of(pageable.getPage(), pageable.getSize(), pageable.getSort()));
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, ProductVariant.class),
                pageable.getPageRequest(),
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
                project().andExclude("product"),
                limit(100)
                //  sortOperation,
                //  limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "productVariant", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return results;
    }
}
