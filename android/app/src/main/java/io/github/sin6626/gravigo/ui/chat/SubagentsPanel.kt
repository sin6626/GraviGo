package io.github.sin6626.gravigo.ui.chat

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.model.SubagentItem
import io.github.sin6626.gravigo.ui.demo.AccentBlue
import io.github.sin6626.gravigo.ui.demo.Ink
import io.github.sin6626.gravigo.ui.demo.SecondaryInk
import io.github.sin6626.gravigo.ui.demo.quietClickable

@Composable
fun SubagentsPanel(
    subagents: List<SubagentItem>,
    isLoading: Boolean,
    error: String?,
    onOpen: (SubagentItem) -> Unit,
    onRetry: () -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    val shape = RoundedCornerShape(22.dp)
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color(0xFFF4F7FB), shape)
            .animateContentSize(tween(180))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics { stateDescription = if (error != null && subagents.isEmpty()) appString(R.string.msg_11dc2641e2ec) else appString(R.string.msg_1df95554c7f9, subagents.size) },
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = if (expanded) appString(R.string.msg_1213a49b9fe5) else appString(R.string.msg_861fb65c7672) }
                .quietClickable(RoundedCornerShape(14.dp)) { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(appString(R.string.msg_acd37b4c796c), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Text(if (error != null && subagents.isEmpty()) appString(R.string.msg_588dbd9dc4b0) else appString(R.string.msg_1df95554c7f9, subagents.size), color = SecondaryInk, fontSize = 13.sp)
            }
            if (isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentBlue)
            else Text(if (expanded) appString(R.string.msg_afd4b783536b) else appString(R.string.msg_00bd3960fea8), color = AccentBlue, fontSize = 14.sp)
        }
        error?.let {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(appString(R.string.msg_810a1d08ac39), Modifier.weight(1f), color = SecondaryInk, fontSize = 13.sp)
                Text(
                    appString(R.string.msg_b8784c8dd563),
                    Modifier.heightIn(min = 48.dp).padding(horizontal = 12.dp)
                        .semantics { contentDescription = appString(R.string.msg_32337decb9b7) }
                        .quietClickable(RoundedCornerShape(12.dp), onClick = onRetry),
                    color = AccentBlue,
                    fontSize = 14.sp,
                )
            }
        }
        AnimatedVisibility(
            visible = expanded && subagents.isNotEmpty(),
            enter = expandVertically(tween(180), expandFrom = Alignment.Top) + fadeIn(tween(120)),
            exit = shrinkVertically(tween(160), shrinkTowards = Alignment.Top) + fadeOut(tween(100)),
        ) {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 260.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                subagents.forEach { subagent ->
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 60.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .semantics { contentDescription = appString(R.string.msg_fa6fdd8d1cbb, subagent.displayTitle, subagent.displayStatus) }
                            .quietClickable(RoundedCornerShape(16.dp)) { onOpen(subagent) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(subagent.displayTitle, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                listOfNotNull(
                                    subagent.displayStatus,
                                    subagent.stepCount.takeIf { it > 0 }?.let { appString(R.string.msg_7131663e23be, it) },
                                ).joinToString(" · "),
                                color = when (subagent.activityStatus) {
                                    io.github.sin6626.gravigo.data.model.ConversationStatus.RUNNING -> AccentBlue
                                    io.github.sin6626.gravigo.data.model.ConversationStatus.ACTION -> Color(0xFF9A6700)
                                    io.github.sin6626.gravigo.data.model.ConversationStatus.ERROR -> Color(0xFFB3261E)
                                    else -> SecondaryInk
                                },
                                fontSize = 13.sp,
                            )
                        }
                        Text(appString(R.string.msg_db8db0530432), color = AccentBlue, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SubagentReadOnlyBar() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color(0xFFF4F7FB), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .semantics { contentDescription = appString(R.string.msg_eefd375052f7) },
    ) {
        Text(appString(R.string.msg_0912cb61f80f), color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        Text(appString(R.string.msg_d7ee2dc493fc), color = SecondaryInk, fontSize = 13.sp)
    }
}
