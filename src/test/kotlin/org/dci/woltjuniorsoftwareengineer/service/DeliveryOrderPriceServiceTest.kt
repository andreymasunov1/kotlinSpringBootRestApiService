package org.dci.woltjuniorsoftwareengineer.service

import org.dci.woltjuniorsoftwareengineer.enums.VenueSlug
import org.dci.woltjuniorsoftwareengineer.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeliveryOrderPriceServiceTest {

    private val deliveryOrderPriceService = DeliveryOrderPriceService()

    @Test
    fun `calculateDeliveryOrderPrice should return correct response values for border values`() {
        // Given
        val request = DeliveryOrderPriceRequest(
            venueSlug = VenueSlug.STOCKHOLM.slug,
            cartValue = 0,
            userLat = 90.0,
            userLon = 180.0
        )

        val staticData = VenueStaticData(coordinates = Pair(179.99, 90.0))
        val dynamicData = VenueDynamicData(
            basePrice = 190.0,
            orderMinimumNoSurcharge = 1000.0,
            distanceRanges = listOf(
                DistanceRanges(min = 0.0, max = 500.0, a = 0, b = 0.0),
                DistanceRanges(min = 500.0, max = 1000.0, a = 100, b = 0.0),
                DistanceRanges(min = 1000.0, max = 1500.0, a = 200, b = 0.0),
                DistanceRanges(min = 1500.0, max = 2000.0, a = 200, b = 1.0),
                DistanceRanges(min = 2000.0, max = 0.0, a = 0, b = 0.0)
            )
        )

        // When
        val response = deliveryOrderPriceService.calculateDeliveryOrderPrice(request, staticData, dynamicData)

        // Then
        assertEquals(1190.0, response.totalPrice)
        assertEquals(request.cartValue, response.cartValue)
        assertEquals(Delivery(190.0, 0.0), response.delivery)
        assertEquals(dynamicData.orderMinimumNoSurcharge - request.cartValue, response.smallOrderSurcharge)
    }

    @Test
    fun `calculateDeliveryOrderPrice should return correct response values`() {
        // Given
        val request = DeliveryOrderPriceRequest(
            venueSlug = VenueSlug.HELSINKI.slug,
            cartValue = 1000,
            userLat = 60.17094,
            userLon = 24.93087
        )

        val staticData = VenueStaticData(coordinates = Pair(24.928135119506578, 60.1701214337518))
        val dynamicData = VenueDynamicData(
            basePrice = 190.0,
            orderMinimumNoSurcharge = 1000.0,
            distanceRanges = listOf(
                DistanceRanges(min = 0.0, max = 500.0, a = 0, b = 0.0),
                DistanceRanges(min = 500.0, max = 1000.0, a = 100, b = 0.0),
                DistanceRanges(min = 1000.0, max = 1500.0, a = 200, b = 0.0),
                DistanceRanges(min = 1500.0, max = 2000.0, a = 200, b = 1.0),
                DistanceRanges(min = 2000.0, max = 0.0, a = 0, b = 0.0)
            )
        )

        // When
        val response = deliveryOrderPriceService.calculateDeliveryOrderPrice(request, staticData, dynamicData)

        // Then
        assertEquals(1190.0, response.totalPrice)
        assertEquals(request.cartValue, response.cartValue)
        assertEquals(Delivery(190.0, 177.0), response.delivery)
        assertEquals(dynamicData.orderMinimumNoSurcharge - request.cartValue, response.smallOrderSurcharge)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid user longitude`() {
        // Given
        val invalidVenueSlug = VenueSlug.STOCKHOLM.slug
        val cartValue = 0
        val userLat = 90.00
        val userLon = -180.0050

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            DeliveryOrderPriceRequest(
                venueSlug = invalidVenueSlug,
                cartValue = cartValue,
                userLat = userLat,
                userLon = userLon
            )
        }
        assertEquals("Longitude must be greater than or equal to -180 and less than or equal to 180", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid user latitude`() {
        // Given
        val invalidVenueSlug = VenueSlug.BERLIN.slug
        val cartValue = 0
        val userLat = 90.01
        val userLon = 13.4050

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            DeliveryOrderPriceRequest(
                venueSlug = invalidVenueSlug,
                cartValue = cartValue,
                userLat = userLat,
                userLon = userLon
            )
        }
        assertEquals("Latitude must be greater than or equal to -90 and less than or equal to 90", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid cart value`() {
        // Given
        val invalidVenueSlug = VenueSlug.HELSINKI.slug
        val cartValue = -1
        val userLat = 52.5200
        val userLon = 13.4050

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            DeliveryOrderPriceRequest(
                venueSlug = invalidVenueSlug,
                cartValue = cartValue,
                userLat = userLat,
                userLon = userLon
            )
        }
        assertEquals("Cart value must be equal or greater than 0", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException for invalid venue slug`() {
        // Given
        val invalidVenueSlug = "invalidVenue" // This should not exist in your VenueSlug enum
        val cartValue = 100
        val userLat = 52.5200
        val userLon = 13.4050

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            DeliveryOrderPriceRequest(
                venueSlug = invalidVenueSlug,
                cartValue = cartValue,
                userLat = userLat,
                userLon = userLon
            )
        }
        assertEquals("Invalid venue slug: $invalidVenueSlug", exception.message)
    }
}