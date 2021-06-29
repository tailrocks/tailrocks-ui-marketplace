/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.tailrocks.marketplace.api.model.Image;
import com.tailrocks.marketplace.grpc.v1.catalog.section.Icon;
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput;
import org.jooq.JSONB;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import javax.inject.Inject;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

/**
 * @author Alexey Zhokhov
 */
@Mapper(
        componentModel = "jsr330",
        injectionStrategy = CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public class ImageMapper {

    private ObjectMapper objectMapper;

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JSONB toJSONB(IconInput iconInput) {
        if (iconInput == null) {
            return null;
        }
        if (!iconInput.hasUrl()) {
            return null;
        }
        var image = new Image(
                iconInput.getUrl().getValue(), iconInput.getWidth().getValue(), iconInput.getHeight().getValue()
        );
        try {
            var json = objectMapper.writeValueAsString(image);
            return JSONB.jsonb(json);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

    public Icon toIcon(JSONB jsonb) {
        if (jsonb == null) {
            return Icon.getDefaultInstance();
        }
        try {
            var image = objectMapper.readValue(jsonb.data(), Image.class);

            return Icon.newBuilder()
                    .setUrl(StringValue.of(image.getUrl()))
                    .setWidth(UInt32Value.of(image.getWidth()))
                    .setHeight(UInt32Value.of(image.getHeight()))
                    .build();
        } catch (JsonProcessingException ignored) {
            return Icon.getDefaultInstance();
        }
    }

}
