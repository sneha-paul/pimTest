package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.ProductVariant;
import com.bigname.pim.util.Pageable;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Arrays;

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
}
