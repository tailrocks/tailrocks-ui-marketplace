/*
 * Copyright 2021 Alexey Zhokhov
 */
package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetry
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN_
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.System.currentTimeMillis
import java.util.*

@MicronautTest(transactional = false)
@OpenTelemetry
class ComponentCollectionIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Create {

        // GIVEN:
        private var givenKeycloakUserId = UUID.randomUUID().toString()
        private val givenSlug = "myslug-${currentTimeMillis()}"
        private val givenName = "My Name ${currentTimeMillis()}"
        private val givenDescription = "Some description."

        @BeforeAll
        fun init() {
            val item = WHEN_ {
                tailrocksMarketplaceClient.createComponentCollection(
                    givenKeycloakUserId, givenName, givenSlug, givenDescription
                )
            }

            THEN {
                item.also {
                    it.id.shouldNotBeNull()
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.description.value shouldBe givenDescription
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }

        @Test
        fun `can not find by unknown user id`() {
            val givenKeycloakUserId = UUID.randomUUID().toString()

            val result = WHEN_ {
                tailrocksMarketplaceClient.findAllComponentCollectionsByKeycloakUserId(givenKeycloakUserId)
            }

            THEN("empty result will be returned") {
                result.isEmpty().shouldBeTrue()
            }
        }

        @Test
        fun `can not find by unknown user id and slug`() {
            val givenKeycloakUserId = UUID.randomUUID().toString()
            val givenSlug = "abc"

            val result = WHEN_ {
                tailrocksMarketplaceClient.findComponentCollectionByKeycloakUserIdAndSlug(
                    givenKeycloakUserId, givenSlug
                )
            }

            THEN("empty result will be returned") {
                result.isEmpty().shouldBeTrue()
            }
        }

        @Test
        fun `can find by user id`() {
            val result = WHEN_ {
                tailrocksMarketplaceClient.findAllComponentCollectionsByKeycloakUserId(givenKeycloakUserId)
            }

            THEN("empty result will be returned") {
                result.size shouldBe 1

                result[0].also {
                    it.id.shouldNotBeNull()
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.description.value shouldBe givenDescription
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }

        @Test
        fun `can find by user id and slug`() {
            val item = WHEN_ {
                tailrocksMarketplaceClient.findComponentCollectionByKeycloakUserIdAndSlug(
                    givenKeycloakUserId, givenSlug
                )
            }

            THEN("empty result will be returned") {
                item.isPresent.shouldBeTrue()
                item.get().also {
                    it.id.shouldNotBeNull()
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.description.value shouldBe givenDescription
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }

    }

}
