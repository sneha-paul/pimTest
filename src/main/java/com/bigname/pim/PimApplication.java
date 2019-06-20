package com.bigname.pim;

import com.bigname.pim.config.*;
import com.m7.xtreme.xcore.XcoreApplication;
import com.m7.xtreme.xcore.config.CacheConfig;
import com.m7.xtreme.xcore.config.ConfigProperties;
import com.m7.xtreme.xcore.config.EmailConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication(scanBasePackages = {"com.m7.xtreme.xcore", "com.bigname.pim.api", "com.bigname.pim.client", "com.bigname.pim.data.loader", "com.bigname.pim.data.exportor"})
@EnableMongoRepositories(basePackages = {"com.m7.xtreme.xcore.persistence.mongo", "com.bigname.pim.api.persistence"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern={"com.m7.xtreme.xcore.persistence.mongo.dao.GenericDAO"})})
@EnableJpaRepositories(basePackages = {"com.m7.xtreme.xcore.persistence.jpa"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern={"com.m7.xtreme.xcore.persistence.jpa.dao.GenericDAO"})})
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableConfigurationProperties(ConfigProperties.class)
//@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
//@EnableLoadTimeWeaving
//
// -javaagent:C:\DevStudio\spring-instrument-5.0.8.RELEASE.jar
// -javaagent:C:\DevStudio\aspectjweaver-1.8.13.jar


@Import({
//		AppConfig.class,
		WebConfig.class,
		CacheConfig.class,
		TilesConfig.class,
		PIMSecurityConfig.class,
		EmailConfig.class
})
public class PimApplication extends XcoreApplication {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PimApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PimApplication.class, args);
	}
}
