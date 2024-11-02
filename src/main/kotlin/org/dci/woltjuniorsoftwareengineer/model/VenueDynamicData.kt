package org.dci.woltjuniorsoftwareengineer.model

/**
 * Represents dynamic data specific to a venue, including pricing and distance-based fee ranges.
 *
 * @property orderMinimumNoSurcharge the minimum cart value required to avoid a surcharge
 * @property basePrice the base price for orders from this venue
 * @property distanceRanges list of distance-based fee ranges used to calculate additional delivery charges
 */
data class VenueDynamicData(
    val orderMinimumNoSurcharge: Double,
    val basePrice: Double,
    val distanceRanges: List<DistanceRange>
)

/**
 * Represents a range of distances and associated pricing parameters used to calculate delivery fees.
 *
 * @property min the minimum distance threshold for this range
 * @property max the maximum distance threshold for this range
 * @property a a constant factor used in fee calculations (purpose dependent on business logic)
 * @property b a variable factor used in fee calculations (purpose dependent on business logic)
 * @property flag an optional flag providing additional information or conditions for this range
 */
data class DistanceRange(
    val min: Double,
    val max: Double,
    val a: Int,
    val b: Double,
    val flag: String? = null
)
