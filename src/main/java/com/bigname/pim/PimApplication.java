package com.bigname.pim;

import com.bigname.pim.api.persistence.dao.BaseDAO;
import com.bigname.pim.config.SecurityConfig;
import com.bigname.pim.config.TilesConfig;
import com.bigname.pim.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.bigname.pim.api", "com.bigname.pim.client", "com.bigname.pim.data.loader"})
@EnableMongoRepositories(basePackages = {"com.bigname.pim.api.persistence"})
//@EnableCaching
@Import({
		WebConfig.class,
		TilesConfig.class,
		SecurityConfig.class
})
public class PimApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PimApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PimApplication.class, args);
	}
}
