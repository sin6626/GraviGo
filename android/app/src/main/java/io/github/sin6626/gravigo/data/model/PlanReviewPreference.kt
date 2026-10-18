package io.github.sin6626.gravigo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlanReviewPreference(
    val policy: String = "ALWAYS",
    val planningCommand: String = "plan",
    val planningModeToggleSupported: Boolean = false,
)
