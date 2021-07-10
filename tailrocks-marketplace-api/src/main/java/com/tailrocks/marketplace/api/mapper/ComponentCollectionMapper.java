package com.tailrocks.marketplace.api.mapper;

import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionInput;
import com.tailrocks.marketplace.jooq.tables.records.ComponentCollectionRecord;
import com.zhokhov.jambalaya.micronaut.mapstruct.protobuf.ProtobufConvertersMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

/**
 * @author Alexey Zhokhov
 */
@Mapper(
        componentModel = "jsr330",
        injectionStrategy = CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                ProtobufConvertersMapper.class
        }
)
public interface ComponentCollectionMapper {

    ComponentCollection toCatalogSection(ComponentCollectionRecord componentCollectionRecord);

    // ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    ComponentCollectionRecord toComponentCollectionRecord(
            ComponentCollectionInput componentCollectionInput,
            @MappingTarget ComponentCollectionRecord componentCollectionRecord
    );

}
