package com.bigname.pim;

import com.bigname.pim.api.persistence.dao.BaseDAO;
import com.bigname.pim.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;

@SpringBootApplication(scanBasePackages = {"com.bigname.pim.api", "com.bigname.pim.client", "com.bigname.pim.data.loader"})
@EnableMongoRepositories(basePackages = {"com.bigname.pim.api.persistence"})
@EnableAspectJAutoProxy()
//@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
//@EnableLoadTimeWeaving
//
// -javaagent:C:\DevStudio\spring-instrument-5.0.8.RELEASE.jar
// -javaagent:C:\DevStudio\aspectjweaver-1.8.13.jar


@Import({
		AppConfig.class,
		WebConfig.class,
		CacheConfig.class,
		TilesConfig.class,
		SecurityConfig.class
})
public class PimApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PimApplication.class);
	}

	/*@Bean
	public InstrumentationLoadTimeWeaver loadTimeWeaver()  throws Throwable {
		InstrumentationLoadTimeWeaver loadTimeWeaver = new InstrumentationLoadTimeWeaver();
		return loadTimeWeaver;
	}*/

	public static void main(String[] args) {
		SpringApplication.run(PimApplication.class, args);
	}
}
