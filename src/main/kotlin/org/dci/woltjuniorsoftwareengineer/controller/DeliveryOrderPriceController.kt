package org.dci.woltjuniorsoftwareengineer.controller

import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceRequest
import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceResponse
import org.dci.woltjuniorsoftwareengineer.service.DeliveryOrderPriceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for handling API requests related to delivery order price calculation.
 */
@RestController
@RequestMapping("/api/\${api.version}")
class DeliveryOrderPriceController(
    // Constructor injection for service dependency
    private val deliveryOrderPriceService: DeliveryOrderPriceService
) {

    /**
     * Endpoint to get the calculated delivery order price based on venue and user location details.
     * @param venueSlug the unique identifier of the venue
     * @param cartValue the total value of the user's cart
     * @param userLat the latitude of the user's location
     * @param userLon the longitude of the user's location
     * @return ResponseEntity containing the calculated delivery order price
     */
    @GetMapping("/delivery-order-price")
    fun getDeliveryOrderPrice(
        @RequestParam("venue_slug") venueSlug: String,
        @RequestParam("cart_value") cartValue: Int,
        @RequestParam("user_lat") userLat: Double,
        @RequestParam("user_lon") userLon: Double,
    ): ResponseEntity<DeliveryOrderPriceResponse> {
        // Constructing a request object with the provided parameters
        val request = DeliveryOrderPriceRequest(
            venueSlug = venueSlug,
            cartValue = cartValue,
            userLat = userLat,
            userLon = userLon
        )

        // Calculating the delivery order price by delegating to the service layer
        val deliveryOrderPriceResponse = deliveryOrderPriceService.getDeliveryOrderPrice(request)

        // Returning the calculated delivery order price wrapped in a ResponseEntity for proper HTTP response
        return ResponseEntity.ok(deliveryOrderPriceResponse)
    }
}
