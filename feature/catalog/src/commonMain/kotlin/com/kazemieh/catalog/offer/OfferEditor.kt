package com.kazemieh.catalog.offer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class OfferMode { SHOWCASE, RESERVABLE, BUYABLE }
data class OfferEditorState(val mode:OfferMode=OfferMode.SHOWCASE,val priceMinorUnits:String="",val onHand:String="",val fulfillment:String="",val lastConfirmedLabel:String?=null,val stale:Boolean=false)
fun OfferEditorState.canSave():Boolean=when(mode){OfferMode.SHOWCASE->priceMinorUnits.isBlank();OfferMode.RESERVABLE->fulfillment.isNotBlank();OfferMode.BUYABLE->priceMinorUnits.toLongOrNull()?.let{it>=0}==true&&onHand.toIntOrNull()?.let{it>=0}==true&&fulfillment.isNotBlank()}
@Composable fun OfferEditor(state: OfferEditorState, onChange: (OfferEditorState) -> Unit, onSave: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("عرضهٔ فروشگاه", style = MaterialTheme.typography.titleLarge)
        SingleChoiceSegmentedButtonRow {
            OfferMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(state.mode == mode, { onChange(state.copy(mode = mode)) }, SegmentedButtonDefaults.itemShape(index, OfferMode.entries.size)) { Text(mode.name) }
            }
        }
        if (state.mode != OfferMode.SHOWCASE) OutlinedTextField(state.priceMinorUnits, { onChange(state.copy(priceMinorUnits = it)) }, label = { Text("قیمت ریال") })
        if (state.mode == OfferMode.BUYABLE) OutlinedTextField(state.onHand, { onChange(state.copy(onHand = it)) }, label = { Text("موجودی") })
        if (state.mode != OfferMode.SHOWCASE) OutlinedTextField(state.fulfillment, { onChange(state.copy(fulfillment = it)) }, label = { Text("روش تحویل") })
        state.lastConfirmedLabel?.let { Text("آخرین تأیید: $it" + if (state.stale) " — نیازمند تأیید" else "", color = if (state.stale) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant) }
        Button(onClick = onSave, enabled = state.canSave()) { Text("ذخیره") }
    }
}
