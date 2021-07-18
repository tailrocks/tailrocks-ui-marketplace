/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.repository;

import com.tailrocks.marketplace.api.mapper.CatalogSectionMapper;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionInput;
import com.tailrocks.marketplace.grpc.v1.catalog.section.FindCatalogSectionRequest;
import com.tailrocks.marketplace.jooq.tables.records.CatalogSectionRecord;
import com.zhokhov.jambalaya.tenancy.jooq.AbstractTenantRepository;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.transaction.annotation.ReadOnly;
import org.bson.types.ObjectId;
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
import static com.zhokhov.jambalaya.seo.SlugUtils.generateSlug;
import static org.jooq.impl.DSL.noCondition;

@Singleton
public class CatalogSectionRepository extends AbstractTenantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogSectionRepository.class);

    private final CatalogSectionMapper catalogSectionMapper;

    public CatalogSectionRepository(
            @Property(name = "micronaut.application.name") String applicationName,
            DSLContext dslContext,
            CatalogSectionMapper catalogSectionMapper
    ) {
        super(applicationName, dslContext);
        this.catalogSectionMapper = catalogSectionMapper;
    }

    @ReadOnly
    public List<CatalogSectionRecord> find(@NonNull FindCatalogSectionRequest request) {
        checkNotNull(request, "request");

        return getDslContext()
                .selectFrom(CATALOG_SECTION)
                .where(generateFindCondition(request.getCriteriaList()))
                .fetch();
    }

    @ReadOnly
    public int getMaxSortOrder() {
        return getDslContext()
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
            @NonNull CatalogSectionInput catalogSectionInput
    ) {
        checkNotNull(catalogSectionInput, "catalogSectionInput");

        var item = catalogSectionMapper.toCatalogSectionRecord(
                catalogSectionInput,
                getDslContext().newRecord(CATALOG_SECTION)
        );

        if (!catalogSectionInput.hasSlug()) {
            item.setSlug(generateSlug(item.getName()));
        }

        if (!catalogSectionInput.hasSortOrder()) {
            item.setSortOrder(getMaxSortOrder());
        }

        item.setId(ObjectId.get().toHexString());
        item.store();

        LOG.info("Created {}", item.getId());

        return item;
    }

    @Transactional
    public void deleteAll() {
        LOG.warn("Deleting all");

        int records = getDslContext()
                .delete(CATALOG_SECTION)
                .execute();

        LOG.info("Deleted {} records", records);
    }

    private Condition generateFindCondition(List<FindCatalogSectionRequest.Criteria> criteriaList) {
        var result = DSL.noCondition();

        for (var criteria : criteriaList) {
            result = result.or(generateCondition(criteria));
        }

        return result;
    }

    private Condition generateCondition(FindCatalogSectionRequest.Criteria criteria) {
        var result = noCondition();

        if (criteria.getSlugCount() > 0) {
            result = result.and(CATALOG_SECTION.SLUG.in(criteria.getSlugList()));
        }

        if (result.toString().equals(noCondition().toString())) {
            throw new RuntimeException("No any criteria added to the condition");
        }

        return result;
    }

}
