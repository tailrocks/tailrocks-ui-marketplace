/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client.config;

import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantServiceGrpc;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidatingClientInterceptor;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.tailrocks.marketplace.api.client.config.Constants.GRPC_CHANNEL;
import static com.tailrocks.marketplace.api.client.config.Constants.TENANT_SERVICE_NAME;

/**
 * @author Alexey Zhokhov
 */
@Factory
public class Configuration {

    private final ValidatorIndex index = new ReflectiveValidatorIndex();

    @Singleton
    public CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return CatalogSectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

    @Singleton
    @Named(TENANT_SERVICE_NAME)
    public TenantServiceGrpc.TenantServiceBlockingStub tenantServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return TenantServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

}