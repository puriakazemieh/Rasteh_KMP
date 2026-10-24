package com.kazemieh.bazaar.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kazemieh.domain.authorization.Capability

/** Capability-gated admin surface. Callers must not create this screen without its server snapshot. */
data class MembershipRow(val id:String,val userLabel:String,val role:String,val status:String)
data class ReviewRow(val id:String,val agentLabel:String,val submittedAt:String)
@Composable fun AuthorizationAdminScreen(capabilities:Set<Capability>, memberships:List<MembershipRow>, reviewQueue:List<ReviewRow>, onRevoke:(String)->Unit, onReview:(String,Boolean)->Unit) {
    if (Capability.SHOP_MEMBERSHIP_MANAGE !in capabilities && Capability.AGENT_DRAFT_REVIEW !in capabilities && Capability.PLATFORM_ROLE_MANAGE !in capabilities) { Text("دسترسی به مدیریت مجاز نیست"); return }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        if(Capability.PLATFORM_ROLE_MANAGE in capabilities) item { Text("نقش‌های پلتفرم",style=MaterialTheme.typography.titleLarge) }
        if(Capability.SHOP_MEMBERSHIP_MANAGE in capabilities) { item { Text("عضویت‌های فروشگاه",style=MaterialTheme.typography.titleLarge) }; items(memberships){m->Card{Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("${m.userLabel} · ${m.role} · ${m.status}"); if(m.status!="REVOKED") TextButton({onRevoke(m.id)}){Text("لغو")}}} } }
        if(Capability.AGENT_DRAFT_REVIEW in capabilities) { item { Text("صف بررسی بازاریاب",style=MaterialTheme.typography.titleLarge) }; items(reviewQueue){d->Card{Column(Modifier.padding(12.dp)){Text("${d.agentLabel} · ${d.submittedAt}");Row{TextButton({onReview(d.id,true)}){Text("تأیید")};TextButton({onReview(d.id,false)}){Text("رد")}}}} } }
    }
}
