package com.bigname.pim.core.config;

import com.m7.xtreme.xcore.config.BaseJpaConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@EnableJpaRepositories(basePackages = {"com.m7.xtreme.xplatform.persistence.dao.primary.jpa", "com.bigname.pim.core.persistence.dao.jpa"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern={"com.m7.xtreme.xcore.persistence.dao.jpa.GenericDAO"})})
public class PimJpaConfig extends BaseJpaConfig {

}
