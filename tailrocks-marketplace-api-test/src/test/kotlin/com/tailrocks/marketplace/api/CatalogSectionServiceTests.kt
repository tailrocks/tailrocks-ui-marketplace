/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api

import com.google.protobuf.StringValue
import com.google.protobuf.UInt32Value
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.grpc.v1.catalog.section.IconInput
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class CatalogSectionServiceTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
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
            val response = tailrocksMarketplaceClient.createCatalogSectionWithResponse(
                givenSlug, givenName, givenDescription, givenSortOrder,
                IconInput.newBuilder()
                    .setUrl(StringValue.of(givenIconUrl))
                    .setWidth(UInt32Value.of(givenIconWidth))
                    .setHeight(UInt32Value.of(givenIconHeight))
                    .build(),
                null
            )

            // THEN:
            response.itemCount shouldBe 1
            response.getItem(0).also {
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
            val response = tailrocksMarketplaceClient.findCatalogSectionBySlugWithResponse(givenSlug, null)

            // THEN: a one card will be returned
            response.itemCount shouldBe 1
            response.getItem(0).also {
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
