package com.bigname.pim.config;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"com.bigname.pim.api.persistence.dao.secondary"}, mongoTemplateRef = "secondaryMongoTemplate")
public class SecondaryMongoConfig {
    @Value("${spring.data.mongodb.secondary.uri}")
    private String primaryURI;

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() {
        return new MongoTemplate(secondaryFactory());
    }

    @Bean
    public MongoDbFactory secondaryFactory() {
        return new SimpleMongoDbFactory(new MongoClientURI(primaryURI));
    }
}
