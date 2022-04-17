package com.tailrocks.marketplace.api

import com.tailrocks.jambalaya.junit.opentelemetry.OpenTelemetry
import com.tailrocks.jambalaya.junit.opentelemetry.OpenTelemetryUtils.THEN
import com.tailrocks.jambalaya.junit.opentelemetry.OpenTelemetryUtils.WHEN_
import com.tailrocks.marketplace.api.client.TailrocksMarketplaceClient
import com.tailrocks.marketplace.grpc.v1.catalog.section.CatalogSection
import com.tailrocks.marketplace.grpc.v1.component.Component
import com.tailrocks.marketplace.grpc.v1.component.collection.ComponentCollection
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.*

/**
 * @author Alexey Zhokhov
 */
@MicronautTest(transactional = false)
@OpenTelemetry
class ComponentIntegrationTests(
    private val tailrocksMarketplaceClient: TailrocksMarketplaceClient
) {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Create {

        // GIVEN:
        private val givenPrice = BigDecimal("0.5")
        private val givenSourceCodeHtml = "<div>Hello World</div>"
        private val givenTitle = "Title"
        private val givenDescription = "Description"
        private lateinit var givenComponentCollection: ComponentCollection
        private lateinit var givenCatalogSection: CatalogSection
        private lateinit var givenComponent: Component

        @BeforeAll
        fun init() {
            givenComponent = WHEN_ {
                givenComponentCollection = tailrocksMarketplaceClient.createComponentCollection(
                    UUID.randomUUID().toString(), "Test", null, null
                )

                givenCatalogSection = tailrocksMarketplaceClient.createCatalogSection(
                    null, "Hero ${System.currentTimeMillis()}", null, null, null
                )
                tailrocksMarketplaceClient.createComponent(
                    givenComponentCollection.id, givenCatalogSection.id, givenPrice, givenSourceCodeHtml,
                    givenTitle, givenDescription
                )
            }

            THEN {
                givenComponent.also {
                    it.id.shouldNotBeNull()
                    it.componentCollectionId shouldBe givenComponentCollection.id
                    it.catalogSectionId shouldBe givenCatalogSection.id
                    it.title.value shouldBe givenTitle
                    it.description.value shouldBe givenDescription
                    it.sourceCodeHtml.value shouldBe givenSourceCodeHtml
                    it.hasPrice().shouldBeTrue()
                    it.price.currencyCode shouldBe "USD"
                    it.price.nanos shouldBe 50
                    it.sourceCodeHtml.value shouldBe givenSourceCodeHtml
                }
            }
        }

        @Test
        fun `can not find by unknown id`() {
            val givenId = ObjectId().toHexString()

            val item = WHEN_ {
                tailrocksMarketplaceClient.findComponentById(givenId)
            }

            THEN("empty result will be returned") {
                item.isEmpty.shouldBeTrue()
            }
        }

    }

}
