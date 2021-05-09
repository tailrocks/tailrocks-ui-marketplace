/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.repository;

import com.tailrocks.marketplace.api.mapper.CatalogSectionMapper;
import com.tailrocks.marketplace.api.tenant.Tenant;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionInput;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import com.tailrocks.marketplace.jooq.tables.records.CatalogSectionRecord;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.transaction.annotation.ReadOnly;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;

import static com.tailrocks.marketplace.jooq.tables.CatalogSection.CATALOG_SECTION;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;
import static org.jooq.impl.DSL.noCondition;

@Singleton
public class CatalogSectionRepository extends AbstractRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogSectionRepository.class);

    private final CatalogSectionMapper catalogSectionMapper;

    public CatalogSectionRepository(
            DSLContext dslContext,
            CatalogSectionMapper catalogSectionMapper
    ) {
        super(dslContext);
        this.catalogSectionMapper = catalogSectionMapper;
    }

    @ReadOnly
    public List<CatalogSectionRecord> find(@NonNull Tenant tenant, @NonNull FindCatalogSectionRequest request) {
        checkNotNull(tenant, "tenant");
        checkNotNull(request, "request");

        return getDslContext(tenant)
                .selectFrom(CATALOG_SECTION)
                .where(generateFindCondition(request.getCriteriaList()))
                .fetch();
    }

    @ReadOnly
    public int getMaxSortOrder(@NonNull Tenant tenant) {
        return getDslContext(tenant)
                .select(DSL.max(CATALOG_SECTION.SORT_ORDER))
                .from(CATALOG_SECTION)
                .fetchOptional()
                .map(it -> {
                    if (it.value1() == null) {
                        return 0;
                    }
                    return it.value1() + 1;
                })
                .orElse(0);
    }

    @Transactional
    public CatalogSectionRecord create(
            @NonNull Tenant tenant,
            @NonNull CatalogSectionInput catalogSectionInput
    ) {
        checkNotNull(tenant, "tenant");
        checkNotNull(catalogSectionInput, "catalogSectionInput");

        CatalogSectionRecord item = catalogSectionMapper.toCatalogSectionRecord(
                catalogSectionInput,
                getDslContext(tenant).newRecord(CATALOG_SECTION)
        );

        if (!catalogSectionInput.hasSortOrder()) {
            item.setSortOrder(getMaxSortOrder(tenant));
        }

        item.store();

        LOG.info("Created {}", item.getId());

        return item;
    }

    @Transactional
    public void deleteAll(@NonNull Tenant tenant) {
        LOG.warn("Deleting all");

        int records = getDslContext(tenant)
                .delete(CATALOG_SECTION)
                .execute();

        LOG.info("Deleted {} records", records);
    }

    private Condition generateFindCondition(List<FindCatalogSectionRequest.Criteria> criteriaList) {
        Condition result = DSL.noCondition();

        for (FindCatalogSectionRequest.Criteria criteria : criteriaList) {
            result = result.or(generateCondition(criteria));
        }

        return result;
    }

    private Condition generateCondition(FindCatalogSectionRequest.Criteria criteria) {
        Condition result = noCondition();

        if (criteria.getSlugCount() > 0) {
            result = result.and(CATALOG_SECTION.SLUG.in(criteria.getSlugList()));
        }

        if (result.toString().equals(noCondition().toString())) {
            throw new RuntimeException("No any criteria added to the condition");
        }

        return result;
    }

}
