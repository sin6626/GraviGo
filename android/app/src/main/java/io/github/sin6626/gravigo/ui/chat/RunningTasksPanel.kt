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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.model.RunningTaskItem
import io.github.sin6626.gravigo.ui.demo.AccentBlue
import io.github.sin6626.gravigo.ui.demo.Ink
import io.github.sin6626.gravigo.ui.demo.SecondaryInk
import io.github.sin6626.gravigo.ui.demo.quietClickable
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun RunningTasksPanel(
    tasks: List<RunningTaskItem>,
    stoppingIds: Set<String>,
    error: String?,
    onStop: (RunningTaskItem) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    val now by produceState(System.currentTimeMillis(), tasks) {
        while (true) {
            delay(1_000)
            value = System.currentTimeMillis()
        }
    }
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
            .background(Color(0xFFF4F7FB), shape)
            .animateContentSize(tween(180))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics { stateDescription = appString(R.string.msg_93ab5da7bdf0, tasks.size) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .semantics { contentDescription = if (expanded) appString(R.string.msg_52a1b443973e) else appString(R.string.msg_bb32e0011cef) }
                .quietClickable(shape = RoundedCornerShape(14.dp)) { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(appString(R.string.msg_4c46c904395c), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Text(appString(R.string.msg_323fa903a13a, tasks.size), color = SecondaryInk, fontSize = 13.sp)
            }
            Text(if (expanded) appString(R.string.msg_afd4b783536b) else appString(R.string.msg_00bd3960fea8), color = AccentBlue, fontSize = 14.sp)
        }
        error?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, color = Color(0xFFB3261E), fontSize = 13.sp)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(180), expandFrom = Alignment.Top) + fadeIn(tween(120)),
            exit = shrinkVertically(tween(160), shrinkTowards = Alignment.Top) + fadeOut(tween(100)),
        ) {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 260.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tasks.forEach { task ->
                    val stopping = task.stableId in stoppingIds
                    Column(
                        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp))
                            .animateContentSize(tween(180)).padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(15.dp), color = AccentBlue, strokeWidth = 2.dp)
                            Text(
                                task.displayTitle,
                                modifier = Modifier.padding(start = 8.dp).weight(1f),
                                color = Ink,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            task.displayCommand.ifBlank { appString(R.string.msg_9dc7ac114aca) },
                            color = SecondaryInk,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                appString(R.string.msg_1edc4e23ed0c, runningTaskDuration(task.startedAt, now)),
                                modifier = Modifier.weight(1f),
                                color = SecondaryInk,
                                fontSize = 12.sp,
                            )
                            Box(
                                Modifier.heightIn(min = 48.dp).padding(start = 8.dp)
                                    .semantics { contentDescription = appString(R.string.msg_bc86159da759, task.displayTitle) }
                                    .quietClickable(RoundedCornerShape(12.dp), enabled = !stopping) { onStop(task) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (stopping) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(Modifier.size(16.dp), color = Color(0xFFB3261E), strokeWidth = 2.dp)
                                        Text(appString(R.string.msg_8a99be840844), color = SecondaryInk, fontSize = 13.sp, modifier = Modifier.padding(start = 7.dp))
                                    }
                                } else {
                                    Text(appString(R.string.msg_ca4d973c0b00), color = Color(0xFFB3261E), fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun runningTaskDuration(startedAt: String?, nowMs: Long): String {
    val start = startedAt?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() } ?: return appString(R.string.msg_444a23b03171)
    val seconds = ((nowMs - start).coerceAtLeast(0L) / 1_000L)
    val hours = seconds / 3_600
    val minutes = seconds % 3_600 / 60
    val rest = seconds % 60
    return when {
        hours > 0 -> appString(R.string.msg_422911db5a11, hours, minutes)
        minutes > 0 -> appString(R.string.msg_e3327d91cca1, minutes, rest)
        else -> appString(R.string.msg_028e69c4e457, rest)
    }
}
