package com.tailrocks.marketplace.api.repository;

import com.tailrocks.marketplace.api.mapper.ComponentCollectionMapper;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionInput;
import com.tailrocks.marketplace.grpc.v1.component.collection.FindComponentCollectionRequest;
import com.tailrocks.marketplace.grpc.v1.component.collection.UpdateComponentCollectionRequest;
import com.tailrocks.marketplace.jooq.tables.records.ComponentCollectionRecord;
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
import java.util.Optional;

import static com.tailrocks.marketplace.jooq.Tables.COMPONENT_COLLECTION;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotBlank;
import static com.zhokhov.jambalaya.checks.Preconditions.checkNotNull;
import static com.zhokhov.jambalaya.seo.SlugUtils.generateSlug;
import static org.jooq.impl.DSL.noCondition;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentCollectionRepository extends AbstractTenantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentCollectionRepository.class);

    private final ComponentCollectionMapper componentCollectionMapper;

    public ComponentCollectionRepository(
            @Property(name = "micronaut.application.name") String applicationName,
            DSLContext dslContext,
            ComponentCollectionMapper componentCollectionMapper
    ) {
        super(applicationName, dslContext);
        this.componentCollectionMapper = componentCollectionMapper;
    }

    @ReadOnly
    public Optional<ComponentCollectionRecord> findById(@NonNull String id) {
        checkNotBlank(id, "id");

        return getDslContext()
                .selectFrom(COMPONENT_COLLECTION)
                .where(COMPONENT_COLLECTION.ID.eq(id))
                .fetchOptional();
    }

    @ReadOnly
    public List<ComponentCollectionRecord> find(@NonNull FindComponentCollectionRequest request) {
        checkNotNull(request, "request");

        return getDslContext()
                .selectFrom(COMPONENT_COLLECTION)
                .where(generateFindCondition(request.getCriteriaList()))
                .fetch();
    }

    @Transactional
    public ComponentCollectionRecord create(
            @NonNull ComponentCollectionInput componentCollectionInput
    ) {
        checkNotNull(componentCollectionInput, "componentCollectionInput");

        var item = componentCollectionMapper.toComponentCollectionRecord(
                componentCollectionInput,
                getDslContext().newRecord(COMPONENT_COLLECTION)
        );

        if (!componentCollectionInput.hasSlug()) {
            item.setSlug(generateSlug(item.getName()));
        }

        item.setId(ObjectId.get().toHexString());
        item.store();

        LOG.info("Created {}", item.getId());

        return item;
    }

    @Transactional
    public ComponentCollectionRecord update(@NonNull ComponentCollectionRecord accountRecord,
                                            @NonNull UpdateComponentCollectionRequest updateRequest) {
        checkNotNull(updateRequest, "updateRequest");

        var item = componentCollectionMapper.toComponentCollectionRecord(
                updateRequest, accountRecord
        );

        if (updateRequest.hasSlug()) {
            item.setSlug(generateSlug(updateRequest.getSlug().getValue()));
        }

        getDslContext().attach(item);

        item.store();

        LOG.info("Updated {}", item.getId());

        return item;
    }

    @Transactional
    public void deleteAll() {
        LOG.warn("Deleting all");

        int records = getDslContext()
                .delete(COMPONENT_COLLECTION)
                .execute();

        LOG.info("Deleted {} records", records);
    }

    private Condition generateFindCondition(List<FindComponentCollectionRequest.Criteria> criteriaList) {
        var result = DSL.noCondition();

        for (FindComponentCollectionRequest.Criteria criteria : criteriaList) {
            result = result.or(generateCondition(criteria));
        }

        return result;
    }

    private Condition generateCondition(FindComponentCollectionRequest.Criteria criteria) {
        var result = noCondition();

        if (criteria.getIdCount() > 0) {
            result = result.and(COMPONENT_COLLECTION.ID.in(criteria.getIdList()));
        }

        if (criteria.getKeycloakUserIdCount() > 0) {
            result = result.and(COMPONENT_COLLECTION.KEYCLOAK_USER_ID.in(criteria.getKeycloakUserIdList()));
        }

        if (criteria.getSlugCount() > 0) {
            result = result.and(COMPONENT_COLLECTION.SLUG.in(criteria.getSlugList()));
        }

        if (result.toString().equals(noCondition().toString())) {
            throw new RuntimeException("No any criteria added to the condition");
        }

        return result;
    }

}
