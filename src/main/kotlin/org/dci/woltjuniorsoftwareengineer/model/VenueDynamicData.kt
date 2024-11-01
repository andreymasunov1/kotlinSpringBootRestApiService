package org.dci.woltjuniorsoftwareengineer.model

data class VenueDynamicData (
    val orderMinimumNoSurcharge: Double,
    val basePrice: Double,
    val distanceRanges: List<DistanceRanges>
)

data class DistanceRanges (
    val min: Double,
    val max: Double,
    val a: Int,
    val b: Double,
    val flag: String? = null
)