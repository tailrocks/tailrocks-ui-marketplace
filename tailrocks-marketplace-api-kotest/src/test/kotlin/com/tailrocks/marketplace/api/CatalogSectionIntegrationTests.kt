package com.tailrocks.marketplace.api

import com.google.protobuf.StringValue
import com.google.protobuf.UInt32Value
import com.tailrocks.jambalaya.tenancy.TenancyUtils.callWithTenant
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import java.lang.System.currentTimeMillis

@MicronautTest(transactional = false)
class CatalogSectionIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) : ShouldSpec({

    context("create catalog section") {
        val givenSlug = "myslug-${currentTimeMillis()}"
        val givenName = "My Name ${currentTimeMillis()}"
        val givenDescription = "Some description."
        val givenSortOrder = 1
        val givenIconUrl = "https://tailrocks.com/image/1"
        val givenIconWidth = 200
        val givenIconHeight = 150

        val givenCatalogSection = tailrocksMarketplaceClient.createCatalogSection(
            givenSlug, givenName, givenDescription, givenSortOrder,
            IconInput.newBuilder()
                .setUrl(StringValue.of(givenIconUrl))
                .setWidth(UInt32Value.of(givenIconWidth))
                .setHeight(UInt32Value.of(givenIconHeight))
                .build()
        )

        should("created with correct data") {
            givenCatalogSection.apply {
                id shouldNot beNull()
                slug shouldBe givenSlug
                name shouldBe givenName
                icon.apply {
                    url.value shouldBe givenIconUrl
                    width.value shouldBe givenIconWidth
                    height.value shouldBe givenIconHeight
                }
                description.value shouldBe givenDescription
                sortOrder shouldBe givenSortOrder
            }
        }

        context("can not find unknown slug") {
            val card = tailrocksMarketplaceClient.findCatalogSectionBySlug("unknown_slug")

            should("an empty optional will be returned") {
                card.isEmpty shouldBe true
            }
        }

        context("can find just created catalog section") {
            val response = tailrocksMarketplaceClient.findCatalogSectionBySlug(givenSlug)

            should("a one card will be returned") {
                response.isPresent shouldBe true
                response.get().apply {
                    id shouldBe givenCatalogSection.id
                    slug shouldBe givenCatalogSection.slug
                    name shouldBe givenCatalogSection.name
                    icon.apply {
                        url.value shouldBe givenCatalogSection.icon.url.value
                        width.value shouldBe givenCatalogSection.icon.width.value
                        height.value shouldBe givenCatalogSection.icon.height.value
                    }
                    description.value shouldBe givenCatalogSection.description.value
                    sortOrder shouldBe givenCatalogSection.sortOrder
                }
            }
        }

    }

    context("create with only required values") {
        val tenantName = "test${currentTimeMillis()}"
        tailrocksMarketplaceClient.provisionTenant(tenantName)

        val givenSlug = "hero"
        val givenName = "Hero"

        val givenCatalogSection = callWithTenant(tenantName) {
            tailrocksMarketplaceClient.createCatalogSection(
                givenSlug, givenName, null, null, null
            )
        }

        should("created with correct values") {
            givenCatalogSection.apply {
                id shouldNot beNull()
                slug shouldBe givenSlug
                name shouldBe givenName
                icon.apply {
                    hasUrl().shouldBeFalse()
                    hasWidth().shouldBeFalse()
                    hasHeight().shouldBeFalse()
                }
                hasDescription().shouldBeFalse()
                sortOrder shouldBe 0
            }
        }
    }

    context("find all") {
        val tenantName = "test${currentTimeMillis()}"

        tailrocksMarketplaceClient.provisionTenant(tenantName)

        try {
            val item1 = callWithTenant(tenantName) {
                tailrocksMarketplaceClient.createCatalogSection(
                    "hero", "Hero", null, 0, null
                )
            }
            val item2 = callWithTenant(tenantName) {
                tailrocksMarketplaceClient.createCatalogSection(
                    "features", "Features", null, 1, null
                )
            }
            val item3 = callWithTenant(tenantName) {
                tailrocksMarketplaceClient.createCatalogSection(
                    "testimonials", "Testimonials", null, null, null
                )
            }

            val response = callWithTenant(tenantName) { tailrocksMarketplaceClient.findAllCatalogSection() }

            should("returns correct structure") {
                response.size shouldBe 3

                response[0].apply {
                    slug shouldBe item1.slug
                    sortOrder shouldBe item1.sortOrder
                }

                response[1].apply {
                    slug shouldBe item2.slug
                    sortOrder shouldBe item2.sortOrder
                }

                response[2].apply {
                    slug shouldBe item3.slug
                    sortOrder shouldBe item3.sortOrder
                }
            }
        } finally {
            tailrocksMarketplaceClient.dropTenant(tenantName)
        }
    }

})
