package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sin6626.gravigo.data.model.DesktopMcpServer
import io.github.sin6626.gravigo.data.model.DesktopPermissionStatus
import io.github.sin6626.gravigo.data.model.DesktopStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DesktopStatusBottomSheet(
    onDismiss: () -> Unit,
    status: DesktopStatus?,
    loading: Boolean,
    refreshing: Boolean,
    error: String?,
    onRefresh: () -> Unit,
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
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).navigationBarsPadding()) {
            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(appString(R.string.msg_f7af68a7c161), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center))
                Box(
                    Modifier.align(Alignment.CenterEnd).size(48.dp)
                        .quietClickable(RoundedCornerShape(14.dp), enabled = !loading && !refreshing, onClick = onRefresh)
                        .semantics { contentDescription = if (refreshing) appString(R.string.msg_357bd39df153) else appString(R.string.msg_75bb7a1272ba) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (refreshing) CircularProgressIndicator(Modifier.size(20.dp), color = AccentBlue, strokeWidth = 2.dp)
                    else Text(appString(R.string.msg_aee887434131), color = if (loading) SecondaryInk.copy(alpha = .4f) else AccentBlue,
                        fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Text(appString(R.string.msg_b97f74a633ab),
                color = SecondaryInk, fontSize = 13.sp, lineHeight = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp))
            Crossfade(targetState = status, animationSpec = tween(180), label = "desktop status") { value ->
                when {
                    value == null && loading -> LoadingDesktopStatus()
                    value == null -> DesktopStatusError(error ?: appString(R.string.msg_da27318eedc9), onRefresh)
                    else -> DesktopStatusContent(value, error, onRefresh)
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(appString(R.string.msg_c0b3fbff51cc), color = AccentBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun LoadingDesktopStatus() {
    Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CircularProgressIndicator(Modifier.size(24.dp), color = AccentBlue, strokeWidth = 2.dp)
            Text(appString(R.string.msg_916cbcc2cfa8), color = SecondaryInk, fontSize = 14.sp)
        }
    }
}

@Composable
private fun DesktopStatusContent(status: DesktopStatus, error: String?, onRetry: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxWidth().heightIn(max = 570.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { PermissionCard(status.permission) }
        item {
            val ready = status.mcpServers.count { it.enabled && it.status.contains("READY", true) }
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(appString(R.string.msg_a203f86cf6a6), color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f))
                Text(if (status.mcpServers.isEmpty()) appString(R.string.msg_156219e305f6) else appString(R.string.msg_8bdf450032ec, ready, status.mcpServers.size),
                    color = SecondaryInk, fontSize = 13.sp)
            }
        }
        if (status.mcpServers.isEmpty()) item {
            Text(appString(R.string.msg_b68a96835547), color = SecondaryInk, fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF7F7F7), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 24.dp))
        }
        items(status.mcpServers, key = { it.name }) { McpServerCard(it) }
        if (error != null) item { DesktopStatusError(error, onRetry) }
        item { Spacer(Modifier.height(2.dp)) }
    }
}

@Composable
private fun PermissionCard(permission: DesktopPermissionStatus) {
    Column(
        Modifier.fillMaxWidth().background(Color(0xFFF7F7F7), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(appString(R.string.msg_785b2eece3e0), color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        StatusValue(appString(R.string.msg_a504543d35af), permissionPresetLabel(permission.preset))
        Text(permissionSourceLabel(permission.source), color = SecondaryInk, fontSize = 12.sp)
        StatusValue(appString(R.string.msg_05a3ad9bb1da), terminalPolicyLabel(permission.terminalPolicy))
        StatusValue(appString(R.string.msg_8b4670d298e3), filePolicyLabel(permission.nonWorkspaceFilePolicy))
        StatusValue(appString(R.string.msg_4f8f3547c8b9), permission.sandboxEnabled?.let { if (it) appString(R.string.msg_8da97ddda990) else appString(R.string.sandbox_off) } ?: appString(R.string.msg_6efe94f4ced0))
        StatusValue(appString(R.string.msg_6d652d7766db), if (permission.planReviewPolicy.equals("TURBO", true)) appString(R.string.msg_8c4e343b1ca0) else appString(R.string.msg_ba52ddc17e1d))
    }
}

@Composable
private fun StatusValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = SecondaryInk, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun McpServerCard(server: DesktopMcpServer) {
    val color by animateColorAsState(mcpStatusColor(server), tween(180), label = "MCP status")
    val status = mcpStatusLabel(server)
    Column(
        Modifier.fillMaxWidth().background(Color(0xFFF7F7F7), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .semantics { contentDescription = "${server.name.ifBlank { appString(R.string.msg_1cee7e6e390c) }}，$status" },
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(color, CircleShape))
            Text(server.name.ifBlank { appString(R.string.msg_1cee7e6e390c) }, color = Ink, fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
            Text(status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text(mcpToolSummary(server), color = SecondaryInk, fontSize = 13.sp)
        if (server.requiresAuthentication) Text(appString(R.string.msg_d3c42a692a74), color = Color(0xFFE58A00), fontSize = 12.sp)
        if (server.error.isNotBlank()) Text(server.error, color = Color(0xFFB3261E), fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun DesktopStatusError(message: String, onRetry: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).background(Color(0xFFFFF2F1), RoundedCornerShape(16.dp))
            .padding(start = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(message, color = Color(0xFFB3261E), fontSize = 13.sp, lineHeight = 18.sp,
            modifier = Modifier.weight(1f))
        TextButton(onClick = onRetry, modifier = Modifier.heightIn(min = 48.dp)
            .semantics { contentDescription = appString(R.string.msg_402201d65b68) }) { Text(appString(R.string.msg_b8784c8dd563), color = AccentBlue) }
    }
}

internal fun permissionPresetLabel(value: String): String = when (value.uppercase()) {
    "REQUEST_REVIEW" -> appString(R.string.msg_40d24d9584e7)
    "VETTED" -> "Vetted"
    "AUTO" -> appString(R.string.msg_7eb336e42cb5)
    "TURBO" -> "Turbo"
    else -> appString(R.string.msg_844b8cc8dff7)
}

private fun permissionSourceLabel(value: String): String = when (value.uppercase()) {
    "PERMISSION_PRESET" -> appString(R.string.msg_5fac8d59f306)
    "EFFECTIVE_SETTINGS" -> appString(R.string.msg_90ca5f07a4d0)
    else -> appString(R.string.msg_929a460ec685)
}

private fun terminalPolicyLabel(value: String): String = when {
    value.contains("EAGER", true) -> appString(R.string.msg_e2354023bafe)
    value.endsWith("_AUTO", true) || value.equals("AUTO", true) -> appString(R.string.msg_c69b6f431ab4)
    value.contains("OFF", true) -> appString(R.string.msg_c5dd3c30e065)
    value.contains("SANDBOX", true) -> appString(R.string.msg_ec267e921abd)
    else -> appString(R.string.msg_6efe94f4ced0)
}

private fun filePolicyLabel(value: String): String = when {
    value.contains("ALLOW", true) -> appString(R.string.msg_ce7ef28b670a)
    value.contains("DENY", true) -> appString(R.string.msg_136de7a8c46f)
    value.contains("ASK", true) -> appString(R.string.msg_f00ef1be4493)
    else -> appString(R.string.msg_6efe94f4ced0)
}

private fun mcpStatusLabel(server: DesktopMcpServer): String = when {
    !server.enabled || server.status.contains("DISABLED", true) -> appString(R.string.msg_a8c3698b5b8c)
    server.status.contains("READY", true) -> appString(R.string.msg_5be0323e8adc)
    server.status.contains("PENDING", true) -> appString(R.string.msg_a898478be181)
    server.status.contains("ERROR", true) -> appString(R.string.msg_0bc1fb72ae1b)
    else -> appString(R.string.msg_4d8c1c5b4283)
}

private fun mcpStatusColor(server: DesktopMcpServer): Color = when {
    !server.enabled || server.status.contains("DISABLED", true) -> SecondaryInk
    server.status.contains("READY", true) -> Color(0xFF188038)
    server.status.contains("PENDING", true) -> Color(0xFFE58A00)
    server.status.contains("ERROR", true) -> Color(0xFFB3261E)
    else -> SecondaryInk
}

private fun mcpToolSummary(server: DesktopMcpServer): String = when {
    !server.enabled -> appString(R.string.msg_2519bbdf4398, server.toolCount)
    server.toolErrorCount > 0 -> appString(R.string.msg_a61b00eac520, server.enabledToolCount, server.toolErrorCount)
    else -> appString(R.string.msg_a1eebf8069e0, server.enabledToolCount)
}
