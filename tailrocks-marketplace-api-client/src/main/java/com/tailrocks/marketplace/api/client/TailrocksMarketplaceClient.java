/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.client;

import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionInput;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionListResponse;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionServiceGrpc;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CreateCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class TailrocksMarketplaceClient extends AbstractClient {

    private final CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub;

    public TailrocksMarketplaceClient(
            CatalogSectionServiceGrpc.CatalogSectionServiceBlockingStub catalogSectionServiceBlockingStub
    ) {
        this.catalogSectionServiceBlockingStub = catalogSectionServiceBlockingStub;
    }

    public Optional<CatalogSection> findCatalogSectionBySlug(String slug, String tenant) {
        return returnSingle(
                catalogSectionServiceBlockingStub
                        .withOption(TenantClientInterceptor.TENANT_OPTION, requireTenant(tenant))
                        .find(
                                FindCatalogSectionRequest.newBuilder()
                                        .addCriteria(FindCatalogSectionRequest.Criteria.newBuilder()
                                                .addSlug(slug)
                                                .build())
                                        .build()
                        )
        );
    }

    public List<CatalogSection> findAll(String tenant) {
        return catalogSectionServiceBlockingStub
                .withOption(TenantClientInterceptor.TENANT_OPTION, requireTenant(tenant))
                .find(
                        FindCatalogSectionRequest.newBuilder()
                                .setSort(FindCatalogSectionRequest.Sort.SORT_ORDER_ASC)
                                .build()
                )
                .getItemList();
    }

    public CatalogSection createCatalogSection(
            String slug, String name, String description, Integer sortOrder, IconInput icon, String tenant
    ) {
        CatalogSectionInput.Builder inputBuilder = CatalogSectionInput.newBuilder()
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

        return catalogSectionServiceBlockingStub
                .withOption(TenantClientInterceptor.TENANT_OPTION, requireTenant(tenant))
                .create(
                        CreateCatalogSectionRequest.newBuilder()
                                .addItem(inputBuilder.build())
                                .build()
                )
                .getItem(0);
    }

    private CatalogSection mustReturnSingle(CatalogSectionListResponse response) {
        if (response.getItemCount() != 1) {
            throw new RuntimeException("Incorrect item count. Muse be 1 not " + response.getItemCount());
        }
        return response.getItem(0);
    }

    private Optional<CatalogSection> returnSingle(CatalogSectionListResponse response) {
        if (response.getItemCount() > 0) {
            return Optional.of(mustReturnSingle(response));
        } else {
            return Optional.empty();
        }
    }

}
