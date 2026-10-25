package com.kazemieh.domain.canonical

/** Exact, currency-tagged money for all new commerce contracts. */
data class Money(
    val minorUnits: Long,
    val currency: String = "IRR",
) {
    init {
        require(minorUnits >= 0) { "Money must not be negative" }
        require(currency == "IRR") { "Only IRR is supported by the canonical marketplace contract" }
    }
}

enum class SaleMode { SHOWCASE, RESERVABLE, BUYABLE }

data class ShopOffer(
    val id: String,
    val productId: String,
    val shopLocationId: String,
    val title: String,
    val price: Money? = null,
    val saleMode: SaleMode,
    val availableQuantity: Int,
    val stale: Boolean = false,
    val lastConfirmedAt: String? = null,
)
