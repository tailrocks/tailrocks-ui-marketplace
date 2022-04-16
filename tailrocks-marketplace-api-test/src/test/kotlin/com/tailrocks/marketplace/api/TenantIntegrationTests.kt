package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetry
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.GIVEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN
import com.zhokhov.jambalaya.tenancy.TenancyUtils.runWithTenant
import io.grpc.StatusRuntimeException
import io.kotest.matchers.booleans.shouldBeTrue
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.System.currentTimeMillis

/**
 * @author Alexey Zhokhov
 */
@MicronautTest(transactional = false)
@OpenTelemetry
class TenantIntegrationTests constructor(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) {

    @Test
    fun `tenant provisioning`() {
        val tenantName = "test${currentTimeMillis()}"

        GIVEN("tenant does not exist") {
            runWithTenant(tenantName) {
                assertThrows<StatusRuntimeException> {
                    tailrocksMarketplaceClient.findAllCatalogSection()
                }
            }
        }

        WHEN("provision a new tenant") {
            tailrocksMarketplaceClient.provisionTenant(tenantName)
        }

        THEN("returns empty list") {
            runWithTenant(tenantName) {
                val result = tailrocksMarketplaceClient.findAllCatalogSection()

                result.isEmpty().shouldBeTrue()
            }
        }

        WHEN("drop just created tenant") {
            tailrocksMarketplaceClient.dropTenant(tenantName)
        }

        THEN("throws an error - tenant deleted") {
            runWithTenant(tenantName) {
                assertThrows<StatusRuntimeException> {
                    tailrocksMarketplaceClient.findAllCatalogSection()
                }
            }
        }
    }

}
