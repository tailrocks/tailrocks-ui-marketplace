/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client;

import io.micronaut.core.util.StringUtils;

// TODO move to jambalaya
public abstract class AbstractClient {

    private String defaultTenant;

    public void setDefaultTenant(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    protected String requireTenant(String tenant) {
        if (tenant == null) {
            tenant = defaultTenant;
        }
        if (StringUtils.isEmpty(tenant)) {
            // TODO custom exception
            throw new RuntimeException("Tenant not set");
        }
        return tenant;
    }

}
