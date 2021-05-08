/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.tenant;

public enum Tenant {

    MAIN("public"),
    TESTING("test");

    private String schema;

    Tenant(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

}
