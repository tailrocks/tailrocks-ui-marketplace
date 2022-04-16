package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.api.mapper.CatalogSectionMapper;
import com.tailrocks.marketplace.api.repository.CatalogSectionRepository;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionListResponse;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CreateCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class CatalogSectionGrpcEndpoint extends CatalogSectionServiceGrpc.CatalogSectionServiceImplBase {

    private final CatalogSectionRepository catalogSectionRepository;
    private final CatalogSectionMapper catalogSectionMapper;

    public CatalogSectionGrpcEndpoint(
            CatalogSectionRepository catalogSectionRepository,
            CatalogSectionMapper catalogSectionMapper
    ) {
        this.catalogSectionRepository = catalogSectionRepository;
        this.catalogSectionMapper = catalogSectionMapper;
    }

    @Override
    public void find(FindCatalogSectionRequest request,
                     StreamObserver<CatalogSectionListResponse> responseObserver) {
        List<CatalogSection> items = catalogSectionRepository.find(request).stream()
                .map(catalogSectionMapper::toCatalogSection)
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
                .map(catalogSectionRepository::create)
                .map(catalogSectionMapper::toCatalogSection)
                .collect(toList());

        responseObserver.onNext(CatalogSectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

}
