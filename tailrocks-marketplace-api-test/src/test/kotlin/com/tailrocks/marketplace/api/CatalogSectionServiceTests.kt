/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api

import com.google.protobuf.StringValue
import com.google.protobuf.UInt32Value
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.api.repository.CatalogSectionRepository
import com.tailrocks.marketplace.api.tenant.Tenant
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class CatalogSectionServiceTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient,
    private val catalogSectionRepository: CatalogSectionRepository
) {

    @Nested
    inner class Create {

        // GIVEN:
        private val givenSlug = "myslug-${Date().time}"
        private val givenName = "My Name ${Date().time}"
        private val givenDescription = "Some description."
        private val givenSortOrder = 1
        private val givenIconUrl = "https://tailrocks.com/image/1"
        private val givenIconWidth = 200
        private val givenIconHeight = 150

        init {
            // WHEN:
            catalogSectionRepository.deleteAll(Tenant.TESTING)

            val item = tailrocksMarketplaceClient.createCatalogSection(
                givenSlug, givenName, givenDescription, givenSortOrder,
                IconInput.newBuilder()
                    .setUrl(StringValue.of(givenIconUrl))
                    .setWidth(UInt32Value.of(givenIconWidth))
                    .setHeight(UInt32Value.of(givenIconHeight))
                    .build(),
                null
            )

            // THEN:
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

        @Test
        fun `can not find unknown slug`() {
            // WHEN:
            val card = tailrocksMarketplaceClient.findCatalogSectionBySlug("unknown_slug", null)

            // THEN: an empty optional will be returned
            card.isEmpty.shouldBeTrue()
        }

        @Test
        fun `can find just created catalog section`() {
            // WHEN:
            val response = tailrocksMarketplaceClient.findCatalogSectionBySlug(givenSlug, null)

            // THEN: a one card will be returned
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

    @Test
    fun `create with only required values`() {
        // GIVEN:
        catalogSectionRepository.deleteAll(Tenant.TESTING)

        val givenSlug = "hero"
        val givenName = "Hero"

        // WHEN:
        val item = tailrocksMarketplaceClient.createCatalogSection(
            givenSlug, givenName, null, null, null, null
        )

        // THEN:
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

    @Test
    fun `find all`() {
        // GIVEN:
        catalogSectionRepository.deleteAll(Tenant.TESTING)

        val item1 = tailrocksMarketplaceClient.createCatalogSection(
            "hero", "Hero", null, 0, null, null
        )
        val item2 = tailrocksMarketplaceClient.createCatalogSection(
            "features", "Features", null, 1, null, null
        )
        val item3 = tailrocksMarketplaceClient.createCatalogSection(
            "testimonials", "Testimonials", null, null, null, null
        )

        // WHEN:
        val response = tailrocksMarketplaceClient.findAll(null)

        // THEN:
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
