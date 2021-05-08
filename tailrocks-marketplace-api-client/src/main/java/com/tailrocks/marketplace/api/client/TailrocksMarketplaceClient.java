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
        return returnFirst(findCatalogSectionBySlugWithResponse(slug, tenant));
    }

    public CatalogSectionListResponse findCatalogSectionBySlugWithResponse(String slug, String tenant) {
        return catalogSectionServiceBlockingStub
                .withOption(TenantClientInterceptor.TENANT_OPTION, requireTenant(tenant))
                .find(
                        FindCatalogSectionRequest.newBuilder()
                                .addCriteria(FindCatalogSectionRequest.Criteria.newBuilder()
                                        .addSlug(slug)
                                        .build())
                                .build()
                );
    }

    public CatalogSection createCatalogSection(
            String slug, String name, String description, int sortOrder, IconInput icon, String tenant
    ) {
        return createPaymentMethodWithResponse(slug, name, description, sortOrder, icon, tenant).getItem(0);
    }

    public CatalogSectionListResponse createPaymentMethodWithResponse(
            String slug, String name, String description, int sortOrder, IconInput icon, String tenant
    ) {
        CatalogSectionInput.Builder inputBuilder = CatalogSectionInput.newBuilder()
                .setSlug(StringValue.of(slug))
                .setName(StringValue.of(name))
                .setSortOrder(UInt32Value.of(sortOrder));

        if (description != null) {
            inputBuilder.setDescription(StringValue.of(description));
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
                );
    }

    private Optional<CatalogSection> returnFirst(CatalogSectionListResponse response) {
        if (response.getItemCount() > 0) {
            return Optional.of(response.getItem(0));
        } else {
            return Optional.empty();
        }
    }

}
