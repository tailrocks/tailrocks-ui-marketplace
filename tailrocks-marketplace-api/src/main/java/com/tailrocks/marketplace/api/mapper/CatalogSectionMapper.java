package com.tailrocks.marketplace.api.mapper;

import com.tailrocks.jambalaya.micronaut.mapstruct.protobuf.ProtobufConvertersMapper;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection;
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSectionInput;
import com.tailrocks.marketplace.jooq.tables.records.CatalogSectionRecord;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(
        // TODO use MappingConstants.ComponentModel.JSR330
        componentModel = "jsr330",
        injectionStrategy = CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                ProtobufConvertersMapper.class,
                ImageMapper.class
        }
)
public interface CatalogSectionMapper {

    CatalogSection toCatalogSection(CatalogSectionRecord catalogSectionRecord);

    // ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    CatalogSectionRecord toCatalogSectionRecord(CatalogSectionInput catalogSectionInput,
                                                @MappingTarget CatalogSectionRecord catalogSectionRecord);

}
