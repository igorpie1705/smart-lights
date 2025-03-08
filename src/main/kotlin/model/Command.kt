package model

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val type: String,
    val vehicleId: String? = null,
    val startRoad: String? = null,
    val endRoad: String? = null
)
