package com.antigravity.mobile.ui.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.R
import com.antigravity.mobile.data.model.AccountProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppSettingsBottomSheet(defaultFileWrap: Boolean, onDefaultFileWrap: (Boolean) -> Unit,
    profile: AccountProfile? = null, loading: Boolean = false, error: String? = null,
    onRefresh: () -> Unit = {}, onDismiss: () -> Unit) {
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var closing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var linkError by remember { mutableStateOf(false) }
    val version = remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull()
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = .24f), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(Modifier.padding(top = 12.dp, bottom = 8.dp).size(36.dp, 4.dp)
                .background(Color(0xFFDADDE1), RoundedCornerShape(2.dp)))
        }) {
        Column(Modifier.fillMaxWidth().heightIn(max = (LocalConfiguration.current.screenHeightDp * .85f).dp)
            .verticalScroll(rememberScrollState()).navigationBarsPadding().padding(horizontal = 24.dp)
            .padding(bottom = 12.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("账号与设置", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f).semantics { heading() })
                Box(Modifier.size(48.dp).semantics { contentDescription = "关闭账号与设置" }
                    .quietClickable(CircleShape, enabled = !closing) {
                        closing = true
                        scope.launch {
                            try { sheet.hide(); if (!sheet.isVisible) onDismiss() }
                            finally { closing = false }
                        }
                    }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = SecondaryInk, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccountAvatar(profile, 64.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(profile?.name?.ifBlank { profile.email } ?: if (loading) "正在读取账号…" else "账号资料暂不可用",
                        color = Ink, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    profile?.email?.takeIf(String::isNotBlank)?.let { Text(it, color = SecondaryInk, fontSize = 13.sp) }
                    profile?.planName?.takeIf(String::isNotBlank)?.let {
                        Text(it, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.background(AccentBlue.copy(alpha = .08f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                IconButton(onClick = onRefresh, enabled = !loading, modifier = Modifier.size(48.dp)) {
                    if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = AccentBlue, strokeWidth = 2.dp)
                    else Icon(Icons.Outlined.Refresh, contentDescription = "刷新账号资料", tint = SecondaryInk, modifier = Modifier.size(20.dp))
                }
            }
            Text("来自电脑端当前登录的 Antigravity 账号", color = SecondaryInk, fontSize = 12.sp, lineHeight = 18.sp)
            error?.let {
                Text(if (profile == null) "账号资料读取失败，可点击重试" else "刷新失败，当前显示上次读取的资料",
                    color = SecondaryInk, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                TextButton(onClick = onRefresh, enabled = !loading) { Text("重试账号资料", color = AccentBlue) }
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEBEDF0))
            Spacer(Modifier.height(20.dp))
            Text("阅读", color = SecondaryInk, fontSize = 12.sp)
            Row(Modifier.fillMaxWidth().heightIn(min = 64.dp)
                .toggleable(value = defaultFileWrap, role = Role.Switch, onValueChange = onDefaultFileWrap)
                .semantics { contentDescription = "文件默认换行" }
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("文件默认换行", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text("代码与纯文本适应屏幕宽度", color = SecondaryInk, fontSize = 12.sp, lineHeight = 18.sp)
                }
                Switch(checked = defaultFileWrap, onCheckedChange = null,
                    colors = SwitchDefaults.colors(checkedTrackColor = AccentBlue, checkedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE9EBEE), uncheckedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White))
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEBEDF0))
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painterResource(R.drawable.multigravity_mark), contentDescription = null, modifier = Modifier.size(40.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(stringResource(R.string.app_name), color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(version?.let { "版本 $it" } ?: "版本信息暂不可用", color = SecondaryInk, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("把Antigravity，带在身边", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                lineHeight = 23.sp)
            Spacer(Modifier.height(8.dp))
            Text("连接电脑上的 Antigravity，在手机上查看任务进度、发送指令，审阅方案与代码变更。",
                color = SecondaryInk, fontSize = 13.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(8.dp))
            Text("基于九哥（GHSaiMo）的 antigravity-mobile 项目开发，持续打磨移动端体验与跨设备协作能力。",
                color = SecondaryInk, fontSize = 13.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("本项目代码" to "https://github.com/sin6626/AntigravityMobile-Android",
                    "原项目" to "https://github.com/GHSaiMo/antigravity-mobile").forEach { (label, uri) ->
                    TextButton(onClick = {
                        linkError = runCatching { uriHandler.openUri(uri) }.isFailure
                    }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(label, color = AccentBlue, fontSize = 13.sp)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null,
                            tint = AccentBlue, modifier = Modifier.size(14.dp))
                    }
                }
            }
            if (linkError) Text("无法打开浏览器，请稍后重试", color = SecondaryInk, fontSize = 12.sp)
        }
    }
}
