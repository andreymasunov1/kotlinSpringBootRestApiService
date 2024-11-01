package org.dci.woltjuniorsoftwareengineer.controller

import org.dci.woltjuniorsoftwareengineer.model.DeliveryOrderPriceRequest
import org.dci.woltjuniorsoftwareengineer.service.DeliveryOrderPriceService
import org.dci.woltjuniorsoftwareengineer.service.VenueDynamicService
import org.dci.woltjuniorsoftwareengineer.service.VenueStaticService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/\${api.version}")
class DeliveryOrderPriceController(
    @Autowired private val deliveryOrderPriceService: DeliveryOrderPriceService,
    @Autowired private val venueStaticService: VenueStaticService,
    @Autowired private val venueDynamicService: VenueDynamicService
) {

    @GetMapping("/delivery-order-price")
    fun getDeliveryOrderPrice(
        @RequestParam("venue_slug") venueSlug: String,
        @RequestParam("cart_value") cartValue: Int,
        @RequestParam("user_lat") userLat: Double,
        @RequestParam("user_lon") userLon: Double,
    ): ResponseEntity<Any> {
        val value = DeliveryOrderPriceRequest(venueSlug, cartValue, userLat, userLon)
        val venueStaticData = venueStaticService.getStaticData(value.venueSlug)
        val venueDynamicData = venueDynamicService.getDynamicData(value.venueSlug)
        return ResponseEntity.ok(deliveryOrderPriceService.calculateDeliveryOrderPrice(value, venueStaticData, venueDynamicData))
    }
}