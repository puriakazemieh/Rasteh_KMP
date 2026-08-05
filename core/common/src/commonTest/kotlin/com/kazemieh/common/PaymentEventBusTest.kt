package com.kazemieh.common

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentEventBusTest {
    @Test
    fun `reset removes a payment success hint`() = runTest {
        PaymentEventBus.publish(PaymentResult("success", "1"))
        PaymentEventBus.reset()

        assertEquals("none", PaymentEventBus.events.first().status)
    }
}
