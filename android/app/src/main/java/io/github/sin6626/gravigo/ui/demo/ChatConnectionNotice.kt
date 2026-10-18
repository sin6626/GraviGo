package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.service.ConnectionStatus
import io.github.sin6626.gravigo.ui.chat.ChatUiState

internal fun chatConnectionIssue(state: ChatUiState): String? = when {
    !state.isPaired -> null
    !state.networkAvailable -> appString(R.string.msg_b31fe6f3d61b)
    state.isRetryingConnection -> appString(R.string.msg_90e28b54e030)
    state.messagesError != null -> appString(R.string.msg_5118b131521c)
    state.selectedConversationId == null && state.conversationsError != null -> appString(R.string.msg_a818ad46894c)
    state.selectedConversationId != null && state.connectionStatus == ConnectionStatus.FAILED -> appString(R.string.msg_29d254dd7a9b)
    else -> null
}

@Composable
internal fun ChatConnectionNotice(title: String, retrying: Boolean, onRetry: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        .semantics { liveRegion = LiveRegionMode.Polite }, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = SecondaryInk.copy(alpha = .7f),
            modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (retrying) appString(R.string.msg_90e28b54e030) else title, color = SecondaryInk, fontSize = 13.sp, lineHeight = 18.sp,
            modifier = Modifier.weight(1f).padding(vertical = 8.dp))
        Box(Modifier.size(48.dp).semantics { contentDescription = appString(R.string.msg_c059b61c6a28); role = Role.Button }
            .quietClickable(CircleShape, enabled = !retrying, onClick = onRetry), contentAlignment = Alignment.Center) {
            if (retrying) CircularProgressIndicator(Modifier.size(16.dp), color = AccentBlue, strokeWidth = 1.5.dp)
            else Icon(Icons.Outlined.Refresh, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
        }
    }
}
