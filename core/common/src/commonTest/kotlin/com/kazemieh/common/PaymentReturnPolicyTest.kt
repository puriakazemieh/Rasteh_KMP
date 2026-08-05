package com.kazemieh.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class PaymentReturnPolicyTest {
    @Test
    fun `a success deep link remains unconfirmed until server verification`() {
        val result = paymentReturnResolution("success")

        assertNotNull(result)
        assertFalse(result.success)
    }
}
