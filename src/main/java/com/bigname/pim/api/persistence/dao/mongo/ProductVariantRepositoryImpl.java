package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.ProductVariant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductVariantRepositoryImpl extends GenericRepositoryImpl<ProductVariant, Criteria> implements ProductVariantRepository {
    private final String INDEX = "product1_index";
    private final String TYPE = "product1_type";
    private RestHighLevelClient restHighLevelClient;
    private ObjectMapper objectMapper;


    public ProductVariantRepositoryImpl(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        super(ProductVariant.class);
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
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

    @Override
    public void createVariantByElastic(ProductVariant productVariant) {

        Map dataMap = objectMapper.convertValue(productVariant, Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, productVariant.getId())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
            response.getId();
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
    }

    public void updateVariantByElastic(String id, ProductVariant productVariant){
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        Map<String, Object> error = new HashMap<>();
        error.put("Error", "Unable to update book");
        try {
            String bookJson = objectMapper.writeValueAsString(productVariant);
            updateRequest.doc(bookJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            Map<String, Object> sourceAsMap = updateResponse.getGetResult().sourceAsMap();
        }catch (JsonProcessingException e){
            e.getMessage();
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }

    }
}
