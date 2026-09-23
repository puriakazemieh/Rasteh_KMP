package com.kazemieh.data.auth.mapper

import com.kazemieh.network.profile.dto.response.UserResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class UserMapperTest {
    @Test
    fun `profile falls back to normalized first and last name`() {
        val profile = UserResponse(
            id = 7,
            email = "person@example.invalid",
            city = "Tehran",
            postalCode = 12345,
            role = "CUSTOMER",
            firstName = "Ada",
            lastName = "Lovelace",
        ).toProfile()

        assertEquals("Ada Lovelace", profile.fullName)
        assertEquals("CUSTOMER", profile.role)
    }
}
