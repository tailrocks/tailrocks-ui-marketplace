package com.tailrocks.marketplace.api.repository;

import com.tailrocks.jambalaya.tenancy.jooq.AbstractTenantRepository;
import com.tailrocks.marketplace.api.mapper.ComponentMapper;
import com.tailrocks.marketplace.grpc.v1.component.ComponentInput;
import com.tailrocks.marketplace.grpc.v1.component.FindComponentRequest;
import com.tailrocks.marketplace.jooq.tables.records.ComponentRecord;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.transaction.annotation.ReadOnly;
import org.bson.types.ObjectId;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;

import static com.tailrocks.jambalaya.checks.Preconditions.checkNotNull;
import static com.tailrocks.marketplace.jooq.tables.Component.COMPONENT;
import static org.jooq.impl.DSL.noCondition;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentRepository extends AbstractTenantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentRepository.class);

    private final ComponentMapper componentMapper;

    public ComponentRepository(
            @Property(name = "micronaut.application.name") String applicationName,
            DSLContext dslContext,
            ComponentMapper componentMapper
    ) {
        super(applicationName, dslContext);
        this.componentMapper = componentMapper;
    }

    @ReadOnly
    public List<ComponentRecord> find(@NonNull FindComponentRequest request) {
        checkNotNull(request, "request");

        return getDslContext()
                .selectFrom(COMPONENT)
                .where(generateFindCondition(request.getCriteriaList()))
                .fetch();
    }

    @Transactional
    public ComponentRecord create(
            @NonNull ComponentInput componentInput
    ) {
        checkNotNull(componentInput, "componentInput");

        var item = componentMapper.toComponentRecord(
                componentInput,
                getDslContext().newRecord(COMPONENT)
        );

        // TODO remove me
        item.setIcon(JSONB.valueOf(null));
        item.setRendering(JSONB.valueOf(null));
        // end TODO remove me

        item.setId(ObjectId.get().toHexString());
        item.store();

        LOG.info("Created {}", item.getId());

        return item;
    }

    private Condition generateFindCondition(List<FindComponentRequest.Criteria> criteriaList) {
        var result = DSL.noCondition();

        for (var criteria : criteriaList) {
            result = result.or(generateCondition(criteria));
        }

        return result;
    }

    private Condition generateCondition(FindComponentRequest.Criteria criteria) {
        var result = noCondition();

        if (criteria.getIdCount() > 0) {
            result = result.and(COMPONENT.ID.in(criteria.getIdList()));
        }

        if (criteria.getComponentCollectionIdCount() > 0) {
            result = result.and(COMPONENT.COMPONENT_COLLECTION_ID.in(criteria.getComponentCollectionIdList()));
        }

        if (criteria.getCatalogSectionIdCount() > 0) {
            result = result.and(COMPONENT.CATALOG_SECTION_ID.in(criteria.getCatalogSectionIdList()));
        }

        if (result.toString().equals(noCondition().toString())) {
            throw new RuntimeException("No any criteria added to the condition");
        }

        return result;
    }

}
