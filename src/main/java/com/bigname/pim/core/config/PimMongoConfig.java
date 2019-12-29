package com.bigname.pim.core.config;

import com.m7.xtreme.xcore.config.BaseMongoConfig;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"com.m7.xtreme.xplatform.persistence.dao.primary.mongo", "com.bigname.pim.core.persistence.dao.mongo"}, mongoTemplateRef = "primaryMongoTemplate")
@Import({
//    SecondaryMongoConfig.class
})
public class PimMongoConfig extends BaseMongoConfig {

}
