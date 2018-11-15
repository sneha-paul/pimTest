package com.bigname.pim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.LoadTimeWeavingConfigurer;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */

@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class AppConfig {/*implements LoadTimeWeavingConfigurer {

    @Override
    public LoadTimeWeaver getLoadTimeWeaver() {
        return new ReflectiveLoadTimeWeaver();
    }*/
}
