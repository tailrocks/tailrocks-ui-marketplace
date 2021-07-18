package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.api.mapper.ComponentMapper;
import com.tailrocks.marketplace.api.repository.ComponentRepository;
import com.tailrocks.marketplace.grpc.v1.component.Component;
import com.tailrocks.marketplace.grpc.v1.component.ComponentListResponse;
import com.tailrocks.marketplace.grpc.v1.component.ComponentServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.CreateComponentRequest;
import com.tailrocks.marketplace.grpc.v1.component.FindComponentRequest;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentGrpcEndpoint extends ComponentServiceGrpc.ComponentServiceImplBase {

    private final ComponentRepository componentRepository;
    private final ComponentMapper componentMapper;

    public ComponentGrpcEndpoint(ComponentRepository componentRepository,
                                 ComponentMapper componentMapper) {
        this.componentRepository = componentRepository;
        this.componentMapper = componentMapper;
    }

    @Override
    public void find(FindComponentRequest request,
                     StreamObserver<ComponentListResponse> responseObserver) {
        List<Component> items = componentRepository.find(request).stream()
                .map(componentMapper::toComponent)
                .collect(toList());

        responseObserver.onNext(ComponentListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateComponentRequest request,
                       StreamObserver<ComponentListResponse> responseObserver) {
        List<Component> items = request.getItemList().stream()
                .map(componentRepository::create)
                .map(componentMapper::toComponent)
                .collect(toList());

        responseObserver.onNext(ComponentListResponse.newBuilder()
                .addAllItem(items)
                .build());
        responseObserver.onCompleted();
    }

}
