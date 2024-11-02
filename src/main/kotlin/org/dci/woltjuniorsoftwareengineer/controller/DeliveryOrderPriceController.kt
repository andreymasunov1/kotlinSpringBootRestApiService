package org.dci.woltjuniorsoftwareengineer.controller

import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceRequest
import org.dci.woltjuniorsoftwareengineer.service.DeliveryOrderPriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/\${api.version}")
class DeliveryOrderPriceController(
    @Autowired private val deliveryOrderPriceService: DeliveryOrderPriceService,
) {

    @GetMapping("/delivery-order-price")
    fun getDeliveryOrderPrice(
        @RequestParam("venue_slug") venueSlug: String,
        @RequestParam("cart_value") cartValue: Int,
        @RequestParam("user_lat") userLat: Double,
        @RequestParam("user_lon") userLon: Double,
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(deliveryOrderPriceService.getDeliveryOrderPrice(
            DeliveryOrderPriceRequest(venueSlug, cartValue, userLat, userLon)
        ))
    }
}