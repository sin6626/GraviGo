package com.antigravity.mobile.data.model

import kotlinx.serialization.json.*

data class AccountProfile(
    val name: String,
    val email: String,
    val profilePictureUrl: String,
    val planName: String,
)

internal fun parseAccountProfile(body: JsonObject): AccountProfile {
    val user = body["userStatus"]?.jsonObject ?: error("未返回账号资料")
    val name = user["name"]?.jsonPrimitive?.contentOrNull.orEmpty().trim()
    val email = user["email"]?.jsonPrimitive?.contentOrNull.orEmpty().trim()
    check(name.isNotBlank() || email.isNotBlank()) { "电脑端暂未返回已登录账号" }
    return AccountProfile(name, email,
        user["profilePictureUrl"]?.jsonPrimitive?.contentOrNull.orEmpty().trim(),
        user["planStatus"]?.jsonObject?.get("planInfo")?.jsonObject
            ?.get("planName")?.jsonPrimitive?.contentOrNull.orEmpty().trim())
}
