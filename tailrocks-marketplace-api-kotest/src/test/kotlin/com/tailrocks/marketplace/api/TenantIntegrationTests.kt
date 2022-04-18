package com.tailrocks.marketplace.api

import com.tailrocks.jambalaya.tenancy.TenancyUtils.runWithTenant
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import io.grpc.StatusRuntimeException
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
class TenantIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) : ShouldSpec({

    context("tenant provisioning") {
        context("tenant does not exist") {
            val tenantName = "test${System.currentTimeMillis()}"

            runWithTenant(tenantName) {
                assertThrows<StatusRuntimeException> {
                    tailrocksMarketplaceClient.findAllCatalogSection()
                }
            }

            context("provision a new tenant") {
                tailrocksMarketplaceClient.provisionTenant(tenantName)

                should("returns empty list") {
                    runWithTenant(tenantName) {
                        val result = tailrocksMarketplaceClient.findAllCatalogSection()

                        result.isEmpty().shouldBeTrue()
                    }
                }

                context("drop just created tenant") {
                    tailrocksMarketplaceClient.dropTenant(tenantName)

                    should("throws an error - tenant deleted") {
                        runWithTenant(tenantName) {
                            assertThrows<StatusRuntimeException> {
                                tailrocksMarketplaceClient.findAllCatalogSection()
                            }
                        }
                    }
                }
            }
        }
    }

})
