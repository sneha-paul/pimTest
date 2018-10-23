package com.bigname.pim.config;

import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by Manu on 8/3/2018.
 */
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.js/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.css/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.jpg/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.gif/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.png/**").addResourceLocations("/static/");
    }

    @Bean("cacheKeyGenerator")
    public CacheKeyGenerator cacheKeyGenerator() {
        return new CacheKeyGenerator();
    }

    @Bean(name = "validator")
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator(ChannelDAO dao) { //TODO - change with a generic app config DAO
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        /*Channel ecommerce = new Channel();
        ecommerce.setChannelName("Amazon");
        ecommerce.setChannelId("AMAZON");
        ecommerce.setActive("Y");
        dao.save(ecommerce);*/
        /*if(dao.countByIdNotNull() == 0) {
            factory.setResources(new Resource[]{new ClassPathResource("data.json")});
        }*/
        return factory;
    }
}
