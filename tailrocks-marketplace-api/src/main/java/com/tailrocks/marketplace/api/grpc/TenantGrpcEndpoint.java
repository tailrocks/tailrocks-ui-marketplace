package com.tailrocks.marketplace.api.grpc;

import com.zhokhov.jambalaya.grpc.v1.tenant.DropTenantRequest;
import com.zhokhov.jambalaya.grpc.v1.tenant.DropTenantResponse;
import com.zhokhov.jambalaya.grpc.v1.tenant.ProvisionTenantRequest;
import com.zhokhov.jambalaya.grpc.v1.tenant.ProvisionTenantResponse;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantServiceGrpc;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantStatus;
import io.grpc.stub.StreamObserver;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.jdbc.DriverDataSource;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class TenantGrpcEndpoint extends TenantServiceGrpc.TenantServiceImplBase {

    @Property(name = "datasources.default.url") String url;
    @Property(name = "datasources.default.username") String username;
    @Property(name = "datasources.default.password") String password;

    @Override
    public void provision(ProvisionTenantRequest request,
                          StreamObserver<ProvisionTenantResponse> responseObserver) {
        String datasourceUrl = url;

        if (datasourceUrl.startsWith("jdbc:otel:")) {
            datasourceUrl = datasourceUrl.replace("jdbc:otel:", "");
        }

        if (datasourceUrl.startsWith("jdbc:")) {
            datasourceUrl = datasourceUrl.replace("jdbc:", "");
        }

        var uri = URI.create(datasourceUrl);

        String query = (StringUtils.isEmpty(uri.getQuery()) ? "" : "&") + "currentSchema=" + request.getName().getValue();

        try {
            var modifiedUri = new URI(
                    uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query,
                    uri.getFragment()
            );

            DataSource dataSource = new DriverDataSource(
                    Thread.currentThread().getContextClassLoader(),
                    null,
                    "jdbc:" + modifiedUri,
                    username,
                    password
            );

            var fluentConfiguration = new FluentConfiguration()
                    .dataSource(dataSource);

            var flyway = fluentConfiguration.load();
            flyway.migrate();

            responseObserver.onNext(
                    ProvisionTenantResponse.newBuilder()
                            .setStatus(TenantStatus.TENANT_STATUS_CREATED)
                            .build()
            );
            responseObserver.onCompleted();
        } catch (URISyntaxException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void drop(DropTenantRequest request,
                     StreamObserver<DropTenantResponse> responseObserver) {
        String datasourceUrl = url;

        if (datasourceUrl.startsWith("jdbc:otel:")) {
            datasourceUrl = datasourceUrl.replace("jdbc:otel:", "jdbc:");
        }

        DataSource dataSource = new DriverDataSource(
                Thread.currentThread().getContextClassLoader(),
                null,
                datasourceUrl,
                username,
                password
        );

        try {
            try (var connection = dataSource.getConnection()) {
                String sql = "DROP SCHEMA " + request.getName().getValue() + " CASCADE;";

                try (var statement = connection.createStatement()) {
                    statement.execute(sql);

                    responseObserver.onNext(
                            DropTenantResponse.newBuilder()
                                    .setStatus(TenantStatus.TENANT_STATUS_DROPPED)
                                    .build()
                    );
                    responseObserver.onCompleted();
                }
            }
        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

}
