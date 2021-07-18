package com.tailrocks.marketplace.api.mapper;

import com.google.type.Money;
import com.tailrocks.marketplace.grpc.v1.component.Component;
import com.tailrocks.marketplace.grpc.v1.component.ComponentInput;
import com.tailrocks.marketplace.jooq.tables.records.ComponentRecord;
import com.zhokhov.jambalaya.micronaut.mapstruct.protobuf.ProtobufConvertersMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

import static com.zhokhov.jambalaya.protobuf.ProtobufConverters.toBigDecimal;
import static com.zhokhov.jambalaya.protobuf.ProtobufConverters.toMoney;
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
                ProtobufConvertersMapper.class
        }
)
public interface ComponentMapper {

    Component toComponent(ComponentRecord componentRecord);

    // ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "icon", ignore = true)
    @Mapping(target = "rendering", ignore = true)
    // mapping
    @Mapping(target = "priceCurrency", constant = "USD")
    ComponentRecord toComponentRecord(
            ComponentInput componentInput,
            @MappingTarget ComponentRecord componentRecord
    );

    default BigDecimal map(Money value) {
        return toBigDecimal(value);
    }

    default Money map(BigDecimal value) {
        return toMoney(value, "USD");
    }

}
