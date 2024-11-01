package org.dci.woltjuniorsoftwareengineer.model

import org.dci.woltjuniorsoftwareengineer.enums.VenueSlug

data class DeliveryOrderPriceRequest(

    val venueSlug: String,
    val cartValue: Int,
    val userLat: Double,
    val userLon: Double
) {
    init {
        require(venueSlug.isNotEmpty()) { "Venue slug must not be empty" }
        require(VenueSlug.entries.any { it.slug == venueSlug }) { "Invalid venue slug: $venueSlug" }
        require(cartValue >= 0) { "Cart value must be equal or greater than 0" }
        require(userLat in -90.0..90.0) { "Latitude must be greater than or equal to -90 and less than or equal to 90" }
        require(userLon in -180.0..180.0) { "Longitude must be greater than or equal to -180 and less than or equal to 180" }
    }
}