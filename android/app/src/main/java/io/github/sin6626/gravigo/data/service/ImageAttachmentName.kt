package io.github.sin6626.gravigo.data.service

internal fun imageAttachmentName(clientMessageId: String, index: Int): String =
    "multigravity-$clientMessageId-$index"
