package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.Catalog;
import com.bigname.pim.core.domain.Category;
import com.bigname.pim.core.domain.RootCategory;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class CatalogRepositoryImpl extends GenericRepositoryImpl<Catalog, Criteria> implements CatalogRepository {

    public CatalogRepositoryImpl() {
        super(Catalog.class);
    }

    @SuppressWarnings("unchecked")
    public Page<Map<String, Object>> getRootCategories(String catalogId, Pageable pageable, boolean... activeRequired) {
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

        Criteria criteria = new Criteria();
        String[] activeOptions = PlatformUtil.getActiveOptions(activeRequired);
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

        boolean showDiscontinued = PlatformUtil.showDiscontinued(activeRequired);
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
                match(Criteria.where("catalogId").is(catalogId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(rootCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "rootCategory"),
                match(criteria),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        return PageableExecutionUtils.getPage(
                mongoTemplate.aggregate(aggregation, "rootCategory", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList()),
                pageable,
                () -> mongoTemplate.count(new Query().addCriteria(Criteria.where("catalogId").is(catalogId)), RootCategory.class));

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
        searchCriteria.andOperator(Criteria.where("active").in(Arrays.asList(PlatformUtil.getActiveOptions(activeRequired))));

        Aggregation aggregation = newAggregation(
                match(Criteria.where("catalogId").is(catalogId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(rootCategory, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "rootCategory"),
                match(searchCriteria)
        );

        //TODO - need to find an option to get the total count along with the paginated result, instead of running two separate aggregation queries
        long totalCount = mongoTemplate.aggregate(aggregation, "rootCategory", Map.class).getMappedResults().size();


        aggregation = newAggregation(
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

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public List<RootCategory> getAllRootCategories(String catalogId, boolean... activeRequired) {
        Query query = new Query();
        query.addCriteria(Criteria.where("catalogId").is(catalogId).andOperator(Criteria.where("active").in(Arrays.asList(PlatformUtil.getActiveOptions(activeRequired)))));
        return mongoTemplate.find(query, RootCategory.class);
    }

    @Override
    public Page<Category> findAvailableRootCategoriesForCatalog(String catalogId, String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        List<String> excludeIds = getAllRootCategories(catalogId, false).stream().map(RootCategory::getRootCategoryId).collect(Collectors.toList());
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("_id").nin(excludeIds),Criteria.where("active").in(Arrays.asList(PlatformUtil.getActiveOptions(activeRequired))));
        query.addCriteria(criteria).with(pageable);
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Category.class),
                pageable,
                () -> mongoTemplate.count(query, Category.class));
    }
}
