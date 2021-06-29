package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryExtension
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.GIVEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN
import com.zhokhov.jambalaya.tenancy.TenancyUtils.runWithTenant
import io.grpc.StatusRuntimeException
import io.kotest.matchers.booleans.shouldBeTrue
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

/**
 * @author Alexey Zhokhov
 */
@MicronautTest(transactional = false)
@ExtendWith(OpenTelemetryExtension::class)
class TenantTests constructor(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) {

    @Test
    fun `tenant provisioning`() {
        val tenantName = "test${Date().time}"

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