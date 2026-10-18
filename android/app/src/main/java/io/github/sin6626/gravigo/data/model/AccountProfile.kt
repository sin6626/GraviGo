package io.github.sin6626.gravigo.data.model

import io.github.sin6626.gravigo.i18n.localError
import io.github.sin6626.gravigo.R

import kotlinx.serialization.json.*

data class AccountProfile(
    val name: String,
    val email: String,
    val profilePictureUrl: String,
    val planName: String,
)

internal fun parseAccountProfile(body: JsonObject): AccountProfile {
    val user = body["userStatus"]?.jsonObject ?: error(localError(R.string.msg_bd5141abcc52))
    val name = user["name"]?.jsonPrimitive?.contentOrNull.orEmpty().trim()
    val email = user["email"]?.jsonPrimitive?.contentOrNull.orEmpty().trim()
    check(name.isNotBlank() || email.isNotBlank()) { localError(R.string.msg_9d3af9254188) }
    return AccountProfile(name, email,
        user["profilePictureUrl"]?.jsonPrimitive?.contentOrNull.orEmpty().trim(),
        user["planStatus"]?.jsonObject?.get("planInfo")?.jsonObject
            ?.get("planName")?.jsonPrimitive?.contentOrNull.orEmpty().trim())
}
