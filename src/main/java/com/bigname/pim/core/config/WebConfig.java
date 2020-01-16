package com.bigname.pim.core.config;

import com.bigname.pim.core.persistence.dao.mongo.ChannelDAO;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by Manu on 8/3/2018.
 */
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.file.path}")
    private String filePath;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:"+filePath);
        registry.addResourceHandler("/*.js/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.css/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.jpg/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.gif/**").addResourceLocations("/static/");
        registry.addResourceHandler("/*.png/**").addResourceLocations("/static/");
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("login").setViewName("login");
//        registry.addViewController("error").setViewName("error");
        registry.addViewController("register").setViewName("register");
        registry.addViewController("forgotPassword").setViewName("forgotPassword");
        registry.addViewController("/pim/dashboard").setViewName("dashboard");
        registry.addViewController("/").setViewName("redirect:/pim/dashboard");
    }

    /*@Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {@Override
        protected void postProcessContext(Context context) {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("CONFIDENTIAL");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
        }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }*/

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
        if(dao.countByIdNotNull() == 0) {
            factory.setResources(new Resource[]{new ClassPathResource("data.json")});
        }
        return factory;
    }


    @Bean
    public FormattingConversionService conversionService() {
        DefaultFormattingConversionService conversionService =
                new DefaultFormattingConversionService(false);
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
//        registrar.setDateFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        registrar.registerFormatters(conversionService);
        return conversionService;
    }

    /*@Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

        Properties exceptionMappings = new Properties();

        exceptionMappings.put("com.bigname.core.exception.EntityNotFoundException", "error");
        exceptionMappings.put("com.bigname.core.exception.GenericEntityException", "error");
        exceptionMappings.put("com.bigname.core.exception.GenericPlatformException", "error");
        exceptionMappings.put("com.bigname.core.exception.DuplicateEntityException", "error");
        exceptionMappings.put("com.bigname.core.exception.EntityCreateException", "error");
        exceptionMappings.put("com.bigname.core.exception.EntityUpdateException", "error");
        exceptionMappings.put("java.lang.Exception", "error");
        exceptionMappings.put("java.lang.RuntimeException", "error");

        exceptionResolver.setExceptionMappings(exceptionMappings);

        Properties statusCodes = new Properties();

        statusCodes.put("error/404", "404");
        statusCodes.put("error", "500");

        exceptionResolver.setStatusCodes(statusCodes);

        return exceptionResolver;
    }*/
}
