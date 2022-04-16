package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetry
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.GIVEN_
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.zhokhov.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN_
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.bson.types.ObjectId
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
        private lateinit var givenComponentCollection: ComponentCollection

        @BeforeAll
        fun init() {
            givenComponentCollection = WHEN_ {
                tailrocksMarketplaceClient.createComponentCollection(
                    givenKeycloakUserId, givenName, givenSlug, givenDescription
                )
            }

            THEN {
                givenComponentCollection.also {
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
        fun `can not find by unknown id`() {
            val givenId = ObjectId().toHexString()

            val item = WHEN_ {
                tailrocksMarketplaceClient.findComponentCollectionById(givenId)
            }

            THEN("empty result will be returned") {
                item.isEmpty.shouldBeTrue()
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

            THEN {
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
        fun `can find by id`() {
            val item = WHEN_ {
                tailrocksMarketplaceClient.findComponentCollectionById(givenComponentCollection.id)
            }

            THEN {
                item.isPresent.shouldBeTrue()
                item.get().also {
                    it.id shouldBe givenComponentCollection.id
                    it.slug shouldBe givenComponentCollection.slug
                    it.name shouldBe givenComponentCollection.name
                    it.description.value shouldBe givenComponentCollection.description.value
                    it.componentsCount shouldBe givenComponentCollection.componentsCount
                    it.keycloakUserId shouldBe givenComponentCollection.keycloakUserId
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

            THEN {
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

    @Test
    fun `create with only required values`() {
        WHEN_ {
            val givenKeycloakUserId = UUID.randomUUID().toString()
            val givenName = "HeRo Super-Collection!!!"

            val item = tailrocksMarketplaceClient.createComponentCollection(
                givenKeycloakUserId, givenName, null, null
            )

            THEN {
                item.also {
                    it.id.shouldNotBeNull()
                    it.slug shouldBe "hero-super-collection"
                    it.name shouldBe givenName
                    it.hasDescription().shouldBeFalse()
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }
    }

    @Test
    fun `update component collection`() {
        val givenName = "Updated name"
        val givenSlug = "updated-slug"
        val givenDescription = "Updated description."

        val item = GIVEN_ {
            tailrocksMarketplaceClient.createComponentCollection(
                UUID.randomUUID().toString(), "Test", null, null
            )
        }

        val updatedComponentCollection = WHEN_ {
            tailrocksMarketplaceClient.updateComponentCollection(
                item.id, givenName, givenSlug, givenDescription
            )
        }

        THEN {
            updatedComponentCollection.id shouldBe item.id
            updatedComponentCollection.slug shouldBe givenSlug
            updatedComponentCollection.name shouldBe givenName
            updatedComponentCollection.description.value shouldBe givenDescription
            updatedComponentCollection.componentsCount.shouldBeZero()
            updatedComponentCollection.keycloakUserId shouldBe item.keycloakUserId
        }
    }

}
