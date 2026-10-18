package io.github.sin6626.gravigo.data.service

import kotlinx.serialization.json.Json

object JsonConfig {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }
}
