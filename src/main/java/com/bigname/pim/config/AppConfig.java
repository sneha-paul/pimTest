package com.bigname.pim.config;

import org.springframework.context.annotation.EnableLoadTimeWeaving;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */

@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class AppConfig {
}
