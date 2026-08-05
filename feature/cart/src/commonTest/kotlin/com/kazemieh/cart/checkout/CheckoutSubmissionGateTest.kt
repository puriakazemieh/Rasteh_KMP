package com.kazemieh.cart.checkout

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutSubmissionGateTest {
    @Test
    fun `second checkout tap is rejected until the first submission completes`() {
        val gate = CheckoutSubmissionGate()

        assertTrue(gate.tryAcquire())
        assertFalse(gate.tryAcquire())

        gate.release()

        assertTrue(gate.tryAcquire())
    }
}
