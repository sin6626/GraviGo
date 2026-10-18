package io.github.sin6626.gravigo.i18n

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("zh-CN" to "简体中文", "en" to "English").forEach { (tag, label) ->
            FilterChip(selected = AppLanguage.selected(context) == tag,
                onClick = { context.activity()?.let { AppLanguage.change(it, tag) } },
                label = { Text(label) }, modifier = Modifier.heightIn(min = 48.dp))
        }
    }
}
