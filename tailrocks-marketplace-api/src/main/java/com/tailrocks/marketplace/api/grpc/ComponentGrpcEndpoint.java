package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.marketplace.grpc.v1.component.ComponentListResponse;
import com.tailrocks.marketplace.grpc.v1.component.ComponentServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.CreateComponentRequest;
import com.tailrocks.marketplace.grpc.v1.component.FindComponentRequest;
import io.grpc.stub.StreamObserver;

import javax.inject.Singleton;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentGrpcEndpoint extends ComponentServiceGrpc.ComponentServiceImplBase {

    @Override
    public void find(FindComponentRequest request,
                     StreamObserver<ComponentListResponse> responseObserver) {

    }

    @Override
    public void create(CreateComponentRequest request,
                       StreamObserver<ComponentListResponse> responseObserver) {

    }
}
