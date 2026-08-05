package com.kazemieh.common

data class PaymentReturnResolution(
    val success: Boolean,
    val message: String,
)

/**
 * A provider deep link is only a return signal. Financial success is derived
 * from a separately verified server-side order state, never from this value.
 */
fun paymentReturnResolution(providerStatus: String): PaymentReturnResolution? =
    when (providerStatus) {
        "success", "failed" -> PaymentReturnResolution(
            success = false,
            message = "Payment confirmation is pending. Check your orders.",
        )
        else -> null
    }
