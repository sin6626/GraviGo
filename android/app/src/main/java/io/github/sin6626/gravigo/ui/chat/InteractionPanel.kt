package io.github.sin6626.gravigo.ui.chat

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.model.InteractionOption
import io.github.sin6626.gravigo.data.model.PendingInteraction
import io.github.sin6626.gravigo.ui.demo.AccentBlue
import io.github.sin6626.gravigo.ui.demo.Ink
import io.github.sin6626.gravigo.ui.demo.SecondaryInk
import io.github.sin6626.gravigo.ui.demo.quietClickable

@Composable
fun PlanReviewPanel(
    filename: String,
    conversationTitle: String,
    isProceeding: Boolean,
    isSubmittingComments: Boolean,
    commentCount: Int,
    error: String?,
    onView: () -> Unit,
    onProceed: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color(0xFFF4F7FB), shape)
            .border(1.dp, Color(0xFFE1E8F0), shape)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(appString(R.string.msg_2fe7f697abff), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(5.dp))
        Text(appString(R.string.msg_221b1cbfbe8b, conversationTitle.ifBlank { appString(R.string.msg_9800a458c16b) }), color = SecondaryInk, fontSize = 13.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(appString(R.string.msg_90d6711efd79, filename), color = SecondaryInk, fontSize = 13.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color(0xFFB3261E), fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier.weight(1f).height(48.dp)
                    .quietClickable(shape = RoundedCornerShape(14.dp), onClick = onView),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(
                    if (commentCount > 0) appString(R.string.msg_ed0c0e3a7648, commentCount) else appString(R.string.msg_c02d54936449),
                    color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                )
            }
            Box(
                Modifier.weight(1f).height(48.dp)
                    .background(if (isProceeding || isSubmittingComments) AccentBlue.copy(alpha = .55f) else AccentBlue, RoundedCornerShape(14.dp))
                    .semantics { stateDescription = if (isProceeding || isSubmittingComments) appString(R.string.msg_6b70462dbb4c) else appString(R.string.msg_76d8f1522814) }
                    .quietClickable(shape = RoundedCornerShape(14.dp), enabled = !isProceeding && !isSubmittingComments, onClick = onProceed),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                if (isProceeding || isSubmittingComments) {
                    CircularProgressIndicator(Modifier.size(19.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        if (commentCount > 0) appString(R.string.msg_0fe0c0dc9119) else appString(R.string.msg_39be5cf61df9),
                        color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun InteractionPanel(
    interaction: PendingInteraction,
    isSubmitting: Boolean,
    error: String?,
    onChoose: (List<InteractionOption>, String) -> Unit,
) {
    var writeIn by remember(interaction.trajectoryId, interaction.stepIndex) { mutableStateOf("") }
    var selectedIds by remember(interaction.trajectoryId, interaction.stepIndex) { mutableStateOf<Set<String>>(emptySet()) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color(0xFFF6F6F6), RoundedCornerShape(22.dp))
            .heightIn(max = 360.dp).verticalScroll(rememberScrollState())
            .animateContentSize(tween(180))
            .padding(16.dp),
    ) {
        Text(interaction.title.ifBlank { appString(R.string.msg_34cffa29df2a) }, color = Ink, fontSize = 17.sp)
        if (interaction.toolName.isNotBlank()) {
            Spacer(Modifier.height(5.dp))
            Text(appString(R.string.msg_3bc545fae725, interaction.toolName), color = AccentBlue, fontSize = 13.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        if (interaction.description.isNotBlank()) {
            Spacer(Modifier.height(7.dp))
            Text(interaction.description, color = SecondaryInk, fontSize = 14.sp)
        }
        if (interaction.target.isNotBlank()) {
            Spacer(Modifier.height(7.dp))
            Text(interaction.target, color = SecondaryInk, fontSize = 13.sp,
                maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color(0xFFB3261E), fontSize = 13.sp)
        }
        Spacer(Modifier.height(12.dp))
        if (interaction.isMultiSelect) {
            interaction.options.forEach { option ->
                val selected = option.id in selectedIds
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .semantics { contentDescription = appString(R.string.msg_f2abb7db6f15, option.text) }
                        .toggleable(
                            value = selected,
                            enabled = !isSubmitting,
                            role = Role.Checkbox,
                            onValueChange = { checked ->
                                selectedIds = if (checked) selectedIds + option.id else selectedIds - option.id
                            },
                        ),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = null,
                        enabled = !isSubmitting,
                        colors = CheckboxDefaults.colors(checkedColor = AccentBlue),
                    )
                    Text(option.text, color = Ink, fontSize = 15.sp, modifier = Modifier.weight(1f).padding(vertical = 9.dp))
                }
            }
        } else {
            interaction.options.forEach { option ->
                Text(
                    option.text,
                    modifier = Modifier.fillMaxWidth()
                        .quietClickable(enabled = !isSubmitting) { onChoose(listOf(option), writeIn) }
                        .padding(vertical = 10.dp),
                    color = if (option.isDeny) Color(0xFFB3261E) else Ink,
                    fontSize = 15.sp,
                )
            }
        }
        if (interaction.hasWriteIn) {
            Spacer(Modifier.height(8.dp))
            BasicTextField(
                value = writeIn,
                onValueChange = { writeIn = it },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp))
                    .semantics { contentDescription = interaction.writeInLabel.ifBlank { appString(R.string.msg_7b2e460360b7) } }
                    .padding(12.dp),
                decorationBox = { inner ->
                    androidx.compose.foundation.layout.Box {
                        if (writeIn.isBlank()) {
                            Text(interaction.writeInPlaceholder.ifBlank { appString(R.string.msg_7b2e460360b7) },
                                color = SecondaryInk, fontSize = 14.sp)
                        }
                        inner()
                    }
                },
            )
            if (interaction.type == "ask_question" && writeIn.isNotBlank()) {
                if (!interaction.isMultiSelect) {
                    Text(appString(R.string.msg_15f2eb64b1cf), modifier = Modifier.quietClickable(enabled = !isSubmitting) {
                        onChoose(emptyList(), writeIn)
                    }.padding(vertical = 12.dp), color = AccentBlue, fontSize = 15.sp)
                }
            }
        }
        if (interaction.isMultiSelect) {
            val selected = interaction.options.filter { it.id in selectedIds }
            val canSubmit = selected.isNotEmpty() || writeIn.isNotBlank()
            Spacer(Modifier.height(10.dp))
            if (!canSubmit) {
                Text(appString(R.string.msg_b56da92bd86e), color = SecondaryInk, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }
            Box(
                Modifier.fillMaxWidth().height(48.dp)
                    .background(if (canSubmit && !isSubmitting) AccentBlue else Color(0xFFD3D6DA), RoundedCornerShape(14.dp))
                    .semantics {
                        stateDescription = when {
                            isSubmitting -> appString(R.string.msg_6b70462dbb4c)
                            canSubmit -> appString(R.string.msg_afd9f554fdb1)
                            else -> appString(R.string.msg_e30179de855c)
                        }
                    }
                    .quietClickable(
                        shape = RoundedCornerShape(14.dp),
                        enabled = canSubmit && !isSubmitting,
                    ) { onChoose(selected, writeIn) },
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(Modifier.size(19.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(appString(R.string.msg_cbfe17b7e485), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
