package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.api.mapper.ComponentCollectionMapper;
import com.tailrocks.marketplace.api.repository.ComponentCollectionRepository;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionListResponse;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionResponse;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.collection.CreateComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.FindComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.UpdateComponentCollectionRequest;
import com.tailrocks.marketplace.jooq.tables.records.ComponentCollectionRecord;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static io.grpc.Status.INVALID_ARGUMENT;
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
                .map(componentCollectionMapper::toComponentCollection)
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
                .map(componentCollectionMapper::toComponentCollection)
                .collect(toList());

        responseObserver.onNext(ComponentCollectionListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void update(UpdateComponentCollectionRequest request,
                       StreamObserver<ComponentCollectionResponse> responseObserver) {
        Optional<ComponentCollectionRecord> accountRecord = componentCollectionRepository
                .findById(request.getId().getValue());

        if (accountRecord.isEmpty()) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription("Component Collection not found by ID: " + request.getId().getValue())
                    .asRuntimeException());
            return;
        }

        var updatedComponentCollection = componentCollectionRepository.update(accountRecord.get(), request);

        responseObserver.onNext(ComponentCollectionResponse.newBuilder()
                .setItem(componentCollectionMapper.toComponentCollection(updatedComponentCollection))
                .build());
        responseObserver.onCompleted();
    }

}
