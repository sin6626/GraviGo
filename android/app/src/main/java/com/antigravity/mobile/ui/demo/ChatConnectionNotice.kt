package com.antigravity.mobile.ui.demo

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
import com.antigravity.mobile.data.service.ConnectionStatus
import com.antigravity.mobile.ui.chat.ChatUiState

internal fun chatConnectionIssue(state: ChatUiState): String? = when {
    !state.isPaired -> null
    !state.networkAvailable -> "暂无网络连接，草稿已保留"
    state.isRetryingConnection -> "正在重新连接…"
    state.messagesError != null -> "聊天暂未同步，请重试"
    state.selectedConversationId == null && state.conversationsError != null -> "暂时无法连接电脑"
    state.selectedConversationId != null && state.connectionStatus == ConnectionStatus.FAILED -> "连接暂时中断，正在重连"
    else -> null
}

@Composable
internal fun ChatConnectionNotice(title: String, retrying: Boolean, onRetry: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        .semantics { liveRegion = LiveRegionMode.Polite }, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CloudOff, contentDescription = null, tint = SecondaryInk.copy(alpha = .7f),
            modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (retrying) "正在重新连接…" else title, color = SecondaryInk, fontSize = 13.sp, lineHeight = 18.sp,
            modifier = Modifier.weight(1f).padding(vertical = 8.dp))
        Box(Modifier.size(48.dp).semantics { contentDescription = "重试连接"; role = Role.Button }
            .quietClickable(CircleShape, enabled = !retrying, onClick = onRetry), contentAlignment = Alignment.Center) {
            if (retrying) CircularProgressIndicator(Modifier.size(16.dp), color = AccentBlue, strokeWidth = 1.5.dp)
            else Icon(Icons.Outlined.Refresh, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
        }
    }
}
