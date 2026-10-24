package com.kazemieh.domain.authorization

/** Server-provided capability contract. UI visibility is advisory; API remains authoritative. */
enum class Capability {
    PLATFORM_ROLE_MANAGE, SHOP_MEMBERSHIP_MANAGE, SHOP_VIEW_INTERNAL, SHOP_CATALOG_WRITE,
    SHOP_ORDER_OPERATE, SHOP_SUPPORT, AGENT_ASSIGN, AGENT_DRAFT_WRITE, AGENT_DRAFT_SUBMIT, AGENT_DRAFT_REVIEW
}

data class CapabilitySnapshot(val shopId: Long? = null, val capabilities: Set<Capability> = emptySet()) {
    fun allows(capability: Capability) = capability in capabilities
}
