package com.kazemieh.navigation

import com.kazemieh.domain.authorization.Capability
import com.kazemieh.domain.authorization.CapabilitySnapshot

/** Use before navigating to a protected destination; redirect instead of composing protected content. */
fun CapabilitySnapshot.permitsDeepLink(required: Capability): Boolean = allows(required)
