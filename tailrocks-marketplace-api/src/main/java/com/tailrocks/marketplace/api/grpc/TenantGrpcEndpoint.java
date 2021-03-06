package com.tailrocks.marketplace.api.grpc;

import com.tailrocks.jambalaya.grpc.v1.tenant.DropTenantRequest;
import com.tailrocks.jambalaya.grpc.v1.tenant.DropTenantResponse;
import com.tailrocks.jambalaya.grpc.v1.tenant.ProvisionTenantRequest;
import com.tailrocks.jambalaya.grpc.v1.tenant.ProvisionTenantResponse;
import com.tailrocks.jambalaya.grpc.v1.tenant.TenantServiceGrpc;
import com.tailrocks.jambalaya.grpc.v1.tenant.TenantStatus;
import com.tailrocks.jambalaya.tenancy.flyway.TenantFlywayMigrator;
import io.grpc.stub.StreamObserver;
import io.micronaut.context.annotation.Property;
import org.flywaydb.core.api.FlywayException;

import javax.inject.Singleton;
import java.sql.SQLException;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class TenantGrpcEndpoint extends TenantServiceGrpc.TenantServiceImplBase {

    private final TenantFlywayMigrator tenantFlywayMigrator;

    public TenantGrpcEndpoint(
            @Property(name = "datasources.default.url") String url,
            @Property(name = "datasources.default.username") String username,
            @Property(name = "datasources.default.password") String password
    ) {
        this.tenantFlywayMigrator = new TenantFlywayMigrator(url, username, password);
    }

    @Override
    public void provision(ProvisionTenantRequest request, StreamObserver<ProvisionTenantResponse> responseObserver) {
        try {
            tenantFlywayMigrator.migrateSchema(request.getName().getValue());

            responseObserver.onNext(
                    ProvisionTenantResponse.newBuilder()
                            .setStatus(TenantStatus.TENANT_STATUS_CREATED)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (FlywayException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void drop(DropTenantRequest request, StreamObserver<DropTenantResponse> responseObserver) {
        try {
            tenantFlywayMigrator.dropSchema(request.getName().getValue());

            responseObserver.onNext(
                    DropTenantResponse.newBuilder()
                            .setStatus(TenantStatus.TENANT_STATUS_DROPPED)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

}
