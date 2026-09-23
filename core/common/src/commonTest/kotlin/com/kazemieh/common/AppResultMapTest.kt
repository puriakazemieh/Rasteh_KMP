package com.kazemieh.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AppResultMapTest {
    @Test
    fun `map transforms only successful values`() {
        val result = AppResult.Success(21).map { it * 2 }

        assertEquals(42, assertIs<AppResult.Success<Int>>(result).data)
    }

    @Test
    fun `map preserves an error without invoking the transformer`() {
        val result: AppResult<Int> = AppResult.Error("fixture", 400)

        assertIs<AppResult.Error>(result.map { error("must not execute") })
    }
}
