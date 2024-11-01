package org.dci.woltjuniorsoftwareengineer.integration

import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DeliveryOrderPriceServiceIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Value("\${server.port}")
    private lateinit var serverPort: String

    @Test
    fun `calculateDeliveryOrderPrice should return correct delivery response values`() {

        val url = "http://localhost:$serverPort/api/v1/delivery-order-price?venue_slug=home-assignment-venue-helsinki&cart_value=1000&user_lat=60.17094&user_lon=24.93087"

        val response: ResponseEntity<DeliveryOrderPriceResponse> = restTemplate.getForEntity(url, DeliveryOrderPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body!!

        assertEquals(1190.0, responseBody.totalPrice)
        assertEquals(0.0, responseBody.smallOrderSurcharge)
        assertEquals(1000, responseBody.cartValue)
        assertEquals(190.0, responseBody.delivery.fee)
        assertEquals(177.0, responseBody.delivery.distance)
    }
}