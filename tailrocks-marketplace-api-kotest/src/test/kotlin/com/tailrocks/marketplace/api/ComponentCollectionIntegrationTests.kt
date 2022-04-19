package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import java.lang.System.currentTimeMillis
import java.util.*

@MicronautTest
class ComponentCollectionIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) : ShouldSpec({

    context("create component collection") {
        val givenKeycloakUserId = UUID.randomUUID().toString()
        val givenSlug = "myslug-${currentTimeMillis()}"
        val givenName = "My Name ${currentTimeMillis()}"
        val givenDescription = "Some description."
        val givenComponentCollection = tailrocksMarketplaceClient.createComponentCollection(
            givenKeycloakUserId, givenName, givenSlug, givenDescription
        )

        should("create component collection with correct data") {
            givenComponentCollection.also {
                it.id shouldNot beNull()
                it.slug shouldBe givenSlug
                it.name shouldBe givenName
                it.description.value shouldBe givenDescription
                it.componentsCount.shouldBeZero()
                it.keycloakUserId shouldBe givenKeycloakUserId
            }
        }

        context("find by unknown id") {
            val givenId = UUID.randomUUID().toString()

            val item = tailrocksMarketplaceClient.findComponentCollectionById(givenId)

            should("return empty result") {
                item.isEmpty shouldBe true
            }
        }

        context("find by unknown user id") {
            val result = tailrocksMarketplaceClient
                .findAllComponentCollectionsByKeycloakUserId(UUID.randomUUID().toString())

            should("empty result will be returned") {
                result.isEmpty() shouldBe true
            }
        }

        context("can not find by unknown user id and slug") {
            val result = tailrocksMarketplaceClient.findComponentCollectionByKeycloakUserIdAndSlug(
                UUID.randomUUID().toString(), "abc"
            )

            should("empty result will be returned") {
                result.isEmpty shouldBe true
            }
        }

        context("can find by user id") {
            val result = tailrocksMarketplaceClient.findAllComponentCollectionsByKeycloakUserId(givenKeycloakUserId)

            should("return one item") {
                result.size shouldBe 1

                result[0].also {
                    it.id shouldNot beNull()
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.description.value shouldBe givenDescription
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }

        context("can find by id") {
            val item = tailrocksMarketplaceClient.findComponentCollectionById(givenComponentCollection.id)

            should("return correct item") {
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

        context("can find by user id and slug") {
            val item = tailrocksMarketplaceClient.findComponentCollectionByKeycloakUserIdAndSlug(
                givenKeycloakUserId, givenSlug
            )

            should("return correct item") {
                item.isPresent.shouldBeTrue()
                item.get().also {
                    it.id shouldNot beNull()
                    it.slug shouldBe givenSlug
                    it.name shouldBe givenName
                    it.description.value shouldBe givenDescription
                    it.componentsCount.shouldBeZero()
                    it.keycloakUserId shouldBe givenKeycloakUserId
                }
            }
        }

    }

    context("create with only required values") {
        val givenKeycloakUserId = UUID.randomUUID().toString()
        val givenName = "HeRo Super-Collection!!!"

        val item = tailrocksMarketplaceClient.createComponentCollection(
            givenKeycloakUserId, givenName, null, null
        )

        should("create with correct values") {
            item.also {
                it.id shouldNot beNull()
                it.slug shouldBe "hero-super-collection"
                it.name shouldBe givenName
                it.hasDescription() shouldBe false
                it.componentsCount.shouldBeZero()
                it.keycloakUserId shouldBe givenKeycloakUserId
            }
        }
    }

    context("update component collection") {
        val givenName = "Updated name"
        val givenSlug = "updated-slug"
        val givenDescription = "Updated description."

        val item = tailrocksMarketplaceClient.createComponentCollection(
            UUID.randomUUID().toString(), "Test", null, null
        )

        val updatedComponentCollection = tailrocksMarketplaceClient.updateComponentCollection(
            item.id, givenName, givenSlug, givenDescription
        )

        should("update to provided values") {
            updatedComponentCollection.id shouldBe item.id
            updatedComponentCollection.slug shouldBe givenSlug
            updatedComponentCollection.name shouldBe givenName
            updatedComponentCollection.description.value shouldBe givenDescription
            updatedComponentCollection.componentsCount.shouldBeZero()
            updatedComponentCollection.keycloakUserId shouldBe item.keycloakUserId
        }
    }

})
