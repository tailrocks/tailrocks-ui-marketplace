package com.tailrocks.marketplace.api.client.config;

import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub;
import com.tailrocks.marketplace.grpc.v1.component.ComponentServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.ComponentServiceGrpc.ComponentServiceBlockingStub;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionServiceGrpc.ComponentCollectionServiceBlockingStub;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantServiceGrpc;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantServiceGrpc.TenantServiceBlockingStub;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidatingClientInterceptor;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import static com.tailrocks.marketplace.api.client.config.Constants.GRPC_CHANNEL;
import static com.tailrocks.marketplace.api.client.config.Constants.TENANT_SERVICE_NAME;

/**
 * @author Alexey Zhokhov
 */
@Factory
public class Configuration {

    private final ValidatorIndex index = new ReflectiveValidatorIndex();

    @Singleton
    @Named(TENANT_SERVICE_NAME)
    public TenantServiceBlockingStub tenantServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return TenantServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

    @Singleton
    public CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return CatalogSectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

    @Singleton
    public ComponentCollectionServiceBlockingStub componentCollectionServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return ComponentCollectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

    @Singleton
    public ComponentServiceBlockingStub componentServiceBlockingStub(
            @GrpcChannel(GRPC_CHANNEL) ManagedChannel channel
    ) {
        return ComponentServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

}
