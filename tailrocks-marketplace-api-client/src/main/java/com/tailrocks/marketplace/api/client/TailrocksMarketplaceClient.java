/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client;

import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionInput;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CreateCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionInput;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.component.collection.CreateComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.FindComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.UpdateComponentCollectionRequest;
import com.zhokhov.jambalaya.grpc.v1.tenant.DropTenantRequest;
import com.zhokhov.jambalaya.grpc.v1.tenant.ProvisionTenantRequest;
import com.zhokhov.jambalaya.grpc.v1.tenant.TenantServiceGrpc;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static com.tailrocks.marketplace.api.client.config.Constants.DEFAULT_TENANT;
import static com.tailrocks.marketplace.api.client.config.Constants.TENANT_SERVICE_NAME;
import static com.zhokhov.jambalaya.tenancy.TenancyUtils.callWithTenant;
import static com.zhokhov.jambalaya.tenancy.TenancyUtils.getTenantStringOrElse;

@Singleton
public class TailrocksMarketplaceClient {

    private final TenantServiceGrpc.TenantServiceBlockingStub tenantServiceBlockingStub;
    private final CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub;
    private final ComponentCollectionServiceGrpc.ComponentCollectionServiceBlockingStub componentCollectionServiceBlockingStub;

    @Property(name = DEFAULT_TENANT) String defaultTenant;

    @Inject
    public TailrocksMarketplaceClient(
            @Named(TENANT_SERVICE_NAME) TenantServiceGrpc.TenantServiceBlockingStub tenantServiceBlockingStub,
            CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub,
            ComponentCollectionServiceGrpc.ComponentCollectionServiceBlockingStub componentCollectionServiceBlockingStub
    ) {
        this.tenantServiceBlockingStub = tenantServiceBlockingStub;
        this.catalogSectionServiceBlockingStub = catalogSectionServiceBlockingStub;
        this.componentCollectionServiceBlockingStub = componentCollectionServiceBlockingStub;
    }

    public void provisionTenant(@NonNull String name) {
        tenantServiceBlockingStub.provision(ProvisionTenantRequest.newBuilder()
                .setName(StringValue.of(name))
                .build());
    }

    public void dropTenant(@NonNull String name) {
        tenantServiceBlockingStub.drop(DropTenantRequest.newBuilder()
                .setName(StringValue.of(name))
                .build());
    }

    public CatalogSection createCatalogSection(
            // TODO make slug auto-generated
            @NonNull String slug, @NonNull String name, @Nullable String description, @Nullable Integer sortOrder,
            @Nullable IconInput icon
    ) {
        var inputBuilder = CatalogSectionInput.newBuilder()
                .setSlug(StringValue.of(slug))
                .setName(StringValue.of(name));

        if (description != null) {
            inputBuilder.setDescription(StringValue.of(description));
        }

        if (sortOrder != null) {
            inputBuilder.setSortOrder(UInt32Value.of(sortOrder));
        }

        if (icon != null) {
            inputBuilder.setIcon(icon);
        }

        return callWithTenant(getTenantString(), () -> catalogSectionServiceBlockingStub
                .create(
                        CreateCatalogSectionRequest.newBuilder()
                                .addItem(inputBuilder.build())
                                .build()
                )
                .getItem(0)
        );
    }

    public Optional<CatalogSection> findCatalogSectionBySlug(@NonNull String slug) {
        return callWithTenant(getTenantString(), () -> catalogSectionServiceBlockingStub
                .find(
                        FindCatalogSectionRequest.newBuilder()
                                .addCriteria(FindCatalogSectionRequest.Criteria.newBuilder()
                                        .addSlug(slug)
                                        .build())
                                .build()
                )
                .getItemList().stream().findFirst()
        );
    }

    public List<CatalogSection> findAllCatalogSection() {
        return callWithTenant(getTenantString(), () -> catalogSectionServiceBlockingStub
                .find(
                        FindCatalogSectionRequest.newBuilder()
                                .setSort(FindCatalogSectionRequest.Sort.SORT_ORDER_ASC)
                                .build()
                )
                .getItemList()
        );
    }

    public ComponentCollection createComponentCollection(
            @NonNull String keycloakUserId, @NonNull String name, @Nullable String slug, @Nullable String description
    ) {
        var inputBuilder = ComponentCollectionInput.newBuilder()
                .setKeycloakUserId(StringValue.of(keycloakUserId))
                .setName(StringValue.of(name));

        if (slug != null) {
            inputBuilder.setSlug(StringValue.of(slug));
        }

        if (description != null) {
            inputBuilder.setDescription(StringValue.of(description));
        }

        return callWithTenant(getTenantString(), () -> componentCollectionServiceBlockingStub
                .create(CreateComponentCollectionRequest.newBuilder()
                        .addItem(inputBuilder.build())
                        .build())
                .getItem(0)
        );
    }

    public List<ComponentCollection> findAllComponentCollectionsByKeycloakUserId(@NonNull String keycloakUserId) {
        return callWithTenant(getTenantString(), () -> componentCollectionServiceBlockingStub
                .find(
                        FindComponentCollectionRequest.newBuilder()
                                .addCriteria(FindComponentCollectionRequest.Criteria.newBuilder()
                                        .addKeycloakUserId(keycloakUserId)
                                        .build())
                                .build()
                )
                .getItemList()
        );
    }

    public List<ComponentCollection> findComponentCollectionsByKeycloakUserIdAndSlug(
            @NonNull Iterable<KeycloakUserIdAndSlug> items
    ) {
        var builder = FindComponentCollectionRequest.newBuilder();

        items.forEach(it -> builder.addCriteria(FindComponentCollectionRequest.Criteria.newBuilder()
                .addKeycloakUserId(it.keycloakUserId())
                .addSlug(it.slug())
                .build()));

        return callWithTenant(getTenantString(), () -> componentCollectionServiceBlockingStub
                .find(builder.build())
                .getItemList()
        );
    }

    public Optional<ComponentCollection> findComponentCollectionById(@NonNull String id) {
        return callWithTenant(getTenantString(), () -> componentCollectionServiceBlockingStub
                .find(
                        FindComponentCollectionRequest.newBuilder()
                                .addCriteria(FindComponentCollectionRequest.Criteria.newBuilder()
                                        .addId(id)
                                        .build())
                                .build()
                )
                .getItemList().stream().findFirst()
        );
    }

    public Optional<ComponentCollection> findComponentCollectionByKeycloakUserIdAndSlug(@NonNull String keycloakUserId,
                                                                                        @NonNull String slug) {
        return findComponentCollectionsByKeycloakUserIdAndSlug(
                List.of(new KeycloakUserIdAndSlug(keycloakUserId, slug))
        )
                .stream()
                .findFirst();
    }

    public ComponentCollection updateComponentCollection(
            @NonNull String id, @Nullable String name, @Nullable String slug, @Nullable String description
    ) {
        var request = UpdateComponentCollectionRequest.newBuilder()
                .setId(StringValue.of(id));

        if (name != null) {
            request.setName(StringValue.of(name));
        }

        if (slug != null) {
            request.setSlug(StringValue.of(slug));
        }

        if (description != null) {
            request.setDescription(StringValue.of(description));
        }

        return callWithTenant(getTenantString(), () -> componentCollectionServiceBlockingStub
                .update(request.build())
                .getItem());
    }

    private String getTenantString() {
        return getTenantStringOrElse(defaultTenant);
    }

}
