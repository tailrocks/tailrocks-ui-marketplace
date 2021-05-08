/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionListResponse;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CreateCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import com.tailrocks.marketplace.api.mapper.CatalogSectionMapper;
import com.tailrocks.marketplace.api.repository.CatalogSectionRepository;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;
import java.util.List;

import static com.tailrocks.marketplace.api.grpc.interceptor.TenantServerInterceptor.TENANT_ID;
import static java.util.stream.Collectors.toList;

@Singleton
public class CatalogSectionGrpcEndpoint extends CatalogSectionServiceGrpc.CatalogSectionServiceImplBase {

    private final CatalogSectionRepository paymentMethodRepository;
    private final CatalogSectionMapper paymentMethodMapper;

    public CatalogSectionGrpcEndpoint(
            CatalogSectionRepository paymentMethodRepository,
            CatalogSectionMapper paymentMethodMapper
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    @Override
    public void find(FindCatalogSectionRequest request,
                     StreamObserver<CatalogSectionListResponse> responseObserver) {
        List<CatalogSection> items = paymentMethodRepository.find(TENANT_ID.get(), request).stream()
                .map(paymentMethodMapper::toCatalogSection)
                .collect(toList());

        responseObserver.onNext(CatalogSectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateCatalogSectionRequest request,
                       StreamObserver<CatalogSectionListResponse> responseObserver) {
        List<CatalogSection> items = request.getItemList().stream()
                .map(it -> paymentMethodRepository.create(TENANT_ID.get(), it))
                .map(paymentMethodMapper::toCatalogSection)
                .collect(toList());

        responseObserver.onNext(CatalogSectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

}
