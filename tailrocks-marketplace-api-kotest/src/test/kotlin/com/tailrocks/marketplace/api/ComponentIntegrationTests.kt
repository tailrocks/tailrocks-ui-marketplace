package com.tailrocks.marketplace.api

import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import java.math.BigDecimal
import java.util.*

@MicronautTest(transactional = false)
class ComponentIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) : ShouldSpec({

    context("create component") {
        val givenPrice = BigDecimal("0.5")
        val givenSourceCodeHtml = "<div>Hello World</div>"
        val givenTitle = "Title"
        val givenDescription = "Description"
        val givenComponentCollection = tailrocksMarketplaceClient.createComponentCollection(
            UUID.randomUUID().toString(), "Test", null, null
        )
        val givenCatalogSection = tailrocksMarketplaceClient.createCatalogSection(
            null, "Hero ${System.currentTimeMillis()}", null, null, null
        )
        val givenComponent = tailrocksMarketplaceClient.createComponent(
            givenComponentCollection.id, givenCatalogSection.id, givenPrice, givenSourceCodeHtml,
            givenTitle, givenDescription
        )

        should("create component with correct data") {
            givenComponent.apply {
                id.shouldNotBeNull()
                componentCollectionId shouldBe givenComponentCollection.id
                catalogSectionId shouldBe givenCatalogSection.id
                title.value shouldBe givenTitle
                description.value shouldBe givenDescription
                sourceCodeHtml.value shouldBe givenSourceCodeHtml
                hasPrice().shouldBeTrue()
                price.currencyCode shouldBe "USD"
                price.nanos shouldBe 50
                sourceCodeHtml.value shouldBe givenSourceCodeHtml
            }
        }

        context("find component by unknown id") {
            val givenId = UUID.randomUUID().toString()

            val item = tailrocksMarketplaceClient.findComponentById(givenId)

            should("return empty result") {
                item.isEmpty.shouldBeTrue()
            }
        }
    }

})
