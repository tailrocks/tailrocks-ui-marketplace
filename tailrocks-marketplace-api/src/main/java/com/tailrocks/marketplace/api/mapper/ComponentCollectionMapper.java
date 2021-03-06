package com.tailrocks.marketplace.api.mapper;

import com.tailrocks.jambalaya.micronaut.mapstruct.protobuf.CommonConvertersMapper;
import com.tailrocks.jambalaya.micronaut.mapstruct.protobuf.ProtobufConvertersMapper;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection;
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollectionInput;
import com.tailrocks.marketplace.grpc.v1.component.collection.UpdateComponentCollectionRequest;
import com.tailrocks.marketplace.jooq.tables.records.ComponentCollectionRecord;
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
        // TODO use MappingConstants.ComponentModel.JSR330
        componentModel = "jsr330",
        injectionStrategy = CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                ProtobufConvertersMapper.class,
                CommonConvertersMapper.class
        }
)
public interface ComponentCollectionMapper {

    ComponentCollection toComponentCollection(ComponentCollectionRecord componentCollectionRecord);

    // ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "componentsCount", ignore = true)
    ComponentCollectionRecord toComponentCollectionRecord(
            ComponentCollectionInput componentCollectionInput,
            @MappingTarget ComponentCollectionRecord componentCollectionRecord
    );

    // ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "keycloakUserId", ignore = true)
    @Mapping(target = "componentsCount", ignore = true)
    @Mapping(target = "slug", ignore = true)
    ComponentCollectionRecord toComponentCollectionRecord(
            UpdateComponentCollectionRequest updateComponentCollectionRequest,
            @MappingTarget ComponentCollectionRecord componentCollectionRecord
    );

}
