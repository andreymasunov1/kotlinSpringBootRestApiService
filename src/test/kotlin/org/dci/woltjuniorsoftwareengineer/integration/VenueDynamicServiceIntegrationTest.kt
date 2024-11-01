package org.dci.woltjuniorsoftwareengineer.integration

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dci.woltjuniorsoftwareengineer.model.DistanceRanges
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VenueDynamicServiceIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    enum class VenueSlug(val slug: String, val orderMinimumNoSurcharge: Double, val basePrice: Double, val distanceRangesNodeSize: Int) {
        HELSINKI("home-assignment-venue-helsinki", 1000.0, 190.0, 5),
        STOCKHOLM("home-assignment-venue-stockholm", 10000.0, 900.0, 5),
        BERLIN("home-assignment-venue-berlin", 1000.0, 190.0, 5)
    }

    @ParameterizedTest
    @MethodSource("venueProvider")
    fun `calculateDeliveryOrderPrice should return correct delivery response values`(venue: VenueSlug) {

        val url = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/${venue.slug}/dynamic"

        val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body!!

        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(responseBody)

        val orderMinimumNoSurcharge =
            root.path("venue_raw").path("delivery_specs").path("order_minimum_no_surcharge")
                .asDouble()
        val basePrice =
            root.path("venue_raw").path("delivery_specs").path("delivery_pricing")
                .path("base_price").asDouble()
        val distanceRangesNode =
            root.path("venue_raw").path("delivery_specs").path("delivery_pricing")
                .path("distance_ranges")

        assertEquals(venue.orderMinimumNoSurcharge, orderMinimumNoSurcharge)
        assertEquals(venue.basePrice, basePrice)
        assertEquals(venue.distanceRangesNodeSize, distanceRangesNode.size())
        assertNotNull(distanceRangesNode)
    }

    companion object {
        @JvmStatic
        fun venueProvider() = VenueSlug.entries
    }
}