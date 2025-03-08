package model

import kotlinx.serialization.Serializable

@Serializable
data class CommandList(
    val commands: List<Command>
)