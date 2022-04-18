package com.tailrocks.marketplace.api.config;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Duration;

import static io.opentelemetry.semconv.resource.attributes.ResourceAttributes.SERVICE_NAME;

@Factory
public class OpenTelemetryConfig {

    @Bean
    @Singleton
    public SpanExporter otelSpanExporter() {
        return OtlpGrpcSpanExporter.builder()
                .setTimeout(Duration.ZERO)
                .build();
    }

    @Bean(preDestroy = "close")
    @Singleton
    public SpanProcessor otelSpanProcessor(SpanExporter spanExporter) {
        return BatchSpanProcessor.builder(spanExporter)
                .setScheduleDelay(Duration.ZERO)
                .setMaxExportBatchSize(1)
                .build();
    }

    @Inject
    @Bean
    @Singleton
    public SdkTracerProvider otelSdkTraceProvider(SpanProcessor spanProcessor, ApplicationContext applicationContext) {
        String applicationName = applicationContext.getProperty("micronaut.application.name", String.class)
                .orElse(io.micronaut.context.env.Environment.DEFAULT_NAME);

        return SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(Resource.getDefault().toBuilder().put(SERVICE_NAME, applicationName).build())
                .build();
    }

    @Bean
    @Context
    public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider) {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(
                        TextMapPropagator.composite(
                                W3CTraceContextPropagator.getInstance(),
                                W3CBaggagePropagator.getInstance()
                        )
                ))
                .buildAndRegisterGlobal();
    }

    @PreDestroy
    public void preDestroy() {
        GlobalOpenTelemetry.resetForTest();
    }

}
