package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.api.mapper.ComponentCollectionMapper;
import com.tailrocks.marketplace.api.repository.ComponentCollectionRepository;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionListResponse;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.collection.CreateComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.FindComponentCollectionRequest;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentCollectionGrpcEndpoint extends ComponentCollectionServiceGrpc.ComponentCollectionServiceImplBase {

    private final ComponentCollectionRepository componentCollectionRepository;
    private final ComponentCollectionMapper componentCollectionMapper;

    public ComponentCollectionGrpcEndpoint(
            ComponentCollectionRepository componentCollectionRepository,
            ComponentCollectionMapper componentCollectionMapper
    ) {
        this.componentCollectionRepository = componentCollectionRepository;
        this.componentCollectionMapper = componentCollectionMapper;
    }

    @Override
    public void find(FindComponentCollectionRequest request,
                     StreamObserver<ComponentCollectionListResponse> responseObserver) {
        List<ComponentCollection> items = componentCollectionRepository.find(request).stream()
                .map(componentCollectionMapper::toCatalogSection)
                .collect(toList());

        responseObserver.onNext(ComponentCollectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateComponentCollectionRequest request,
                       StreamObserver<ComponentCollectionListResponse> responseObserver) {
        List<ComponentCollection> items = request.getItemList().stream()
                .map(componentCollectionRepository::create)
                .map(componentCollectionMapper::toCatalogSection)
                .collect(toList());

        responseObserver.onNext(ComponentCollectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

}
