package org.dci.woltjuniorsoftwareengineer.integration

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceResponse
import org.dci.woltjuniorsoftwareengineer.model.VenueStaticData
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.InputStream
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VenueStaticServiceIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    enum class VenueSlug(val slug: String, val latitude: Double, val longitude: Double) {
        HELSINKI("home-assignment-venue-helsinki", 24.928135119506578, 60.1701214337518),
        STOCKHOLM("home-assignment-venue-stockholm", 18.0314984, 59.3466978),
        BERLIN("home-assignment-venue-berlin", 13.4536149, 52.5003197)
    }

    @ParameterizedTest
    @MethodSource("venueProvider")
    fun `calculateDeliveryOrderPrice should return correct delivery response values`(venue: VenueSlug) {

        val url = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/${venue.slug}/static"

        val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body!!

        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(responseBody)
        val coordinatesNode = root.path("venue_raw").path("location").path("coordinates")
        val latitude = coordinatesNode[0].asDouble()
        val longitude = coordinatesNode[1].asDouble()

        assertEquals(venue.latitude, latitude)
        assertEquals(venue.longitude, longitude)
    }

    companion object {
        @JvmStatic
        fun venueProvider() = VenueSlug.entries
    }
}