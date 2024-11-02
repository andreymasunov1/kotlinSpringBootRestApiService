package org.dci.woltjuniorsoftwareengineer.model

/**
 * Represents static data specific to a venue, including fixed geographical coordinates.
 *
 * @property coordinates a pair representing the latitude and longitude of the venue location
 *  - First value: latitude, within the range [-90.0, 90.0]
 *  - Second value: longitude, within the range [-180.0, 180.0]
 */
data class VenueStaticData(
    val coordinates: Pair<Double, Double>
) {
    init {
        // Ensure the latitude is within valid range
        require(coordinates.first in -90.0..90.0) { "Latitude must be between -90 and 90 degrees" }
        // Ensure the longitude is within valid range
        require(coordinates.second in -180.0..180.0) { "Longitude must be between -180 and 180 degrees" }
    }
}
