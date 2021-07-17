package com.tailrocks.marketplace.api.repository;

import com.zhokhov.jambalaya.tenancy.jooq.AbstractTenantRepository;
import io.micronaut.context.annotation.Property;
import org.jooq.DSLContext;

import javax.inject.Singleton;

/**
 * @author Alexey Zhokhov
 */
@Singleton
public class ComponentRepository extends AbstractTenantRepository {

    public ComponentRepository(
            @Property(name = "micronaut.application.name") String applicationName,
            DSLContext dslContext
    ) {
        super(applicationName, dslContext);
    }

}
