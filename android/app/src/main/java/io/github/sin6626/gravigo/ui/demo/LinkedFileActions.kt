package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import io.github.sin6626.gravigo.data.service.mimeTypeForFilename
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private enum class LinkedFileAction { SAVE, SHARE, OPEN }

internal fun unsupportedPreviewHint(filename: String): String {
    val mime = mimeTypeForFilename(filename)
    return when {
        mime.startsWith("audio/") || mime.startsWith("video/") -> appString(R.string.msg_10795590f6ea)
        mime == "application/pdf" || filename.substringAfterLast('.', "").lowercase() in
            setOf("doc", "docx", "xls", "xlsx", "ppt", "pptx") -> appString(R.string.msg_3cc39fe26240)
        else -> appString(R.string.msg_b3a7645384cb)
    }
}

@Composable
internal fun LinkedFileActions(
    uri: String,
    filename: String,
    showOpen: Boolean,
    viewModel: ChatViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mimeType = remember(filename) { mimeTypeForFilename(filename) }
    var preparedFile by remember(uri) { mutableStateOf<File?>(null) }
    var busy by remember(uri) { mutableStateOf<LinkedFileAction?>(null) }
    var feedback by remember(uri) { mutableStateOf<String?>(null) }
    var feedbackIsError by remember(uri) { mutableStateOf(false) }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { target ->
        val source = preparedFile
        if (target == null || source == null) {
            busy = null
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val saved = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(target, "w")?.use { output ->
                        source.inputStream().use { input -> input.copyTo(output) }
                    } ?: error(appString(R.string.msg_9d8c31021d0b))
                }
            }
            busy = null
            feedbackIsError = saved.isFailure
            feedback = saved.fold({ appString(R.string.msg_2553a785fef2) }, { it.message ?: appString(R.string.msg_6309a3bb5ba4) })
        }
    }

    fun launchExternal(action: LinkedFileAction, file: File) {
        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = when (action) {
            LinkedFileAction.SHARE -> Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TITLE, filename)
            }
            LinkedFileAction.OPEN -> Intent(Intent.ACTION_VIEW).apply { setDataAndType(contentUri, mimeType) }
            LinkedFileAction.SAVE -> return
        }.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
            clipData = ClipData.newRawUri(filename, contentUri)
        }
        runCatching {
            context.startActivity(Intent.createChooser(intent,
                if (action == LinkedFileAction.SHARE) appString(R.string.msg_7aeae6204892) else appString(R.string.msg_698a12969a42)))
        }.onFailure {
            feedbackIsError = true
            feedback = if (action == LinkedFileAction.OPEN) appString(R.string.msg_92b6c06166c5) else appString(R.string.msg_7db469dbe261)
        }
    }

    fun runAction(action: LinkedFileAction) {
        if (busy != null) return
        busy = action
        feedback = null
        val cached = preparedFile?.takeIf(File::isFile)
        if (cached != null) {
            if (action == LinkedFileAction.SAVE) saveLauncher.launch(filename)
            else { busy = null; launchExternal(action, cached) }
            return
        }
        scope.launch {
            viewModel.downloadLinkedFile(uri, filename).fold(
                onSuccess = { file ->
                    preparedFile = file
                    if (action == LinkedFileAction.SAVE) saveLauncher.launch(filename)
                    else { busy = null; launchExternal(action, file) }
                },
                onFailure = { error ->
                    busy = null
                    feedbackIsError = true
                    feedback = error.message ?: appString(R.string.msg_7255193529b2)
                },
            )
        }
    }

    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LinkedFileActionButton(appString(R.string.msg_a3030bf8f16d), appString(R.string.msg_4d8dc23ed6e1), Icons.Outlined.Download, busy == LinkedFileAction.SAVE,
                busy == null) { runAction(LinkedFileAction.SAVE) }
            LinkedFileActionButton(appString(R.string.msg_7e564575eb7d), appString(R.string.msg_7aeae6204892), Icons.Outlined.Share, busy == LinkedFileAction.SHARE,
                busy == null) { runAction(LinkedFileAction.SHARE) }
            if (showOpen) LinkedFileActionButton(
                if (mimeType.startsWith("audio/") || mimeType.startsWith("video/")) appString(R.string.msg_c3396195e91c) else appString(R.string.msg_c771248e511f),
                appString(R.string.msg_707d3f88e0c4), Icons.AutoMirrored.Outlined.OpenInNew, busy == LinkedFileAction.OPEN,
                busy == null,
            ) { runAction(LinkedFileAction.OPEN) }
        }
        AnimatedVisibility(feedback != null) {
            Text(feedback.orEmpty(), color = if (feedbackIsError) Color(0xFFB3261E) else Color(0xFF1B6E3C),
                fontSize = 13.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
private fun RowScope.LinkedFileActionButton(
    label: String,
    description: String,
    icon: ImageVector,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier.weight(1f).height(48.dp)
            .background(if (enabled) Color(0xFFF2F5F9) else Color(0xFFF2F5F9).copy(alpha = .55f), shape)
            .semantics { contentDescription = description }
            .quietClickable(shape = shape, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentBlue)
        else Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(18.dp))
            Text(label, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
