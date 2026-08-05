package com.kazemieh.network.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SafeApiCallCancellationTest {
    @Test
    fun `cancellation is rethrown instead of becoming a stale UI error`() = runTest {
        assertFailsWith<CancellationException> {
            safeApiCall<String> { throw CancellationException("test cancellation") }
        }
    }
}
