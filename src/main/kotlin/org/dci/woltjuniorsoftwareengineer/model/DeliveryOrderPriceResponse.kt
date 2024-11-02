package org.dci.woltjuniorsoftwareengineer.model

/**
 * Data class representing the response for a delivery order price request.
 *
 * @property totalPrice the total calculated price for the delivery order, including any applicable fees
 * @property smallOrderSurcharge any additional surcharge applied to small orders
 * @property cartValue the value of the items in the cart (without fees or surcharges)
 * @property delivery details related to the delivery, including fee and distance
 */
data class DeliveryOrderPriceResponse(
    val totalPrice: Double,
    val smallOrderSurcharge: Double,
    val cartValue: Int,
    val delivery: Delivery
)

/**
 * Data class representing details about the delivery component of the order.
 *
 * @property fee the fee associated with delivering the order
 * @property distance the distance for the delivery in kilometers or meters
 */
data class Delivery(
    val fee: Double,
    val distance: Double
)
