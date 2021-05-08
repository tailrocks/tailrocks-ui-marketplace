/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.repository;

import com.tailrocks.marketplace.api.tenant.Tenant;
import org.jooq.DSLContext;

// TODO move to jambalaya
public abstract class AbstractRepository {

    private final DSLContext dslContext;

    protected AbstractRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    protected DSLContext getDslContext(Tenant tenant) {
        dslContext.setSchema(tenant.getSchema()).execute();
        return dslContext;
    }

}
