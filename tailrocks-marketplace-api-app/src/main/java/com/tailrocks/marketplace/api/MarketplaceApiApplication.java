/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api;

import com.tailrocks.marketplace.jooq.tables.records.CatalogSectionRecord;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.TimeZone;

@TypeHint(
        value = {
                CatalogSectionRecord.class
        },
        accessType = {TypeHint.AccessType.ALL_DECLARED_CONSTRUCTORS}
)
public class MarketplaceApiApplication {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceApiApplication.class);

    static {
        log.debug("Setting UTC time zone by default");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    public static void main(String[] args) {
        Micronaut.run(MarketplaceApiApplication.class, args);
    }

}
