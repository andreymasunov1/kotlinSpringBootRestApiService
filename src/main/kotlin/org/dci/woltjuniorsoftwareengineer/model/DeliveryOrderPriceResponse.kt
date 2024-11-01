package org.dci.woltjuniorsoftwareengineer.model

data class DeliveryOrderPriceResponse (
    val totalPrice: Double,
    val smallOrderSurcharge: Double,
    val cartValue: Int,
    val delivery: Delivery
)

data class Delivery(
    val fee: Double,
    val distance: Double
)