package org.dci.woltjuniorsoftwareengineer.service

import org.dci.woltjuniorsoftwareengineer.exception.CustomException
import org.dci.woltjuniorsoftwareengineer.model.*
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class DeliveryOrderPriceService(
    private val venueStaticService: VenueStaticService,
    private val venueDynamicService: VenueDynamicService
) {

    /**
     * Retrieves the delivery order price by fetching static and dynamic data for a venue and
     * calculating the delivery fee, small order surcharge, and total price.
     */
    fun getDeliveryOrderPrice(
        deliveryOrderPriceRequest: DeliveryOrderPriceRequest
    ): DeliveryOrderPriceResponse {
        val venueStaticData = venueStaticService.getStaticData(deliveryOrderPriceRequest.venueSlug)
            ?: throw CustomException("Venue static data not found for slug: ${deliveryOrderPriceRequest.venueSlug}")

        val venueDynamicData = venueDynamicService.getDynamicData(deliveryOrderPriceRequest.venueSlug)
            ?: throw CustomException("Venue dynamic data not found for slug: ${deliveryOrderPriceRequest.venueSlug}")

        return calculateDeliveryOrderPrice(deliveryOrderPriceRequest, venueStaticData, venueDynamicData)
    }

    /**
     * Calculates the total delivery order price based on the order details, venue static data, and dynamic data.
     */
    private fun calculateDeliveryOrderPrice(
        request: DeliveryOrderPriceRequest,
        venueStaticData: VenueStaticData,
        venueDynamicData: VenueDynamicData
    ): DeliveryOrderPriceResponse {
        val smallOrderSurcharge = calculateSmallOrderSurcharge(venueDynamicData, request.cartValue)
        val deliveryDistance = calculateDistance(venueStaticData.coordinates, request.userLat, request.userLon)
        val deliveryFee = calculateDeliveryFee(venueDynamicData, deliveryDistance)
        val totalPrice = calculateTotalPrice(request.cartValue, smallOrderSurcharge, deliveryFee)

        return DeliveryOrderPriceResponse(
            totalPrice = totalPrice,
            smallOrderSurcharge = smallOrderSurcharge,
            cartValue = request.cartValue,
            delivery = Delivery(fee = deliveryFee, distance = deliveryDistance)
        )
    }

    /**
     * Calculates the delivery fee based on the distance and applicable range within the venue's dynamic data.
     */
    private fun calculateDeliveryFee(
        dynamicData: VenueDynamicData,
        deliveryDistance: Double
    ): Double {
        val applicableRangeIndex = findApplicableRangeIndex(deliveryDistance, dynamicData.distanceRanges)
        val range = dynamicData.distanceRanges[applicableRangeIndex]

        return dynamicData.basePrice + range.a + (range.b * deliveryDistance / 10)
    }

    /**
     * Calculates the small order surcharge if the cart value is below the minimum threshold.
     */
    private fun calculateSmallOrderSurcharge(dynamicData: VenueDynamicData, cartValue: Int): Double {
        val deficit = dynamicData.orderMinimumNoSurcharge - cartValue
        return if (deficit > 0) deficit else 0.0
    }

    /**
     * Calculates the total price of the order including cart value, small order surcharge, and delivery fee.
     */
    private fun calculateTotalPrice(cartValue: Int, smallOrderSurcharge: Double, deliveryFee: Double): Double {
        return cartValue + smallOrderSurcharge + deliveryFee
    }

    /**
     * Finds the index of the distance range applicable for the delivery distance.
     */
    private fun findApplicableRangeIndex(
        deliveryDistance: Double,
        distanceRanges: List<DistanceRange>
    ): Int {
        val rangeIndex = distanceRanges.indexOfFirst { range ->
            (range.max == 0.0 && deliveryDistance >= range.min) || // Last range for unlimited max distance
                    (deliveryDistance in range.min..range.max)              // Distance falls within range
        }

        if (rangeIndex == -1 || distanceRanges[rangeIndex].max == 0.0) {
            throw CustomException("Delivery is not possible for distances greater than ${distanceRanges.last().min}")
        }

        return rangeIndex
    }

    /**
     * Calculates the distance between the user's location and the venue using the Haversine formula.
     */
    private fun calculateDistance(coordinates: Pair<Double, Double>, userLat: Double, userLon: Double): Double {
        val (venueLon, venueLat) = coordinates
        val R = 6371.0 // Radius of Earth in kilometers

        val latDistance = Math.toRadians(userLat - venueLat)
        val lonDistance = Math.toRadians(userLon - venueLon)

        val a = sin(latDistance / 2).pow(2) +
                cos(Math.toRadians(venueLat)) * cos(Math.toRadians(userLat)) *
                sin(lonDistance / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceInMeters = R * c * 1000

        return round(distanceInMeters)
    }
}
