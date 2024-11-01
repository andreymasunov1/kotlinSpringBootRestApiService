package org.dci.woltjuniorsoftwareengineer.service

import org.dci.woltjuniorsoftwareengineer.exception.CustomException
import org.dci.woltjuniorsoftwareengineer.exception.ExternalApiException
import org.dci.woltjuniorsoftwareengineer.model.*
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class DeliveryOrderPriceService {

    fun calculateDeliveryOrderPrice(

        value: DeliveryOrderPriceRequest,
        staticData: VenueStaticData?,
        dynamicData: VenueDynamicData?
    ): DeliveryOrderPriceResponse {

        val smallOrderSurcharge = getSmallOrderSurcharge(dynamicData, value)
        val deliveryDistance = findDistance(staticData!!, value)
        val indexOfApplicableRange = findApplicableRange(deliveryDistance, dynamicData!!.distanceRanges)
        val deliveryFee = getDeliveryFee(dynamicData, indexOfApplicableRange, deliveryDistance)
        val delivery = Delivery(deliveryFee, deliveryDistance)
        val totalPrice = getTotalPrice(value, smallOrderSurcharge, deliveryFee)

        return DeliveryOrderPriceResponse(
            totalPrice,
            smallOrderSurcharge,
            value.cartValue,
            delivery
        )
    }

    private fun getDeliveryFee(
        dynamicData: VenueDynamicData?,
        indexOfApplicableRange: Int?,
        deliveryDistance: Double
    ) = dynamicData!!.basePrice +
            dynamicData.distanceRanges[indexOfApplicableRange!!].a +
            dynamicData.distanceRanges[indexOfApplicableRange].b * deliveryDistance / 10

    private fun getSmallOrderSurcharge(
        dynamicData: VenueDynamicData?,
        value: DeliveryOrderPriceRequest
    ): Double {
        val surcharge = dynamicData!!.orderMinimumNoSurcharge - value.cartValue
        return if (surcharge < 0) 0.0 else surcharge
    }

    private fun getTotalPrice(
        value: DeliveryOrderPriceRequest,
        smallOrderSurcharge: Double,
        deliveryFee: Double
    ): Double {
        val totalPrice = value.cartValue + smallOrderSurcharge + deliveryFee
        return totalPrice
    }

    private fun findApplicableRange(
        deliveryDistance: Double,
        distanceRanges: List<DistanceRanges>
    ): Int? {
        val applicableRange = distanceRanges.withIndex().firstOrNull { (index, distanceRange) ->
            // Check if it's the last range with max == 0 and the distance is greater than or equal to min
            (distanceRange.max == 0.0 && deliveryDistance >= distanceRange.min)
                    ||
                    // Otherwise, check if the delivery distance is in the range (min <= distance < max)
                    (deliveryDistance in distanceRange.min..distanceRange.max)
        }

        // If the applicable range has max == 0, throw a custom exception
        applicableRange?.let { (index, distanceRange) ->
            if (distanceRange.max == 0.0) {
                throw CustomException("Delivery is not possible for distances greater than ${distanceRange.min}")
            }
            return index
        }
        return null
    }

    private fun findDistance(staticData: VenueStaticData, value: DeliveryOrderPriceRequest): Double {
        val lat1 = staticData.coordinates.second
        val lon1 = staticData.coordinates.first
        val lat2 = value.userLat
        val lon2 = value.userLon

        val R = 6371.0 // Radius of the Earth in kilometers

        // Convert degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Differences in latitude and longitude
        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        // Apply the Haversine formula
        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInMeters = R * c * 1000

        return round(distanceInMeters)
    }
}
