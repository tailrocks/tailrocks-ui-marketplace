/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api

import com.google.protobuf.StringValue
import com.google.protobuf.UInt32Value
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.api.repository.CatalogSectionRepository
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryExtension
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.GIVEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN_
import com.zhokhov.jambalaya.tenancy.TenancyUtils.runWithTestingTenant
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@MicronautTest(transactional = false)
@ExtendWith(OpenTelemetryExtension::class)
class CatalogSectionServiceTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient,
    private val catalogSectionRepository: CatalogSectionRepository
) {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Create {

        // GIVEN:
        private val givenSlug = "myslug-${Date().time}"
        private val givenName = "My Name ${Date().time}"
        private val givenDescription = "Some description."
        private val givenSortOrder = 1
        private val givenIconUrl = "https://tailrocks.com/image/1"
        private val givenIconWidth = 200
        private val givenIconHeight = 150

        @BeforeEach
        fun init() {
            val item = WHEN_ {
                runWithTestingTenant {
                    catalogSectionRepository.deleteAll()
                }

                tailrocksMarketplaceClient.createCatalogSection(
                    givenSlug, givenName, givenDescription, givenSortOrder,
                    IconInput.newBuilder()
                        .setUrl(StringValue.of(givenIconUrl))
                        .setWidth(UInt32Value.of(givenIconWidth))
                        .setHeight(UInt32Value.of(givenIconHeight))
                        .build()
                )
            }

            THEN {
                item.also {
                    it.id shouldBeGreaterThan 0
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.icon.apply {
                        url.value shouldBe givenIconUrl
                        width.value shouldBe givenIconWidth
                        height.value shouldBe givenIconHeight
                    }
                    it.description.value shouldBe givenDescription
                    it.sortOrder shouldBe givenSortOrder
                }
            }
        }

        @Test
        fun `can not find unknown slug`() {
            val card = WHEN_ { tailrocksMarketplaceClient.findCatalogSectionBySlug("unknown_slug") }

            THEN("an empty optional will be returned") {
                card.isEmpty.shouldBeTrue()
            }
        }

        @Test
        fun `can find just created catalog section`() {
            val response = WHEN_ { tailrocksMarketplaceClient.findCatalogSectionBySlug(givenSlug) }

            THEN("a one card will be returned") {
                response.isPresent.shouldBeTrue()
                response.get().also {
                    it.id shouldBeGreaterThan 0
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.icon.apply {
                        url.value shouldBe givenIconUrl
                        width.value shouldBe givenIconWidth
                        height.value shouldBe givenIconHeight
                    }
                    it.description.value shouldBe givenDescription
                    it.sortOrder shouldBe givenSortOrder
                }
            }
        }

    }

    @Test
    fun `create with only required values`() {
        GIVEN {
            runWithTestingTenant {
                catalogSectionRepository.deleteAll()
            }
        }

        WHEN_ {
            val givenSlug = "hero"
            val givenName = "Hero"

            val item = tailrocksMarketplaceClient.createCatalogSection(
                givenSlug, givenName, null, null, null
            )

            THEN {
                item.also {
                    it.id shouldBeGreaterThan 0
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.icon.apply {
                        hasUrl().shouldBeFalse()
                        hasWidth().shouldBeFalse()
                        hasHeight().shouldBeFalse()
                    }
                    it.hasDescription().shouldBeFalse()
                    it.sortOrder shouldBe 0
                }
            }
        }
    }

    @Test
    fun `find all`() {
        GIVEN {
            runWithTestingTenant {
                catalogSectionRepository.deleteAll()
            }
        }

        WHEN_ {
            val item1 = tailrocksMarketplaceClient.createCatalogSection(
                "hero", "Hero", null, 0, null
            )
            val item2 = tailrocksMarketplaceClient.createCatalogSection(
                "features", "Features", null, 1, null
            )
            val item3 = tailrocksMarketplaceClient.createCatalogSection(
                "testimonials", "Testimonials", null, null, null
            )

            val response = tailrocksMarketplaceClient.findAll()

            THEN {
                response.size shouldBe 3

                response[0].also {
                    it.slug shouldBe item1.slug
                    it.sortOrder shouldBe item1.sortOrder
                }

                response[1].also {
                    it.slug shouldBe item2.slug
                    it.sortOrder shouldBe item2.sortOrder
                }

                response[2].also {
                    it.slug shouldBe item3.slug
                    it.sortOrder shouldBe item3.sortOrder
                }
            }
        }
    }

}
