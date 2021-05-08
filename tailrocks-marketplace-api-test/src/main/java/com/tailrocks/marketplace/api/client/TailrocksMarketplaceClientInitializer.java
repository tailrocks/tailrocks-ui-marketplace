/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;

import javax.inject.Singleton;

@Singleton
public class TailrocksMarketplaceClientInitializer implements BeanCreatedEventListener<TailrocksMarketplaceClient> {

    @Override
    public TailrocksMarketplaceClient onCreated(BeanCreatedEvent<TailrocksMarketplaceClient> event) {
        event.getBean().setDefaultTenant("testing");
        return event.getBean();
    }

}
