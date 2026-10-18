package io.github.sin6626.gravigo.data.model

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import kotlinx.serialization.Serializable

@Serializable
data class SubagentItem(
    val id: String,
    val parentConversationId: String,
    val rootConversationId: String? = null,
    val title: String = "",
    val status: String = "",
    val stepCount: Int = 0,
    val lastModifiedTime: String? = null,
    val nestingDepth: Int = 0,
    val needsInput: Boolean = false,
    val hasError: Boolean = false,
) {
    val displayTitle: String get() = ConversationItem.sanitizeTitle(title).let {
        if (it == "未命名会话") appString(R.string.msg_74d15b417198) else it
    }
    val activityStatus: ConversationStatus get() = when {
        needsInput || status.contains("WAIT", true) || status.contains("ACTION", true) -> ConversationStatus.ACTION
        hasError || status.contains("ERROR", true) || status.contains("FAIL", true) -> ConversationStatus.ERROR
        status.contains("RUNNING", true) -> ConversationStatus.RUNNING
        else -> ConversationStatus.IDLE
    }
    val displayStatus: String get() = when (activityStatus) {
        ConversationStatus.ACTION -> appString(R.string.msg_8fb3a3261b43)
        ConversationStatus.ERROR -> appString(R.string.msg_9344597efd49)
        ConversationStatus.RUNNING -> appString(R.string.msg_1f0eb99b7ed0)
        else -> appString(R.string.msg_f28461bb49c8)
    }
}

@Serializable
data class SubagentsResponse(val subagents: List<SubagentItem> = emptyList())
