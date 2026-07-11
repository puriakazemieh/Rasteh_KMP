package com.kazemieh.bazaar

import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.Shop

/** صفِ تأییدِ فروشندگان (ادمین). فیلترِ وضعیت + اقداماتِ تأیید/رد/تعلیق. */
data class AdminShopsState(
    val status: String = "PENDING", // PENDING | APPROVED | REJECTED | SUSPENDED
    val shops: AppResult<List<Shop>> = AppResult.Loading,
    val actingId: Long? = null,
)

sealed interface AdminShopsIntent {
    data class OnStatusChange(val status: String) : AdminShopsIntent
    data object Refresh : AdminShopsIntent
    data class Approve(val id: Long) : AdminShopsIntent
    data class Reject(val id: Long) : AdminShopsIntent
    data class Suspend(val id: Long) : AdminShopsIntent
}
