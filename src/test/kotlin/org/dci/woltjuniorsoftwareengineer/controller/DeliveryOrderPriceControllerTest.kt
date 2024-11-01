package org.dci.woltjuniorsoftwareengineer.controller

import org.dci.woltjuniorsoftwareengineer.enums.VenueSlug
import org.dci.woltjuniorsoftwareengineer.model.*
import org.dci.woltjuniorsoftwareengineer.service.DeliveryOrderPriceService
import org.dci.woltjuniorsoftwareengineer.service.VenueDynamicService
import org.dci.woltjuniorsoftwareengineer.service.VenueStaticService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.ResponseEntity

class DeliveryOrderPriceControllerTest {
    private lateinit var deliveryOrderPriceService: DeliveryOrderPriceService
    private lateinit var venueStaticService: VenueStaticService
    private lateinit var venueDynamicService: VenueDynamicService
    private lateinit var deliveryOrderPriceController: DeliveryOrderPriceController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        deliveryOrderPriceService = Mockito.mock(DeliveryOrderPriceService::class.java)
        venueStaticService = Mockito.mock(VenueStaticService::class.java)
        venueDynamicService = Mockito.mock(VenueDynamicService::class.java)

        deliveryOrderPriceController = DeliveryOrderPriceController(
            deliveryOrderPriceService,
            venueStaticService,
            venueDynamicService
        )
    }

    @Test
    fun `test getDeliveryOrderPrice returns calculated price`() {
        val venueSlug = VenueSlug.HELSINKI.slug
        val cartValue = 100
        val userLat = 60.0
        val userLon = 24.0

        val deliveryOrderPriceRequest = DeliveryOrderPriceRequest(venueSlug, cartValue, userLat, userLon)
        val staticData = VenueStaticData(coordinates = Pair(37.7749, -122.4194))
        val dynamicData = VenueDynamicData(150.0, 3.0, emptyList()) // Replace with appropriate data
        val expectedPrice = DeliveryOrderPriceResponse(1190.0, 1000.0, 300, Delivery(1.0, 2000.2)) // Expected price from service calculation

        // Mocking the service responses
        Mockito.`when`(venueStaticService.getStaticData(venueSlug)).thenReturn(staticData)
        Mockito.`when`(venueDynamicService.getDynamicData(venueSlug)).thenReturn(dynamicData)
        Mockito.`when`(deliveryOrderPriceService.calculateDeliveryOrderPrice(deliveryOrderPriceRequest, staticData, dynamicData))
            .thenReturn(expectedPrice)

        val response: ResponseEntity<Any> = deliveryOrderPriceController.getDeliveryOrderPrice(
            venueSlug, cartValue, userLat, userLon
        )

        assertNotNull(response)
        assertEquals(200, response.statusCodeValue)
        assertEquals(expectedPrice, response.body)
    }

    @Test
    fun `test getDeliveryOrderPrice returns error for invalid venue slug`() {
        val invalidVenueSlug = "invalid-venue-slug"
        val cartValue = 100
        val userLat = 60.0
        val userLon = 24.0

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DeliveryOrderPriceRequest(invalidVenueSlug, cartValue, userLat, userLon)
        }

        assertTrue(exception.message!!.contains("Invalid venue slug"))

        // Now assert that the controller returns the correct error when invalid slug is used
        val httpException = assertThrows(IllegalArgumentException::class.java) {
            deliveryOrderPriceController.getDeliveryOrderPrice(
                invalidVenueSlug, cartValue, userLat, userLon
            )
        }

        // Validate that the controller throws the expected HTTP exception
        assertTrue(httpException.message!!.contains("Invalid venue slug"))
    }

    @Test
    fun `test getDeliveryOrderPrice returns error for invalid cart value`() {
        val invalidVenueSlug = VenueSlug.HELSINKI.slug
        val cartValue = -1
        val userLat = 60.0
        val userLon = 24.0

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DeliveryOrderPriceRequest(invalidVenueSlug, cartValue, userLat, userLon)
        }

        assertTrue(exception.message!!.contains("Cart value must be equal or greater than 0"))

        // Now assert that the controller returns the correct error when invalid slug is used
        val httpException = assertThrows(IllegalArgumentException::class.java) {
            deliveryOrderPriceController.getDeliveryOrderPrice(
                invalidVenueSlug, cartValue, userLat, userLon
            )
        }

        // Validate that the controller throws the expected HTTP exception
        assertTrue(httpException.message!!.contains("Cart value must be equal or greater than 0"))
    }

    @Test
    fun `test getDeliveryOrderPrice returns error for invalid user latitude`() {
        val invalidVenueSlug = VenueSlug.HELSINKI.slug
        val cartValue = 100
        val userLat = -90.1
        val userLon = 24.0

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DeliveryOrderPriceRequest(invalidVenueSlug, cartValue, userLat, userLon)
        }

        assertTrue(exception.message!!.contains("Latitude must be greater than or equal to -90 and less than or equal to 90"))

        // Now assert that the controller returns the correct error when invalid slug is used
        val httpException = assertThrows(IllegalArgumentException::class.java) {
            deliveryOrderPriceController.getDeliveryOrderPrice(
                invalidVenueSlug, cartValue, userLat, userLon
            )
        }

        // Validate that the controller throws the expected HTTP exception
        assertTrue(httpException.message!!.contains("Latitude must be greater than or equal to -90 and less than or equal to 90"))
    }

    @Test
    fun `test getDeliveryOrderPrice returns error for invalid user longitude`() {
        val invalidVenueSlug = VenueSlug.HELSINKI.slug
        val cartValue = 100
        val userLat = 30.2
        val userLon = 180.1

        val exception = assertThrows(IllegalArgumentException::class.java) {
            DeliveryOrderPriceRequest(invalidVenueSlug, cartValue, userLat, userLon)
        }

        assertTrue(exception.message!!.contains("Longitude must be greater than or equal to -180 and less than or equal to 180"))

        // Now assert that the controller returns the correct error when invalid slug is used
        val httpException = assertThrows(IllegalArgumentException::class.java) {
            deliveryOrderPriceController.getDeliveryOrderPrice(
                invalidVenueSlug, cartValue, userLat, userLon
            )
        }

        // Validate that the controller throws the expected HTTP exception
        assertTrue(httpException.message!!.contains("Longitude must be greater than or equal to -180 and less than or equal to 180"))
    }
}