package io.github.sin6626.gravigo.data.model

import io.github.sin6626.gravigo.i18n.localError
import io.github.sin6626.gravigo.R

import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

@Serializable
data class ChatModel(
    val id: String,
    val label: String,
    val model: String,
    val supportsImages: Boolean,
    val supportedMimeTypes: Set<String> = emptySet(),
)

/** Account-scoped GetUserStatus catalog, with no hardcoded model aliases. */
internal fun parseChatModels(body: JsonObject): List<ChatModel> {
    val user = body["userStatus"]?.jsonObject ?: error(localError(R.string.msg_ea0e57f5a38b))
    val tier = user["planStatus"]?.jsonObject?.get("planInfo")?.jsonObject?.get("teamsTier")?.jsonPrimitive?.contentOrNull
    val configs = user["cascadeModelConfigData"]?.jsonObject?.get("clientModelConfigs")?.jsonArray
        ?: error(localError(R.string.msg_ea0e57f5a38b))
    return configs.mapNotNull { entry ->
        val item = entry.jsonObject
        val allowed = item["allowedTiers"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()
        if (allowed.isNotEmpty() && tier == null) error(localError(R.string.msg_c3a133b52250))
        if (allowed.isNotEmpty() && tier !in allowed) return@mapNotNull null
        val id = item["modelId"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
        val label = item["label"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
        val model = item["modelOrAlias"]?.jsonObject?.get("model")?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
        if (id.isBlank() || label.isBlank() || !model.matches(Regex("MODEL_[A-Z0-9_]+"))) return@mapNotNull null
        val mimeTypes = item["supportedMimeTypes"]?.jsonObject?.mapNotNull { (mime, enabled) ->
            mime.takeIf { enabled.jsonPrimitive.booleanOrNull == true }
        }?.toSet().orEmpty()
        ChatModel(id, label, model, item["supportsImages"]?.jsonPrimitive?.booleanOrNull == true, mimeTypes)
    }.distinctBy { it.id }.also { check(it.isNotEmpty()) { localError(R.string.msg_3de9489f1f9e) } }
}
