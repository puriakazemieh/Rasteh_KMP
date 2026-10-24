package com.kazemieh.network.onboarding

import com.kazemieh.network.common.safeApiCallRaw
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable data class ShopApplicationRequest(val idempotencyKey:String,val name:String,val phone:String?=null,val address:String?=null,val preciseLocationConsent:Boolean=false)
@Serializable data class ShopApplicationResponse(val id:String,val status:String,val reviewReason:String?=null)
/** Shared seller/agent onboarding transport. Retrying the same draft is safe through idempotencyKey. */
interface OnboardingApi { suspend fun createDraft(request:ShopApplicationRequest):ShopApplicationResponse; suspend fun consent(id:String):ShopApplicationResponse; suspend fun submit(id:String):ShopApplicationResponse }
class OnboardingApiImpl(private val client:HttpClient):OnboardingApi {
 override suspend fun createDraft(request:ShopApplicationRequest):ShopApplicationResponse=safeApiCallRaw{client.post("/api/shop-applications"){contentType(ContentType.Application.Json);setBody(request)}}
 override suspend fun consent(id:String):ShopApplicationResponse=safeApiCallRaw{client.post("/api/shop-applications/$id/consent")}
 override suspend fun submit(id:String):ShopApplicationResponse=safeApiCallRaw{client.post("/api/shop-applications/$id/submit")}
}
