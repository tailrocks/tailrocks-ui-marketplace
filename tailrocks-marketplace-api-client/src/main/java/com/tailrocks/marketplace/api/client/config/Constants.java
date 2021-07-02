/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client.config;

/**
 * @author Alexey Zhokhov
 */
public final class Constants {

    public static final String TENANT_SERVICE_NAME = "marketplace";

    public static final String PREFIX = "tailrocks.client.marketplace";

    public static final String GRPC_CHANNEL = "${" + PREFIX + ".grpc-channel}";
    public static final String DEFAULT_TENANT = PREFIX + ".default-tenant";

    private Constants() {
    }

}
