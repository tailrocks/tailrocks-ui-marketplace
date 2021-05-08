/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.test;

import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidatorIndex;
import io.envoyproxy.pgv.grpc.ValidatingClientInterceptor;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;

@Factory
public class Clients {

    // Create a validator index that reflectively loads generated validators
    private final ValidatorIndex index = new ReflectiveValidatorIndex();

    @Bean
    public CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) ManagedChannel channel
    ) {
        return CatalogSectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(new ValidatingClientInterceptor(index));
    }

}
