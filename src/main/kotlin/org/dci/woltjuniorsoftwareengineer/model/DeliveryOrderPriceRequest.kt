package org.dci.woltjuniorsoftwareengineer.model

import org.dci.woltjuniorsoftwareengineer.enums.VenueSlug

/**
 * Data class representing a request for calculating the delivery order price.
 *
 * @property venueSlug the unique identifier for the venue, validated to ensure it matches a predefined slug
 * @property cartValue the total value of the user's shopping cart, must be non-negative
 * @property userLat the latitude of the user's location, constrained to valid geographical values (-90 to 90)
 * @property userLon the longitude of the user's location, constrained to valid geographical values (-180 to 180)
 */
data class DeliveryOrderPriceRequest(
    val venueSlug: String,
    val cartValue: Int,
    val userLat: Double,
    val userLon: Double
) {
    init {
        // Validate venueSlug to ensure it is not empty and matches an allowed venue
        require(venueSlug.isNotEmpty()) { "Venue slug must not be empty" }
        require(VenueSlug.entries.any { it.slug == venueSlug }) { "Invalid venue slug: $venueSlug" }

        // Validate cartValue to ensure it is non-negative
        require(cartValue >= 0) { "Cart value must be equal or greater than 0" }

        // Validate userLat to ensure it falls within the valid latitude range
        require(userLat in -90.0..90.0) { "Latitude must be between -90 and 90" }

        // Validate userLon to ensure it falls within the valid longitude range
        require(userLon in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }
}
