package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.model.PlanReviewPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorkModeBottomSheet(
    onDismiss: () -> Unit,
    planningSelected: Boolean,
    currentCommandName: String?,
    onSelectPlanning: (Boolean) -> Unit,
    preference: PlanReviewPreference?,
    loadingPreference: Boolean,
    updatingPreference: Boolean,
    selectingMode: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelectPolicy: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(Modifier.padding(top = 10.dp, bottom = 4.dp).size(36.dp, 4.dp)
                .background(Color(0xFFDCDCDC), RoundedCornerShape(2.dp)))
        },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(appString(R.string.msg_78d18c6b29c7), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp))
            Text(appString(R.string.msg_1857e1c4ce43), color = SecondaryInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            WorkModeRow(
                title = appString(R.string.msg_e2354023bafe),
                subtitle = appString(R.string.msg_f2c3a8dcde4f),
                selected = !planningSelected,
                enabled = !selectingMode,
                description = appString(R.string.msg_3d9b52d62b9e),
                onClick = { onSelectPlanning(false) },
            )
            WorkModeRow(
                title = appString(R.string.msg_c97c9604982a),
                subtitle = currentCommandName?.takeUnless { it.equals("plan", true) }
                    ?.let { appString(R.string.msg_0d19520619a7, it) }
                    ?: appString(R.string.msg_096f409db8a0),
                selected = planningSelected,
                enabled = !selectingMode,
                description = appString(R.string.msg_4bf828359bc8),
                busy = selectingMode,
                onClick = { onSelectPlanning(true) },
            )

            Text(appString(R.string.msg_fcd1a793c5d8), color = SecondaryInk, fontSize = 12.sp,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
            when {
                loadingPreference && preference == null -> Row(
                    Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = AccentBlue, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(appString(R.string.msg_cd87d88d2f84), color = SecondaryInk, fontSize = 14.sp)
                }
                preference != null -> {
                    WorkModeRow(
                        title = appString(R.string.msg_4e4399bf6d85),
                        subtitle = appString(R.string.msg_02af2c84112a),
                        selected = preference.policy == "ALWAYS",
                        enabled = !updatingPreference,
                        description = appString(R.string.msg_58f55ca70bd1),
                        busy = updatingPreference && preference.policy != "ALWAYS",
                        onClick = { onSelectPolicy("ALWAYS") },
                    )
                    WorkModeRow(
                        title = appString(R.string.msg_679e5268e719),
                        subtitle = appString(R.string.msg_7ebc8dbfe0d1),
                        selected = preference.policy == "TURBO",
                        enabled = !updatingPreference,
                        warning = true,
                        description = appString(R.string.msg_c867e79b3e7e),
                        busy = updatingPreference && preference.policy != "TURBO",
                        onClick = { onSelectPolicy("TURBO") },
                    )
                }
            }
            error?.let {
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(it, color = Color(0xFFB3261E), fontSize = 13.sp, lineHeight = 18.sp,
                        modifier = Modifier.weight(1f))
                    TextButton(onClick = onRetry, enabled = !loadingPreference && !updatingPreference && !selectingMode) {
                        Text(appString(R.string.msg_b8784c8dd563), color = AccentBlue)
                    }
                }
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) { Text(appString(R.string.msg_c0b3fbff51cc), color = AccentBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium) }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun WorkModeRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    enabled: Boolean,
    description: String,
    busy: Boolean = false,
    warning: Boolean = false,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (selected) (if (warning) Color(0xFFFFF4E5) else AccentBlue.copy(alpha = .08f)) else Color(0xFFF7F7F7),
        tween(180), label = "work mode background",
    )
    val outline by animateColorAsState(
        if (selected) (if (warning) Color(0xFFE58A00) else AccentBlue) else Color.Transparent,
        tween(180), label = "work mode outline",
    )
    Row(
        Modifier.fillMaxWidth().heightIn(min = 68.dp).clip(RoundedCornerShape(18.dp))
            .background(background).border(1.dp, outline, RoundedCornerShape(18.dp))
            .quietClickable(RoundedCornerShape(18.dp), enabled = enabled, onClick = onClick)
            .semantics {
                contentDescription = description
                this.selected = selected
                role = Role.RadioButton
            }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = if (enabled) Ink else SecondaryInk.copy(alpha = .55f),
                fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = SecondaryInk, fontSize = 12.sp, lineHeight = 17.sp)
        }
        if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = AccentBlue, strokeWidth = 2.dp)
        else Box(
            Modifier.size(20.dp).border(1.5.dp, if (selected) outline else Color(0xFFB9B9B9), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(10.dp).background(outline, CircleShape))
        }
    }
}
