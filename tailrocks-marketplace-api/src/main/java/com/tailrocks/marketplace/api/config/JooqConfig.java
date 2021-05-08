/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.jooq.conf.Settings;

import javax.inject.Named;
import javax.inject.Singleton;

@Factory
public class JooqConfig {

    @Bean
    @Singleton
    @Named("default")
    public Settings jooqSettings() {
        return new Settings()
                .withReturnAllOnUpdatableRecord(true)
                .withExecuteWithOptimisticLocking(true)
                .withRenderSchema(false);
    }

}
