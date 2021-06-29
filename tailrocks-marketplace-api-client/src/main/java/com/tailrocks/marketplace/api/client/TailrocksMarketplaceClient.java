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
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static com.zhokhov.jambalaya.tenancy.TenancyUtils.callWithTenant;

@Singleton
public class TailrocksMarketplaceClient {

    private final CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub;

    @Value("${tailrocks.client.marketplace.default-tenant:}")
    String defaultTenant;

    public TailrocksMarketplaceClient(
            CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub
    ) {
        this.catalogSectionServiceBlockingStub = catalogSectionServiceBlockingStub;
    }

    public Optional<CatalogSection> findCatalogSectionBySlug(@NonNull String slug) {
        return callWithTenant(defaultTenant, () -> catalogSectionServiceBlockingStub
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

    public List<CatalogSection> findAll() {
        return callWithTenant(defaultTenant, () -> catalogSectionServiceBlockingStub
                .find(
                        FindCatalogSectionRequest.newBuilder()
                                .setSort(FindCatalogSectionRequest.Sort.SORT_ORDER_ASC)
                                .build()
                )
                .getItemList()
        );
    }

    public CatalogSection createCatalogSection(
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

        return callWithTenant(defaultTenant, () -> catalogSectionServiceBlockingStub
                .create(
                        CreateCatalogSectionRequest.newBuilder()
                                .addItem(inputBuilder.build())
                                .build()
                )
                .getItem(0)
        );
    }

}
