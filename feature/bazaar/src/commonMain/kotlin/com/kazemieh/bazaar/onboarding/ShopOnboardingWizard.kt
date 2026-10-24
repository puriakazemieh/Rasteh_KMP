package com.kazemieh.bazaar.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Stateless, recovery-friendly wizard: caller persists the draft locally and retries with the same idempotency key. */
data class ShopApplicationDraft(val idempotencyKey:String,val name:String="",val phone:String="",val address:String="",val preciseLocationConsent:Boolean=false)
@Composable fun ShopOnboardingWizard(draft:ShopApplicationDraft, syncing:Boolean, error:String?, onChange:(ShopApplicationDraft)->Unit,onSave:()->Unit,onSubmit:()->Unit) {
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Text("ثبت فروشگاه",style=MaterialTheme.typography.headlineSmall)
  OutlinedTextField(draft.name,{onChange(draft.copy(name=it))},label={Text("نام فروشگاه")},modifier=Modifier.fillMaxWidth())
  OutlinedTextField(draft.phone,{onChange(draft.copy(phone=it))},label={Text("شماره تماس")},modifier=Modifier.fillMaxWidth())
  OutlinedTextField(draft.address,{onChange(draft.copy(address=it))},label={Text("نشانی")},modifier=Modifier.fillMaxWidth())
  Row { Checkbox(draft.preciseLocationConsent,{onChange(draft.copy(preciseLocationConsent=it))}); Text("با ثبت مکان دقیق برای بررسی موافقم") }
  if(error!=null) Text(error,color=MaterialTheme.colorScheme.error)
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick=onSave,enabled=!syncing){Text("ذخیره پیش‌نویس")};Button(onClick=onSubmit,enabled=!syncing&&draft.name.isNotBlank()&&draft.preciseLocationConsent){Text(if(syncing)"در حال ارسال" else "ارسال برای بررسی")}}
 }
}
